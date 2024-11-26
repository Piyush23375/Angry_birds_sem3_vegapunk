package com.angrybirds;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class BlueBird extends Bird {
    private boolean specialAbilityActivated = false;
    private static final int SPLIT_COUNT = 2; // Adjusted to 2 additional birds
    private float birdScale;

    // List to track additional birds created by special ability
    private List<Bird> additionalBirds = new ArrayList<>();

    public BlueBird(World world, float x, float y, float scale) {
        super(world, "blue_bird.png", x, y, scale);
        this.baseDensity = 1.0f;
        this.birdScale = scale;
        setDensity(baseDensity);
    }

    public Bird[] specialAbility() {
        if (!specialAbilityActivated) {
            Vector2 currentPosition = getPosition();
            Vector2 currentVelocity = getBody().getLinearVelocity();

            // Create additional birds with slight variation in position and velocity
            for (int i = 0; i < SPLIT_COUNT; i++) {
                float offsetX = (i - 0.5f) * 20f; // Spread birds horizontally
                float offsetY = (i - 0.5f) * 20f; // Spread birds vertically

                BlueBird newBird = new BlueBird(getBody().getWorld(),
                    currentPosition.x + offsetX,
                    currentPosition.y + offsetY,
                    this.birdScale);

                // Apply slightly varied velocity to each new bird
                newBird.setVelocity(
                    currentVelocity.x * (1f + (i * 0.2f)),
                    currentVelocity.y * (1f + (i * 0.2f))
                );

                additionalBirds.add(newBird);
            }

            specialAbilityActivated = true;
            return additionalBirds.toArray(new Bird[0]);
        }
        return new Bird[0];
    }

    @Override
    public void dispose() {
        // Dispose of the original bird
        super.dispose();

        // Dispose of all additional birds created by special ability
        for (Bird bird : additionalBirds) {
            bird.dispose();
        }
        additionalBirds.clear();
    }

    public boolean isSpecialAbilityActivated() {
        return specialAbilityActivated;
    }
}
