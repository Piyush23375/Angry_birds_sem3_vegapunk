package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadingScreen implements Screen {

    private Game game;
    private Stage stage;
    private Texture backgroundTexture; // Texture for the background
    private SpriteBatch batch; // SpriteBatch to draw the background

    public LoadingScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Initialize stage and SpriteBatch
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        // Load the background image
        backgroundTexture = new Texture(Gdx.files.internal("loading_screen_bg.jpg")); // Change the file name as needed

        // Simulate asset loading or transition to the first screen after a short delay
        // In real scenarios, you would load assets here, but for now, we move to FirstScreen
        Gdx.app.postRunnable(() -> game.setScreen(new FirstScreen(game)));
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background (scales to fit the screen size)
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Update and draw the stage (if needed)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update the stage's viewport when the window is resized
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        // Do not dispose resources here as the screen might be reused later
        // Dispose resources only when no longer needed, in the dispose() method
    }

    @Override
    public void dispose() {
        // Properly dispose of resources to prevent memory leaks
        backgroundTexture.dispose();
        stage.dispose();
        batch.dispose();
    }
}
