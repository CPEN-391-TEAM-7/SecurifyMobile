package com.example.securify.ui.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

public class VolleyRequest {

    private static final String baseAddress = " http://54.70.155.180";

    public static final String GET_RECENT_DOMAIN_REQUEST_ACTIVITY = "GET_RECENT_DOMAIN_REQUEST_ACTIVITY";
    public static final String GET_ALL_TIME_MOST_REQUESTED_DOMAINS = "GET_ALL_TIME_MOST_REQUESTED_DOMAINS";
    public static final String GET_BY_DATE_MOST_REQUESTED_DOMAINS = "GET_BY_DATE_MOST_REQUESTED_DOMAINS";
    public static final String GET_BLACKLIST = "GET_BLACKLIST";
    public static final String GET_WHITELIST = "GET_WHITELIST";
    public static final String GET_DOMAIN_STATUS = "GET_DOMAIN_STATUS";

    private static final String recentDomainUrl = "/activity/recent/";
    private static final String mostRequestedAllTimeDomainUrl = "/activity/allTimeMostRequested/";
    private static final String mostRequestedBtwnDatesUrl = "/activity/mostRequested/";
    private static final String blackListUrl = "/domain/Blacklist?userId=";
    private static final String whiteListUrl = "/domain/Whitelist?userId=";
    private static final String domainStatusIdUrl = "/domain?userId=";

    public static final String POST_NEW_DOMAIN = "POST_NEW_DOMAIN";

    private static final String addNewDomain = "/domain?userId=";

    public static final String PUT_BLACKLIST = "PUT_BLACKLIST";
    public static final String PUT_WHITELIST = "PUT_WHITELIST";


    public static void addRequest(Context context, String request, String userID, String domainName, String listType, JSONObject jsonObject, VolleyResponseListener listener) {

        String url;
        int requestType;
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
            case POST_NEW_DOMAIN:
                url = baseAddress + addNewDomain + userID + "&domainName=" + domainName + "&listType=" + listType;
                requestType = Request.Method.POST;
                break;
            case PUT_BLACKLIST:
                url = baseAddress + addNewDomain + userID + "&listType=Blacklist";
                requestType = Request.Method.PUT;
                break;
            case PUT_WHITELIST:
                url = baseAddress + addNewDomain + userID + "&listType=Whitelist";
                requestType = Request.Method.PUT;
                break;
            default:
                return;
        }

        sendRequest(context, url, jsonObject, listener, requestType);

    }

    private static void sendRequest(Context context, String url, JSONObject jsonObject, VolleyResponseListener listener, int requestType) {


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