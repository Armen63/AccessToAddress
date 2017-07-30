package com.example.armen.accesstoaddress.io.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.armen.accesstoaddress.db.handler.PlQueryHandler;
import com.example.armen.accesstoaddress.db.pojo.UrlModel;
import com.example.armen.accesstoaddress.db.pojo.UrlResponse;
import com.example.armen.accesstoaddress.io.bus.BusProvider;
import com.example.armen.accesstoaddress.io.bus.event.ApiEvent;
import com.example.armen.accesstoaddress.io.rest.HttpRequestManager;
import com.example.armen.accesstoaddress.io.rest.HttpResponseUtil;
import com.example.armen.accesstoaddress.util.Constant;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import static com.example.armen.accesstoaddress.util.Constant.API.ACCESS_EXIST;


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
     */

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
        int requestType = intent.getExtras().getInt(Constant.Extra.REQUEST_TYPE);
        Log.i(LOG_TAG, requestType + Constant.Symbol.SPACE + url);

        HttpURLConnection connection;

        switch (requestType) {
            case Constant.RequestType.URL_LIST:

                // calling API
                connection = HttpRequestManager.executeRequest(
                        url,
                        Constant.RequestMethod.GET,
                        null
                );

                // parse API result to get json string
                String jsonList = HttpResponseUtil.parseResponse(connection);

                // deserialize json string to model
                UrlResponse urlResponse = new Gson().fromJson(jsonList, UrlResponse.class);

                // check server data (null if something went wrong)
                if (urlResponse != null) {

                    // get all urls
                    ArrayList<UrlModel> urlModels = urlResponse.getUrlModels();
                    for(UrlModel ddd: urlModels){
                        ddd.setImage(ACCESS_EXIST);
                    }
                    // add all urls into db
                    PlQueryHandler.addUrlModels(this, urlModels);

                    // post to UI
                    BusProvider.getInstance().post(new ApiEvent<>(ApiEvent.EventType.Url_LIST_LOADED, true, urlModels));

                } else {
                    BusProvider.getInstance().post(new ApiEvent<>(ApiEvent.EventType.Url_LIST_LOADED, false));
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