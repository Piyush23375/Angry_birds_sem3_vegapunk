package com.angrybirds;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
       
        setScreen(new MainMenuScreen(this));
    }
}
