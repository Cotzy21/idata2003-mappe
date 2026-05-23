package no.ntnu.idatx2003.millions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import no.ntnu.idatx2003.millions.controller.MainController;
import no.ntnu.idatx2003.millions.view.MainView;

/**
 * JavaFX application entry point for the Millions stock trading game.
 */
public class App extends Application {

    /**
     * Creates the JavaFX application.
     */
    public App() {
        // Required by JavaFX.
    }

    /**
     * Starts the JavaFX application and connects the main view to its controller.
     *
     * @param stage the primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        MainView view = new MainView();
        MainController controller = new MainController(view);
        controller.initialize();

        stage.setTitle("Millions - Stock Trading Game");
        stage.setScene(new Scene(view.getRoot(), 1180, 760));
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.show();
    }
}
