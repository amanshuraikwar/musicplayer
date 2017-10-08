package app.sonu.com.musicplayer.di.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import app.sonu.com.musicplayer.AppBus;
import app.sonu.com.musicplayer.PerSlidingUpPanelBus;
import app.sonu.com.musicplayer.data.DataManager;
import app.sonu.com.musicplayer.di.ActivityContext;
import app.sonu.com.musicplayer.di.PerActivity;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.main.MainMvpPresenter;
import app.sonu.com.musicplayer.ui.main.MainPresenter;
import app.sonu.com.musicplayer.ui.mediaitemdetail.MediaItemDetailMvpPresenter;
import app.sonu.com.musicplayer.ui.mediaitemdetail.MediaItemDetailPresenter;

import app.sonu.com.musicplayer.ui.search.SearchActivityMvpPresenter;
import app.sonu.com.musicplayer.ui.search.SearchActivityPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by sonu on 15/9/17.
 */

@Module
public class ActivityModule {
    private FragmentActivity mActivity;

    public ActivityModule(@NonNull FragmentActivity activity) {
        mActivity = activity;
    }

    @Provides
    @PerActivity
    @ActivityContext
    Context getContext() {
        return mActivity;
    }

    @Provides
    @PerActivity
    FragmentActivity getActivity() {
        return mActivity;
    }

    @Provides
    @PerActivity
    MainMvpPresenter getHomeActivityMvpPresenter(
            MainPresenter homeActivityPresenter) {
        return homeActivityPresenter;
    }

    @Provides
    @PerActivity
    MainPresenter getHomeActivityPresenter(DataManager dataManager,
                                           AppBus appBus) {
        return new MainPresenter(dataManager, appBus);
    }

    @Provides
    @PerActivity
    MediaItemDetailMvpPresenter getMediaItemDetailMvpPresenter(
            MediaItemDetailPresenter mediaItemDetailPresenter) {
        return mediaItemDetailPresenter;
    }

    @Provides
    @PerActivity
    MediaItemDetailPresenter getMediaItemDetailPresenter(DataManager dataManager,
                                            AppBus appBus,
                                            PerSlidingUpPanelBus slidingUpPanelBus) {
        return new MediaItemDetailPresenter(dataManager,
                new MediaBrowserManager(null,
                        MediaItemDetailPresenter.class.getSimpleName()),
                appBus, slidingUpPanelBus);
    }

    @Provides
    @PerActivity
    SearchActivityMvpPresenter getSearchActivityMvpPresenter(
            SearchActivityPresenter searchActivityPresenter) {
        return searchActivityPresenter;
    }

    @Provides
    @PerActivity
    SearchActivityPresenter getSearchActivityPresenter(DataManager dataManager,
                                                       AppBus appBus,
                                                       PerSlidingUpPanelBus slidingUpPanelBus) {
        return new SearchActivityPresenter(dataManager,
                new MediaBrowserManager(null,
                        SearchActivityPresenter.class.getSimpleName()),
                appBus,
                slidingUpPanelBus);
    }
}
