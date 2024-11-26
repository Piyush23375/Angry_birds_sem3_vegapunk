package com.angrybirds;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
public class YellowBird extends Bird {
    private boolean specialAbilityActivated = false;
    private static final float SPEED_BOOST_MULTIPLIER = 2.0f;
    private static final float DENSITY_BOOST_MULTIPLIER = 1.5f;

    public YellowBird(World world, float x, float y, float scale) {

        super(world,"yellow_bird.png", x, y, scale);
        this.baseDensity = 0.7f;
        // Immediately update the body's mass with new density
        setDensity(baseDensity);
    }

    public void specialAbility() {
        if (!specialAbilityActivated) {
            // Increase speed
            Vector2 currentVelocity = getBody().getLinearVelocity();
            getBody().setLinearVelocity(
                currentVelocity.x * SPEED_BOOST_MULTIPLIER,
                currentVelocity.y * SPEED_BOOST_MULTIPLIER
            );

            // Increase density
            float newDensity = baseDensity * DENSITY_BOOST_MULTIPLIER;
            setDensity(newDensity);

            specialAbilityActivated = true;
        }
    }

    public boolean isSpecialAbilityActivated() {
        return specialAbilityActivated;
    }
}
