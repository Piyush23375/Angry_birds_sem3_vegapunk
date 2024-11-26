package com.angrybirds;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class RedBird extends Bird {

    public RedBird( World world, float x, float y, float scale) {
        super(world,"red_bird.png", x, y, scale);
        this.baseDensity = 1.5f;
        setDensity(baseDensity);
    }
}
