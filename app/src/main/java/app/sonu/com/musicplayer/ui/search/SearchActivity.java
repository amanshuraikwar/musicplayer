package app.sonu.com.musicplayer.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.ActivityModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderActivity;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderMvpPresenter;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderMvpView;
import app.sonu.com.musicplayer.ui.mediaitemdetail.MediaItemDetailActivity;

/**
 * Created by sonu on 21/9/17.
 */

public class SearchActivity
        extends MusicPlayerHolderActivity<SearchActivityMvpPresenter>
        implements SearchActivityMvpView {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private Fragment childFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMusicPlayerHolderComponent =
                DaggerMusicPlayerHolderComponent
                        .builder()
                        .musicPlayerHolderModule(new MusicPlayerHolderModule())
                        .applicationComponent(
                                ((MyApplication)getApplicationContext())
                                        .getApplicationComponent())
                        .build();

        mMusicPlayerHolderComponent
                .activityComponentBuilder()
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);

        if (childFragment == null) {
            childFragment = new SearchFragment();
            loadChild(childFragment);
        }

        mPresenter.onCreate(this);
    }
}
