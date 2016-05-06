package com.searcher.service;

import com.searcher.model.User;
import com.squareup.okhttp.MediaType;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public interface UserService {

    @GET("user")
    Call<User> getUserById(@Query("id") String id);

    @GET("user")
    Call<User> getUserByPhone(@Query("phone") String phone);

    @Headers("Content-Type: application/json")
    @POST("user/create")
    Call<User> createUser(@Body User user);


}
