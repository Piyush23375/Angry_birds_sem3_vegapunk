package com.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bird {
    private Texture texture; // Bird texture
    private Vector2 position; // Position of the bird
    private float scale; // Scale factor for the bird size

    // Width and height for the bird
    private float width;
    private float height;

    // Constructor accepting texture path, position, and scale
    public Bird(String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y); // Use Vector2 for position
        this.scale = scale; // Set scale for size adjustment
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
    }

    private Texture loadTexture(String texturePath) {
        // Load the texture from the specified path
        return new Texture(Gdx.files.internal(texturePath));
    }

    // Method to draw the bird at its current position
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

    // Method to dispose of the texture
    public void dispose() {
        texture.dispose();
    }

    // Optionally, methods to move the bird or interact with the game can be added here
}
