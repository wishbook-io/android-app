package com.wishbook.catalog.commonmodels.responses;

import com.wishbook.catalog.commonmodels.Invitee;

/**
 * Created by prane on 29-03-2016.
 */
public class Response_BuyerFull {

    private String id;
    private Response_BuyingCompany buying_company;
    private String status;
    private String fix_amount;
    private String broker_company;
    private String brokerage_fees;
    private String percentage_amount;
    private Invitee invitee;
    private String payment_duration;
    private String discount;
    private String cash_discount;
    private String credit_limit;
    private String details;
    private Response_BuyerGroupType group_type;
    private String buying_person_name;
    private String created_at;
    private Credit_reference credit_reference;


    public Invitee getInvitee() {
        return invitee;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBrokerage_fees() {
        return brokerage_fees;
    }

    public void setBrokerage_fees(String brokerage_fees) {
        this.brokerage_fees = brokerage_fees;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public Response_BuyerFull(String id, Response_BuyingCompany buyingCompany, String status, String fixAmount, String percentageAmount, Invitee invitee, Response_BuyerGroupType groupType) {
        this.id = id;
        this.buying_company = buyingCompany;
        this.status = status;
        this.fix_amount = fixAmount;
        this.percentage_amount = percentageAmount;
        this.invitee = invitee;
        this.group_type = groupType;
    }

    public Response_BuyerFull() {
    }

    public Response_BuyingCompany getBuying_company() {
        return buying_company;
    }

    public void setBuying_company(Response_BuyingCompany buying_company) {
        this.buying_company = buying_company;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Response_BuyingCompany getBuyingCompany() {
        return buying_company;
    }

    public void setBuyingCompany(Response_BuyingCompany buyingCompany) {
        this.buying_company = buyingCompany;
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


    public void setInvitee(Invitee invitee) {
        this.invitee = invitee;
    }

    public Response_BuyerGroupType getGroupType() {
        return group_type;
    }

    public void setGroupType(Response_BuyerGroupType groupType) {
        this.group_type = groupType;
    }

    public String getBuying_person_name() {
        return buying_person_name;
    }

    public void setBuying_person_name(String buying_person_name) {
        this.buying_person_name = buying_person_name;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Credit_reference getCredit_reference() {
        return credit_reference;
    }

    public void setCredit_reference(Credit_reference credit_reference) {
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
