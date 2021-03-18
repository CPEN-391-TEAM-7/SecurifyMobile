package com.example.securify.ui.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

public class VolleyGetRequest {

    private static final String baseAddress = " http://54.70.155.180";
    public static final String GET_RECENT_DOMAIN_REQUEST_ACTIVITY = "GET_RECENT_DOMAIN_REQUEST_ACTIVITY";
    public static final String GET_ALL_TIME_MOST_REQUESTED_DOMAINS = "GET_ALL_TIME_MOST_REQUESTED_DOMAINS";
    public static final String GET_BY_DATE_MOST_REQUESTED_DOMAINS = "GET_BY_DATE_MOST_REQUESTED_DOMAINS";
    public static final String GET_BLACKLIST = "GET_BLACKLIST";
    public static final String GET_WHITELIST = "GET_WHITELIST";
    public static final String GET_DOMAIN_STATUS = "GET_DOMAIN_STATUS";

    private static final String recentDomainUrl = "/activity/recent/";

    public static void addRequest(Context context, String request, String userID, JSONObject jsonObject, VolleyResponseListener listener) {

        String url = "";
        switch (request) {
            case GET_RECENT_DOMAIN_REQUEST_ACTIVITY:
                 url = baseAddress + recentDomainUrl + userID;
                break;
            case GET_ALL_TIME_MOST_REQUESTED_DOMAINS:
                break;
            case GET_BY_DATE_MOST_REQUESTED_DOMAINS:
                break;
            case GET_BLACKLIST:
                break;
            case GET_WHITELIST:
                break;
            case GET_DOMAIN_STATUS:
                break;
            default:
                return;
        }

        sendRequest(context, url, jsonObject, listener);


    }

    private static void sendRequest(Context context, String url, JSONObject jsonObject, VolleyResponseListener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
