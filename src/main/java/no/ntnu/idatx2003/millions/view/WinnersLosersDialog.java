package no.ntnu.idatx2003.millions.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import no.ntnu.idatx2003.millions.model.Exchange;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.observer.Observer;
import no.ntnu.idatx2003.millions.util.Validate;

import java.util.List;

/**
 * Dialog showing the current top gainers and losers in the market.
 */
public final class WinnersLosersDialog extends Dialog<Void> implements Observer<Exchange> {
    private static final PseudoClass GAINER = PseudoClass.getPseudoClass("gainer");
    private static final PseudoClass LOSER = PseudoClass.getPseudoClass("loser");

    private final Exchange exchange;
    private final ObservableList<Stock> gainerRows = FXCollections.observableArrayList();
    private final ObservableList<Stock> loserRows = FXCollections.observableArrayList();

    /**
     * Creates a market overview dialog.
     *
     * @param exchange the exchange to observe; must not be {@code null}
     */
    public WinnersLosersDialog(Exchange exchange) {
        this.exchange = Validate.requireNonNull(exchange, "exchange");
        setTitle("Markedet");
        setHeaderText("Ukens vinnere og tapere");
        getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
        getDialogPane().setContent(createContent());
        setOnShown(event -> {
            this.exchange.addObserver(this);
            refresh();
        });
        setOnHidden(event -> this.exchange.removeObserver(this));
        refresh();
    }

    @Override
    public void update(Exchange updatedExchange) {
        refresh();
    }

    private HBox createContent() {
        TableView<Stock> gainers = createStockTable(gainerRows, GAINER, "gainer");
        TableView<Stock> losers = createStockTable(loserRows, LOSER, "loser");

        VBox gainersBox = new VBox(8, sectionTitle("Vinnere"), gainers);
        VBox losersBox = new VBox(8, sectionTitle("Tapere"), losers);
        HBox.setHgrow(gainersBox, Priority.ALWAYS);
        HBox.setHgrow(losersBox, Priority.ALWAYS);
        VBox.setVgrow(gainers, Priority.ALWAYS);
        VBox.setVgrow(losers, Priority.ALWAYS);

        HBox content = new HBox(14, gainersBox, losersBox);
        content.setPadding(new Insets(8, 4, 4, 4));
        content.setPrefWidth(760);
        content.setPrefHeight(320);
        return content;
    }

    private TableView<Stock> createStockTable(ObservableList<Stock> rows, PseudoClass rowClass, String styleClass) {
        TableView<Stock> table = new TableView<>(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("Ingen data"));

        TableColumn<Stock, String> symbol = TableColumns.column("Symbol", Stock::getSymbol, 0.18);
        TableColumn<Stock, String> company = TableColumns.column("Selskap", Stock::getCompany, 0.42);
        TableColumn<Stock, String> price = TableColumns.column("Pris",
                stock -> ViewFormat.money(stock.getSalesPrice()), 0.20);
        TableColumn<Stock, String> change = TableColumns.column("Endring",
                stock -> ViewFormat.signedMoney(stock.getLatestPriceChange()), 0.20);

        table.getColumns().setAll(List.of(symbol, company, price, change));
        table.setRowFactory(unused -> {
            TableRow<Stock> row = new TableRow<>();
            row.getStyleClass().add(styleClass);
            row.pseudoClassStateChanged(rowClass, true);
            return row;
        });
        return table;
    }

    private void refresh() {
        gainerRows.setAll(exchange.getGainers(5));
        loserRows.setAll(exchange.getLosers(5));
    }

    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }
}
