package com.angrybirds;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        // Set the initial screen when the application starts
        setScreen(new MainMenuScreen(this));
    }
}
