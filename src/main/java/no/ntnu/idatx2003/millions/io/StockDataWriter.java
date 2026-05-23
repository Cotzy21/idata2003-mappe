package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Stock;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Defines a strategy for writing stock data to a character stream.
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
