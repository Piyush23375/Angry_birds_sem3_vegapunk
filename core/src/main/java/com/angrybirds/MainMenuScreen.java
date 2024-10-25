package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen implements Screen {

    private Game game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    // Load button textures
    private Texture playTexture;
    private Texture playHoverTexture;
    private Texture exitTexture;
    private Texture exitHoverTexture;

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Initialize camera and set its viewport dimensions
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        // Initialize the stage with a camera-based viewport
        stage = new Stage(new ScreenViewport(camera));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        // Load the UI skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Load the background texture
        backgroundTexture = new Texture(Gdx.files.internal("main_menu_bg.jpg"));

        // Load button textures (normal and hover)
        playTexture = new Texture(Gdx.files.internal("Play.png")); // Normal Play button
        playHoverTexture = new Texture(Gdx.files.internal("Play_hover.png")); // Hover Play button

        exitTexture = new Texture(Gdx.files.internal("exit.png")); // Normal Exit button
        exitHoverTexture = new Texture(Gdx.files.internal("EXIT_hover.png")); // Hover Exit button

        // Create the Play button
        ImageButton playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playTexture)));
        playButton.addListener(new HoverClickListener(playButton, "Play.png", "Play_hover.png", game, new FirstScreen(game), false, false));

        // Create the Exit button
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(exitTexture)));
        exitButton.addListener(new HoverClickListener(exitButton, "exit.png", "EXIT_hover.png", game, null, true, false));

        // Layout the buttons using a table
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(playButton).pad(20);
        table.row();
        table.add(exitButton).pad(20);

        // Add the table to the stage
        stage.addActor(table);
    }

    private void saveCurrentGame() {
        // Save the current game state to a file
        FileHandle file = Gdx.files.local("saved_game.txt");

        // For demonstration, let's save a dummy current level (you can replace this with your actual logic)
        int currentLevel = 1; // Replace with your logic to get the current level
        String gameState = "Level: " + currentLevel + "\n"; // Add more game state information as needed

        // Write the game state to the file
        file.writeString(gameState, false); // Overwrite the existing file
        System.out.println("Game saved: " + gameState); // Debug output
    }

    private void loadSavedGame() {
        // Implement your load game logic here
        System.out.println("Load saved game logic here");
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera
        camera.update();

        // Set the camera's projection matrix for the batch
        batch.setProjectionMatrix(camera.combined);

        // Draw the background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Update and draw the stage (UI components)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the stage's viewport when the window size changes
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // No need to dispose resources in hide method
    }

    @Override
    public void dispose() {
        // Properly dispose of resources
        stage.dispose();
        skin.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        playTexture.dispose();
        playHoverTexture.dispose();
        exitTexture.dispose();
        exitHoverTexture.dispose();
    }

    // Inner class for hover and click listener
    private class HoverClickListener extends ClickListener {
        private final ImageButton button;
        private final String normalTexturePath;
        private final String hoverTexturePath;
        private final Game game;
        private final Screen nextScreen;
        private final boolean isExit;
        private final boolean isLoad;

        public HoverClickListener(ImageButton button, String normalTexturePath, String hoverTexturePath, Game game, Screen nextScreen, boolean isExit, boolean isLoad) {
            this.button = button;
            this.normalTexturePath = normalTexturePath;
            this.hoverTexturePath = hoverTexturePath;
            this.game = game;
            this.nextScreen = nextScreen;
            this.isExit = isExit;
            this.isLoad = isLoad;
        }

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            button.getStyle().imageUp = new TextureRegionDrawable(new Texture(Gdx.files.internal(hoverTexturePath)));
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            button.getStyle().imageUp = new TextureRegionDrawable(new Texture(Gdx.files.internal(normalTexturePath)));
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            // Change button style to hover when clicked
            button.getStyle().imageUp = new TextureRegionDrawable(new Texture(Gdx.files.internal(hoverTexturePath)));
            button.setTransform(true); // Enable scaling and transformation
            button.setScale(0.95f); // Scale the button slightly to simulate a pressed effect

            // Delay the action to avoid immediate transition
            Gdx.app.postRunnable(() -> {
                if (nextScreen != null) {
                    game.setScreen(nextScreen);
                } else if (isExit) {
                    saveCurrentGame();
                    Gdx.app.exit();
                } else if (isLoad) {
                    loadSavedGame();
                }
                // Reset to normal texture after action
                button.getStyle().imageUp = new TextureRegionDrawable(new Texture(Gdx.files.internal(normalTexturePath)));
                button.setScale(1.0f); // Reset scale
            });
        }
    }
}
