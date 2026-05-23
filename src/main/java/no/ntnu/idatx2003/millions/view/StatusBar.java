package no.ntnu.idatx2003.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Displays application status and error messages.
 */
public class StatusBar {
    private final HBox root;
    private final Label messageLabel;

    /**
     * Creates the status bar.
     */
    public StatusBar() {
        messageLabel = new Label("Ready.");
        messageLabel.setTextFill(Color.web("#384150"));

        root = new HBox(messageLabel);
        root.setPadding(new Insets(10, 18, 12, 18));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dde1e7; -fx-border-width: 1 0 0 0;");
    }

    /**
     * Returns the JavaFX root node for this status bar.
     *
     * @return the root node
     */
    public HBox getRoot() {
        return root;
    }

    /**
     * Shows a message.
     *
     * @param message the message to show
     * @param error whether the message represents an error
     */
    public void showMessage(String message, boolean error) {
        messageLabel.setText(message == null || message.isBlank() ? "No details available." : message);
        messageLabel.setTextFill(error ? Color.web("#b42318") : Color.web("#1f6f43"));
    }
}
