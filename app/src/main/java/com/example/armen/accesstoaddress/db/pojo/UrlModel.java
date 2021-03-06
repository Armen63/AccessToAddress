package com.example.armen.accesstoaddress.db.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


public class UrlModel implements Parcelable, Comparable{

    @SerializedName("url_id")
    private long id;

    @SerializedName("url_address")
    private String urlAddress;

    @SerializedName("image")
    private String image;

    @SerializedName("response_time")
    private Integer responseTime;

    public UrlModel() {
    }


    public UrlModel(long id, String urlAddress) {
        this.id = id;
        this.urlAddress = urlAddress;
    }


    protected UrlModel(Parcel in) {
        id = in.readLong();
        urlAddress = in.readString();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(urlAddress);
        dest.writeString(image);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}