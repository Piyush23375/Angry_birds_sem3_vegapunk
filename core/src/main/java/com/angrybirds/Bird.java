package com.angrybirds;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;


public class Bird {
    private Texture texture;
    private Vector2 position;
    private float scale;
    private Body body;

    private float width;
    private float height;
    public static final float PPM = 100.0f;

    public Bird(World world,String texturePath, float x, float y, float scale) {
        this.texture = loadTexture(texturePath);
        this.position = new Vector2(x, y);
        this.scale = scale;
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(texture.getWidth() * scale / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.6f; // Bounciness

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void setBodyType(BodyDef.BodyType bodyType) {
        body.setType(bodyType);
    }


    public Rectangle getBoundingBox() {
        return new Rectangle(body.getPosition().x * PPM, body.getPosition().y * PPM,
            texture.getWidth() * scale, texture.getHeight() * scale);
    }

    public void setPosition(float x, float y) {
        body.setTransform(x / PPM, y / PPM, body.getAngle());
    }


    private Texture loadTexture(String texturePath) {
        return new Texture(Gdx.files.internal(texturePath));
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, body.getPosition().x * PPM - (texture.getWidth() * scale / 2),
            body.getPosition().y * PPM - (texture.getHeight() * scale / 2),
            texture.getWidth() * scale, texture.getHeight() * scale);
    }

    public void setVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void launch(Vector2 force) {
        body.applyLinearImpulse(force, body.getWorldCenter(), true);
    }

    public void dispose() {
        texture.dispose();
    }
}
