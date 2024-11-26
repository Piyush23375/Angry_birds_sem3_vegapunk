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
    private Body body;
    private static final float PPM = 100.0f;
    private static final float DENSITY = 1.0f;

    public Structure(World world, String texturePath, float x, float y, float scale) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.position = new Vector2(x, y);
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;

    PhysicsManager physicsManager = new PhysicsManager(world);
    this.body = physicsManager.createStructureBody(
        (position.x + width / 2) / PPM,
        (position.y + height / 2) / PPM,
        width / PPM,
        height / PPM,
        DENSITY
    );

    body.setUserData(this);
}

    public void update() {
        position.set(
            (body.getPosition().x * PPM) - width / 2,
            (body.getPosition().y * PPM) - height / 2
        );
    }

    public void draw(Batch batch) {
        batch.draw(
            texture,
            position.x,
            position.y,
            width / 2,
            height / 2,
            width,
            height,
            1f,
            1f,
            (float) Math.toDegrees(body.getAngle()),
            0,
            0,
            texture.getWidth(),
            texture.getHeight(),
            false,
            false
        );
    }

    public Body getBody() {
        return body;
    }

    public void dispose() {
        texture.dispose();
    }
}
