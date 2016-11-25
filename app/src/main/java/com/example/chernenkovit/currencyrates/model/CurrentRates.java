package com.example.chernenkovit.currencyrates.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/** Current exchange rate model. */

@Root(name = "exchangerate")
public class CurrentRates {

    @ElementList(inline = true)
    private List<ExchangeRate> exchangeRate = new ArrayList<ExchangeRate>();

    public List<ExchangeRate> getExchangeRate() {
        return exchangeRate;
    }

    @Override
    public String toString() {
        return "ClassPojo [exchangerate = " + exchangeRate + "]";
    }

    @Root(name = "exchangerate")
    public static class ExchangeRate {

        @Attribute(name = "ccy")
        private String ccy;

        @Attribute(name = "unit")
        private String unit;

        @Attribute(name = "ccy_name_en")
        private String ccy_name_en;

        @Attribute(name = "buy")
        private String buy;

        @Attribute(name = "ccy_name_ru")
        private String ccy_name_ru;

        @Attribute(name = "date")
        private String date;

        @Attribute(name = "base_ccy")
        private String base_ccy;

        @Attribute(name = "ccy_name_ua")
        private String ccy_name_ua;

        public String getCcy() {
            return ccy;
        }

        public void setCcy(String ccy) {
            this.ccy = ccy;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getCcy_name_en() {
            return ccy_name_en;
        }

        public void setCcy_name_en(String ccy_name_en) {
            this.ccy_name_en = ccy_name_en;
        }

        public String getBuy() {
            return buy;
        }

        public void setBuy(String buy) {
            this.buy = buy;
        }

        public String getCcy_name_ru() {
            return ccy_name_ru;
        }

        public void setCcy_name_ru(String ccy_name_ru) {
            this.ccy_name_ru = ccy_name_ru;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getBase_ccy() {
            return base_ccy;
        }

        public void setBase_ccy(String base_ccy) {
            this.base_ccy = base_ccy;
        }

        public String getCcy_name_ua() {
            return ccy_name_ua;
        }

        public void setCcy_name_ua(String ccy_name_ua) {
            this.ccy_name_ua = ccy_name_ua;
        }

        @Override
        public String toString() {
            return "ClassPojo [ccy = " + ccy + ", unit = " + unit + ", ccy_name_en = " + ccy_name_en + ", buy = " + buy + ", ccy_name_ru = " + ccy_name_ru + ", date = " + date + ", base_ccy = " + base_ccy + ", ccy_name_ua = " + ccy_name_ua + "]";
        }
    }
}
