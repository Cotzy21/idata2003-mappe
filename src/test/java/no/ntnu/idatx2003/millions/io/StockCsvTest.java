package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.exception.InvalidStockDataException;
import no.ntnu.idatx2003.millions.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CSV stock reader and writer")
class StockCsvTest {
    private StockDataReader reader;
    private StockDataWriter writer;
    private List<Stock> stocks;

    @BeforeEach
    void setUp() {
        reader = new CsvStockReader();
        writer = new CsvStockWriter();
        stocks = List.of(
                new Stock("AAPL", "Apple Inc.", new BigDecimal("150.50")),
                new Stock("MSFT", "Microsoft", new BigDecimal("300.75")));
    }

    @DisplayName("read parses valid stock data from a Reader")
    @Test
    void read_whenCsvIsValid_returnsStocks() throws IOException, InvalidStockDataException {
        String csv = "# Ticker,Name,Price\n"
                + "\n"
                + "AAPL,Apple Inc.,150.50\n"
                + "MSFT,Microsoft,300.75\n";

        List<Stock> parsedStocks = reader.read(new StringReader(csv));

        assertEquals(2, parsedStocks.size());
        assertEquals("AAPL", parsedStocks.getFirst().getSymbol());
        assertEquals("Apple Inc.", parsedStocks.getFirst().getCompany());
        assertEquals(0, new BigDecimal("150.50").compareTo(parsedStocks.getFirst().getSalesPrice()));
    }

    @DisplayName("read ignores comments and blank lines")
    @Test
    void read_whenCsvHasCommentsAndBlankLines_ignoresThem() throws IOException, InvalidStockDataException {
        String csv = "# This is a comment\n"
                + "# Ticker,Name,Price\n"
                + "\n"
                + "AAPL,Apple Inc.,150.50\n"
                + "\n"
                + "MSFT,Microsoft,300.75\n";

        List<Stock> parsedStocks = reader.read(new StringReader(csv));

        assertEquals(2, parsedStocks.size());
    }

    @DisplayName("read throws InvalidStockDataException for wrong field count")
    @Test
    void read_whenCsvHasWrongFieldCount_throwsInvalidStockDataException() {
        String csv = "AAPL,Apple Inc.,150.50,Extra\n";

        assertThrows(InvalidStockDataException.class, () -> reader.read(new StringReader(csv)));
    }

    @DisplayName("read throws InvalidStockDataException for invalid price")
    @Test
    void read_whenPriceIsInvalid_throwsInvalidStockDataException() {
        String csv = "AAPL,Apple Inc.,notanumber\n";

        assertThrows(InvalidStockDataException.class, () -> reader.read(new StringReader(csv)));
    }

    @DisplayName("read throws InvalidStockDataException for negative price")
    @Test
    void read_whenPriceIsNegative_throwsInvalidStockDataException() {
        String csv = "AAPL,Apple Inc.,-150.50\n";

        assertThrows(InvalidStockDataException.class, () -> reader.read(new StringReader(csv)));
    }

    @DisplayName("read throws InvalidStockDataException for blank symbol")
    @Test
    void read_whenSymbolIsBlank_throwsInvalidStockDataException() {
        String csv = " ,Apple Inc.,150.50\n";

        assertThrows(InvalidStockDataException.class, () -> reader.read(new StringReader(csv)));
    }

    @DisplayName("write creates properly formatted CSV")
    @Test
    void write_whenStocksAreValid_writesCsv() throws IOException {
        StringWriter output = new StringWriter();

        writer.write(output, stocks);

        String csv = output.toString();
        List<String> lines = csv.lines().toList();
        assertEquals("# Stock Exchange Data", lines.get(0));
        assertEquals("# Ticker,Name,Price", lines.get(1));
        assertEquals("AAPL,Apple Inc.,150.50", lines.get(3));
        assertEquals("MSFT,Microsoft,300.75", lines.get(4));
    }
}
