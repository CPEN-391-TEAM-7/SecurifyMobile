package com.example.securify.ui.volley;

/**
 * Determines appropriate action upon receiving HTTP call response
 */
public interface VolleyResponseListener {
    void onError(Object response);
    void onResponse(Object response);
}
