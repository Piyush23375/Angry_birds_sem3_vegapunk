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
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    private GameObject[] pigs;
    private Bird[] birds;
    private OrthographicCamera camera;
    private PhysicsManager physicsManager;
    private List<Bird> activeBirds = new ArrayList<>();
    private Vector3 originalCameraPosition;
    private List<Structure> structuresToDestroy = new ArrayList<>();
    private List<Pig> pigsToDestroy = new ArrayList<>();

    private static final float SLINGSHOT_SCALE = 0.5f;
    private static final float BIRD_SCALE = 0.05f;
    private static final float BLACK_BIRD_SCALE = 0.08f;
    private static final float STRUCTURE_SCALE = 1f;
    private static final float YELLOW_BIRD_SCALE = 0.1f;
    public static final float PPM = 100.0f;
    private static final float MINIMUM_BIRD_VELOCITY = 0.1f;
    private static final float PIG_SCALE = 0.5f;

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
        physicsManager = new PhysicsManager(world);
        debugRenderer = new Box2DDebugRenderer();

        createGround();

        createBackgroundForLevel();
        createBirdsForLevel();
        createStructuresForLevel();
        createSlingshot();

        createPauseButton();
        setupCollisionHandler();
        setupInputProcessor();
        // In the show() method of GameScreen

    }
    private void resetCameraPosition() {
        if (originalCameraPosition != null) {
            camera.position.set(originalCameraPosition);
            camera.update();
        }
    }

    private void createGround() {
        float platformWidth = 1600; // Increased platform width
        float platformHeight = 10 / PPM;
        platformY = 460 / PPM;
        createBox(platformWidth / 2, platformY, platformWidth, platformHeight, false);
    }

    public int getLevel() {
        return this.level;
    }

    public GameObject[] getStructures() {
        return this.structures;
    }

    public Bird[] getBirds() {
        return this.birds;
    }

    public int getSlingshotCurrentBirdIndex() {
        // Implement a method to get the current bird's index in the queue
        return slingshot.getCurrentBirdIndex();
    }

    public float getCameraX() {
        return camera.position.x;
    }

    public float getCameraY() {
        return camera.position.y;
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
        // Store original camera position at the start of rendering
        Vector3 originalCameraPosition = new Vector3(camera.position);

        // Reset camera to original position before applying any shake
        camera.position.set(originalCameraPosition);

        // Apply shake if current bird is a BlackBird
        Bird currentBird = slingshot.getCurrentBird();
        if (currentBird instanceof BlackBird blackBird) {
            if (blackBird.isShaking()) {
                // Apply random shake offset
                float shakeOffsetX = blackBird.getShakeOffset();
                float shakeOffsetY = blackBird.getShakeOffset();

                camera.position.x += shakeOffsetX;
                camera.position.y += shakeOffsetY;

                // Update shake
                blackBird.updateShake(delta);
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        processPigDestruction();
        processStructureDestruction();

        world.step(1/60f, 8, 3);

        // Handle special ability input for the current bird
        handleSpecialAbilityInput();

        checkBirdStateAndLaunchNext();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

        float slingshotWidth = camera.viewportWidth * SLINGSHOT_SCALE;
        float slingshotHeight = slingshotWidth * 2;
        float slingshotXOffset = 20;
        float slingshotYOffset = 10;
        slingshot.draw(batch, slingshotXOffset, slingshotYOffset);

        // Update and render all active birds
        updateAndRenderBirds(delta);
        renderPigs(batch);
        renderStructures(batch);

        batch.end();

        debugRenderer.render(world, camera.combined);
        stage.act(delta);
        stage.draw();

        // Optional: Ensure camera returns to original position after rendering
        camera.position.set(originalCameraPosition);
        camera.update();
    }

    private void handleSpecialAbilityInput() {
        Bird currentBird = slingshot.getCurrentBird();
        if (currentBird != null && isBirdInAir(currentBird)) {
            // Check for touch input to activate special ability
            if (Gdx.input.justTouched()) {
                activateSpecialAbility(currentBird);
            }
        }
    }


    private boolean isBirdInAir(Bird bird) {
        // Check if the bird is in the air by verifying its velocity
        return bird.getBody().getType() == BodyDef.BodyType.DynamicBody &&
            Math.abs(bird.getBody().getLinearVelocity().y) > 0.1f;
    }

    private void activateSpecialAbility(Bird bird) {
        if (bird instanceof YellowBird yellowBird) {
            if (!yellowBird.isSpecialAbilityActivated()) {
                yellowBird.specialAbility();
            }
        } else if (bird instanceof BlueBird blueBird) {
            if (!blueBird.isSpecialAbilityActivated()) {
                Bird[] newBirds = blueBird.specialAbility();
                // Add new birds to the active birds list
                activeBirds.addAll(Arrays.asList(newBirds));
            }
        } else if (bird instanceof BlackBird blackBird) {
            if (!blackBird.isSpecialAbilityActivated()) {
                blackBird.specialAbility();  // Pass the world here
            }
        }
    }

    private void updateAndRenderBirds(float delta) {
        // Create a copy of the active birds list to safely modify during iteration
        List<Bird> birdsToRender = new ArrayList<>(activeBirds);

        // Include the current bird from the slingshot
        Bird currentBird = slingshot.getCurrentBird();
        if (currentBird != null && !birdsToRender.contains(currentBird)) {
            birdsToRender.add(currentBird);
        }

        // Update and render birds
        for (Bird bird : new ArrayList<>(birdsToRender)) {
            if (!bird.isDisposed()) {
                bird.draw(batch);
            }

            // Remove birds that are out of bounds
            if (isBirdOutOfBounds(bird)) {
                bird.dispose();
                activeBirds.remove(bird);
            }
        }
    }

    private boolean isBirdOutOfBounds(Bird bird) {
        Vector2 position = bird.getBody().getPosition();
        return position.x > camera.viewportWidth / PPM ||
            position.y < 0 ||
            position.x < 0;
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
                // If the current bird is a BlueBird that has activated its special ability,
                // dispose of all additional birds as well
                if (currentBird instanceof BlueBird blueBird && blueBird.isSpecialAbilityActivated()) {
                    // Dispose of the original bird and its additional birds

                    currentBird.dispose();

                    // Remove the bird from active birds list
                    activeBirds.remove(currentBird);
                } else {
                    // For other bird types, just dispose of the current bird
                    currentBird.dispose();
                }

                // Load the next bird
                slingshot.update();
            }
        }
    }

    private void renderPigs(SpriteBatch batch){
        for(GameObject gameObject : pigs){
            if(gameObject instanceof Pig){
                ((Pig)gameObject).draw(batch);
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

    private float calculateDamage(float impactSpeed) {
        // More nuanced damage calculation based on impact speed
        // Adjust these values to balance the game
        float baseDamage = 10f;
        float speedMultiplier = 0.75f;
        float calculatedDamage = baseDamage + (impactSpeed * speedMultiplier);

        System.out.println("Impact Speed: " + impactSpeed +
            ", Calculated Damage: " + calculatedDamage);

        return calculatedDamage;
    }

    private void setupCollisionHandler() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                Body bodyA = fixtureA.getBody();
                Body bodyB = fixtureB.getBody();

                Object userDataA = bodyA.getUserData();
                Object userDataB = bodyB.getUserData();

                // Process collisions for Level 1
                if (level == 1) {
                    // Collision between Bird and Structure
                    if ((userDataA instanceof Bird && userDataB instanceof Structure) ||
                        (userDataA instanceof Structure && userDataB instanceof Bird)) {

                        Bird bird = (userDataA instanceof Bird) ? (Bird)userDataA : (Bird)userDataB;
                        Structure structure = (userDataA instanceof Structure) ? (Structure)userDataA : (Structure)userDataB;

                        Vector2 velocity = bodyA == bird.getBody() ? bodyA.getLinearVelocity() : bodyB.getLinearVelocity();
                        float impactSpeed = velocity.len();

                        float damage = calculateDamage(impactSpeed);
                        structure.applyDamage(damage);

                        System.out.println("Bird Impact Details:");
                        System.out.println("Velocity: " + velocity);
                        System.out.println("Impact Speed: " + impactSpeed);
                        printStructureHealth(structure);

                        if (structure.isDestroyed()) {
                            structuresToDestroy.add(structure);
                        }
                    }

                    // Bird and Pig collision
                     else if ((userDataA instanceof Bird && userDataB instanceof Pig) ||
                        (userDataA instanceof Pig && userDataB instanceof Bird)) {

                        Bird bird = (userDataA instanceof Bird) ? (Bird)userDataA : (Bird)userDataB;
                        Pig pig = (userDataA instanceof Pig) ? (Pig)userDataA : (Pig)userDataB;

                        Vector2 velocity = bodyA == bird.getBody() ? bodyA.getLinearVelocity() : bodyB.getLinearVelocity();
                        float impactSpeed = velocity.len();

                        float damage = calculateDamage(impactSpeed);
                        pig.applyDamage(damage);

                        System.out.println("Bird-Pig Impact Details:");
                        System.out.println("Velocity: " + velocity);
                        System.out.println("Impact Speed: " + impactSpeed);
                        System.out.println("Pig Health: " + pig.getHealth() + " / " + pig.getMaxHealth());

                        if (pig.isDestroyed()) {
                            // Queue pig for removal
                            pigsToDestroy.add(pig);
                        }
                    }
                }

                else if (level == 2) {
                    if ((userDataA instanceof Bird && userDataB instanceof Structure) ||
                        (userDataA instanceof Structure && userDataB instanceof Bird)) {

                        Bird bird = (userDataA instanceof Bird) ? (Bird)userDataA : (Bird)userDataB;
                        Structure structure = (userDataA instanceof Structure) ? (Structure)userDataA : (Structure)userDataB;

                        Vector2 velocity = bodyA == bird.getBody() ? bodyA.getLinearVelocity() : bodyB.getLinearVelocity();
                        float impactSpeed = velocity.len();

                        float damage = calculateDamage(impactSpeed);
                        structure.applyDamage(damage);

                        System.out.println("Bird Impact Details:");
                        System.out.println("Velocity: " + velocity);
                        System.out.println("Impact Speed: " + impactSpeed);
                        printStructureHealth(structure);

                        if (structure.isDestroyed()) {
                            structuresToDestroy.add(structure);
                        }
                    }

                    // Bird and Pig collision
                    else if ((userDataA instanceof Bird && userDataB instanceof Pig) ||
                        (userDataA instanceof Pig && userDataB instanceof Bird)) {

                        Bird bird = (userDataA instanceof Bird) ? (Bird)userDataA : (Bird)userDataB;
                        Pig pig = (userDataA instanceof Pig) ? (Pig)userDataA : (Pig)userDataB;

                        Vector2 velocity = bodyA == bird.getBody() ? bodyA.getLinearVelocity() : bodyB.getLinearVelocity();
                        float impactSpeed = velocity.len();

                        float damage = calculateDamage(impactSpeed);
                        pig.applyDamage(damage);

                        System.out.println("Bird-Pig Impact Details:");
                        System.out.println("Velocity: " + velocity);
                        System.out.println("Impact Speed: " + impactSpeed);
                        System.out.println("Pig Health: " + pig.getHealth() + " / " + pig.getMaxHealth());

                        if (pig.getHealth() <= 0 && !pig.isDestroyed()) {
                            System.out.println("Pig is being destroyed!");
                            pigsToDestroy.add(pig);
                        }
                    }
                }

                else if (level == 3) {
                    if ((userDataA instanceof Bird && userDataB instanceof Structure) ||
                        (userDataA instanceof Structure && userDataB instanceof Bird)) {

                        Bird bird = (userDataA instanceof Bird) ? (Bird)userDataA : (Bird)userDataB;
                        Structure structure = (userDataA instanceof Structure) ? (Structure)userDataA : (Structure)userDataB;

                        Vector2 velocity = bodyA == bird.getBody() ? bodyA.getLinearVelocity() : bodyB.getLinearVelocity();
                        float impactSpeed = velocity.len();

                        float damage = calculateDamage(impactSpeed);
                        structure.applyDamage(damage);

                        System.out.println("Bird Impact Details:");
                        System.out.println("Velocity: " + velocity);
                        System.out.println("Impact Speed: " + impactSpeed);
                        printStructureHealth(structure);

                        if (structure.isDestroyed()) {
                            structuresToDestroy.add(structure);
                        }
                    }

                    // Bird and Pig collision
                    else if ((userDataA instanceof Bird && userDataB instanceof Pig) ||
                        (userDataA instanceof Pig && userDataB instanceof Bird)) {

                        Bird bird = (userDataA instanceof Bird) ? (Bird)userDataA : (Bird)userDataB;
                        Pig pig = (userDataA instanceof Pig) ? (Pig)userDataA : (Pig)userDataB;

                        Vector2 velocity = bodyA == bird.getBody() ? bodyA.getLinearVelocity() : bodyB.getLinearVelocity();
                        float impactSpeed = velocity.len();

                        float damage = calculateDamage(impactSpeed);
                        pig.applyDamage(damage);

                        System.out.println("Bird-Pig Impact Details:");
                        System.out.println("Velocity: " + velocity);
                        System.out.println("Impact Speed: " + impactSpeed);
                        System.out.println("Pig Health: " + pig.getHealth() + " / " + pig.getMaxHealth());

                        if (pig.getHealth() <= 0 && !pig.isDestroyed()) {
                            System.out.println("Pig is being destroyed!");
                            pigsToDestroy.add(pig);
                        }
                    }
                }
            }


            @Override
            public void endContact(Contact contact) {
                // Not needed for this implementation
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                // Not needed for this implementation
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                // Not needed for this implementation
            }
        });
    }
    public void processStructureDestruction() {
        if (structuresToDestroy.isEmpty()) {
            return;
        }

        // Safely remove structures outside of collision handling
        for (Structure structure : structuresToDestroy) {
            removeStructureAdvanced(structure);
        }

        // Clear the destruction queue
        structuresToDestroy.clear();
    }

    public void processPigDestruction() {
        if (pigsToDestroy.isEmpty()) {
            return;
        }

        // Create a copy of the list to avoid concurrent modification
        List<Pig> pigsToRemove = new ArrayList<>(pigsToDestroy);
        pigsToDestroy.clear();

        // Safely remove pigs outside of collision handling
        for (Pig pig : pigsToRemove) {
            if (pig != null && !pig.isDestroyed()) {
                removePigAdvanced(pig);
            }
        }
    }

    private void removePigAdvanced(Pig pig) {
        if (pig == null || pig.isDestroyed()) {
            return;  // Prevent multiple removals
        }

        // Mark the pig as destroyed before removal
        pig.setDestroyed(true);

        // Dispose of the pig's textures
        pig.dispose();

        // Remove from the physics world
        if (pig.getBody() != null) {
            world.destroyBody(pig.getBody());
        }

        // Create a new list without the destroyed pig
        List<GameObject> pigList = new ArrayList<>(Arrays.asList(pigs));
        pigList.remove(pig);
        pigs = pigList.toArray(new GameObject[0]);

        System.out.println("Pig successfully destroyed and removed from game world");
    }



    private void printStructureHealth(Structure structure) {
        // Print health details to the console
        System.out.println("Structure Health Report:");
        System.out.println("Current Health: " + structure.getHealth());
        System.out.println("Max Health: " + structure.getMaxHealth());
        System.out.printf("Health Percentage: %.2f%%\n", structure.getHealthPercentage());
        System.out.println("Is Destroyed: " + structure.isDestroyed());
        System.out.println("----------------------------");
    }


    private void removeStructureAdvanced(Structure structure) {
        // Dispose of the structure's textures
        structure.dispose();

        // Remove from the physics world
        world.destroyBody(structure.getBody());

        // Create a new list without the destroyed structure
        List<GameObject> structureList = new ArrayList<>(Arrays.asList(structures));
        structureList.remove(structure);
        structures = structureList.toArray(new GameObject[0]);

        // Optional: Add any additional cleanup or game state update logic
        System.out.println("Structure destroyed and removed from game world");
    }



    private Structure createStructureWithUserData(String textureName,String damagedpath, float x, float y, float scale) {
        Structure structure = new Structure(world, textureName,damagedpath, x, y, scale);
        structure.getBody().setUserData(structure);
        return structure;
    }

    private void createStructuresForLevel() {
        switch (level) {
            case 1:
                structures = new GameObject[]{
                    new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block4.png", 1000 , 500 , STRUCTURE_SCALE,20f), // Base horizontal rod
                    new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block3.png", 1150 , 500 , STRUCTURE_SCALE,20f), // Glass rod above the block
                    new Structure(world, "Horizontal_wood_block1.png","Horizontal_wood_block4.png", 980 , 700 , STRUCTURE_SCALE,15f), // Wood block above the glass rod
                    //new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block4.png", 1075 , 500 , STRUCTURE_SCALE,75f), // Base horizontal rod
                    //new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block3.png", 1180 , 700 , STRUCTURE_SCALE,75f), // Glass rod above the block

                };

                pigs = new GameObject[]{
                    new Pig(world, "Small_Pig.png", "Small_Pig_damage.png", 1075, 540, PIG_SCALE, 75f),
                    /*new Pig(world, "Small_Pig.png", "Small_pig_damage.png", 1100, 525, PIG_SCALE, 75f),
                    new Pig(world, "Moustache_Pig.png", "Moustache_Pig_damage.png", 1020, 540, PIG_SCALE, 75f),
                    new Pig(world, "Small_Pig.png", "Small_pig_damage.png", 1020, 525, PIG_SCALE, 75f),
                    new Pig(world, "King_Pig.png", "King_pig_damage.png", 1050, 725, PIG_SCALE, 150f)*/
                };
                break;

            case 2:
                structures = new GameObject[]{
                    new Structure(world, "Vertical_wood_block1.png","Vertical_wood_block4.png", 1000 , 500 , STRUCTURE_SCALE,15f), // Base horizontal rod
                    new Structure(world, "Vertical_wood_block1.png","Vertical_wood_block3.png", 1150 , 500 , STRUCTURE_SCALE,15f), // Glass rod above the block
                    new Structure(world, "Horizontal_stone_block1.png","Horizontal_stone_block4.png", 980 , 700 , STRUCTURE_SCALE,20f), // Wood block above the glass rod
                    new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block4.png", 1085 , 500 , STRUCTURE_SCALE,20f), // Base horizontal rod
                    //new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block3.png", 1180 , 700 , STRUCTURE_SCALE,75f), // Glass rod above the block

                };

                pigs = new GameObject[]{
                    new Pig(world, "Moustache_Pig.png", "Moustache_Pig_damage.png", 1100, 540, PIG_SCALE, 100f),
                    new Pig(world, "Small_Pig.png", "Small_pig_damage.png", 1100, 525, PIG_SCALE, 75f),
                    new Pig(world, "Moustache_Pig.png", "Moustache_Pig_damage.png", 1020, 540, PIG_SCALE, 100f),
                    new Pig(world, "Small_Pig.png", "Small_pig_damage.png", 1020, 525, PIG_SCALE, 75f),
                    new Pig(world, "King_Pig.png", "King_pig_damage.png", 1050, 725, PIG_SCALE, 150f)
                };
                break;

            case 3:
                structures = new GameObject[]{
                    new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block4.png", 1000 , 500 , STRUCTURE_SCALE,20f), // Base horizontal rod
                    new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block3.png", 1150 , 500 , STRUCTURE_SCALE,20f), // Glass rod above the block
                    new Structure(world, "Horizontal_wood_block1.png","Horizontal_wood_block4.png", 980 , 700 , STRUCTURE_SCALE,15f), // Wood block above the glass rod
                    new Structure(world, "Vertical_glass_block1.png","Vertical_glass_block4.png", 1030 , 700 , STRUCTURE_SCALE,10f), // Base horizontal rod
                    new Structure(world, "Vertical_glass_block1.png","Vertical_glass_block3.png", 1100 , 700 , STRUCTURE_SCALE,10f), // Glass rod above the block
                    //new Structure(world, "Horizontal_wood_block1.png","Horizontal_wood_block4.png", 1000 , 900 , STRUCTURE_SCALE,25f), // Wood block above the glass rod
                    new Structure(world, "Vertical_stone_block1.png","Vertical_stone_block3.png", 1075 , 500 , STRUCTURE_SCALE,20f), // Base horizontal rod
                    new Structure(world, "Horizontal_wood_block1.png","Horizontal_wood_block4.png", 980 , 900 , STRUCTURE_SCALE,15f), // Wood block above the glass rod

                };

                pigs = new GameObject[]{
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1100 ,540 , PIG_SCALE,75f),
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1100 ,525 , PIG_SCALE,75f),
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1020 ,540 , PIG_SCALE,75f),
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1020 ,525 , PIG_SCALE,75f),
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1100 ,560 , PIG_SCALE,75f),
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1100 ,580 , PIG_SCALE,75f),
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1020 ,560 , PIG_SCALE,75f),
                    new Pig(world, "Small_Pig.png","Small_pig_damage.png",1020 ,580 , PIG_SCALE,75f),
                    new Pig(world, "Moustache_Pig.png","Moustache_Pig_damage.png",1055 ,710 , PIG_SCALE,100f),
                    new Pig(world, "Moustache_Pig.png","Moustache_Pig_damage.png",1055 ,745 , PIG_SCALE,100f),
                    new Pig(world, "Moustache_Pig.png","Moustache_Pig_damage.png",1060 ,780 , PIG_SCALE,100f),
                    new Pig(world, "King_Pig.png", "King_pig_damage.png", 1045, 890, PIG_SCALE, 150f)
                };
                break;
        }
    }
    private void createBirdsForLevel() {
        switch (level) {
            case 1:
                birds = new Bird[]{
                    new RedBird(world, 100 / PPM, platformY + 50 / PPM, BIRD_SCALE),
                    new BlueBird(world, 250 / PPM, platformY + 50 / PPM, BIRD_SCALE),
                    new BlackBird(world, 200/PPM, platformY+ 50 /PPM, BLACK_BIRD_SCALE)
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
                    new BlueBird(world, 50 / PPM, platformY - 100 / PPM, BIRD_SCALE),
                    new BlackBird(world, 200/PPM, platformY+ 50 /PPM, BLACK_BIRD_SCALE)
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
        // Dispose of active birds along with other resources
        for (Bird bird : activeBirds) {
            bird.dispose();
        }

        // Existing dispose method continues as before
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

        for( GameObject pigs : pigs) {
            pigs.dispose();
        }

        pauseButtonTexture.dispose();
        pauseButtonHoverTexture.dispose();
        pauseButtonPressedTexture.dispose();
    }
}
