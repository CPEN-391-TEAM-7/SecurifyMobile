package com.example.securify.ui.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

public class VolleyRequest {

    private static final String TAG = "HTTP REQUEST";

    private static final String baseAddress = "https://securifyapi.online/";

    public static final String GET_RECENT_DOMAIN_REQUEST_ACTIVITY = "GET_RECENT_DOMAIN_REQUEST_ACTIVITY";
    public static final String GET_ALL_TIME_MOST_REQUESTED_DOMAINS = "GET_ALL_TIME_MOST_REQUESTED_DOMAINS";
    public static final String GET_BY_DATE_MOST_REQUESTED_DOMAINS = "GET_BY_DATE_MOST_REQUESTED_DOMAINS";
    public static final String GET_BLACKLIST = "GET_BLACKLIST";
    public static final String GET_WHITELIST = "GET_WHITELIST";
    public static final String GET_DOMAIN_STATUS = "GET_DOMAIN_STATUS";

    private static final String recentDomainUrl = "/activity/recent/";
    private static final String mostRequestedAllTimeDomainUrl = "/activity/allTimeMostRequested/";
    private static final String mostRequestedBtwnDatesUrl = "/activity/mostRequested/";
    private static final String blackListUrl = "/domain/Blacklist/";
    private static final String whiteListUrl = "/domain/Whitelist/";
    private static final String domainStatusIdUrl = "/domain?userId=";

    public static final String POST_REGISTER_USER = "POST_REGISTER_USER";

    private static final String updateDomain = "/domain/update";
    private static final String registerUser = "/user/register";

    public static final String PUT_LIST = "PUT_LIST";


    public static void addRequest(Context context, String request, String userID, String domainName, String listType, JSONObject jsonObject, VolleyResponseListener listener) {

        String url;
        int requestType;

        Log.i(TAG, "Adding a Request...");

        switch (request) {
            case GET_RECENT_DOMAIN_REQUEST_ACTIVITY:
                url = baseAddress + recentDomainUrl + userID;
                requestType = Request.Method.GET;
                break;
            case GET_ALL_TIME_MOST_REQUESTED_DOMAINS:
                url = baseAddress + mostRequestedAllTimeDomainUrl + userID;
                requestType = Request.Method.GET;
                break;
            case GET_BY_DATE_MOST_REQUESTED_DOMAINS:
                url = baseAddress + mostRequestedBtwnDatesUrl + userID;
                requestType = Request.Method.GET;
                break;
            case GET_BLACKLIST:
                url = baseAddress + blackListUrl + userID;
                requestType = Request.Method.GET;
                break;
            case GET_WHITELIST:
                url = baseAddress + whiteListUrl + userID;
                requestType = Request.Method.GET;
                break;
            case GET_DOMAIN_STATUS:
                url = baseAddress + domainStatusIdUrl + userID + "&domainName=" + domainName;
                requestType = Request.Method.GET;
                break;
            case POST_REGISTER_USER:
                url = baseAddress + registerUser;
                requestType = Request.Method.POST;
                break;
            case PUT_LIST:
                url = baseAddress + updateDomain + userID;
                requestType = Request.Method.PUT;
                break;
            default:
                return;
        }

        sendRequest(context, url, jsonObject, listener, requestType);

    }

    private static void sendRequest(Context context, String url, JSONObject jsonObject, VolleyResponseListener listener, int requestType) {

        Log.i(TAG, "Sending a request...");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestType, url, jsonObject, new Response.Listener<JSONObject>() {
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
