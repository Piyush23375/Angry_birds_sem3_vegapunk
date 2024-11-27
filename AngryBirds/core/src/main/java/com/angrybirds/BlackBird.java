package com.angrybirds;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class BlackBird extends Bird {
    private boolean specialAbilityActivated = false;
    private boolean isShaking = false;
    private float shakeDuration = 0.5f;  // Total shake duration
    private float currentShakeDuration = 0f;
    private float shakeIntensity = 10f;  // Maximum shake offset

    public BlackBird(World world, float x, float y, float scale) {
        super(world, "black_bird.png", x, y, scale);
    }

    public void specialAbility() {
        // Explosion effect or shake activation
        isShaking = true;
        currentShakeDuration = shakeDuration;
    }

    public void updateShake(float deltaTime) {
        if (isShaking) {
            currentShakeDuration -= deltaTime;

            // Stop shaking when duration is over
            if (currentShakeDuration <= 0) {
                isShaking = false;
            }
        }
    }

    public boolean isShaking() {
        return isShaking;
    }

    public float getShakeOffset() {
        if (!isShaking) return 0;

        // Random shake offset between -shakeIntensity and +shakeIntensity
        return MathUtils.random(-shakeIntensity, shakeIntensity);
    }

    // Override dispose method to clean up any additional resources
    @Override
    public void dispose() {
        super.dispose();
        isShaking = false;
        currentShakeDuration = 0;
    }

    public boolean isSpecialAbilityActivated() {
        return specialAbilityActivated;
    }
}
