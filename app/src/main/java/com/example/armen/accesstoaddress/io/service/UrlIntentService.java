package com.example.armen.accesstoaddress.io.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.armen.accesstoaddress.db.handler.PlQueryHandler;
import com.example.armen.accesstoaddress.db.pojo.UrlModel;
import com.example.armen.accesstoaddress.db.pojo.UrlResponse;
import com.example.armen.accesstoaddress.io.bus.ApiEvent;
import com.example.armen.accesstoaddress.io.bus.BusProvider;
import com.example.armen.accesstoaddress.io.rest.HttpRequestManager;
import com.example.armen.accesstoaddress.io.rest.HttpResponseUtil;
import com.example.armen.accesstoaddress.util.Constant;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;


public class UrlIntentService extends IntentService {

    private static final String LOG_TAG = UrlIntentService.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public UrlIntentService() {
        super(UrlIntentService.class.getName());
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Start/stop commands
    // ===========================================================

    /**
     * @param url         - calling api url
     * @param requestType - string constant that helps us to distinguish what request it is
     * @param postEntity  - POST request entity (json string that must be sent on server)
     */
    public static void start(Context context, String url, String postEntity,
                             int requestType) {
        Intent intent = new Intent(context, UrlIntentService.class);
        intent.putExtra(Constant.Extra.URL, url);
        intent.putExtra(Constant.Extra.REQUEST_TYPE, requestType);
        intent.putExtra(Constant.Extra.POST_ENTITY, postEntity);
        context.startService(intent);
    }

    public static void start(Context context, String url,
                             int requestType) {
        Intent intent = new Intent(context, UrlIntentService.class);
        intent.putExtra(Constant.Extra.URL, url);
        intent.putExtra(Constant.Extra.REQUEST_TYPE, requestType);
        context.startService(intent);
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getExtras().getString(Constant.Extra.URL);
        String data = intent.getExtras().getString(Constant.Extra.POST_ENTITY);
        int requestType = intent.getExtras().getInt(Constant.Extra.REQUEST_TYPE);
        Log.i(LOG_TAG, requestType + Constant.Symbol.SPACE + url);

        HttpURLConnection connection;

        switch (requestType) {
            case Constant.RequestType.PRODUCT_LIST:

                // calling API
                connection = HttpRequestManager.executeRequest(
                        url,
                        "GET",
                        null
                );

                // parse API result to get json string
                String jsonList = HttpResponseUtil.parseResponse(connection);

                // deserialize json string to model
                UrlResponse productResponse = new Gson().fromJson(jsonList, UrlResponse.class);

                // check server data (null if something went wrong)
                if (productResponse != null) {

                    // get all products
                    ArrayList<UrlModel> urlModels = productResponse.getUrlModels();

                    // add all products into db
                    PlQueryHandler.addUrlModels(this, urlModels);

                    // post to UI
                    BusProvider.getInstance().post(new ApiEvent<>(ApiEvent.EventType.PRODUCT_LIST_LOADED, true, urlModels));

                } else {
                    BusProvider.getInstance().post(new ApiEvent<>(ApiEvent.EventType.PRODUCT_LIST_LOADED, false));
                }
                break;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Util
    // ===========================================================

}