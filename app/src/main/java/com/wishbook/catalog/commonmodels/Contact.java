package com.wishbook.catalog.commonmodels;

/**
 * Created by prane on 20-03-2016.
 */
public class Contact {

    public String contactName;
    public String contactNumber;
    public boolean isContactChecked;
    public boolean isWishbookContact;
    public String companyName;


    public Contact(String contactName, String contactNumber, boolean isContactChecked,boolean isWishbookContact) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.isContactChecked = isContactChecked;
        this.isWishbookContact=isWishbookContact;
    }

    public void setContactChecked(boolean contactChecked) {
        isContactChecked = contactChecked;
    }

    public boolean isWishbookContact() {
        return isWishbookContact;
    }

    public void setWishbookContact(boolean wishbookContact) {
        isWishbookContact = wishbookContact;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isContactChecked() {
        return isContactChecked;
    }

    public void setIsContactChecked(boolean isContactChecked) {
        this.isContactChecked = isContactChecked;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
