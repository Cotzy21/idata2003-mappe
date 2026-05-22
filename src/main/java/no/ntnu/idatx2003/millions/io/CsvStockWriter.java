package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Stock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes stocks as CSV data.
 */
public class CsvStockWriter implements StockDataWriter {
    private static final Logger LOG = Logger.getLogger(CsvStockWriter.class.getName());

    /**
     * Creates a CSV stock writer.
     */
    public CsvStockWriter() {
        // Public constructor for use through the StockDataWriter strategy interface.
    }

    /**
     * Writes stocks to a CSV file.
     *
     * @param file the output file; must not be {@code null}
     * @param stocks the stocks to write; must not be {@code null}
     * @throws IOException if the file cannot be written
     */
    public void write(File file, List<Stock> stocks) throws IOException {
        Objects.requireNonNull(file, "file must not be null");
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            write(writer, stocks);
        } catch (IOException exception) {
            LOG.log(Level.WARNING, "Could not write stock data to " + file, exception);
            throw exception;
        }
    }

    /**
     * Writes stocks as CSV data.
     *
     * @param writer the writer that receives CSV data; must not be {@code null}
     * @param stocks the stocks to write; must not be {@code null}
     * @throws IOException if the stream cannot be written
     */
    @Override
    public void write(Writer writer, List<Stock> stocks) throws IOException {
        Objects.requireNonNull(writer, "writer must not be null");
        Objects.requireNonNull(stocks, "stocks must not be null");

        writer.write("# Stock Exchange Data");
        writer.write(System.lineSeparator());
        writer.write("# Ticker,Name,Price");
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());

        for (Stock stock : stocks) {
            writeStock(writer, Objects.requireNonNull(stock, "stocks must not contain null"));
        }
    }

    private void writeStock(Writer writer, Stock stock) throws IOException {
        writer.write(String.format(Locale.US, "%s,%s,%s%n",
                stock.getSymbol(),
                stock.getCompany(),
                stock.getSalesPrice()));
    }
}
