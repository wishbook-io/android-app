package com.wishbook.catalog.commonmodels.responses;

public class ResponseCreditLines {

    private String id;

    private String approved_line;

    private String created;

    private String is_active;

    private String company;

    private String available_line;

    private String nbfc_partner;

    private String used_line;

    private String modified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApproved_line() {
        return approved_line;
    }

    public void setApproved_line(String approved_line) {
        this.approved_line = approved_line;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAvailable_line() {
        return available_line;
    }

    public void setAvailable_line(String available_line) {
        this.available_line = available_line;
    }

    public String getNbfc_partner() {
        return nbfc_partner;
    }

    public void setNbfc_partner(String nbfc_partner) {
        this.nbfc_partner = nbfc_partner;
    }

    public String getUsed_line() {
        return used_line;
    }

    public void setUsed_line(String used_line) {
        this.used_line = used_line;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", approved_line = " + approved_line + ", created = " + created + ", is_active = " + is_active + ", company = " + company + ", available_line = " + available_line + ", nbfc_partner = " + nbfc_partner + ", used_line = " + used_line + ", modified = " + modified + "]";
    }
}
