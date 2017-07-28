package com.example.armen.accesstoaddress.io.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.armen.accesstoaddress.io.bus.BusProvider;
import com.example.armen.accesstoaddress.io.rest.HttpRequestManager;
import com.example.armen.accesstoaddress.io.rest.HttpResponseUtil;
import com.example.armen.accesstoaddress.pojo.UrlModel;
import com.example.armen.accesstoaddress.pojo.ContactResponse;
import com.example.armen.accesstoaddress.util.Constant;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class UrlListIntentService extends IntentService {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UrlListIntentService.class.getSimpleName();

    private class Extra {
        static final String URL = "CONTACT_LIST";
        static final String POST_ENTITY = "POST_ENTITY";
        static final String REQUEST_TYPE = "REQUEST_TYPE";
    }

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public UrlListIntentService() {
        super(UrlListIntentService.class.getName());
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

    public static void start(Context context, String url, String postEntity, int requestType) {
        Intent intent = new Intent(context, UrlListIntentService.class);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        intent.putExtra(Extra.POST_ENTITY, postEntity);
        context.startService(intent);
    }

    public static void start(Context context, String url, int requestType) {
        Intent intent = new Intent(context, UrlListIntentService.class);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        context.startService(intent);
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getExtras().getString(Extra.URL);
        String data = intent.getExtras().getString(Extra.POST_ENTITY);
        int requestType = intent.getExtras().getInt(Extra.REQUEST_TYPE);
        Log.i(LOG_TAG, requestType + Constant.Symbol.SPACE + url);

        HttpURLConnection connection;

        switch (requestType) {
            case HttpRequestManager.RequestType.URL_LIST:

                connection = HttpRequestManager.executeRequest(
                        url,
                        HttpRequestManager.RequestMethod.GET,
                        null
                );

                String jsonList = HttpResponseUtil.parseResponse(connection);

                ContactResponse contactResponse = new Gson().fromJson(jsonList, ContactResponse.class);
                ArrayList<UrlModel> urlModels = contactResponse.getUrlModels();


                BusProvider.getInstance().post(urlModels);

                break;

            case HttpRequestManager.RequestType.URL_ITEM:

                connection = HttpRequestManager.executeRequest(
                        url,
                        HttpRequestManager.RequestMethod.GET,
                        null
                );

                String jsonItem = HttpResponseUtil.parseResponse(connection);

                UrlModel urlModel = new Gson().fromJson(jsonItem, UrlModel.class);


                BusProvider.getInstance().post(urlModel);

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