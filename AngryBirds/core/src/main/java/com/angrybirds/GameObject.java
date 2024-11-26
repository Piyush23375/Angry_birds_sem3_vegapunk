package com.angrybirds;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.physics.box2d.*;


public interface GameObject {
    void draw(Batch batch);
    void dispose();


}
