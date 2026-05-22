package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.model.Stock;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Defines a strategy for reading stock data from a character stream.
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
