package com.angrybirds;
import com.badlogic.gdx.physics.box2d.*;
public class YellowBird extends Bird {

    public YellowBird(World world, float x, float y, float scale) {
        super(world,"yellow_bird.png", x, y, scale);
    }
}
