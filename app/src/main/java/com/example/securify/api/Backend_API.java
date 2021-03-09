package com.example.securify.api;

import com.example.securify.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Backend_API {
    /**
     * Define REST API Calls here
     * Remember that this is just an interface, Retrofit will handle all of the implementation :)
     * You can see testapi for reference where Post is the object getting pulled.
     * Example:
     *      @GET("<route>")
     *      Call<T> getT();
     */
    @Headers({
            "Content-type: application/json"
    })

    @POST("/user/register")
    Call<User> registerUser(@Body User user);

}
