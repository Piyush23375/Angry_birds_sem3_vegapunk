package com.angrybirds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;
import java.util.Arrays;
import java.util.LinkedList;
import com.badlogic.gdx.InputMultiplexer;

public class GameScreen implements Screen {
    private World world;
    private Box2DDebugRenderer debugRenderer;
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

    private static final float SLINGSHOT_SCALE = 0.5f;
    private static final float BIRD_SCALE = 0.05f;
    private static final float STRUCTURE_SCALE = 1f;
    private static final float YELLOW_BIRD_SCALE = 0.1f;
    public static final float PPM = 100.0f;
    private static final float MINIMUM_BIRD_VELOCITY = 0.1f;

    private Texture pauseButtonTexture;
    private Texture pauseButtonHoverTexture;
    private Texture pauseButtonPressedTexture;

    private float platformY;

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

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        createGround();

        createBackgroundForLevel();
        createBirdsForLevel();
        createStructuresForLevel();
        createSlingshot();

        createPauseButton();

        setupInputProcessor();
    }

    private void createGround() {
        float platformWidth = 1600; // Increased platform width
        float platformHeight = 10 / PPM;
        platformY = 460 / PPM;
        createBox(platformWidth / 2, platformY, platformWidth, platformHeight, false);
    }

    private Body createBox(float x, float y, float width, float height, boolean isDynamic) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isDynamic ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = isDynamic ? 1f : 0f;
        fixtureDef.friction = 2f;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    private void createSlingshot() {
        LinkedList<Bird> birdQueue = new LinkedList<>(Arrays.asList(birds));
        slingshot = new Slingshot(world, "slingshot.png", 250, 450, 0.3f, birdQueue, camera);
    }

    private void createPauseButton() {
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

    private void setupInputProcessor() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(slingshot.getInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 60f, 6, 2);
        checkBirdStateAndLaunchNext();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

        // Draw slingshot
        float slingshotWidth = camera.viewportWidth * SLINGSHOT_SCALE;
        float slingshotHeight = slingshotWidth * 2;
        float slingshotXOffset = 20;
        float slingshotYOffset = 10;
        slingshot.draw(batch, slingshotXOffset, slingshotYOffset);

        // Draw birds
        for (Bird bird : birds) {
            if (!bird.isDisposed()) {
                bird.draw(batch);
            }
        }

        // Draw structures
        renderStructures(batch);

        batch.end();

        // Always render debug renderer to show hitboxes
        debugRenderer.render(world, camera.combined);

        stage.act(delta);
        stage.draw();
    }

    private void checkBirdStateAndLaunchNext() {
        Bird currentBird = slingshot.getCurrentBird();
        if (currentBird != null && currentBird.getBody().getType() == BodyDef.BodyType.DynamicBody) {
            Vector2 position = currentBird.getBody().getPosition();
            Vector2 velocity = currentBird.getBody().getLinearVelocity();

            // Remove bird and launch next if:
            // 1. Bird is off-screen horizontally or vertically
            // 2. Bird's velocity is very low
            boolean isOffScreen = position.x > camera.viewportWidth / PPM ||
                position.y < 0 ||
                position.x < 0;
            boolean isSlowMoving = Math.abs(velocity.x) < MINIMUM_BIRD_VELOCITY &&
                Math.abs(velocity.y) < MINIMUM_BIRD_VELOCITY;

            if (isOffScreen || isSlowMoving) {
                currentBird.dispose(); // Clean up current bird
                slingshot.update(); // This will load the next bird
            }
        }
    }

    private void renderStructures(SpriteBatch batch) {
        for (GameObject gameObject : structures) {
            if (gameObject instanceof Structure) {
                ((Structure) gameObject).update();
            }
            gameObject.draw(batch);
        }
    }
    }

    private void createBackgroundForLevel() {
        if (backgroundTexture != null) backgroundTexture.dispose();

        switch (level) {
            case 1:
                backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg_level1.png"));
                break;
            case 2:
                backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg_level2.png"));
                break;
            case 3:
                backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg_level3.png"));
                break;
            default:
                backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg.png"));
        }
    }

    private void createStructuresForLevel() {
        switch (level) {
            case 1:
                structures = new GameObject[]{
                    new Structure(world, "glass_rod.png", 1000 / PPM, 500 / PPM, STRUCTURE_SCALE), // Base horizontal rod
                    new Structure(world, "wood_block.png", 1000 / PPM, 400 / PPM, STRUCTURE_SCALE), // Block on top of the rod
                    new Structure(world, "glass_rod.png", 1000 / PPM, 350 / PPM, STRUCTURE_SCALE), // Glass rod above the block
                    new Structure(world, "wood_block.png", 1000 / PPM, 300 / PPM, STRUCTURE_SCALE), // Wood block above the glass rod
                    new MediumPig(world, 1050 / PPM, 250 / PPM, STRUCTURE_SCALE), // Pig on the top block

                    new Structure(world, "wood_block.png", 1100 / PPM, 500 / PPM, STRUCTURE_SCALE), // Glass block to the right
                    new Structure(world, "wood_rod.png", 1100 / PPM, 450 / PPM, STRUCTURE_SCALE), // Wood rod above the glass block
                    new Structure(world, "wood_block.png", 1200 / PPM, 500 / PPM, STRUCTURE_SCALE), // Another glass block to the right
                    new Structure(world, "wood_block.png", 1200 / PPM, 600 / PPM, STRUCTURE_SCALE), // Wood block above the last glass block

                    new Structure(world, "wood_rod.png", 1700 / PPM, 500 / PPM, STRUCTURE_SCALE), // Horizontal rod
                    new Structure(world, "wood_block.png", 1700 / PPM, 400 / PPM, STRUCTURE_SCALE), // Block above the rod
                    new MediumPig(world, 1800 / PPM, 450 / PPM, STRUCTURE_SCALE), // Pig on top of the block
                    new Structure(world, "wood_rod.png", 1900 / PPM, 500 / PPM, STRUCTURE_SCALE), // Another horizontal rod
                    new Structure(world, "wood_block.png", 1900 / PPM, 400 / PPM, STRUCTURE_SCALE) // Block above the second rod
                };
                break;
            case 2:
                structures = new GameObject[]{
                    new Structure(world, "glass_rod.png", 1700 / PPM, 500 / PPM, STRUCTURE_SCALE), // Base horizontal rod
                    new Structure(world, "wood_block.png", 1700 / PPM, 450 / PPM, STRUCTURE_SCALE), // Block on top of the rod
                    new Structure(world, "wood_rod.png", 1600 / PPM, 500 / PPM, STRUCTURE_SCALE), // Wood rod above the glass block
                    new MediumPig(world, 1550 / PPM, 200 / PPM, STRUCTURE_SCALE), // Pig on the wood rod
                    new Structure(world, "wood_block.png", 1750 / PPM, 500 / PPM, STRUCTURE_SCALE), // Glass block to the right
                    new Structure(world, "wood_block.png", 1500 / PPM, 550 / PPM, STRUCTURE_SCALE), // Wood block above the glass block
                    new Structure(world, "glass_rod.png", 1620 / PPM, 500 / PPM, STRUCTURE_SCALE) // Another base horizontal rod to the right
                };
                break;
            case 3:
                structures = new GameObject[]{
                    new Structure(world, "wood_rod.png", 1000 / PPM, 500 / PPM, STRUCTURE_SCALE), // Base horizontal rod
                    new Structure(world, "wood_block.png", 1000 / PPM, 500 / PPM, STRUCTURE_SCALE), // Block on top of the rod
                    new Structure(world, "glass_rod.png", 1000 / PPM, 450 / PPM, STRUCTURE_SCALE), // Glass rod above the block
                    new Structure(world, "wood_block.png", 1000 / PPM, 500 / PPM, STRUCTURE_SCALE), // Wood block above the glass rod
                    new MediumPig(world, 1050 / PPM, 250 / PPM, STRUCTURE_SCALE), // Pig on the top block
                    new Structure(world, "wood_block.png", 1100 / PPM, 500 / PPM, STRUCTURE_SCALE), // Glass block to the right
                    new Structure(world, "wood_rod.png", 1100 / PPM, 500 / PPM, STRUCTURE_SCALE), // Wood rod above the glass block
                    new Structure(world, "wood_block.png", 1200 / PPM, 500 / PPM, STRUCTURE_SCALE), // Another glass block to the right
                    new Structure(world, "wood_block.png", 1200 / PPM, 600 / PPM, STRUCTURE_SCALE) // Wood block above the last glass block
                };
                break;
        }
    }

    private void createBirdsForLevel() {
        switch (level) {
            case 1:
                birds = new Bird[]{
                    new RedBird(world, 100 / PPM, platformY + 50 / PPM, BIRD_SCALE),
                    new BlueBird(world, 50 / PPM, platformY + 50 / PPM, BIRD_SCALE)
                };
                break;
            case 2:
                birds = new Bird[]{
                    new YellowBird(world, 50 / PPM, platformY + 50 / PPM, YELLOW_BIRD_SCALE),
                    new BlueBird(world, 50 / PPM, platformY - 50 / PPM, BIRD_SCALE)
                };
                break;
            case 3:
                birds = new Bird[]{
                    new RedBird(world, 50 / PPM, platformY + 100 / PPM, BIRD_SCALE),
                    new YellowBird(world, 50 / PPM, platformY, YELLOW_BIRD_SCALE),
                    new BlueBird(world, 50 / PPM, platformY - 100 / PPM, BIRD_SCALE)
                };
                break;
            default:
                birds = new Bird[]{
                    new RedBird(world, 50 / PPM, platformY + 50 / PPM, BIRD_SCALE)
                };
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        slingshot.dispose();
        world.dispose();
        debugRenderer.dispose();

        for (Bird bird : birds) {
            bird.dispose();
        }

        for (GameObject structure : structures) {
            structure.dispose();
        }

        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
        pauseButtonPressedTexture.dispose();
    }
}
