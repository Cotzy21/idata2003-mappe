package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Stock;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Strategy interface for writing stock data to a character stream.
 *
 * <p>This is the write half of the <em>Strategy</em> pattern used for stock
 * file I/O. The concrete strategy ({@link CsvStockWriter} today) is selected
 * by the {@link no.ntnu.idatx2003.millions.controller.MainController} and
 * passed into the controllers, so introducing a new format does not require
 * touching any caller.</p>
 *
 * @see StockDataReader
 * @see CsvStockWriter
 */
public interface StockDataWriter {

    /**
     * Writes stock data to the given writer.
     *
     * @param writer the writer that receives stock data; must not be {@code null}
     * @param stocks the stocks to write; must not be {@code null}
     * @throws IOException if the stream cannot be written
     */
    void write(Writer writer, List<Stock> stocks) throws IOException;
}
