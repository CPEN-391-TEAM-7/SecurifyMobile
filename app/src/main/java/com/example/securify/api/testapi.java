package com.example.securify.api;

import com.example.securify.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface testapi {

    @GET("posts")
    Call<List<Post>> getPosts();

}
