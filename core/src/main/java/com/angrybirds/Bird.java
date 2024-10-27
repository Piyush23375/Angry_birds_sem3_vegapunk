//testing auto update to inteliji
package com.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bird {
    private Texture texture;
    private Vector2 position;
    private float scale;

    private float width;
    private float height;

    public Bird(String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y);
        this.scale = scale;
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
    }

    private Texture loadTexture(String texturePath) {
        return new Texture(Gdx.files.internal(texturePath));
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

    public void dispose() {
        texture.dispose();
    }
}
