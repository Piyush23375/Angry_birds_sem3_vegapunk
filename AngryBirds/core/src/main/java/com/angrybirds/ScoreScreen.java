package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ScoreScreen implements Screen {

    private Game game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private int score;
    private int stars;

    public ScoreScreen(Game game, int score, int stars) {
        this.game = game;
        this.score = score;
        this.stars = stars;
    }

    @Override
    public void show() {
        stage = new Stage();
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("score_screen_bg.png"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label scoreLabel = new Label("Score: " + score, skin);
        Label starsLabel = new Label("Stars: " + stars, skin);

        table.add(scoreLabel).pad(10);
        table.row();
        table.add(starsLabel).pad(10);

        stage.addActor(table);

        Texture backButtonTexture = new Texture(Gdx.files.internal("back.png"));
        Texture backButtonHoverTexture = new Texture(Gdx.files.internal("back_hover.png"));
        Texture backButtonPressedTexture = new Texture(Gdx.files.internal("back.png"));

        TextureRegionDrawable backNormalDrawable = new TextureRegionDrawable(backButtonTexture);
        TextureRegionDrawable backHoverDrawable = new TextureRegionDrawable(backButtonHoverTexture);
        TextureRegionDrawable backPressedDrawable = new TextureRegionDrawable(backButtonPressedTexture);

        ImageButton backButton = new ImageButton(backNormalDrawable, backHoverDrawable, backPressedDrawable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new PauseScreen(game, 1, null));
            }
        });

        Table backTable = new Table();
        backTable.setFillParent(true);
        backTable.bottom().left();
        backTable.add(backButton).pad(10).size(100, 100);

        stage.addActor(backTable);

        Texture nextLevelButtonTexture = new Texture(Gdx.files.internal("next_level.png"));
        Texture nextLevelHoverTexture = new Texture(Gdx.files.internal("next_level_hover.png"));
        Texture nextLevelPressedTexture = new Texture(Gdx.files.internal("next_level.png"));

        TextureRegionDrawable nextLevelNormalDrawable = new TextureRegionDrawable(nextLevelButtonTexture);
        TextureRegionDrawable nextLevelHoverDrawable = new TextureRegionDrawable(nextLevelHoverTexture);
        TextureRegionDrawable nextLevelPressedDrawable = new TextureRegionDrawable(nextLevelPressedTexture);

        ImageButton nextLevelButton = new ImageButton(nextLevelNormalDrawable, nextLevelHoverDrawable, nextLevelPressedDrawable);
        nextLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, getNextLevel()));
            }
        });

        Table nextTable = new Table();
        nextTable.setFillParent(true);
        nextTable.bottom().right();
        nextTable.add(nextLevelButton).pad(10).size(100, 100);

        stage.addActor(nextTable);
    }

    private int getNextLevel() {
        return 2;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        stage.dispose();
        skin.dispose();
        batch.dispose();
    }
}
