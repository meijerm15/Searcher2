package com.searcher.service;

import android.util.TypedValue;

import com.searcher.model.Channel;
import com.searcher.model.ImageChannel;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public interface ChannelService {

    @GET("channel")
    Call<Channel> getChannelById(@Query("id") String id);

    @GET("channel/all")
    Call<List<Channel>> getChannels();

    @Headers("Content-Type: application/json")
    @POST("channel/create")
    Call<Channel> createChannel(@Body Channel channel);

    @DELETE("channel/remove")
    Call<Void> removeChannel(@Query("channel") String channelId);

    @POST("channel/addmember")
    Call<Channel> addMemberChannel(@Query("channel") String channelId, @Query("user") String userId);

    @DELETE("channel/removemember")
    Call<Void> removeMemberChannel(@Query("channel") String channelId, @Query("user") String userId);

    @Headers("Content-Type: application/json")
    @POST("channel/edit")
    Call<Void> editChannel(@Body Channel channel);

    @Multipart
    @POST("channel/image")
    Call<Channel> addChannelImage(@Part("image") RequestBody image, @Part("channel") String channelId);

    @Headers("Content-Type: application/json")
    @POST("channel/image")
    Call<Channel> addChannelImage(@Body ImageChannel imageChannel);
}
