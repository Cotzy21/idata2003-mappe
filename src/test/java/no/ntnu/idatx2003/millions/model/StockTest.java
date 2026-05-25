package no.ntnu.idatx2003.millions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Stock Tests")
class StockTest {
    private Stock stock;

    @BeforeEach
    void setUp() {
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
    }

    @DisplayName("Constructor: creates stock with valid inputs")
    @Test
    void testConstructor_validInputs() {
        assertEquals("AAPL", stock.getSymbol());
        assertEquals("Apple Inc.", stock.getCompany());
        assertEquals(0, stock.getSalesPrice().compareTo(new BigDecimal("150.00")));
    }

    @DisplayName("Constructor: throws exception for null symbol")
    @Test
    void testConstructor_nullSymbol() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stock(null, "Apple", new BigDecimal("150")));
    }

    @DisplayName("Constructor: throws exception for blank symbol")
    @Test
    void testConstructor_blankSymbol() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stock("", "Apple", new BigDecimal("150")));
    }

    @Test
    void constructor_whenSymbolIsBlank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stock("   ", "Apple", new BigDecimal("150")));
    }

    @DisplayName("Constructor: throws exception for null company")
    @Test
    void testConstructor_nullCompany() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stock("AAPL", null, new BigDecimal("150")));
    }

    @DisplayName("Constructor: throws exception for negative price")
    @Test
    void testConstructor_negativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stock("AAPL", "Apple", new BigDecimal("-10")));
    }

    @Test
    void constructor_whenInitialPriceIsZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stock("AAPL", "Apple", BigDecimal.ZERO));
    }

    @Test
    void constructor_whenInitialPriceIsNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Stock("AAPL", "Apple", new BigDecimal("-0.01")));
    }

    @DisplayName("getSalesPrice: returns latest price")
    @Test
    void testGetSalesPrice() {
        assertEquals(0, stock.getSalesPrice().compareTo(new BigDecimal("150.00")));
    }

    @DisplayName("addNewSalesPrice: adds price to history")
    @Test
    void testAddNewSalesPrice() {
        stock.addNewSalesPrice(new BigDecimal("155.00"));
        assertEquals(0, stock.getSalesPrice().compareTo(new BigDecimal("155.00")));
    }

    @DisplayName("addNewSalesPrice: throws exception for negative price")
    @Test
    void testAddNewSalesPrice_negativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.addNewSalesPrice(new BigDecimal("-10")));
    }

    @Test
    void addNewSalesPrice_whenPriceIsNegative_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.addNewSalesPrice(new BigDecimal("-0.01")));
    }

    @DisplayName("getHistoricalPrices: returns all prices")
    @Test
    void testGetHistoricalPrices() {
        stock.addNewSalesPrice(new BigDecimal("155.00"));
        stock.addNewSalesPrice(new BigDecimal("160.00"));

        List<BigDecimal> prices = stock.getHistoricalPrices();
        assertEquals(3, prices.size());
        assertEquals(0, prices.get(0).compareTo(new BigDecimal("150.00")));
        assertEquals(0, prices.get(2).compareTo(new BigDecimal("160.00")));
    }

    @DisplayName("getHighestPrice: returns maximum price")
    @Test
    void testGetHighestPrice() {
        stock.addNewSalesPrice(new BigDecimal("145.00"));
        stock.addNewSalesPrice(new BigDecimal("165.00"));
        stock.addNewSalesPrice(new BigDecimal("155.00"));

        assertEquals(0, stock.getHighestPrice().compareTo(new BigDecimal("165.00")));
    }

    @DisplayName("getLowestPrice: returns minimum price")
    @Test
    void testGetLowestPrice() {
        stock.addNewSalesPrice(new BigDecimal("145.00"));
        stock.addNewSalesPrice(new BigDecimal("165.00"));

        assertEquals(0, stock.getLowestPrice().compareTo(new BigDecimal("145.00")));
    }

    @DisplayName("getLatestPriceChange: returns difference between last two prices")
    @Test
    void testGetLatestPriceChange() {
        stock.addNewSalesPrice(new BigDecimal("155.00"));
        BigDecimal change = stock.getLatestPriceChange();
        assertEquals(0, change.compareTo(new BigDecimal("5.00")));
    }

    @DisplayName("getLatestPriceChange: returns zero if only one price")
    @Test
    void testGetLatestPriceChange_singlePrice() {
        assertEquals(0, stock.getLatestPriceChange().compareTo(BigDecimal.ZERO));
    }

    @Test
    void getLatestPriceChange_whenOnlyOnePrice_returnsZero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(stock.getLatestPriceChange()));
    }
}
