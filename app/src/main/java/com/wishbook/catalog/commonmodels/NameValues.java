package com.wishbook.catalog.commonmodels;

import java.io.Serializable;

/**
 * Created by Vigneshkarnika on 24/05/16.
 */
public class NameValues implements Serializable {
    String name;
    String phone;
    private String type_contacts;
    private Discount_rules discount_rules;

    public NameValues(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Discount_rules getDiscount_rules() {
        return discount_rules;
    }

    public void setDiscount_rules(Discount_rules discount_rules) {
        this.discount_rules = discount_rules;
    }

    public String getType_contacts() {
        return type_contacts;
    }

    public void setType_contacts(String type_contacts) {
        this.type_contacts = type_contacts;
    }

    @Override
    public boolean equals(Object obj) {
        return getName().equals(((NameValues) obj).getName()) && getPhone().equals(((NameValues) obj).getPhone());
    }


    public class Discount_rules implements Serializable {
        private String cash_discount;

        private String credit_discount;

        private double single_pcs_discount;

        public String getCash_discount() {
            return cash_discount;
        }

        public void setCash_discount(String cash_discount) {
            this.cash_discount = cash_discount;
        }

        public String getCredit_discount() {
            return credit_discount;
        }

        public void setCredit_discount(String credit_discount) {
            this.credit_discount = credit_discount;
        }

        public double getSingle_pcs_discount() {
            return single_pcs_discount;
        }

        public void setSingle_pcs_discount(double single_pcs_discount) {
            this.single_pcs_discount = single_pcs_discount;
        }
    }
}
