package no.ntnu.idatx2003.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.math.BigDecimal;

/**
 * Displays the game title and key player metrics.
 */
public class HeaderView {
    private final VBox root;
    private final Label weekLabel;
    private final Label cashLabel;
    private final Label portfolioValueLabel;
    private final Label netWorthLabel;
    private final Label statusLabel;

    /**
     * Creates the header view.
     */
    public HeaderView() {
        Label title = new Label("Millions");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: 700;");

        Label subtitle = new Label("Stock trading game");
        subtitle.setTextFill(Color.web("#5d6673"));

        weekLabel = metricLabel();
        cashLabel = metricLabel();
        portfolioValueLabel = metricLabel();
        netWorthLabel = metricLabel();
        statusLabel = metricLabel();

        GridPane metrics = createMetrics();
        HBox header = new HBox(24, new VBox(2, title, subtitle), metrics);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 22, 16, 22));
        HBox.setHgrow(metrics, Priority.ALWAYS);

        root = new VBox(header, new Separator());
    }

    /**
     * Returns the JavaFX root node for this header.
     *
     * @return the root node
     */
    public VBox getRoot() {
        return root;
    }

    /**
     * Updates the metric labels.
     *
     * @param week the current week
     * @param cash the player's cash balance
     * @param portfolioValue the current portfolio value
     * @param netWorth the player's net worth
     * @param status the player status text
     */
    public void update(int week, BigDecimal cash, BigDecimal portfolioValue, BigDecimal netWorth, String status) {
        weekLabel.setText(Integer.toString(week));
        cashLabel.setText(ViewFormat.money(cash));
        portfolioValueLabel.setText(ViewFormat.money(portfolioValue));
        netWorthLabel.setText(ViewFormat.money(netWorth));
        statusLabel.setText(status);
    }

    private GridPane createMetrics() {
        GridPane metrics = new GridPane();
        metrics.setHgap(18);
        metrics.setVgap(4);
        metrics.add(new Label("Week"), 0, 0);
        metrics.add(weekLabel, 0, 1);
        metrics.add(new Label("Cash"), 1, 0);
        metrics.add(cashLabel, 1, 1);
        metrics.add(new Label("Portfolio"), 2, 0);
        metrics.add(portfolioValueLabel, 2, 1);
        metrics.add(new Label("Net worth"), 3, 0);
        metrics.add(netWorthLabel, 3, 1);
        metrics.add(new Label("Status"), 4, 0);
        metrics.add(statusLabel, 4, 1);
        return metrics;
    }

    private static Label metricLabel() {
        Label label = new Label();
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: 700;");
        return label;
    }
}
