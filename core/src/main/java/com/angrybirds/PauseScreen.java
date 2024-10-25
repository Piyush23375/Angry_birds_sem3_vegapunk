package com.angrybirds;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class PauseScreen implements Screen {

    private Game game;
    private int level;
    private Stage stage;
    private Skin skin;
    private GameScreen gameScreen;
    private Texture backgroundTexture; // Texture for the background
    private SpriteBatch batch; // SpriteBatch to draw the background
    private OrthographicCamera camera; // Camera for handling screen resizing

    // Sample score and stars variables (replace with actual game data)
    private int score = 150; // Example score
    private int stars = 3; // Example number of stars

    public PauseScreen(Game game, int level, GameScreen gameScreen) {
        this.game = game;
        this.level = level;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480); // Set initial camera dimensions (adjust as necessary)

        // Initialize stage and SpriteBatch
        stage = new Stage(new ScreenViewport(camera));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        // Load the skin and background image
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("pause_screen_bg.jpg")); // Change the file name as needed

        // Restart Button
        Texture restartButtonTexture = new Texture(Gdx.files.internal("restart.png"));
        Texture restartButtonHoverTexture = new Texture(Gdx.files.internal("restart_hover.png"));
        Texture restartButtonPressedTexture = new Texture(Gdx.files.internal("restart_pressed.png"));

        TextureRegionDrawable restartNormalDrawable = new TextureRegionDrawable(restartButtonTexture);
        TextureRegionDrawable restartHoverDrawable = new TextureRegionDrawable(restartButtonHoverTexture);
        TextureRegionDrawable restartPressedDrawable = new TextureRegionDrawable(restartButtonPressedTexture);

        ImageButton restartButton = new ImageButton(restartNormalDrawable, restartHoverDrawable, restartPressedDrawable);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, level)); // Restart current level
            }
        });

        // Return to Levels Button
        Texture levelsButtonTexture = new Texture(Gdx.files.internal("go_to_levels.png"));
        Texture levelsButtonHoverTexture = new Texture(Gdx.files.internal("go_to_levels_hover.png"));
        Texture levelsButtonPressedTexture = new Texture(Gdx.files.internal("go_to_levels_pressed.png"));

        TextureRegionDrawable levelsNormalDrawable = new TextureRegionDrawable(levelsButtonTexture);
        TextureRegionDrawable levelsHoverDrawable = new TextureRegionDrawable(levelsButtonHoverTexture);
        TextureRegionDrawable levelsPressedDrawable = new TextureRegionDrawable(levelsButtonPressedTexture);

        ImageButton levelsButton = new ImageButton(levelsNormalDrawable, levelsHoverDrawable, levelsPressedDrawable);
        levelsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new LevelsScreen(game)); // Return to levels screen
            }
        });

        // Resume Button
        Texture resumeButtonTexture = new Texture(Gdx.files.internal("resume.png"));
        Texture resumeButtonHoverTexture = new Texture(Gdx.files.internal("resume_hover.png"));
        Texture resumeButtonPressedTexture = new Texture(Gdx.files.internal("resume_pressed.png"));

        TextureRegionDrawable resumeNormalDrawable = new TextureRegionDrawable(resumeButtonTexture);
        TextureRegionDrawable resumeHoverDrawable = new TextureRegionDrawable(resumeButtonHoverTexture);
        TextureRegionDrawable resumePressedDrawable = new TextureRegionDrawable(resumeButtonPressedTexture);

        ImageButton resumeButton = new ImageButton(resumeNormalDrawable, resumeHoverDrawable, resumePressedDrawable);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(gameScreen); // Resume current game
            }
        });

        // Score Button
        Texture scoreButtonTexture = new Texture(Gdx.files.internal("score.png"));
        Texture scoreButtonHoverTexture = new Texture(Gdx.files.internal("score_hover.png"));
        Texture scoreButtonPressedTexture = new Texture(Gdx.files.internal("score.png"));

        TextureRegionDrawable scoreNormalDrawable = new TextureRegionDrawable(scoreButtonTexture);
        TextureRegionDrawable scoreHoverDrawable = new TextureRegionDrawable(scoreButtonHoverTexture);
        TextureRegionDrawable scorePressedDrawable = new TextureRegionDrawable(scoreButtonPressedTexture);

        ImageButton scoreButton = new ImageButton(scoreNormalDrawable, scoreHoverDrawable, scorePressedDrawable);
        scoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new ScoreScreen(game, score, stars)); // Navigate to ScoreScreen
            }
        });

        // Layout buttons in a table aligned to the left
        Table table = new Table();
        table.setFillParent(true);
        table.left(); // Align the table to the left and top
        table.add(restartButton).pad(10).size(100, 100); // Add Restart button
        table.row();
        table.add(levelsButton).pad(10).size(100, 100); // Add Return to Levels button
        table.row();
        table.add(resumeButton).pad(10).size(100, 100); // Add Resume button
        table.row();
        table.add(scoreButton).pad(10).size(100, 100); // Add Score button

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera
        camera.update();
        batch.setProjectionMatrix(camera.combined); // Make sure the SpriteBatch uses the camera's projection

        // Draw the background (scales based on screen size)
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport and camera to accommodate new width and height
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height); // Adjust camera to the new window size
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Properly dispose of resources to prevent memory leaks
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}
