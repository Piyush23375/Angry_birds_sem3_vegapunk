package com.angrybirds;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JUnitTest {
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private MainMenuScreen mainmenuscreen;
    private GameScreen gamescreen;
    private int level;

    @BeforeEach
    public void setUp() {
        // Capture System.out
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Initialize LevelScreen with Main instance
        Main game = new Main();
        mainmenuscreen = new MainMenuScreen(game);
        gamescreen = new GameScreen(game , level);
    }

    @Test
    public void testPlayButtonClick() {
        // Simulate pause button click
        // Assuming you have a method to trigger the pause button action
        mainmenuscreen.playButtonClicked(); // You might need to add this method to your LevelScreen class

        // Get the captured output
        String output = outContent.toString();

        // Verify the output contains the expected message
        assertTrue(output.contains("Game Played"),
            "play button click should print 'Game Played'");

        // Additional assertions you might want to add:
        assertTrue(mainmenuscreen.isPlayed(),
            "Game should be in Played state after clicking play button");
    }

    @Test
    public void testPauseButtonClick() {
        // Simulate pause button click
        // Assuming you have a method to trigger the pause button action
        gamescreen.pauseButtonClicked(); // You might need to add this method to your LevelScreen class

        // Get the captured output
        String output = outContent.toString();

        // Verify the output contains the expected message
        assertTrue(output.contains("Game Paused"),
            "Pause button click should print 'Game Paused'");

        // Additional assertions you might want to add:
        assertTrue(gamescreen.isPaused(),
            "Game should be in paused state after clicking pause button");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }


}
