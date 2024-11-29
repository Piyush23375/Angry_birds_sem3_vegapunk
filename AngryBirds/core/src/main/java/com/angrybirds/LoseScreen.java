package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoseScreen implements Screen {
    private Game game;
    private Stage stage;
    private OrthographicCamera camera;
    private Skin skin;
    private int currentLevel;
    private Music backgroundMusic;

    // Textures for buttons and background
    private Texture restartTexture;
    private Texture restartHoverTexture;
    private Texture levelsTexture;
    private Texture levelsHoverTexture;
    private Texture backgroundTexture;

    public LoseScreen(Game game, int currentLevel) {
        this.game = game;
        this.currentLevel = currentLevel;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load background texture
        backgroundTexture = new Texture(Gdx.files.internal("losescreen.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        // Load background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("angry-birds-level-failed-1.mp3"));
        backgroundMusic.setLooping(false);
        backgroundMusic.play();

        // Load button textures
        restartTexture = new Texture(Gdx.files.internal("restart.png"));
        restartHoverTexture = new Texture(Gdx.files.internal("restart_hover.png"));
        levelsTexture = new Texture(Gdx.files.internal("go_to_levels.png"));
        levelsHoverTexture = new Texture(Gdx.files.internal("go_to_levels_hover.png"));

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

        // Create layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Add background to stage first
        stage.addActor(backgroundImage);

        // Add buttons to table
        table.add(restartButton).pad(10).size(100, 100);
        table.add(levelsButton).pad(10).size(100, 100);

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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        backgroundMusic.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        restartTexture.dispose();
        restartHoverTexture.dispose();
        levelsTexture.dispose();
        levelsHoverTexture.dispose();
        backgroundTexture.dispose();
        backgroundMusic.dispose();
    }
}
