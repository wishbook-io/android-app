package com.wishbook.catalog.commonmodels;

import java.util.ArrayList;

/**
 * Created by Vigneshkarnika on 24/05/16.
 */
public class UploadContactsModel {
   ArrayList<NameValues> contacts;

    public UploadContactsModel(ArrayList<NameValues> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<NameValues> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<NameValues> contacts) {
        this.contacts = contacts;
    }
}
