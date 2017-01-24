package com.example.chernenkovit.ExRates.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/** Selected date exchange rate model. */
public class DateRates {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("bank")
    @Expose
    private String bank;
    @SerializedName("baseCurrency")
    @Expose
    private int baseCurrency;
    @SerializedName("baseCurrencyLit")
    @Expose
    private String baseCurrencyLit;
    @SerializedName("exchangeRate")
    @Expose
    private List<ExchangeRate> exchangeRate = new ArrayList<ExchangeRate>();

    /**
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return The bank
     */
    public String getBank() {
        return bank;
    }

    /**
     * @param bank The bank
     */
    public void setBank(String bank) {
        this.bank = bank;
    }

    /**
     * @return The baseCurrency
     */
    public int getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * @param baseCurrency The baseCurrency
     */
    public void setBaseCurrency(int baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    /**
     * @return The baseCurrencyLit
     */
    public String getBaseCurrencyLit() {
        return baseCurrencyLit;
    }

    /**
     * @param baseCurrencyLit The baseCurrencyLit
     */
    public void setBaseCurrencyLit(String baseCurrencyLit) {
        this.baseCurrencyLit = baseCurrencyLit;
    }

    /**
     * @return The exchangeRate
     */
    public List<ExchangeRate> getExchangeRate() {
        return exchangeRate;
    }

    /**
     * @param exchangeRate The exchangeRate
     */
    public void setExchangeRate(List<ExchangeRate> exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    public class ExchangeRate {

        @SerializedName("baseCurrency")
        @Expose
        private String baseCurrency;
        @SerializedName("currency")
        @Expose
        private String currency;
        @SerializedName("saleRateNB")
        @Expose
        private float saleRateNB;
        @SerializedName("purchaseRateNB")
        @Expose
        private float purchaseRateNB;
        @SerializedName("saleRate")
        @Expose
        private float saleRate;
        @SerializedName("purchaseRate")
        @Expose
        private float purchaseRate;

        /**
         * @return The baseCurrency
         */
        public String getBaseCurrency() {
            return baseCurrency;
        }

        /**
         * @param baseCurrency The baseCurrency
         */
        public void setBaseCurrency(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        /**
         * @return The currency
         */
        public String getCurrency() {
            return currency;
        }

        /**
         * @param currency The currency
         */
        public void setCurrency(String currency) {
            this.currency = currency;
        }

        /**
         * @return The saleRateNB
         */
        public float getSaleRateNB() {
            return saleRateNB;
        }

        /**
         * @param saleRateNB The saleRateNB
         */
        public void setSaleRateNB(float saleRateNB) {
            this.saleRateNB = saleRateNB;
        }

        /**
         * @return The purchaseRateNB
         */
        public float getPurchaseRateNB() {
            return purchaseRateNB;
        }

        /**
         * @param purchaseRateNB The purchaseRateNB
         */
        public void setPurchaseRateNB(float purchaseRateNB) {
            this.purchaseRateNB = purchaseRateNB;
        }

        /**
         * @return The saleRate
         */
        public float getSaleRate() {
            return saleRate;
        }

        /**
         * @param saleRate The saleRate
         */
        public void setSaleRate(float saleRate) {
            this.saleRate = saleRate;
        }

        /**
         * @return The purchaseRate
         */
        public float getPurchaseRate() {
            return purchaseRate;
        }

        /**
         * @param purchaseRate The purchaseRate
         */
        public void setPurchaseRate(float purchaseRate) {
            this.purchaseRate = purchaseRate;
        }

    }
}
