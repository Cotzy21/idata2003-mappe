package no.ntnu.idatx2003.millions.io;

import no.ntnu.idatx2003.millions.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CSV Reader/Writer Tests")
class StockCsvTest {
    @TempDir
    Path tempDir;

    private List<Stock> testStocks;

    @BeforeEach
    void setUp() {
        testStocks = new ArrayList<>();
        testStocks.add(new Stock("AAPL", "Apple Inc.", new BigDecimal("150.50")));
        testStocks.add(new Stock("MSFT", "Microsoft", new BigDecimal("300.75")));
    }

    @DisplayName("Write and read: preserves stock data")
    @Test
    void testWriteAndRead() throws IOException {
        Path csvFile = tempDir.resolve("stocks.csv");
        StockCsvWriter.writeStocks(csvFile.toString(), testStocks);

        List<Stock> readStocks = StockCsvReader.readStocks(csvFile.toFile());

        assertEquals(2, readStocks.size());
        assertEquals("AAPL", readStocks.get(0).getSymbol());
        assertEquals("Apple Inc.", readStocks.get(0).getCompany());
        assertEquals(0, readStocks.get(0).getSalesPrice().compareTo(new BigDecimal("150.50")));
    }

    @DisplayName("Read: handles comments and blank lines")
    @Test
    void testRead_commentsAndBlankLines() throws IOException {
        Path csvFile = tempDir.resolve("stocks_with_comments.csv");
        String content = "# This is a comment\n" +
                "# Ticker,Name,Price\n" +
                "\n" +
                "AAPL,Apple Inc.,150.50\n" +
                "\n" +
                "MSFT,Microsoft,300.75\n";
        Files.write(csvFile, content.getBytes());

        List<Stock> stocks = StockCsvReader.readStocks(csvFile.toFile());

        assertEquals(2, stocks.size());
    }

    @DisplayName("Read: throws exception for invalid format")
    @Test
    void testRead_invalidFormat() throws IOException {
        Path csvFile = tempDir.resolve("invalid.csv");
        String content = "AAPL,Apple Inc.,150.50,Extra\n"; // Too many fields
        Files.write(csvFile, content.getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> StockCsvReader.readStocks(csvFile.toFile()));
    }

    @DisplayName("Read: throws exception for invalid price")
    @Test
    void testRead_invalidPrice() throws IOException {
        Path csvFile = tempDir.resolve("invalid_price.csv");
        String content = "AAPL,Apple Inc.,notanumber\n";
        Files.write(csvFile, content.getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> StockCsvReader.readStocks(csvFile.toFile()));
    }

    @DisplayName("Read: throws exception for negative price")
    @Test
    void testRead_negativePrice() throws IOException {
        Path csvFile = tempDir.resolve("negative_price.csv");
        String content = "AAPL,Apple Inc.,-150.50\n";
        Files.write(csvFile, content.getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> StockCsvReader.readStocks(csvFile.toFile()));
    }

    @DisplayName("Write: creates properly formatted file")
    @Test
    void testWrite_format() throws IOException {
        Path csvFile = tempDir.resolve("test_output.csv");
        StockCsvWriter.writeStocks(csvFile.toString(), testStocks);

        List<String> lines = Files.readAllLines(csvFile);

        // Should have header (3 lines) + 2 stocks + 1 blank = 6 lines, but actual is 5
        // Header and stocks only, no trailing blank line
        assertEquals(5, lines.size());
        // Check first stock line (after blank line from header)
        assertEquals("AAPL,Apple Inc.,150.50", lines.get(3));
    }
}


