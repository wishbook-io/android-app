package com.wishbook.catalog.commonmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.wishbook.catalog.commonmodels.responses.Response_BuyerGroupType;
import com.wishbook.catalog.commonmodels.responses.Response_BuyingCompany;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 14/11/16.
 */
public class MergeContacts implements Comparable<MergeContacts>,Parcelable {
    String phone;
    String name;
    String company_name;
    private String id;
    private String status;


    public MergeContacts(String phone, String name, String company_name, String status, String id, Boolean header) {
        this.phone = phone;
        this.name = name;
        this.company_name = company_name;
        this.status = status;
        this.id = id;

    }

    public MergeContacts() {
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

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public int compareTo(MergeContacts another) {
        return this.getName().compareTo(another.getName());
    }


    @Override
    public int describeContents() {
        return 0;
    }
    private MergeContacts(Parcel in) {
        phone = in.readString();
        name = in.readString();
        company_name = in.readString();
        id = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(name);
        dest.writeString(company_name);
        dest.writeString(id);
        dest.writeString(status);
    }

    public static final Parcelable.Creator<MergeContacts> CREATOR
            = new Parcelable.Creator<MergeContacts>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public MergeContacts createFromParcel(Parcel in) {
            return new MergeContacts(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public MergeContacts[] newArray(int size) {
            return new MergeContacts[size];
        }
    };
}
