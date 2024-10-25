package com.angrybirds;

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

public class PauseScreen implements Screen {

    private Game game;
    private int level;
    private Stage stage;
    private Skin skin;
    private GameScreen gameScreen;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private int score = 150;
    private int stars = 3;

    public PauseScreen(Game game, int level, GameScreen gameScreen) {
        this.game = game;
        this.level = level;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        stage = new Stage(new ScreenViewport(camera));
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("pause_screen_bg.jpg"));

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
                game.setScreen(new GameScreen(game, level));
            }
        });

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
                game.setScreen(new LevelsScreen(game));
            }
        });

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
                game.setScreen(gameScreen);
            }
        });

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
                game.setScreen(new ScoreScreen(game, score, stars));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.left();
        table.add(restartButton).pad(10).size(100, 100);
        table.row();
        table.add(levelsButton).pad(10).size(100, 100);
        table.row();
        table.add(resumeButton).pad(10).size(100, 100);
        table.row();
        table.add(scoreButton).pad(10).size(100, 100);

        stage.addActor(table);
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
    public void hide() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}

