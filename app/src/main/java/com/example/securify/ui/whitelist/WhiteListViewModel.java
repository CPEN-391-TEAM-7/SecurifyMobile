package com.example.securify.ui.whitelist;

import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.securify.api.testapi;
import com.example.securify.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WhiteListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<List<Post>> posts;

    public WhiteListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Whitelist");
    }

    public LiveData<List<Post>> getPosts() {
        if (posts == null) {
            posts = new MutableLiveData<List<Post>>();
//            loadPosts();
        }

        return posts;
    }

//    private void loadPosts() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://jsonplaceholder.typicode.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        testapi testapi = retrofit.create(testapi.class);
//
//        Call<List<Post>> call = testapi.getPosts();
//
//        call.enqueue(new Callback<List<Post>>() {
//            @Override
//            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
//
//                if(!response.isSuccessful()) {
//                    Log.e("RETROFIT", "Couldn't fetch posts");
//                    return;
//                }
//
//                 posts = response.body();
//
//                for (Post post : posts) {
//                    String content = "";
//                    content += "ID: " + post.getId() + "\n";
//                    content += "User ID: " + post.getUserId() + "\n";
//                    content += "Title: " + post.getTitle() + "\n";
//                    content += "Text: " + post.getText() + "\n\n";
//
//                    posts.add(content);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Post>> call, Throwable t) {
//                textViewResult.setText(t.getMessage());
//            }
//        });
//    }

    public LiveData<String> getText() {
        return mText;
    }
}