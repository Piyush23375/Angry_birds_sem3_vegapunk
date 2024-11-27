//package com.angrybirds;
//
//import com.badlogic.gdx.physics.box2d.World;
//
//public class WSH extends Structure {
//    // Predefined texture paths for horizontal wooden structures
//    private static final String[] WOODEN_HORIZONTAL_TEXTURE = {
//        "textures/wood_plank_horizontal_short.png"
//    };
//
//    // Default health values for wooden structures
//    private static final float DEFAULT_HEALTH = 50f;
//    private static final float WEAK_IMPACT_DAMAGE = 10f;
//    private static final float MEDIUM_IMPACT_DAMAGE = 25f;
//    private static final float STRONG_IMPACT_DAMAGE = 40f;
//
//    // Constructor with default texture and health
//    public WSH(World world, float x, float y, float scale) {
//        this(world, WOODEN_HORIZONTAL_TEXTURE[0], x, y, scale, DEFAULT_HEALTH);
//    }
//
//    // Constructor with specific texture path and default health
//    public WSH(World world, String texturePath, float x, float y, float scale) {
//        this(world, texturePath, x, y, scale, DEFAULT_HEALTH);
//    }

    // Full constructor with custom health
//    public WSH(World world, String texturePath, float x, float y, float scale, float health) {
//        super(world, texturePath, x, y, scale, health);
//    }
//
//
//
//    // Optional method to handle structure destruction
//    protected void onDestroyed() {
//        // You can add special effects, sound, or game logic here
//        System.out.println("Wooden structure destroyed!");
//    }
//}
