package com.example.armen.accesstoaddress.db.pojo;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UrlResponse {

    @SerializedName("urls")
    private ArrayList<UrlModel> urlModels;

    public UrlResponse() {
    }

    public UrlResponse(ArrayList<UrlModel> urlModels) {
        this.urlModels = urlModels;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public ArrayList<UrlModel> getUrlModels() {
        return urlModels;
    }

    public void setUrlModels(ArrayList<UrlModel> urlModels) {
        this.urlModels = urlModels;
    }

}
