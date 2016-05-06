package com.searcher.application;


import com.searcher.BuildConfig;
import com.searcher.model.Channel;
import com.searcher.service.ChannelService;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import timber.log.Timber;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }
}
