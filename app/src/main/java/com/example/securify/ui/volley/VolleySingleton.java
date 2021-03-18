package com.example.securify.ui.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    public static String startDate = "startDate";
    public static String endDate = "endDate";
    public static String limit = "limit";
    public static String listTypes = "listTypes";
    public static String activities = "activities";
    public static String listType = "listType";
    public static String domainName = "domainName";
    public static String timestamp = "timestamp";
    public static String Whitelist = "Whitelist";
    public static String Blacklist = "Blacklist";
    public static String lastEndDate = "lastEndDate";
    public static String count = "count";

    private VolleySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
