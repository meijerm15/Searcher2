package com.searcher.dao;

import com.searcher.model.User;
import com.searcher.service.UserService;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class UserDAO extends DAO<UserService> {

    @Override
    public UserService createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(UserService.class);
    }

    public void getUserById(String id, DAOCallback<User> callback) {
        enqueu(service.getUserById(id), callback);
    }

    public void getUserByPhone(String phone, DAOCallback<User> callback) {
        enqueu(service.getUserByPhone(phone), callback);
    }

    public void createUser(User user, DAOCallback<User> callback) {
        enqueu(service.createUser(user), callback);
    }
}
