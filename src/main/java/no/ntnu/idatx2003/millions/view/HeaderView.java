package no.ntnu.idatx2003.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Stock trading game");
        subtitle.getStyleClass().add("app-subtitle");

        weekLabel = metricLabel();
        cashLabel = metricLabel();
        portfolioValueLabel = metricLabel();
        netWorthLabel = metricLabel();
        statusLabel = metricLabel();

        FlowPane metrics = createMetrics();
        HBox header = new HBox(24, new VBox(2, title, subtitle), metrics);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 22, 16, 22));
        HBox.setHgrow(metrics, Priority.ALWAYS);

        root = new VBox(header, new Separator());
        root.getStyleClass().add("app-header");
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

    private FlowPane createMetrics() {
        FlowPane metrics = new FlowPane(22, 8);
        metrics.setAlignment(Pos.CENTER_LEFT);
        metrics.getChildren().addAll(
                metricItem("Week", weekLabel),
                metricItem("Cash", cashLabel),
                metricItem("Portfolio", portfolioValueLabel),
                metricItem("Net worth", netWorthLabel),
                metricItem("Status", statusLabel));
        return metrics;
    }

    private static VBox metricItem(String name, Label valueLabel) {
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("metric-label");
        VBox item = new VBox(3, nameLabel, valueLabel);
        item.setMinWidth(Region.USE_PREF_SIZE);
        return item;
    }

    private static Label metricLabel() {
        Label label = new Label();
        label.getStyleClass().add("metric-value");
        label.setMinWidth(Region.USE_PREF_SIZE);
        return label;
    }
}
