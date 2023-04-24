package com.wishbook.catalog.commonmodels.responses;

/**
 * Created by nani on 17-04-2016.
 */
public class Response_Suppliers {

    private String percentage_amount;

    private String cash_discount;

    private String group_type;


    private String status;

    private String preferred_logistics;

    private String supplier_approval;

    private String warehouse;

    private String group_type_name;

    private String broker_company;

    private String selling_company_name;

    private String selling_company;

    private String buyer_approval;

    private String discount;

    private String id;

    private String selling_company_phone_number;

    private String is_visible;

    private String selling_company_chat_user;

    private String credit_limit;

    private String fix_amount;

    private String invitee;

    private String payment_duration;

    private String invitee_name;

    private String invitee_phone_number;

    private String buyer_type;

    private String details;

    private String enquiry_catalog;

    private String brokerage_fees;

    private String supplier_person_name;


    public Response_Suppliers(String selling_company_name, String selling_company) {
        this.selling_company_name = selling_company_name;
        this.selling_company = selling_company;
    }

    public String getBuyer_type ()
    {
        return buyer_type;
    }

    public void setBuyer_type (String buyer_type)
    {
        this.buyer_type = buyer_type;
    }

    public String getDetails ()
    {
        return details;
    }

    public void setDetails (String details)
    {
        this.details = details;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [buyer_type = "+buyer_type+", details = "+details+"]";
    }

    public String getGroup_type_name() {
        return group_type_name;
    }



    public void setGroup_type_name(String group_type_name) {
        this.group_type_name = group_type_name;
    }

    public String getInvitee_name() {
        return invitee_name;
    }

    public void setInvitee_name(String invitee_name) {
        this.invitee_name = invitee_name;
    }

    public String getInvitee_phone_number() {
        return invitee_phone_number;
    }

    public void setInvitee_phone_number(String invitee_phone_number) {
        this.invitee_phone_number = invitee_phone_number;
    }

    public String getPercentage_amount() {
        return percentage_amount;
    }

    public void setPercentage_amount(String percentage_amount) {
        this.percentage_amount = percentage_amount;
    }

    public String getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(String cash_discount) {
        this.cash_discount = cash_discount;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPreferred_logistics() {
        return preferred_logistics;
    }

    public void setPreferred_logistics(String preferred_logistics) {
        this.preferred_logistics = preferred_logistics;
    }

    public String getSupplier_approval() {
        return supplier_approval;
    }

    public void setSupplier_approval(String supplier_approval) {
        this.supplier_approval = supplier_approval;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getBroker_company() {
        return broker_company;
    }

    public void setBroker_company(String broker_company) {
        this.broker_company = broker_company;
    }

    public String getSelling_company_name() {
        return selling_company_name;
    }

    public void setSelling_company_name(String selling_company_name) {
        this.selling_company_name = selling_company_name;
    }

    public String getSelling_company() {
        return selling_company;
    }

    public void setSelling_company(String selling_company) {
        this.selling_company = selling_company;
    }

    public String getBuyer_approval() {
        return buyer_approval;
    }

    public void setBuyer_approval(String buyer_approval) {
        this.buyer_approval = buyer_approval;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelling_company_phone_number() {
        return selling_company_phone_number;
    }

    public void setSelling_company_phone_number(String selling_company_phone_number) {
        this.selling_company_phone_number = selling_company_phone_number;
    }

    public String getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(String is_visible) {
        this.is_visible = is_visible;
    }

    public String getSelling_company_chat_user() {
        return selling_company_chat_user;
    }

    public void setSelling_company_chat_user(String selling_company_chat_user) {
        this.selling_company_chat_user = selling_company_chat_user;
    }

    public String getCredit_limit() {
        return credit_limit;
    }

    public void setCredit_limit(String credit_limit) {
        this.credit_limit = credit_limit;
    }

    public String getFix_amount() {
        return fix_amount;
    }

    public void setFix_amount(String fix_amount) {
        this.fix_amount = fix_amount;
    }

    public String getInvitee() {
        return invitee;
    }

    public void setInvitee(String invitee) {
        this.invitee = invitee;
    }

    public String getPayment_duration() {
        return payment_duration;
    }

    public void setPayment_duration(String payment_duration) {
        this.payment_duration = payment_duration;
    }

    public String getEnquiry_catalog() {
        return enquiry_catalog;
    }

    public void setEnquiry_catalog(String enquiry_catalog) {
        this.enquiry_catalog = enquiry_catalog;
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
}
