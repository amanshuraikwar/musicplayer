package app.sonu.com.musicplayer.ui.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.ActivityModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.ui.album.AlbumFragment;
import app.sonu.com.musicplayer.ui.base.BaseActivity;
import app.sonu.com.musicplayer.ui.mediaitemdetail.MediaItemDetailActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 14/9/17.
 */

public class MainActivity extends BaseActivity<MainMvpPresenter>
        implements MainMvpView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.bnv)
    BottomNavigationView bnv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MusicPlayerHolderComponent musicPlayerHolderComponent =
                DaggerMusicPlayerHolderComponent
                        .builder()
                        .musicPlayerHolderModule(new MusicPlayerHolderModule())
                        .applicationComponent(
                                ((MyApplication)getApplicationContext())
                                        .getApplicationComponent())
                        .build();

        musicPlayerHolderComponent
                .activityComponentBuilder()
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);

        ButterKnife.bind(this);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mPresenter.onNavigationItemSelected(item.getItemId());
                return true;
            }
        });

        bnv.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                mPresenter.onNavigationItemReselected(item.getItemId());
            }
        });

        mPresenter.onCreate(this);
    }

    @Override
    public void setNavigationItemSelected(int position) {
        bnv.getMenu().getItem(position).setChecked(true);
    }

    @Override
    public void showNavigationBar() {
        bnv.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void hideNavigationBar() {
        bnv.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blueGrey300));
        }

    }

    @Override
    public void startAlbumActivity(MediaBrowserCompat.MediaItem item, View animatingView) {
        Bundle b = new Bundle();
        b.putParcelable("mediaItem", item);
        b.putInt(MediaItemDetailActivity.MEDIA_ITEM_TYPE_KEY,
                MediaItemDetailActivity.MEDIA_ITEM_TYPE_ALBUM);
        Intent i = new Intent(this, MediaItemDetailActivity.class);
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public void startArtistActivity(MediaBrowserCompat.MediaItem item, View animatingView) {
        Bundle b = new Bundle();
        b.putParcelable("mediaItem", item);
        b.putInt(MediaItemDetailActivity.MEDIA_ITEM_TYPE_KEY,
                MediaItemDetailActivity.MEDIA_ITEM_TYPE_ARTIST);
        Intent i = new Intent(this, MediaItemDetailActivity.class);
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public void startPlaylistActivity(MediaBrowserCompat.MediaItem item, View animatingView) {
        Bundle b = new Bundle();
        b.putParcelable("mediaItem", item);
        b.putInt(MediaItemDetailActivity.MEDIA_ITEM_TYPE_KEY,
                MediaItemDetailActivity.MEDIA_ITEM_TYPE_PLAYLIST);
        Intent i = new Intent(this, MediaItemDetailActivity.class);
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop:called");
    }
}
