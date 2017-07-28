package com.example.armen.accesstoaddress.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class UrlModel implements Parcelable {

    @SerializedName("url_id")
    private long id;

    @SerializedName("url_address")
    private String UrlAddress;

    @SerializedName("image")
    private String image;

    public UrlModel() {
    }

    public UrlModel(long id, String urlAddress) {
        this.id = id;
        UrlAddress = urlAddress;
    }

    public UrlModel(long id, String urlAddress, String image) {
        this.id = id;
        UrlAddress = urlAddress;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrlAddress() {
        return UrlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        UrlAddress = urlAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Creator<UrlModel> getCREATOR() {
        return CREATOR;
    }

    protected UrlModel(Parcel in) {
        id = in.readLong();
        UrlAddress = in.readString();
        image = in.readString();
    }

    public static final Creator<UrlModel> CREATOR = new Creator<UrlModel>() {
        @Override
        public UrlModel createFromParcel(Parcel in) {
            return new UrlModel(in);
        }

        @Override
        public UrlModel[] newArray(int size) {
            return new UrlModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(UrlAddress);
        dest.writeString(image);
    }
}