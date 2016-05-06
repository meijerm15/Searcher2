package com.searcher.dao;

import com.searcher.util.Constants;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;
import timber.log.Timber;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public abstract class DAO<Service> {
    protected static final String BASE_URL = Constants.BASE_URL;

    protected Service service;

    public DAO() {
        service = createService();
    }

    protected OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        if (Constants.HTTP_LOG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor((message) -> Timber.i(message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.networkInterceptors().add(loggingInterceptor);
        }
        return client;
    }

    protected abstract Service createService();

    protected <T> void enqueu(Call<T> call, final DAOCallback<T> callback) {
        call.enqueue(new retrofit.Callback<T>() {
            @Override
            public void onResponse(Response<T> response, Retrofit retrofit) {
                T body = null;
                if (response.isSuccess()) {
                    body = response.body();
                }
                callback.onRespone(body, response.code());
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onRespone(null, -1);
            }
        });
    }
}
