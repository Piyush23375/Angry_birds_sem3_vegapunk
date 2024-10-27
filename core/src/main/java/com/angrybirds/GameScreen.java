package com.angrybirds;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameScreen implements Screen {

    private Game game;
    private int level;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private SpriteBatch batch;
    private Slingshot slingshot;
    private GameObject[] structures;
    private Bird[] birds;
    private OrthographicCamera camera;
    private static final float SLINGSHOT_SCALE = 0.05f;
    private static final float BIRD_SCALE = 0.1f;
    private static final float STRUCTURE_SCALE = 1f;
    private static final float YELLOW_BIRD_SCALE = 0.28f;
    private Texture pauseButtonTexture;
    private Texture pauseButtonHoverTexture;
    private Texture pauseButtonPressedTexture;

    public GameScreen(Game game, int level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg.png"));
        createStructuresForLevel();
        slingshot = new Slingshot("slingshot.png", 25 * SLINGSHOT_SCALE, 50 * SLINGSHOT_SCALE, SLINGSHOT_SCALE);
        createBirdsForLevel();
        pauseButtonTexture = new Texture(Gdx.files.internal("pause.png"));
        pauseButtonHoverTexture = new Texture(Gdx.files.internal("pause_hover.png"));
        pauseButtonPressedTexture = new Texture(Gdx.files.internal("pause_pressed.png"));
        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(pauseButtonTexture);
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(pauseButtonHoverTexture);
        TextureRegionDrawable pressedDrawable = new TextureRegionDrawable(pauseButtonPressedTexture);
        ImageButton pauseButton = new ImageButton(normalDrawable, hoverDrawable, pressedDrawable);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new PauseScreen(game, level, GameScreen.this));
            }
        });
        pauseButton.setSize(120, 120);
        Table pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.bottom().right();
        pauseTable.add(pauseButton).pad(10).size(120, 120);
        stage.addActor(pauseTable);
    }

    private void createStructuresForLevel() {
        if (level == 1) {
            structures = new GameObject[]{
                new Structure("wood_rod.png", 1000, 100, STRUCTURE_SCALE),
                new MediumPig(970, 270, STRUCTURE_SCALE)
            };
        } else if (level == 2) {
            structures = new GameObject[]{
                new Structure("glass_rod.png", 1000, 100, STRUCTURE_SCALE),
                new Structure("glass_rod.png", 1100, 100, STRUCTURE_SCALE),
                new MediumPig(970, 270, STRUCTURE_SCALE),
                new Structure("wood_block.png", 1100, 250, STRUCTURE_SCALE)
            };
        } else if (level == 3) {
            structures = new GameObject[]{
                new Structure("glass_rod.png", 1000, 50, STRUCTURE_SCALE),
                new MediumPig(970, 200, STRUCTURE_SCALE)
            };
        }
    }

    private void createBirdsForLevel() {
        if (level == 1) {
            birds = new Bird[]{
                new RedBird(10, camera.viewportHeight + 475, BIRD_SCALE),
                new BlueBird(10, camera.viewportHeight + 350, BIRD_SCALE)
            };
        } else if (level == 2) {
            birds = new Bird[]{
                new YellowBird(10, camera.viewportHeight + 475, YELLOW_BIRD_SCALE),
                new BlueBird(10, camera.viewportHeight + 350, BIRD_SCALE)
            };
        } else if (level == 3) {
            birds = new Bird[]{
                new RedBird(10, camera.viewportHeight + 475, BIRD_SCALE),
                new YellowBird(10, camera.viewportHeight + 350, YELLOW_BIRD_SCALE),
                new BlueBird(10, camera.viewportHeight + 225, BIRD_SCALE)
            };
        } else {
            birds = new Bird[]{
                new RedBird(10, camera.viewportHeight + 450, BIRD_SCALE)
            };
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        float slingshotWidth = camera.viewportWidth * SLINGSHOT_SCALE;
        float slingshotHeight = slingshotWidth * 2;
        float slingshotXOffset = 20;
        float slingshotYOffset = 10;
        slingshot.draw(batch, slingshotXOffset, slingshotYOffset, slingshotWidth, slingshotHeight);
        for (Bird bird : birds) {
            bird.draw(batch);
        }
        renderStructures(batch);
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    private void renderStructures(SpriteBatch batch) {
        for (GameObject gameObject : structures) {
            gameObject.draw(batch);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        slingshot.dispose();
        for (Bird bird : birds) {
            bird.dispose();
        }
        for (GameObject structure : structures) {
            structure.dispose();
        }
    }
}
