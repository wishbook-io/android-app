package com.wishbook.catalog.commonmodels.responses;


import java.util.ArrayList;

public class ResponseCompanyRating {

    private String total_feedback;

    private ArrayList<Feedback_by> feedback_by;

    private String bank_average_balance_6m;

    private String gst_credit_rating;

    private String bank_statement_pdf;

    private String bank_check_bounces_6m;

    private String bank_monthly_transaction_6m;

    private String id;

    private String crif_score;

    private String average_payment_duration;

    private String company_name;

    private String bureau_report_rating;

    private String company;

    private String financial_statement_rating;

    private String average_gr_rate;

    private String bank_data_source;

    private String rating;

    private String salary;

    private String buying_company;
    private String buying_company_name;

    private String selling_company;

    private String transaction_value;

    private String duration_of_work;

    private String selling_company_name;

    public String getBuying_company(){return buying_company;}

    public String getSelling_company(){return selling_company;}

    public String getTransaction_value(){return  transaction_value;}

    public String getSelling_company_name(){return selling_company_name;}

    public String getDuration_of_work(){return duration_of_work;}

    public String getBuying_company_name() {
        return buying_company_name;
    }

    public void setBuying_company_name(String buying_company_name) {
        this.buying_company_name = buying_company_name;
    }

    public void setBuying_company(String buying_company){this.buying_company=buying_company;}

    public void setSelling_company(String selling_company){this.selling_company=selling_company;}

    public void setTransaction_value(String transaction_value){this.transaction_value=transaction_value;}

    public void setDuration_of_work(String duration_of_work){this.duration_of_work=duration_of_work;}

    public void setSelling_company_name(String selling_company_name){this.selling_company_name=selling_company_name;}

    public String getTotal_feedback() {
        return total_feedback;
    }

    public void setTotal_feedback(String total_feedback) {
        this.total_feedback = total_feedback;
    }

    public ArrayList<Feedback_by> getFeedback_by_names() {
        return feedback_by;
    }

    public void setFeedback_by_names(ArrayList<Feedback_by> feedback_by) {
        this.feedback_by = feedback_by;
    }

    public String getGst_credit_rating() {
        return gst_credit_rating;
    }

    public void setGst_credit_rating(String gst_credit_rating) {
        this.gst_credit_rating = gst_credit_rating;
    }

    public String getBank_statement_pdf() {
        return bank_statement_pdf;
    }

    public void setBank_statement_pdf(String bank_statement_pdf) {
        this.bank_statement_pdf = bank_statement_pdf;
    }

    public String getBank_average_balance_6m() {
        return bank_average_balance_6m;
    }

    public void setBank_average_balance_6m(String bank_average_balance_6m) {
        this.bank_average_balance_6m = bank_average_balance_6m;
    }

    public String getBank_check_bounces_6m() {
        return bank_check_bounces_6m;
    }

    public void setBank_check_bounces_6m(String bank_check_bounces_6m) {
        this.bank_check_bounces_6m = bank_check_bounces_6m;
    }

    public String getBank_monthly_transaction_6m() {
        return bank_monthly_transaction_6m;
    }

    public void setBank_monthly_transaction_6m(String bank_monthly_transaction_6m) {
        this.bank_monthly_transaction_6m = bank_monthly_transaction_6m;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCrif_score() {
        return crif_score;
    }

    public void setCrif_score(String crif_score) {
        this.crif_score = crif_score;
    }

    public String getAverage_payment_duration() {
        return average_payment_duration;
    }

    public void setAverage_payment_duration(String average_payment_duration) {
        this.average_payment_duration = average_payment_duration;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getBureau_report_rating() {
        return bureau_report_rating;
    }

    public void setBureau_report_rating(String bureau_report_rating) {
        this.bureau_report_rating = bureau_report_rating;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFinancial_statement_rating() {
        return financial_statement_rating;
    }

    public void setFinancial_statement_rating(String financial_statement_rating) {
        this.financial_statement_rating = financial_statement_rating;
    }

    public String getAverage_gr_rate() {
        return average_gr_rate;
    }

    public void setAverage_gr_rate(String average_gr_rate) {
        this.average_gr_rate = average_gr_rate;
    }

    public String getBank_data_source() {
        return bank_data_source;
    }

    public void setBank_data_source(String bank_data_source) {
        this.bank_data_source = bank_data_source;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
