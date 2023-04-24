package com.wishbook.catalog.commonmodels;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.wishbook.catalog.Utils.multipleimageselect.models.Image;

/**
 * Created by root on 9/8/17.
 */

public class AppIntroModel implements Parcelable{
    String title;
    String description;
    int image;

    public AppIntroModel(String title, String description, int image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<AppIntroModel> CREATOR = new Creator<AppIntroModel>() {
        @Override
        public AppIntroModel createFromParcel(Parcel source) {
            return new AppIntroModel(source);
        }

        @Override
        public AppIntroModel[] newArray(int size) {
            return new AppIntroModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(image);
    }

    private AppIntroModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        image = in.readInt();
    }

}
