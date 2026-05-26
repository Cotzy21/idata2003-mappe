package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.model.Stock;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Strategy interface for reading stock data from a character stream.
 *
 * <p>This is the read half of the <em>Strategy</em> pattern used for stock
 * file I/O. The concrete strategy ({@link CsvStockReader} today; a future
 * {@code JsonStockReader} would slot in identically) is chosen by the
 * {@link no.ntnu.idatx2003.millions.controller.MainController} when the
 * application starts up, and is passed into the controllers via constructor
 * injection. Call sites depend only on this interface, so adding a new
 * format does not require modifying the controllers or the model.</p>
 *
 * @see StockDataWriter
 * @see CsvStockReader
 */
public interface StockDataReader {

    /**
     * Reads stock data from the given reader.
     *
     * @param reader the reader that provides stock data; must not be {@code null}
     * @return the parsed stocks, never {@code null}
     * @throws IOException if the stream cannot be read
     * @throws InvalidStockDataException if the data format is invalid
     */
    List<Stock> read(Reader reader) throws IOException, InvalidStockDataException;
}
