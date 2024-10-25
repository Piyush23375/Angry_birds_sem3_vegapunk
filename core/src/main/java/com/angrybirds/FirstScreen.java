package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.files.FileHandle;

import java.util.StringTokenizer;

public class FirstScreen implements Screen {

    private Stage stage;
    private Game game;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture loadSavedTexture, exitTexture;
    private Texture loadSavedHoverTexture, exitHoverTexture, levelsHoverTexture;

    public FirstScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        stage = new Stage(new ScreenViewport(camera));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("first_screen_bg.jpg"));
        loadSavedTexture = new Texture(Gdx.files.internal("LOAD-SAVED.png"));
        loadSavedHoverTexture = new Texture(Gdx.files.internal("LOAD-SAVED_hover.png"));
        exitTexture = new Texture(Gdx.files.internal("exit.png"));
        exitHoverTexture = new Texture(Gdx.files.internal("exit_hover.png"));
        levelsHoverTexture = new Texture(Gdx.files.internal("levels_hover.png"));

        ImageButton levelsButton = new ImageButton(new TextureRegionDrawable(new Texture(Gdx.files.internal("levels.png"))));
        levelsButton.addListener(new HoverClickListener(levelsButton, "levels.png", "levels_hover.png", game, new LevelsScreen(game), false, false));

        ImageButton loadButton = new ImageButton(new TextureRegionDrawable(loadSavedTexture));
        loadButton.addListener(new HoverClickListener(loadButton, "LOAD-SAVED.png", "LOAD-SAVED_hover.png", game, null, false, true));

        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(exitTexture));
        exitButton.addListener(new HoverClickListener(exitButton, "exit.png", "exit_hover.png", game, null, true, false));

        Table table = new Table();
        table.setFillParent(true);
        table.add(levelsButton).pad(10);
        table.row();
        table.add(loadButton).padRight(150).padBottom(10);
        table.row();
        table.add(exitButton).pad(10);

        stage.addActor(table);
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
                    saveCurrentGame();
                    Gdx.app.exit();
                } else if (isLoad) {
                    loadSavedGame();
                }
                button.getStyle().imageUp = new TextureRegionDrawable(new Texture(Gdx.files.internal(normalTexturePath)));
                button.setScale(1.0f);
            });
        }
    }

    private void saveCurrentGame() {
        FileHandle file = Gdx.files.local("saved_game.txt");
        int currentLevel = 1;
        String gameState = "Level: " + currentLevel + "\n";
        file.writeString(gameState, false);
        System.out.println("Game saved: " + gameState);
    }

    private void loadSavedGame() {
        FileHandle file = Gdx.files.local("saved_game.txt");
        if (file.exists()) {
            String gameState = file.readString();
            StringTokenizer tokenizer = new StringTokenizer(gameState, "\n");
            while (tokenizer.hasMoreTokens()) {
                String line = tokenizer.nextToken();
                if (line.startsWith("Level: ")) {
                    int savedLevel = Integer.parseInt(line.substring(7));
                    game.setScreen(new GameScreen(game, savedLevel));
                    return;
                }
            }
        } else {
            System.out.println("No saved game found.");
        }
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
    public void hide() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        loadSavedTexture.dispose();
        exitTexture.dispose();
        stage.dispose();
        batch.dispose();
    }
}
