package no.ntnu.idatx2003.millions;

import javafx.application.Application;

/**
 * Entry point for the Millions stock trading game application.
 * Launches the JavaFX Application.
 */
public class Main {
    /**
     * Constructs the application entry point.
     */
    public Main() {
        // Default constructor.
    }

    /**
     * Main method - entry point for the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
