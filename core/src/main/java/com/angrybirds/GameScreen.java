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
import com.badlogic.gdx.physics.box2d.*; // Box2D classes
import com.badlogic.gdx.math.Vector2;
import java.util.Arrays;
import com.badlogic.gdx.InputMultiplexer;

public class GameScreen implements Screen {
    private World world; // Box2D physics world
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

    private static final float SLINGSHOT_SCALE = 0.037f;
    private static final float BIRD_SCALE = 0.05f;
    private static final float STRUCTURE_SCALE = 1f;
    private static final float YELLOW_BIRD_SCALE = 0.1f;
    public static final float PPM = 100.0f; // Adjust based on your scaling

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

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        createGround();

        createBackgroundForLevel();
        createBirdsForLevel();
        createStructuresForLevel();
        createSlingshot();

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

        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        inputMultiplexer.addProcessor(stage);

        inputMultiplexer.addProcessor(slingshot.getInputProcessor());

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    private void createSlingshot() {
        slingshot = new Slingshot(world, "slingshot.png", 250 , 450 , SLINGSHOT_SCALE, Arrays.asList(birds), camera);
    }

    private void createGround() {
        // Set the position and size of the ground
        float groundWidth = camera.viewportWidth; // Full screen width
        float groundHeight = 0f; // Adjust height as needed

        // Ground is static, so we pass false for isDynamic
        createBox(0, 460 / PPM, groundWidth, groundHeight, false);  // Ground at height 450
    }

    // Utility function to create Box2D bodies (static or dynamic)
    private Body createBox(float x, float y, float width, float height, boolean isDynamic) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isDynamic ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);  // Set the body's position

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);  // Half width and height for Box2D coordinates

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = isDynamic ? 1f : 0f;  // Only dynamic bodies have density

        body.createFixture(fixtureDef);
        shape.dispose();  // Dispose the shape after creating fixture

        return body;
    }

    private void createBackgroundForLevel() {
        // Dispose existing background texture if it exists
        if (backgroundTexture != null) backgroundTexture.dispose();

        if (level == 1) {
            backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg_level1.png"));
        } else if (level == 2) {
            backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg_level2.png"));
        } else if (level == 3) {
            backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg_level3.png"));
        } else {
            backgroundTexture = new Texture(Gdx.files.internal("game_screen_bg.png"));
        }
    }

    private void createStructuresForLevel() {
        if (level == 1) {
            structures = new GameObject[]{
                new Structure(world,"wood_rod.png", 170000/ PPM, 50000/ PPM, STRUCTURE_SCALE),
                new MediumPig(world,970/ PPM, 270/ PPM, STRUCTURE_SCALE)
            };
        } else if (level == 2) {
            structures = new GameObject[]{
                new Structure(world,"glass_rod.png", 1000/ PPM, 100/ PPM, STRUCTURE_SCALE),
                new Structure(world,"glass_rod.png", 1100/ PPM, 100/ PPM, STRUCTURE_SCALE),
                new MediumPig(world,970/ PPM, 270/ PPM, STRUCTURE_SCALE),
                new Structure(world,"wood_block.png", 1100/ PPM, 250/ PPM, STRUCTURE_SCALE)
            };

        } else if (level == 3) {
            structures = new GameObject[]{
                new Structure(world,"glass_rod.png", 1000/ PPM, 50/ PPM, STRUCTURE_SCALE),
                new MediumPig(world,970/ PPM, 200/ PPM, STRUCTURE_SCALE)
            };
        }
    }

    private void createBirdsForLevel() {
        if (level == 1) {
            birds = new Bird[]{
                new RedBird(world,285/ PPM, 550/ PPM, BIRD_SCALE),
                new BlueBird(world,230/ PPM, 500/ PPM, BIRD_SCALE)
            };

        } else if (level == 2) {
            birds = new Bird[]{
                new YellowBird(world,10/ PPM, camera.viewportHeight + 475/ PPM, YELLOW_BIRD_SCALE),
                new BlueBird(world,10/ PPM, camera.viewportHeight + 350/ PPM, BIRD_SCALE)
            };

        } else if (level == 3) {
            birds = new Bird[]{
                new RedBird(world,10/ PPM, camera.viewportHeight + 475/ PPM, BIRD_SCALE),
                new YellowBird(world,10/ PPM, camera.viewportHeight + 350/ PPM, YELLOW_BIRD_SCALE),
                new BlueBird(world,10/ PPM, camera.viewportHeight + 225/ PPM, BIRD_SCALE)
            };

        } else {
            birds = new Bird[]{
                new RedBird(world,10, camera.viewportHeight + 450, BIRD_SCALE)
            };
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Step the physics simulation
        world.step(1 / 60f, 6, 2);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Draw the background
        batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

        // Draw the slingshot
        float slingshotWidth = camera.viewportWidth * SLINGSHOT_SCALE;
        float slingshotHeight = slingshotWidth * 2;
        float slingshotXOffset = 20;
        float slingshotYOffset = 10;
        slingshot.draw(batch, slingshotXOffset, slingshotYOffset, slingshotWidth, slingshotHeight);

        // Draw the birds
        for (Bird bird : birds) {
            bird.draw(batch);
        }

        // Draw and update structures
        renderStructures(batch);

        batch.end();

        // Render Box2D debug information
        debugRenderer.render(world, camera.combined);

        // Draw the UI
        stage.act(delta);
        stage.draw();
    }


    private void renderStructures(SpriteBatch batch) {
        for (GameObject gameObject : structures) {
            if (gameObject instanceof Structure) {
                ((Structure) gameObject).update(); // Synchronize texture with Box2D body
            }
            gameObject.draw(batch); // Draw the structure
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
        world.dispose();
        debugRenderer.dispose();
        for (Bird bird : birds) {
            bird.dispose();
        }
        for (GameObject structure : structures) {
            structure.dispose();
        }
    }
}
