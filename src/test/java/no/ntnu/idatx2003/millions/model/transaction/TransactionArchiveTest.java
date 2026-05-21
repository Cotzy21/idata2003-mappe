package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Stock;
import no.ntnu.idatx2003.millions.model.Share;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("TransactionArchive Tests")
class TransactionArchiveTest {
    private TransactionArchive archive;
    private Stock stock;

    @BeforeEach
    void setUp() {
        archive = new TransactionArchive();
        stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00"));
    }

    @DisplayName("Constructor: creates empty archive")
    @Test
    void testConstructor() {
        assertTrue(archive.isEmpty());
    }

    @DisplayName("add: adds transaction successfully")
    @Test
    void testAdd_success() {
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        Purchase purchase = TransactionFactory.createPurchase(share, 1);
        assertTrue(archive.add(purchase));
        assertFalse(archive.isEmpty());
    }

    @DisplayName("add: returns false for null")
    @Test
    void testAdd_null() {
        assertFalse(archive.add(null));
    }

    @DisplayName("isEmpty: returns true for empty archive")
    @Test
    void testIsEmpty_true() {
        assertTrue(archive.isEmpty());
    }

    @DisplayName("isEmpty: returns false when transactions present")
    @Test
    void testIsEmpty_false() {
        Share share = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        Purchase purchase = TransactionFactory.createPurchase(share, 1);
        archive.add(purchase);
        assertFalse(archive.isEmpty());
    }

    @DisplayName("getTransactions: returns all transactions")
    @Test
    void testGetTransactions() {
        Share share1 = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("149.00"));
        Purchase purchase = TransactionFactory.createPurchase(share1, 1);
        Sale sale = TransactionFactory.createSale(share2, 2);

        archive.add(purchase);
        archive.add(sale);

        List<Transaction> transactions = archive.getTransactions();
        assertEquals(2, transactions.size());
    }

    @DisplayName("getTransactions: returns transactions for specific week")
    @Test
    void testGetTransactions_byWeek() {
        Share share1 = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("149.00"));
        Purchase purchase1 = TransactionFactory.createPurchase(share1, 1);
        Purchase purchase2 = TransactionFactory.createPurchase(share2, 1);
        Sale sale = TransactionFactory.createSale(share1, 2);

        archive.add(purchase1);
        archive.add(purchase2);
        archive.add(sale);

        List<Transaction> week1Transactions = archive.getTransactions(1);
        assertEquals(2, week1Transactions.size());

        List<Transaction> week2Transactions = archive.getTransactions(2);
        assertEquals(1, week2Transactions.size());
    }

    @DisplayName("getPurchases: returns purchases for specific week")
    @Test
    void testGetPurchases() {
        Share share1 = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("149.00"));
        Purchase purchase = TransactionFactory.createPurchase(share1, 1);
        Sale sale = TransactionFactory.createSale(share2, 1);

        archive.add(purchase);
        archive.add(sale);

        List<Transaction> purchases = archive.getPurchases(1);
        assertEquals(1, purchases.size());
        assertTrue(purchases.get(0) instanceof Purchase);
    }

    @DisplayName("getSales: returns sales for specific week")
    @Test
    void testGetSales() {
        Share share1 = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        Share share2 = new Share(stock, new BigDecimal("5"), new BigDecimal("149.00"));
        Purchase purchase = TransactionFactory.createPurchase(share1, 1);
        Sale sale = TransactionFactory.createSale(share2, 1);

        archive.add(purchase);
        archive.add(sale);

        List<Transaction> sales = archive.getSales(1);
        assertEquals(1, sales.size());
        assertTrue(sales.get(0) instanceof Sale);
    }

    @DisplayName("countDistinctWeeks: returns number of weeks with transactions")
    @Test
    void testCountDistinctWeeks() {
        Share share1 = new Share(stock, new BigDecimal("10"), new BigDecimal("148.00"));
        Purchase purchase1 = TransactionFactory.createPurchase(share1, 1);
        Purchase purchase2 = TransactionFactory.createPurchase(share1, 1);
        Purchase purchase3 = TransactionFactory.createPurchase(share1, 3);

        archive.add(purchase1);
        archive.add(purchase2);
        archive.add(purchase3);

        assertEquals(2, archive.countDistinctWeeks()); // Weeks 1 and 3
    }
}

