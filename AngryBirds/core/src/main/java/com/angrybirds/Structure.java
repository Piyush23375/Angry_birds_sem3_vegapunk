package com.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;


public class Structure implements GameObject {
    private Texture texture;
    private Texture damagedtexture;
    private Vector2 position;
    private float width;
    private float height;
    private Body body;
    private float health;
    private float maxHealth;
    private static final float PPM = 100.0f;
    private static final float DENSITY = 1.0f;
    private static final float DEFAULT_MAX_HEALTH = 50.0f;

    // Improved impact damage constants
    private static final float MINIMUM_IMPACT_THRESHOLD = 1.0f;
    private static final float MEDIUM_IMPACT_THRESHOLD = 5.0f;
    private static final float WEAK_IMPACT_MULTIPLIER = 0.1f;
    private static final float MEDIUM_IMPACT_MULTIPLIER = 0.5f;
    private static final float STRONG_IMPACT_MULTIPLIER = 1.0f;
    private boolean isdestroyed=false;

    public Structure(World world, String texturePath,String damagedpath, float x, float y, float scale) {
        this(world, texturePath,damagedpath, x, y, scale, DEFAULT_MAX_HEALTH);
    }

    public Structure(World world, String texturePath,String damagedpath, float x, float y, float scale, float maxHealth) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.damagedtexture = new Texture(Gdx.files.internal(damagedpath));
        this.position = new Vector2(x, y);
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        PhysicsManager physicsManager = new PhysicsManager(world);
        this.body = physicsManager.createStructureBody(
            (position.x + width / 2) / PPM,
            (position.y + height / 2) / PPM,
            width / PPM,
            height / PPM,
            DENSITY
        );

        body.setUserData(this);
    }

    public void takeDamage(float damageAmount) {
        health = Math.max(0, health - damageAmount);
    }

    public void heal(float healAmount) {
        health = Math.min(maxHealth, health + healAmount);
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getHealthPercentage() {
        return (health / maxHealth) * 100f;
    }

    public void update() {
        position.set(
            (body.getPosition().x * PPM) - width / 2,
            (body.getPosition().y * PPM) - height / 2
        );
    }

    // Improved impact damage method
    public void applyImpactDamage(float impactVelocity) {
        float calculatedDamage = 0;

        if (impactVelocity < MINIMUM_IMPACT_THRESHOLD) {
            // Very weak impact - minimal or no damage
            calculatedDamage = maxHealth * WEAK_IMPACT_MULTIPLIER;
        } else if (impactVelocity < MEDIUM_IMPACT_THRESHOLD) {
            // Medium impact
            calculatedDamage = maxHealth * MEDIUM_IMPACT_MULTIPLIER;
        } else {
            // Strong impact
            calculatedDamage = maxHealth * STRONG_IMPACT_MULTIPLIER;
        }

        // Take damage proportional to impact velocity
        takeDamage(calculatedDamage);

        // Optional: Add destruction logic
        if (isDestroyed()) {
            onDestroyed();
        }
    }

    // Optional method to handle destruction effects
    protected void onDestroyed() {
        // Implement destruction effects like:
        // - Particle effects
        // - Sound effects
        // - Scoring
        // - Removing from game world
    }

    public void draw(Batch batch) {
        batch.draw(
            texture,
            position.x,
            position.y,
            width / 2,
            height / 2,
            width,
            height,
            1f,
            1f,
            (float) Math.toDegrees(body.getAngle()),
            0,
            0,
            texture.getWidth(),
            texture.getHeight(),
            false,
            false
        );
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

    public boolean isIsdestroyed() {
        return isdestroyed;
    }

    public Body getBody() {
        return body;
    }

    public void dispose() {
        texture.dispose();
    }
}
