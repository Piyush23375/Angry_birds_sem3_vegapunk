package com.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Slingshot {
    private Texture texture; // Slingshot texture
    private Vector2 position; // Position of the slingshot

    // Scale factor for slingshot size
    private float scale;

    // Width and height for the slingshot
    private float width;
    private float height;

    // Constructor accepting texture path, position, and scale
    public Slingshot(String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y); // Use Vector2 for position
        this.scale = scale; // Set scale for size reduction
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
    }

    private Texture loadTexture(String texturePath) {
        // Load the texture from the specified path
        return new Texture(Gdx.files.internal(texturePath));
    }

    // Modified draw method to accept x and y offsets, as well as width and height
    public void draw(SpriteBatch batch, float xOffset, float yOffset, float width, float height) {
        // Draw the slingshot at its current position with the given width and height
        batch.draw(texture, position.x + xOffset, position.y + yOffset, width, height);
    }

    // Method to resize the slingshot based on the given width and height
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void dispose() {
        // Dispose of the texture to prevent memory leaks
        disposeTexture();
    }

    private void disposeTexture() {
        texture.dispose();
    }
}
