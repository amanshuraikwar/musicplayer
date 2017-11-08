package app.sonu.com.musicplayer.di.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.ActivityContext;
import app.sonu.com.musicplayer.di.PerAdapter;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.adapter.simple.SimpleMediaListMvpPresenter;
import app.sonu.com.musicplayer.ui.adapter.simple.SimpleMediaListPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 8/11/17.
 */

@Module
public class AdapterModule {
    private FragmentActivity mActivity;

    public AdapterModule(@NonNull FragmentActivity activity) {
        mActivity = activity;
    }

    @Provides
    @PerAdapter
    @ActivityContext
    Context getContext() {
        return mActivity;
    }

    @Provides
    @PerAdapter
    FragmentActivity getActivity() {
        return mActivity;
    }

    @Provides
    @PerAdapter
    SimpleMediaListMvpPresenter getSimpleMediaListMvpPresenter(
            SimpleMediaListPresenter simpleMediaListPresenter) {
        return simpleMediaListPresenter;
    }

    @Provides
    @PerAdapter
    SimpleMediaListPresenter getSimpleMediaListPresenter(DataManager dataManager) {
        return new SimpleMediaListPresenter(dataManager, new MediaBrowserManager("",
                SimpleMediaListPresenter.class.getSimpleName()), mActivity);
    }
}
