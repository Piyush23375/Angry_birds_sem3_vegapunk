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
    private Texture damagedtexture;
    private float height;
    private Body body;
    private float scale;
    private boolean disposed = false;
    protected float baseDensity = 1.0f;
    private float health;
    private float maxHealth;

    public static final float PPM = 100.0f; // Pixels Per Meter, matching Bird class
    private boolean isdestroyed=false;

    // Physics parameters
    private float rotationDamping = 0.9f;
    private float groundFrictionMultiplier = 1.5f;
    private float maxRotationSpeed = 5.0f;

    public Pig(World world, String texturePath,String damagedpath, float x, float y, float scale,float maxHealth) {
        this.texture = loadTexture(texturePath);
        this.damagedtexture = new Texture(Gdx.files.internal(damagedpath));
        this.position = new Vector2(x, y);

        this.maxHealth = maxHealth;
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        // Create Box2D body
        PigPhysics pigPhysics = new PigPhysics(world);
        this.body = pigPhysics.createStructureBody(
            (position.x + width / 2) / PPM,
            (position.y + height / 2) / PPM,
            width / PPM,
            height / PPM,
            1f
        );

        body.setUserData(this);
    }



    public boolean isDestroyed() {
        return isdestroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.isdestroyed = destroyed;
    }
    public void takeDamage(float damageAmount) {
        health = Math.max(0, health - damageAmount);
    }

    public void heal(float healAmount) {
        health = Math.min(maxHealth, health + healAmount);
    }

//    public boolean isDestroyed() {
//        return health <= 0;
//    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getHealthPercentage() {
        return (health / maxHealth) * 100f;
    }

    public void applyDamage(float damage) {
        health -= damage;
        if(health<0.6f*maxHealth && health>0){
            texture=damagedtexture;
        }
        else if(health <= 0) {
            isdestroyed = true;
        }
    }

    protected void onDestroyed() {
        // Implement destruction effects like:
        // - Particle effects
        // - Sound effects
        // - Scoring
        // - Removing from game world
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
        if (!isdestroyed) {// Rotate the sprite based on the body's angle
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
        isdestroyed = true;
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
