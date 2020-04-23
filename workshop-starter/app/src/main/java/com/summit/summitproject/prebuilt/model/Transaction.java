package com.summit.summitproject.prebuilt.model;

import java.io.Serializable;

/**
 * Represents a simple credit card transaction -- containing the merchant and the amount.
 */
public class Transaction implements Serializable {

    private final String merchant;

    private final String amount;

    public Transaction(String merchant, String amount) {
        this.merchant = merchant;
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Merchant: " + merchant + ", Amount: " + amount; 
    }

}
