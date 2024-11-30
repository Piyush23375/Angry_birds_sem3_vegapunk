package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.files.FileHandle;

public class MainMenuScreen implements Screen {

    private Game game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture playTexture;
    private Texture playHoverTexture;
    private Texture exitTexture;
    private Texture exitHoverTexture;
    private Music backgroundMusic;
    private boolean gamePlayed = false;

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        stage = new Stage(new ScreenViewport(camera));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("main_menu_bg.jpg"));
        playTexture = new Texture(Gdx.files.internal("Play.png"));
        playHoverTexture = new Texture(Gdx.files.internal("Play_hover.png"));
        exitTexture = new Texture(Gdx.files.internal("exit.png"));
        exitHoverTexture = new Texture(Gdx.files.internal("EXIT_hover.png"));

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("main_theme_song.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        ImageButton playButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playTexture)));
        playButton.addListener(new HoverClickListener(playButton, "Play.png", "Play_hover.png", game, new FirstScreen(game), false, false));

        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(exitTexture)));
        exitButton.addListener(new HoverClickListener(exitButton, "exit.png", "EXIT_hover.png", game, null, true, false));

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(playButton).pad(20);
        table.row();
        table.add(exitButton).pad(20);

        stage.addActor(table);
    }

    private void saveCurrentGame() {
        FileHandle file = Gdx.files.local("saved_game.txt");
        int currentLevel = 1;
        String gameState = "Level: " + currentLevel + "\n";
        file.writeString(gameState, false);
        System.out.println("Game saved: " + gameState);
    }

    private void loadSavedGame() {
        System.out.println("Load saved game logic here");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
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
    public void hide() {if (backgroundMusic != null && backgroundMusic.isPlaying()) {
        backgroundMusic.stop();
    }}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        playTexture.dispose();
        playHoverTexture.dispose();
        exitTexture.dispose();
        exitHoverTexture.dispose();

        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }

    public void playButtonClicked() {
        System.out.println("Game Played");
        gamePlayed = true;
    }


    public boolean isPlayed() {
        return gamePlayed;
    }

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
            button.getStyle().imageUp = new TextureRegionDrawable(new Texture(Gdx.files.internal(hoverTexturePath)));
            button.setTransform(true);
            button.setScale(0.95f);

            Gdx.app.postRunnable(() -> {
                if (nextScreen != null) {
                    game.setScreen(nextScreen);
                } else if (isExit) {
                    
                    Gdx.app.exit();
                } else if (isLoad) {
                    loadSavedGame();
                }
                button.getStyle().imageUp = new TextureRegionDrawable(new Texture(Gdx.files.internal(normalTexturePath)));
                button.setScale(1.0f);
            });
        }
    }
}
