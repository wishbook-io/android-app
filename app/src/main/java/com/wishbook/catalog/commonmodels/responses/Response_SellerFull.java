package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.commonmodels.Invitee;

/**
 * Created by prane on 29-03-2016.
 */
public class Response_SellerFull {

    private String id;
    private Response_BuyingCompany selling_company;
    private String status;
    private String fix_amount;
    private String broker_company;
    private String percentage_amount;
    private Invitee invitee;
    private String payment_duration;
    private String discount;
    private String cash_discount;
    private String credit_limit;
    private String brokerage_fees;
    private String supplier_person_name;
    private Response_BuyerGroupType group_type;
    private Response_BuyerFull.Credit_reference credit_reference;

    public Response_SellerFull(String id, Response_BuyingCompany selling_company, String status, String fixAmount, String percentageAmount, Invitee invitee, Response_BuyerGroupType groupType) {
        this.id = id;
        this.selling_company = selling_company;
        this.status = status;
        this.fix_amount = fixAmount;
        this.percentage_amount = percentageAmount;
        this.invitee = invitee;
        this.group_type = groupType;
    }

    public Response_SellerFull() {
    }

    public Response_BuyingCompany getBuying_company() {
        return selling_company;
    }

    public void setBuying_company(Response_BuyingCompany buying_company) {
        this.selling_company = buying_company;
    }

    public String getFix_amount() {
        return fix_amount;
    }

    public void setFix_amount(String fix_amount) {
        this.fix_amount = fix_amount;
    }

    public String getPercentage_amount() {
        return percentage_amount;
    }

    public void setPercentage_amount(String percentage_amount) {
        this.percentage_amount = percentage_amount;
    }

    public String getPayment_duration() {
        return payment_duration;
    }

    public void setPayment_duration(String payment_duration) {
        this.payment_duration = payment_duration;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(String cash_discount) {
        this.cash_discount = cash_discount;
    }

    public String getCredit_limit() {
        return credit_limit;
    }

    public void setCredit_limit(String credit_limit) {
        this.credit_limit = credit_limit;
    }

    public Response_BuyerGroupType getGroup_type() {
        return group_type;
    }

    public void setGroup_type(Response_BuyerGroupType group_type) {
        this.group_type = group_type;
    }

    public String  getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Response_BuyingCompany getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(Response_BuyingCompany selling_company) {
        this.selling_company = selling_company;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFixAmount() {
        return fix_amount;
    }

    public void setFixAmount(String fixAmount) {
        this.fix_amount = fixAmount;
    }

    public String getPercentageAmount() {
        return percentage_amount;
    }

    public void setPercentageAmount(String percentageAmount) {
        this.percentage_amount = percentageAmount;
    }

    public Invitee getInvitee() {
        return invitee;
    }

    public void setInvitee(Invitee invitee) {
        this.invitee = invitee;
    }

    public Response_BuyerGroupType getGroupType() {
        return group_type;
    }

    public void setGroupType(Response_BuyerGroupType groupType) {
        this.group_type = groupType;
    }

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }

    public String getSupplier_person_name() {
        return supplier_person_name;
    }

    public void setSupplier_person_name(String supplier_person_name) {
        this.supplier_person_name = supplier_person_name;
    }

    public Response_BuyerFull.Credit_reference getCredit_reference() {
        return credit_reference;
    }

    public void setCredit_reference(Response_BuyerFull.Credit_reference credit_reference) {
        this.credit_reference = credit_reference;
    }

    public class Credit_reference {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
