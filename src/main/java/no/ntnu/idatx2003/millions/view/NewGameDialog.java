package no.ntnu.idatx2003.millions.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.io.StockDataReader;
import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.util.Validate;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Startup dialog that lets the player choose a name, starting capital and
 * stock data source before the main window opens.
 *
 * <p>The dialog uses dependency injection for the {@link StockDataReader}
 * strategy so the file-picker branch can be exercised in tests via a
 * {@link Reader}-based fake.</p>
 */
public class NewGameDialog extends Dialog<NewGameResult> {

    private final TextField nameField = new TextField();
    private final TextField startingMoneyField = new TextField();
    private final Label fileLabel = new Label("(default stocks)");
    private final Label validationLabel = new Label();

    private final StockDataReader stockDataReader;
    private final Supplier<List<Stock>> defaultStocksSupplier;

    private File chosenFile;
    private List<Stock> resolvedStocks;
    private String fileError;

    /**
     * Creates a new dialog instance.
     *
     * @param stockDataReader the reader strategy used to parse user-chosen
     *     CSV files; must not be {@code null}
     * @param defaultStocksSupplier supplier for the built-in default stock
     *     list used when the user does not pick a file; must not be {@code null}
     * @param suggestedName initial value for the player name field
     * @param suggestedStartingMoney initial value for the starting money field
     */
    public NewGameDialog(StockDataReader stockDataReader,
                         Supplier<List<Stock>> defaultStocksSupplier,
                         String suggestedName,
                         BigDecimal suggestedStartingMoney) {
        this.stockDataReader = Validate.requireNonNull(stockDataReader, "stockDataReader");
        this.defaultStocksSupplier = Validate.requireNonNull(defaultStocksSupplier, "defaultStocksSupplier");

        setTitle("Millions - new game");
        setHeaderText("Set up your trading session");

        nameField.setText(suggestedName == null ? "Trader" : suggestedName);
        startingMoneyField.setText(suggestedStartingMoney == null
                ? "100000.00"
                : ViewFormat.money(suggestedStartingMoney));
        resolvedStocks = defaultStocksSupplier.get();

        getDialogPane().setContent(buildContent());
        getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Start game");
        BooleanBinding invalid = createInvalidBinding();
        okButton.disableProperty().bind(invalid);

        setResultConverter(buttonType -> buttonType == ButtonType.OK ? buildResult() : null);
        setOnShown(event -> Platform.runLater(nameField::requestFocus));
    }

    private GridPane buildContent() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(8, 4, 4, 4));

        nameField.setPromptText("Player name");
        startingMoneyField.setPromptText("Starting capital");
        startingMoneyField.setTextFormatter(new TextFormatter<>(decimalFilter()));

        Button chooseFileButton = new Button("Choose CSV file...");
        chooseFileButton.setOnAction(event -> chooseFile());
        Button defaultStocksButton = new Button("Use default stocks");
        defaultStocksButton.setOnAction(event -> resetToDefaults());

        HBox fileRow = new HBox(8, chooseFileButton, defaultStocksButton);
        fileRow.setAlignment(Pos.CENTER_LEFT);

        validationLabel.getStyleClass().add("muted-label");
        validationLabel.setWrapText(true);

        grid.add(new Label("Player name"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Starting money"), 0, 1);
        grid.add(startingMoneyField, 1, 1);
        grid.add(new Label("Stocks"), 0, 2);
        grid.add(fileRow, 1, 2);
        grid.add(new Label("Selected file"), 0, 3);
        grid.add(fileLabel, 1, 3);
        grid.add(validationLabel, 0, 4, 2, 1);

        nameField.textProperty().addListener((obs, oldValue, newValue) -> refreshValidationMessage());
        startingMoneyField.textProperty().addListener((obs, oldValue, newValue) -> refreshValidationMessage());

        refreshValidationMessage();
        return grid;
    }

    private BooleanBinding createInvalidBinding() {
        return Bindings.createBooleanBinding(this::hasValidationProblem,
                nameField.textProperty(),
                startingMoneyField.textProperty());
    }

    private boolean hasValidationProblem() {
        return parsePlayerName().isEmpty()
                || parseStartingMoney().isEmpty()
                || fileError != null;
    }

    private void refreshValidationMessage() {
        if (parsePlayerName().isEmpty()) {
            validationLabel.setText("Player name must not be blank.");
            return;
        }
        if (parseStartingMoney().isEmpty()) {
            validationLabel.setText("Starting money must be a positive number.");
            return;
        }
        if (fileError != null) {
            validationLabel.setText(fileError);
            return;
        }
        validationLabel.setText("Loaded " + resolvedStocks.size() + " stock(s).");
    }

    private Optional<String> parsePlayerName() {
        String value = nameField.getText();
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }

    private Optional<BigDecimal> parseStartingMoney() {
        String value = startingMoneyField.getText();
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return Optional.empty();
            }
            return Optional.of(amount);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private void chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose stock CSV file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        Node owner = getDialogPane().getScene() == null ? null : getDialogPane();
        File file = chooser.showOpenDialog(owner == null ? null : owner.getScene().getWindow());
        if (file == null) {
            return;
        }
        loadFile(file);
    }

    private void loadFile(File file) {
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<Stock> stocks = stockDataReader.read(reader);
            if (stocks.isEmpty()) {
                fileError = "CSV file contains no stocks.";
                resolvedStocks = defaultStocksSupplier.get();
            } else {
                chosenFile = file;
                resolvedStocks = stocks;
                fileLabel.setText(file.getName() + " (" + stocks.size() + " stocks)");
                fileError = null;
            }
        } catch (IOException | InvalidStockDataException exception) {
            fileError = exception.getMessage();
            resolvedStocks = defaultStocksSupplier.get();
        }
        refreshValidationMessage();
    }

    private void resetToDefaults() {
        chosenFile = null;
        resolvedStocks = defaultStocksSupplier.get();
        fileLabel.setText("(default stocks)");
        fileError = null;
        refreshValidationMessage();
    }

    private NewGameResult buildResult() {
        String name = parsePlayerName().orElseThrow();
        BigDecimal money = parseStartingMoney().orElseThrow();
        return new NewGameResult(name, money, List.copyOf(resolvedStocks), chosenFile);
    }

    private static UnaryOperator<TextFormatter.Change> decimalFilter() {
        return change -> {
            String text = change.getControlNewText();
            return text.matches("\\d*(\\.\\d*)?") ? change : null;
        };
    }

    /**
     * Returns the {@link ButtonBar.ButtonData#OK_DONE} dialog button used by
     * accelerator handling. Exposed mainly for tests.
     *
     * @return the OK button
     */
    public Button getOkButton() {
        return (Button) getDialogPane().lookupButton(ButtonType.OK);
    }
}
