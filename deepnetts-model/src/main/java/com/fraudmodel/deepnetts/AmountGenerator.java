package com.fraudmodel.deepnetts;

import java.util.Objects;
import java.util.Random;


public final class AmountGenerator {

    private AmountGenerator() {
        
    }


    public static double generateAmount(
            double ratioToMedianPurchasePrice,
            int fraud,
            Random random
    ) {
        Objects.requireNonNull(random, "Random generator ne sme biti null.");

        if (!Double.isFinite(ratioToMedianPurchasePrice)
                || ratioToMedianPurchasePrice < 0) {
            throw new IllegalArgumentException(
                    "ratioToMedianPurchasePrice mora biti konačan broj >= 0."
            );
        }

        if (fraud != 0 && fraud != 1) {
            throw new IllegalArgumentException(
                    "fraud mora imati vrednost 0 ili 1."
            );
        }

        double baseAmount = 10.0 + random.nextDouble() * 290.0;

        double amount = baseAmount * ratioToMedianPurchasePrice;

        if (fraud == 1) {
            // uniform(1.5, 3.5)
            double fraudMultiplier = 1.5 + random.nextDouble() * 2.0;
            amount *= fraudMultiplier;
        }

        return Math.round(amount * 100.0) / 100.0;
    }
}
