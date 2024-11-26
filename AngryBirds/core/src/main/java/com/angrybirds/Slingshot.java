package com.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.box2d.*;
import java.util.Queue;
import java.util.LinkedList;

public class Slingshot {
    private Texture texture;
    private Vector2 position;
    private float scale;
    private float width;
    private float height;
    private Queue<Bird> birdQueue;
    private Bird currentBird;
    private Bird draggedBird;
    private final float SLINGSHOT_PULL_LIMIT = 50f;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Vector2 dragStart;
    private Vector2 dragCurrent;
    private boolean isDragging = false;
    private final Vector2 BIRD_READY_POSITION = new Vector2(320, 580);
    private final float LAUNCH_POWER_MULTIPLIER = 0.25f;
    private final float MAX_LAUNCH_SPEED = 14f;
    private int currentBirdIndex = 0;

    public Slingshot(World world, String texturePath, float x, float y, float scale, Queue<Bird> birds, OrthographicCamera camera) {
        this.birdQueue = new LinkedList<>(birds);
        this.camera = camera;
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.position = new Vector2(x, y);
        this.scale = scale;
        this.width = texture.getWidth() * scale;
        this.height = texture.getHeight() * scale;
        this.shapeRenderer = new ShapeRenderer();

        // Set up the first bird
        loadNextBird();
    }

    private void loadNextBird() {
        if (!birdQueue.isEmpty()) {
            currentBird = birdQueue.poll();
            currentBird.setPosition(BIRD_READY_POSITION.x, BIRD_READY_POSITION.y);
            currentBird.setBodyType(BodyDef.BodyType.StaticBody);
        } else {
            currentBird = null;
        }
    }

    public int getCurrentBirdIndex() {
        return currentBirdIndex;
    }

    public void setCurrentBirdIndex(int index) {
        if (index >= 0 && index < birdQueue.size()) {
            currentBirdIndex = index;
        }
    }

    public void draw(SpriteBatch batch, float xOffset, float yOffset) {
        // End SpriteBatch to draw shapes
        batch.end();

        // Draw the drag line if dragging
        if (isDragging && dragStart != null && dragCurrent != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            // Draw the trajectory line
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.line(dragStart.x, dragStart.y, dragCurrent.x, dragCurrent.y);

            // Draw power indicator (perpendicular lines, longer = more power)
            Vector2 dragVector = new Vector2(dragCurrent.x - dragStart.x, dragCurrent.y - dragStart.y);
            float power = dragVector.len() / SLINGSHOT_PULL_LIMIT;
            Vector2 perpendicular = new Vector2(-dragVector.y, dragVector.x).nor();
            float lineLength = 20 * power;

            Vector2 midPoint = new Vector2(
                (dragStart.x + dragCurrent.x) / 2,
                (dragStart.y + dragCurrent.y) / 2
            );

            shapeRenderer.line(
                midPoint.x - perpendicular.x * lineLength,
                midPoint.y - perpendicular.y * lineLength,
                midPoint.x + perpendicular.x * lineLength,
                midPoint.y + perpendicular.y * lineLength
            );

            shapeRenderer.end();
        }

        // Resume SpriteBatch for texture drawing
        batch.begin();

        // Draw the slingshot
        batch.draw(texture, position.x + xOffset, position.y + yOffset, width, height);
    }

    public void update() {
        // Check if we need to load the next bird
        if (currentBird == null ||
            (currentBird.getBody().getType() == BodyDef.BodyType.DynamicBody &&
                !isDragging)) {
            loadNextBird();
        }
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public InputAdapter getInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                Vector2 touchPoint = new Vector2(worldCoords.x, worldCoords.y);

                // Define a dragging area around the slingshot
                Rectangle dragArea = new Rectangle(
                    position.x - width,
                    position.y - height,
                    width * 2,
                    height * 2
                );

                // Only allow interaction with the current bird if it's within the slingshot's drag area
                if (currentBird != null &&
                    dragArea.contains(touchPoint.x, touchPoint.y) &&
                    currentBird.getBoundingBox().contains(touchPoint.x, touchPoint.y)) {
                    draggedBird = currentBird;
                    draggedBird.setBodyType(BodyDef.BodyType.KinematicBody);
                    dragStart = new Vector2(position.x + width/2, position.y + height/2);
                    dragCurrent = touchPoint;
                    isDragging = true;
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isDragging && draggedBird != null) {
                    Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                    dragCurrent = new Vector2(worldCoords.x, worldCoords.y);

                    // Calculate pull vector from slingshot center
                    Vector2 pullVector = dragCurrent.cpy().sub(dragStart);

                    // Limit pull distance
                    if (pullVector.len() > SLINGSHOT_PULL_LIMIT) {
                        pullVector.setLength(SLINGSHOT_PULL_LIMIT);
                        dragCurrent = dragStart.cpy().add(pullVector);
                    }

                    // Update bird position
                    draggedBird.setPosition(dragCurrent.x, dragCurrent.y);
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (isDragging && draggedBird != null) {
                    // Calculate launch vector (from current position to slingshot center)
                    Vector2 launchVector = dragStart.cpy().sub(dragCurrent);

                    // Apply power multiplier and clamp to max speed
                    launchVector.scl(LAUNCH_POWER_MULTIPLIER);
                    if (launchVector.len() > MAX_LAUNCH_SPEED) {
                        launchVector.setLength(MAX_LAUNCH_SPEED);
                    }

                    // Launch the bird
                    draggedBird.setBodyType(BodyDef.BodyType.DynamicBody);
                    draggedBird.setVelocity(launchVector.x, launchVector.y);

                    // Reset drag state
                    draggedBird = null;
                    isDragging = false;
                    dragStart = null;
                    dragCurrent = null;

                    return true;
                }
                return false;
            }
        };
    }

    public void dispose() {
        texture.dispose();
        shapeRenderer.dispose();
    }

    public boolean hasMoreBirds() {
        return !birdQueue.isEmpty() || currentBird != null;
    }

    public Bird getCurrentBird() {
        return currentBird;
    }
}
