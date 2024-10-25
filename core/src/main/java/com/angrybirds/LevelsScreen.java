package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class LevelsScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private Game game;
    private Texture backgroundTexture; // Texture for the background
    private SpriteBatch batch; // SpriteBatch to draw the background
    private OrthographicCamera camera; // Orthographic camera

    public LevelsScreen(Game game) {
        this.game = game;

        // Initialize the orthographic camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set camera to match screen size
    }

    @Override
    public void show() {
        // Initialize stage and SpriteBatch
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        // Load the skin and background image
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("levels_screen_bg.png")); // Adjust the image file as needed

        // Create buttons for each level using images with specified sizes
        ImageButton level1Button = createLevelButton("level1.png", "level1_hover.png", 1, 100, 100); // Smaller size
        ImageButton level2Button = createLevelButton("level2.png", "level2_hover.png", 2, 100, 100); // Smaller size
        ImageButton level3Button = createLevelButton("level3.png", "level3_hover.png", 3, 100, 100); // Smaller size

        // Create the back button
        ImageButton backButton = createBackButton(100, 100); // Smaller size for the back button

        // Layout buttons in a table for levels
        Table levelTable = new Table();
        levelTable.setFillParent(true);

        // Center the table and add level buttons with padding
        levelTable.center(); // Center the table within the stage
        levelTable.add(level1Button).pad(10).size(100, 100); // Set size and padding for level 1
        levelTable.row();
        levelTable.add(level2Button).pad(10).size(100, 100); // Set size and padding for level 2
        levelTable.row();
        levelTable.add(level3Button).pad(10).size(100, 100); // Set size and padding for level 3

        // Add the level table to the stage
        stage.addActor(levelTable);

        // Layout the back button in a separate table
        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.bottom().left(); // Position the back button table to the bottom-left corner
        backTable.add(backButton).pad(10).size(100, 100); // Add back button with padding

        // Add the back button table to the stage
        stage.addActor(backTable);
    }

    private ImageButton createLevelButton(String normalImagePath, String hoverImagePath, int levelNumber, float width, float height) {
        // Load normal and hover textures
        Texture normalTexture = new Texture(Gdx.files.internal(normalImagePath));
        Texture hoverTexture = new Texture(Gdx.files.internal(hoverImagePath));

        // Create drawable for normal and hover states
        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(normalTexture);
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(hoverTexture);

        // Create ImageButton with normal and hover drawables
        ImageButton levelButton = new ImageButton(normalDrawable, hoverDrawable);
        levelButton.setSize(width, height); // Set the size for the ImageButton

        levelButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, levelNumber)); // Load selected level
            }
        });

        return levelButton;
    }

    private ImageButton createBackButton(float width, float height) {
        // Load normal and hover textures for back button
        Texture backNormalTexture = new Texture(Gdx.files.internal("back.png")); // Normal image for back button
        Texture backHoverTexture = new Texture(Gdx.files.internal("back_hover.png")); // Hover image for back button

        // Create drawable for normal and hover states
        TextureRegionDrawable backNormalDrawable = new TextureRegionDrawable(backNormalTexture);
        TextureRegionDrawable backHoverDrawable = new TextureRegionDrawable(backHoverTexture);

        // Create ImageButton for the back button
        ImageButton backButton = new ImageButton(backNormalDrawable, backHoverDrawable);
        backButton.setSize(width, height); // Set size for the back button

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new FirstScreen(game)); // Return to first screen
            }
        });

        return backButton;
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera
        camera.update();

        // Use the camera for drawing the background and stage
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Scale to screen size
        batch.end();

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the stage viewport and camera when the window is resized
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height); // Adjust camera to new size
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // Do not dispose resources here as the screen may be reused
    }

    @Override
    public void dispose() {
        // Properly dispose of resources to prevent memory leaks
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}

