package app.sonu.com.musicplayer.ui.mediaitemdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.ActivityModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.ui.album.AlbumFragment;
import app.sonu.com.musicplayer.ui.artist.ArtistFragment;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderActivity;
import app.sonu.com.musicplayer.ui.playlist.PlaylistFragment;

/**
 * Created by sonu on 21/9/17.
 */

public class MediaItemDetailActivity
        extends MusicPlayerHolderActivity<MediaItemDetailMvpPresenter>
        implements MediaItemDetailMvpView {

    private static final String TAG = MediaItemDetailActivity.class.getSimpleName();

    public static final String MEDIA_ITEM_TYPE_KEY = "media_item_type_key";
    public static final int MEDIA_ITEM_TYPE_ALBUM = 0;
    public static final int MEDIA_ITEM_TYPE_ARTIST = 1;
    public static final int MEDIA_ITEM_TYPE_PLAYLIST = 2;

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
            Bundle extras = getIntent().getExtras();
            switch (extras.getInt(MEDIA_ITEM_TYPE_KEY)) {
                case MEDIA_ITEM_TYPE_ALBUM:
                    childFragment = new AlbumFragment();
                    break;
                case MEDIA_ITEM_TYPE_ARTIST:
                    childFragment = new ArtistFragment();
                    break;
                case MEDIA_ITEM_TYPE_PLAYLIST:
                    childFragment = new PlaylistFragment();
                    break;
            }

            Bundle b = new Bundle();
            b.putParcelable("mediaItem", extras.getParcelable("mediaItem"));

            if (childFragment != null) {
                childFragment.setArguments(b);
                loadChild(childFragment);
            } else {
                Log.w(TAG, "onCreate:mediaItemType is invalid");
            }

        }

        mPresenter.onCreate(this);
    }


}
