package com.example.securify.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.securify.model.Post;

import java.util.List;

public class StatisticsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    //private MutableLiveData<T> object;

    public StatisticsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is statistics fragment");
    }

//    public LiveData<T> getPosts() {
//        if (object == null) {
//            object = new MutableLiveData<List<Post>>();
//            loadPosts();
//        }
//
//        return posts;
//    }

    //    private void loadPosts() {
    //        Retrofit retrofit = new Retrofit.Builder()
    //                .baseUrl("http://<Base url for our endpoint here>")
    //                .addConverterFactory(GsonConverterFactory.create())
    //                .build();
    //
    //        Backend_API api = retrofit.create(Backend_API.class);
    //
    //        Call<List<Post>> call = api.getPosts(); // decalre this in Backend_API.java
    //
                // This is asynchronous call
    //        call.enqueue(new Callback<List<Post>>() {
    //            @Override
    //            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
    //
    //                if(!response.isSuccessful()) {
    //                    // TODO: Handle unsuccessfull API call
    //                    return;
    //                }
    //
    //                 posts = response.body();
    //
    //                 // TODO: Handle the response here
    //            }
    //
    //            @Override
    //            public void onFailure(Call<List<Post>> call, Throwable t) {
    //                // TODO: Handle Failure.
    //            }
    //        });
    //    }

    public LiveData<String> getText() {
        return mText;
    }
}