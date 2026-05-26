package no.ntnu.idatx2003.millions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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

        Scene scene = new Scene(view.getRoot(), 1180, 760);
        java.net.URL stylesheet = App.class.getResource("/styles/millions.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Millions - Stock Trading Game");
        stage.setScene(scene);
        stage.setMinWidth(840);
        stage.setMinHeight(640);
        stage.show();

        installAccelerators(scene, controller);
        controller.initialize();
        view.requestQuantityFocus();
    }

    private void installAccelerators(Scene scene, MainController controller) {
        scene.getAccelerators().put(shortcut(KeyCode.B), controller::onBuySelectedStock);
        scene.getAccelerators().put(shortcut(KeyCode.S), controller::onSellSelectedShare);
        scene.getAccelerators().put(shortcut(KeyCode.N), controller::onAdvanceWeek);
        scene.getAccelerators().put(shortcut(KeyCode.O), controller::onLoadStocks);
        scene.getAccelerators().put(shortcutShift(KeyCode.S), controller::onSaveStocks);
        scene.getAccelerators().put(shortcut(KeyCode.Q), controller::onLiquidateAndExit);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F1), this::showHelp);
    }

    private static KeyCodeCombination shortcut(KeyCode keyCode) {
        return new KeyCodeCombination(keyCode, KeyCombination.SHORTCUT_DOWN);
    }

    private static KeyCodeCombination shortcutShift(KeyCode keyCode) {
        return new KeyCodeCombination(keyCode,
                KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    }

    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Millions help");
        alert.setHeaderText("Millions");
        alert.setContentText("Kjøp og selg aksjer, gå frem uke for uke, og følg portefølje og transaksjoner.");
        alert.showAndWait();
    }
}
