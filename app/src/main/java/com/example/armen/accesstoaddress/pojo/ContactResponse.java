package com.example.armen.accesstoaddress.pojo;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ContactResponse {

    @SerializedName("urls")
    private ArrayList<UrlModel> urlModels;

    public ContactResponse() {
    }

    public ContactResponse(ArrayList<UrlModel> urlModels) {
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
