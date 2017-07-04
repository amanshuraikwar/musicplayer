package app.sonu.com.musicplayer.di.module;


import app.sonu.com.musicplayer.data.network.ApiHelper;
import app.sonu.com.musicplayer.data.network.AppApiHelper;
import app.sonu.com.musicplayer.data.network.RequestHandler;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by sonu on 12/3/17.
 */

@Module
public class NetworkModule {
    public static final int CONNECT_TIMEOUT_IN_MS = 30000;

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    RequestHandler provideRequestHandler(OkHttpClient okHttpClient) {
        return new RequestHandler(okHttpClient);
    }

    @Provides
    @Singleton
    ApiHelper getApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }

    @Provides
    @Singleton
    AppApiHelper getAppApiHelper(RequestHandler requestHandler) {
        return new AppApiHelper(requestHandler);
    }
}
