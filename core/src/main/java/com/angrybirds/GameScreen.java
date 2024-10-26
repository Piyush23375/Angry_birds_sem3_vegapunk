package com.angrybirds;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameScreen implements Screen {

    private Game game;
    private int level;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private Slingshot slingshot;
    private GameObject[] structures; // Ensure this is the only declaration of structures
    private Bird[] birds; // Array to hold bird objects

    private OrthographicCamera camera; // Declare the camera

    // Scale factor for slingshot and birds
    private static final float SLINGSHOT_SCALE = 0.05f; // Proportion of screen width
    private static final float BIRD_SCALE = 0.1f; // Proportion of screen width
    private static final float STRUCTURE_SCALE = 1f;
    private static final float YELLOW_BIRD_SCALE = 0.28f;

    // Pause button textures
    private Texture pauseButtonTexture;
    private Texture pauseButtonHoverTexture;
    private Texture pauseButtonPressedTexture;

    public GameScreen(Game game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480); // Set the camera to the default screen size (640x480)

        // Initialize stage and SpriteBatch
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        // Load the skin and background image
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg.png"));

        // Initialize structures and slingshot
        createStructuresForLevel();
        slingshot = new Slingshot("slingshot.png", 25 * SLINGSHOT_SCALE, 50 * SLINGSHOT_SCALE, SLINGSHOT_SCALE);

        // Initialize the birds
        createBirdsForLevel();

        // Load pause button textures
        pauseButtonTexture = new Texture(Gdx.files.internal("pause.png"));
        pauseButtonHoverTexture = new Texture(Gdx.files.internal("pause_hover.png"));
        pauseButtonPressedTexture = new Texture(Gdx.files.internal("pause_pressed.png"));

        // Create drawables for the button states
        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(pauseButtonTexture);
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(pauseButtonHoverTexture);
        TextureRegionDrawable pressedDrawable = new TextureRegionDrawable(pauseButtonPressedTexture);

        // Create the ImageButton for pause
        ImageButton pauseButton = new ImageButton(normalDrawable, hoverDrawable, pressedDrawable);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new PauseScreen(game, level, GameScreen.this)); // Open pause screen
            }
        });

        // Set size for the pause button
        pauseButton.setSize(120, 120); // Adjust the size if needed

        // Create a table for the pause button
        Table pauseTable = new Table();
        pauseTable.setFillParent(true); // Make the table fill the screen space
        pauseTable.bottom().right(); // Position the pause button in the bottom-right corner

        // Add the pause button to the table
        pauseTable.add(pauseButton).pad(10).size(120, 120);

        // Add the pause button table to the stage
        stage.addActor(pauseTable);
    }

    // Create structures dynamically for each level, resizable according to screen size
    private void createStructuresForLevel() {
        if (level == 1) {
            structures = new GameObject[]{
                new Structure("wood_rod.png", 1000, 100, STRUCTURE_SCALE),
                new MediumPig(970, 270, STRUCTURE_SCALE) // Now valid
            };
        } else if (level == 2) {
            structures = new GameObject[]{
                new Structure("glass_rod.png", 1000, 100, STRUCTURE_SCALE),
                new Structure("glass_rod.png", 1100, 100, STRUCTURE_SCALE),
                new MediumPig(970, 270, STRUCTURE_SCALE), // Now valid
                new Structure("wood_block.png", 1100, 250, STRUCTURE_SCALE)
            };
        } else if (level == 3) {
            structures = new GameObject[]{
                new Structure("glass_rod.png", 1000, 50, STRUCTURE_SCALE),
                new MediumPig(970, 200, STRUCTURE_SCALE) // Now valid
            };
        }
    }

    // Create bird objects for the current level
    private void createBirdsForLevel() {
        if (level == 1) {
            birds = new Bird[]{
                new RedBird(10, camera.viewportHeight + 475, BIRD_SCALE), // Position at the top
                new BlueBird(10, camera.viewportHeight + 350, BIRD_SCALE) // Position just below the first bird
            };
        } else if (level == 2) {
            birds = new Bird[]{
                new YellowBird(10, camera.viewportHeight + 475, YELLOW_BIRD_SCALE), // Position at the top
                new BlueBird(10, camera.viewportHeight + 350, BIRD_SCALE) // Position just below the first bird
            };
        } else if (level == 3) {
            birds = new Bird[]{
                new RedBird(10, camera.viewportHeight + 475, BIRD_SCALE), // Position at the top
                new YellowBird(10, camera.viewportHeight + 350, YELLOW_BIRD_SCALE), // Position just below the first bird
                new BlueBird(10, camera.viewportHeight + 225, BIRD_SCALE) // Position lower
            };
        } else {
            birds = new Bird[]{
                new RedBird(10, camera.viewportHeight + 450, BIRD_SCALE) // Position at the top
            };
        }
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera and set projection matrix for batch
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Draw the background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

        // Draw the slingshot
        float slingshotWidth = camera.viewportWidth * SLINGSHOT_SCALE;
        float slingshotHeight = slingshotWidth * 2;
        float slingshotXOffset = 20;
        float slingshotYOffset = 10;
        slingshot.draw(batch, slingshotXOffset, slingshotYOffset, slingshotWidth, slingshotHeight);

        // Draw the bird objects
        for (Bird bird : birds) {
            bird.draw(batch);
        }

        // Draw the structures
        renderStructures(batch); // Call the method to draw structures

        batch.end();

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    private void renderStructures(SpriteBatch batch) { // Change Batch to SpriteBatch
        for (GameObject gameObject : structures) {
            gameObject.draw(batch);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        slingshot.dispose();
        for (Bird bird : birds) {
            bird.dispose();
        }
        for (GameObject structure : structures) {
            structure.dispose(); // Corrected to use GameObject instead of Structure
        }
    }
}
