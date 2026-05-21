package no.ntnu.idatx2003.millions.model.transaction;

import no.ntnu.idatx2003.millions.model.Share;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculator for purchase transactions.
 * Computes: gross (price × quantity), commission (0.5% of gross), tax (0), total.
 */
public class PurchaseCalculator implements TransactionCalculator {
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.005");
    private static final int SCALE = 2;

    private final Share share;

    /**
     * Constructs a PurchaseCalculator.
     *
     * @param share the Share being purchased
     */
    public PurchaseCalculator(Share share) {
        this.share = share;
    }

    @Override
    public BigDecimal calculateGross() {
        return share.getPurchasePrice()
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
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotal() {
        return calculateGross()
                .add(calculateCommission())
                .add(calculateTax())
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
}

