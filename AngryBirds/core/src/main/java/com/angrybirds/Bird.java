package com.angrybirds;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Bird {
    private Texture texture;
    private Vector2 position;
    private float scale;
    private Body body;
    private boolean disposed = false;
    protected float baseDensity = 1.0f;

    private float width;
    private float height;
    public static final float PPM = 100.0f;

    // New physics parameters
    private float rotationDamping = 0.9f;
    private float groundFrictionMultiplier = 1.5f;
    private float maxRotationSpeed = 5.0f;

    public Bird(World world, String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y);
        this.scale = scale;
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.angularDamping = 0.5f; // Add angular damping for more realistic rotation
        bodyDef.linearDamping = 0.2f; // Add linear damping to reduce unrealistic sliding

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        float radius = texture.getWidth() * scale / 2 / PPM;
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = baseDensity;
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0.6f; // Bounciness

        // Allow rotation but with some constraints
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

        // Check if bird is on ground and moving
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
        // This is a simplified ground check. In a real game, you'd use contact listeners or raycast
        return Math.abs(body.getLinearVelocity().y) < 0.1f;
    }

    public void setBodyType(BodyDef.BodyType bodyType) {
        body.setType(bodyType);
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(body.getPosition().x * PPM, body.getPosition().y * PPM,
            texture.getWidth() * scale, texture.getHeight() * scale);
    }

    public void setPosition(float x, float y) {
        body.setTransform(x / PPM, y / PPM, body.getAngle());
    }

    private Texture loadTexture(String texturePath) {
        return new Texture(Gdx.files.internal(texturePath));
    }

    public void draw(SpriteBatch batch) {
        // Rotate the sprite based on the body's angle
        batch.draw(texture,
            body.getPosition().x * PPM - (texture.getWidth() * scale / 2),
            body.getPosition().y * PPM - (texture.getHeight() * scale / 2),
            texture.getWidth() * scale / 2,
            texture.getHeight() * scale / 2,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
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

    public void launch(Vector2 force) {
        body.applyLinearImpulse(force, body.getWorldCenter(), true);
    }

    public void dispose() {
        texture.dispose();
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public Body getBody() {
        return body;
    }
}
