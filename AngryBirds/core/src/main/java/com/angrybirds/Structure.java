package com.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Structure implements GameObject {
    private Texture texture;
    private Vector2 position;
    private float width;
    private float height;
    private Body body; // Box2D Body for physics
    private static final float PPM = 100.0f;

    public Structure(World world, String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y);
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;

        // Create the physics body for the structure
        createBody(world);
    }

    private Texture loadTexture(String texturePath) {
        return new Texture(Gdx.files.internal(texturePath));
    }

    private void createBody(World world) {
        // Define the body type and position
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Structures are static
        bodyDef.position.set((position.x + width / 2) / PPM, (position.y + height / 2) / PPM);

        // Create the body in the world
        body = world.createBody(bodyDef);

        // Define the shape of the fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM); // Half-width and half-height in meters

        // Define the fixture properties
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // Adjust for desired weight
        fixtureDef.friction = 0.5f; // Adjust for desired friction
        fixtureDef.restitution = 0.2f; // Adjust for desired bounciness

        // Attach the shape to the body
        body.createFixture(fixtureDef);

        // Dispose of the shape once it is no longer needed
        shape.dispose();
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

    public void update() {
        // Synchronize the texture position with the Box2D body's position
        position.set(
            (body.getPosition().x * PPM) - width / 2,
            (body.getPosition().y * PPM) - height / 2
        );
    }

    // Constants
     // Pixels per meter conversion factor
}
