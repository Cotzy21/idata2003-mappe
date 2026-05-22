package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Share;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculator for sale transactions.
 * Computes: gross (current price × quantity), commission (1% of gross),
 * tax (30% of profit, minimum 0), total.
 */
public class SaleCalculator implements TransactionCalculator {
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.01");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.30");
    private static final int SCALE = 2;

    private final Share share;

    /**
     * Constructs a SaleCalculator.
     *
     * @param share the Share being sold
     */
    public SaleCalculator(Share share) {
        this.share = share;
    }

    @Override
    public BigDecimal calculateGross() {
        return share.getStock().getSalesPrice()
                .multiply(share.getQuantity())
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateCommission() {
        return calculateGross()
                .multiply(COMMISSION_RATE)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTax() {
        BigDecimal gross = calculateGross();
        BigDecimal commission = calculateCommission();
        BigDecimal purchaseCost = share.getPurchasePrice()
                .multiply(share.getQuantity())
                .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal profit = gross.subtract(commission).subtract(purchaseCost);
        if (profit.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return profit
                .multiply(TAX_RATE)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateTotal() {
        return calculateGross()
                .subtract(calculateCommission())
                .subtract(calculateTax())
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
}

