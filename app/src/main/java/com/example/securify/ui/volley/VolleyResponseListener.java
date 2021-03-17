package com.example.securify.ui.volley;

public interface VolleyResponseListener {
    void onError(String message);
    void onResponse(Object response);
}
