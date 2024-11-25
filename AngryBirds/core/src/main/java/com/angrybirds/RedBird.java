package com.angrybirds;

import com.badlogic.gdx.physics.box2d.*;

public class RedBird extends Bird {

    public RedBird( World world, float x, float y, float scale) {
        super(world,"red_bird.png", x, y, scale);
    }
}
