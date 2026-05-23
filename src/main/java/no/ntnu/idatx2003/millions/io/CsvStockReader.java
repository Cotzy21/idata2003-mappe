package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.model.Stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads stocks from CSV data.
 * <p>
 * Blank lines and lines beginning with <code>#</code> are ignored. Data lines
 * must use the format <code>Ticker,Name,Price</code>.
 */
public class CsvStockReader implements StockDataReader {
    private static final Logger LOG = Logger.getLogger(CsvStockReader.class.getName());
    private static final int FIELD_COUNT = 3;

    /**
     * Creates a CSV stock reader.
     */
    public CsvStockReader() {
        // Public constructor for use through the StockDataReader strategy interface.
    }

    /**
     * Reads stocks from a CSV file.
     *
     * @param file the CSV file; must not be {@code null}
     * @return the parsed stocks, never {@code null}
     * @throws IOException if the file cannot be read
     * @throws InvalidStockDataException if the file contains invalid stock data
     */
    public List<Stock> read(File file) throws IOException, InvalidStockDataException {
        Objects.requireNonNull(file, "file must not be null");
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            return read(reader);
        } catch (IOException | InvalidStockDataException exception) {
            LOG.log(Level.WARNING, "Could not read stock data from " + file, exception);
            throw exception;
        }
    }

    /**
     * Reads stocks from CSV data.
     *
     * @param reader the reader that provides CSV data; must not be {@code null}
     * @return the parsed stocks, never {@code null}
     * @throws IOException if the stream cannot be read
     * @throws InvalidStockDataException if the stream contains invalid stock data
     */
    @Override
    public List<Stock> read(Reader reader) throws IOException, InvalidStockDataException {
        Objects.requireNonNull(reader, "reader must not be null");
        BufferedReader bufferedReader = reader instanceof BufferedReader existingReader
                ? existingReader
                : new BufferedReader(reader);
        List<Stock> stocks = new ArrayList<>();
        String line;
        int lineNumber = 0;

        while ((line = bufferedReader.readLine()) != null) {
            lineNumber++;
            if (isIgnoredLine(line)) {
                continue;
            }
            stocks.add(parseCsvLine(line, lineNumber));
        }

        return List.copyOf(stocks);
    }

    private boolean isIgnoredLine(String line) {
        String trimmedLine = line.trim();
        return trimmedLine.isBlank() || trimmedLine.startsWith("#");
    }

    private Stock parseCsvLine(String line, int lineNumber) throws InvalidStockDataException {
        String[] fields = line.split(",", -1);
        if (fields.length != FIELD_COUNT) {
            throw invalidLine(lineNumber, "expected 3 fields but found " + fields.length);
        }

        String symbol = fields[0].trim();
        String company = fields[1].trim();
        String priceText = fields[2].trim();
        validateTextField(symbol, "symbol", lineNumber);
        validateTextField(company, "company", lineNumber);
        return new Stock(symbol, company, parsePrice(priceText, lineNumber));
    }

    private void validateTextField(String value, String fieldName, int lineNumber)
            throws InvalidStockDataException {
        if (value.isBlank()) {
            throw invalidLine(lineNumber, fieldName + " must not be blank");
        }
    }

    private BigDecimal parsePrice(String priceText, int lineNumber) throws InvalidStockDataException {
        try {
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw invalidLine(lineNumber, "price must be greater than zero");
            }
            return price;
        } catch (NumberFormatException exception) {
            throw invalidLine(lineNumber, "invalid price: " + priceText, exception);
        }
    }

    private InvalidStockDataException invalidLine(int lineNumber, String message) {
        return new InvalidStockDataException("Invalid CSV stock data at line " + lineNumber + ": " + message);
    }

    private InvalidStockDataException invalidLine(int lineNumber, String message, Throwable cause) {
        return new InvalidStockDataException(
                "Invalid CSV stock data at line " + lineNumber + ": " + message, cause);
    }
}
