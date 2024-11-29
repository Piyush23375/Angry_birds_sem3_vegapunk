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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

public class LevelsScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private Game game;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Music backgroundMusic;
    public LevelsScreen(Game game) {
        this.game = game;


        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("levels_screen_bg.png"));

        ImageButton level1Button = createLevelButton("level1.png", "level1_hover.png", 1, 100, 100);
        ImageButton level2Button = createLevelButton("level2.png", "level2_hover.png", 2, 100, 100);
        ImageButton level3Button = createLevelButton("level3.png", "level3_hover.png", 3, 100, 100);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("main_theme_song.mp3"));
        backgroundMusic.setLooping(true); // Loop the music
        backgroundMusic.play();

        ImageButton backButton = createBackButton(100, 100);

        Table levelTable1 = new Table();
        Table levelTable2 = new Table();
        Table levelTable3 = new Table();

        levelTable1.center();
        levelTable1.add(level1Button).pad(10).size(200, 200);

        levelTable2.center();
        levelTable2.add(level2Button).pad(10).size(260, 260);

        levelTable3.center();
        levelTable3.add(level3Button).pad(10).size(200, 200);


        levelTable1.setPosition(975, 225);
        levelTable2.setPosition(985, 470);
        levelTable3.setPosition(970, 720);

        stage.addActor(levelTable1);
        stage.addActor(levelTable2);
        stage.addActor(levelTable3);



        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.bottom().left();
        backTable.add(backButton).pad(10).size(100, 100);


        stage.addActor(backTable);
    }

    private void saveGame(int levelNumber) {
        FileHandle file = Gdx.files.local("saved_game.txt");
        String gameState = "Level: " + levelNumber + "\n";
        file.writeString(gameState, false); // Overwrites the file
        System.out.println("Game saved: " + gameState);
    }

    private ImageButton createLevelButton(String normalImagePath, String hoverImagePath, int levelNumber, float width, float height) {
        Texture normalTexture = new Texture(Gdx.files.internal(normalImagePath));
        Texture hoverTexture = new Texture(Gdx.files.internal(hoverImagePath));

        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(normalTexture);
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(hoverTexture);

        ImageButton levelButton = new ImageButton(normalDrawable, hoverDrawable);
        levelButton.setSize(width, height);

        levelButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                saveGame(levelNumber); // Save the clicked level
                game.setScreen(new GameScreen(game, levelNumber));
            }
        });

        return levelButton;
    }


    private ImageButton createBackButton(float width, float height) {

        Texture backNormalTexture = new Texture(Gdx.files.internal("back.png"));
        Texture backHoverTexture = new Texture(Gdx.files.internal("back_hover.png"));


        TextureRegionDrawable backNormalDrawable = new TextureRegionDrawable(backNormalTexture);
        TextureRegionDrawable backHoverDrawable = new TextureRegionDrawable(backHoverTexture);


        ImageButton backButton = new ImageButton(backNormalDrawable, backHoverDrawable);
        backButton.setSize(width, height);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new FirstScreen(game));
            }
        });

        return backButton;
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
        camera.setToOrtho(false, width, height);
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
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        batch.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}

