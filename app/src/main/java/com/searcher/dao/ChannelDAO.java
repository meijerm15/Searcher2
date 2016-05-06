package com.searcher.dao;

import com.google.gson.Gson;
import com.searcher.model.Channel;
import com.searcher.model.ImageChannel;
import com.searcher.model.User;
import com.searcher.service.ChannelService;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class ChannelDAO extends DAO<ChannelService> {

    @Override
    public ChannelService createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ChannelService.class);
    }

    public void getChannelById(String id, DAOCallback<Channel> callback) {
        enqueu(service.getChannelById(id), callback);
    }

    public void getChannels(DAOCallback<List<Channel>> callback) {
        enqueu(service.getChannels(), callback);
    }

    public void createChannel(Channel channel, DAOCallback<Channel> callback) {
        enqueu(service.createChannel(channel), callback);
    }

    public void addMember(Channel channel, User user, DAOCallback<Channel> callback) {
        enqueu(service.addMemberChannel(channel.getId(), user.getId()), callback);
    }

    public void removeMember(String id, User user, DAOCallback<Void> callback) {
        enqueu(service.removeMemberChannel(id, user.getId()), callback);
    }

    public void removeChannel(String id, DAOCallback<Void> callback) {
        enqueu(service.removeChannel(id), callback);
    }

    public void editChannel(Channel channel, DAOCallback<Void> callback) {
        enqueu(service.editChannel(channel), callback);
    }

    public void addChannelImage(File file, String channelId, DAOCallback<Channel> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        enqueu(service.addChannelImage(requestBody, channelId), callback);
    }

    public void addChannelImage(ImageChannel imageChannel, DAOCallback<Channel> callback) {
        enqueu(service.addChannelImage(imageChannel), callback);
    }
}
