package com.angrybirds;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Pig implements GameObject {
    private Texture texture;
    private Vector2 position;
    private float width;
    private float height;
    private Body body;
    private float scale;
    private boolean disposed = false;
    protected float baseDensity = 1.0f;

    public static final float PPM = 100.0f; // Pixels Per Meter, matching Bird class

    // Physics parameters
    private float rotationDamping = 0.9f;
    private float groundFrictionMultiplier = 1.5f;
    private float maxRotationSpeed = 5.0f;

    public Pig(World world, String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y);
        this.scale = scale;
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;

        // Create Box2D body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.angularDamping = 0.5f; // Angular damping for realistic rotation
        bodyDef.linearDamping = 0.2f; // Linear damping to reduce unrealistic sliding

        body = world.createBody(bodyDef);

        // Create circular shape for the pig
        CircleShape shape = new CircleShape();
        float radius = Math.min(width, height) * scale / 2 / PPM;
        shape.setRadius(radius);

        // Create fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = baseDensity;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0.5f; // Slightly less bouncy than birds

        // Allow rotation but with constraints
        body.setFixedRotation(false);
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void setDensity(float density) {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setDensity(density);
        }
        body.resetMassData();
    }

    public float getDensity() {
        return baseDensity;
    }

    public void update(float deltaTime) {
        // Apply rotation damping
        float currentAngularVelocity = body.getAngularVelocity();
        if (Math.abs(currentAngularVelocity) > maxRotationSpeed) {
            body.setAngularVelocity(Math.signum(currentAngularVelocity) * maxRotationSpeed);
        }

        // Check if pig is on ground and moving
        if (isOnGround()) {
            // Increase friction when on ground
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setFriction(fixture.getFriction() * groundFrictionMultiplier);
            }

            // Apply additional torque based on linear velocity
            Vector2 linearVelocity = body.getLinearVelocity();
            float torqueIntensity = Math.abs(linearVelocity.x) * 10f;
            body.applyTorque(linearVelocity.x > 0 ? torqueIntensity : -torqueIntensity, true);
        }
    }

    private boolean isOnGround() {
        // Simplified ground check
        return Math.abs(body.getLinearVelocity().y) < 0.1f;
    }

    @Override
    public void draw(Batch batch) {
        // Rotate the sprite based on the body's angle
        batch.draw(texture,
            body.getPosition().x * PPM - (width / 2),
            body.getPosition().y * PPM - (height / 2),
            width / 2,
            height / 2,
            width,
            height,
            1, 1,
            (float) Math.toDegrees(body.getAngle()),
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            false, false);
    }

    public void setVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x * PPM, body.getPosition().y * PPM);
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(body.getPosition().x * PPM, body.getPosition().y * PPM, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
        disposed = true;
    }

    private Texture loadTexture(String texturePath) {
        return new Texture(Gdx.files.internal(texturePath));
    }

    public Body getBody() {
        return body;
    }

    public boolean isDisposed() {
        return disposed;
    }
}
