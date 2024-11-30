package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.audio.Music;

public class WinScreen implements Screen {
    private Game game;
    private Stage stage;
    private OrthographicCamera camera;
    private Skin skin;
    private int currentLevel;

    // Background texture
    private Texture backgroundTexture;

    // Textures for buttons
    private Texture restartTexture;
    private Texture restartHoverTexture;
    private Texture levelsTexture;
    private Texture levelsHoverTexture;
    private Texture nextLevelTexture;
    private Texture nextLevelHoverTexture;

    // Rendering
    private SpriteBatch batch;
    private BitmapFont font;

    // Music
    private Music backgroundMusic;

    public WinScreen(Game game, int currentLevel) {
        this.game = game;
        this.currentLevel = currentLevel;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        batch = new SpriteBatch();
        font = new BitmapFont();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Load background texture
        backgroundTexture = new Texture(Gdx.files.internal("winscreen.png"));

        // Load button textures
        restartTexture = new Texture(Gdx.files.internal("restart.png"));
        restartHoverTexture = new Texture(Gdx.files.internal("restart_hover.png"));
        levelsTexture = new Texture(Gdx.files.internal("go_to_levels.png"));
        levelsHoverTexture = new Texture(Gdx.files.internal("go_to_levels_hover.png"));
        nextLevelTexture = new Texture(Gdx.files.internal("next_level_button.png"));
        nextLevelHoverTexture = new Texture(Gdx.files.internal("next_level_button_hover.png"));

        // Load and play background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("angry-birds-plush-yeah-sfx (1).mp3"));
        backgroundMusic.setLooping(false);
        backgroundMusic.play();

        // Create buttons
        ImageButton restartButton = createImageButton(restartTexture, restartHoverTexture, () -> {
            // Restart current level
            backgroundMusic.stop();
            game.setScreen(new GameScreen(game, currentLevel));
            dispose();
        });

        ImageButton levelsButton = createImageButton(levelsTexture, levelsHoverTexture, () -> {
            // Return to levels screen
            backgroundMusic.stop();
            game.setScreen(new LevelsScreen(game));
            dispose();
        });

        ImageButton nextLevelButton = createImageButton(nextLevelTexture, nextLevelHoverTexture, () -> {
            // Save the next level to saved_game.txt and go to the next level
            saveNextLevel(currentLevel + 1);
            backgroundMusic.stop();
            game.setScreen(new GameScreen(game, currentLevel + 1));
            dispose();
        });

        // Create layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Add buttons to table
        table.add(restartButton).pad(10).size(100, 100);
        table.add(levelsButton).pad(10).size(100, 100);
        table.add(nextLevelButton).pad(10).size(100, 100);

        stage.addActor(table);
    }

    private ImageButton createImageButton(Texture normalTexture, Texture hoverTexture, Runnable action) {
        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(normalTexture);
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(hoverTexture);

        ImageButton button = new ImageButton(normalDrawable, hoverDrawable);
        button.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                action.run();
            }
        });

        return button;
    }

    // Save the next level to saved_game.txt
    private void saveNextLevel(int nextLevel) {
        FileHandle file = Gdx.files.local("saved_game.txt");
        String gameState = "Level: " + nextLevel + "\n";
        file.writeString(gameState, false); // Overwrites the file
        System.out.println("Game saved: " + gameState);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // Render background
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, 640, 480);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        backgroundMusic.pause();
    }

    @Override
    public void resume() {
        backgroundMusic.play();
    }

    @Override
    public void hide() {
        backgroundMusic.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();

        // Dispose of background texture
        backgroundTexture.dispose();

        restartTexture.dispose();
        restartHoverTexture.dispose();
        levelsTexture.dispose();
        levelsHoverTexture.dispose();
        nextLevelTexture.dispose();
        nextLevelHoverTexture.dispose();

        // Dispose of music
        backgroundMusic.dispose();
    }
}
