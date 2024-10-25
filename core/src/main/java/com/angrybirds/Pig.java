package com.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Pig implements GameObject {
    private Texture texture; // Texture for the pig
    private Vector2 position; // Position of the pig

    // Width and height for the pig
    private float width;
    private float height;

    // Constructor accepting texture path and position in terms of screen size
    public Pig(String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y);
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
    }

    private Texture loadTexture(String texturePath) {
        return new Texture(Gdx.files.internal(texturePath));
    }

    @Override
    public void draw(Batch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

    @Override
    public void dispose() {
        disposeTexture();
    }

    private void disposeTexture() {
        texture.dispose();
    }
}
