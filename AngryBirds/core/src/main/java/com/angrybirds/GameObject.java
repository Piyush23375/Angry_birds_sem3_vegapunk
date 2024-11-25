package com.angrybirds;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface GameObject {
    void draw(Batch batch);
    void dispose();
}
