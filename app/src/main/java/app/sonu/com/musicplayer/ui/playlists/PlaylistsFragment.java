package app.sonu.com.musicplayer.ui.playlists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.list.onclicklistener.MediaListHeaderOnClickListener;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderVisitable;
import app.sonu.com.musicplayer.model.Playlist;
import app.sonu.com.musicplayer.ui.createplaylist.CreatePlaylistFragment;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.onclicklistener.PlaylistOnClickListener;
import app.sonu.com.musicplayer.list.visitable.PlaylistVisitable;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 5/9/17.
 */

public class PlaylistsFragment extends BaseFragment<PlaylistsMvpPresenter>
        implements PlaylistsMvpView {

    private static final String TAG = PlaylistsFragment.class.getSimpleName();
    public static final String TAB_TITLE = "Playlists";

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    private PlaylistOnClickListener playlistOnClickListener = new PlaylistOnClickListener() {
        @Override
        public void onPlaylistClick(MediaBrowserCompat.MediaItem item, View animatingView) {
            mPresenter.onPlaylistClicked(item, animatingView);
        }

        @Override
        public void onOptionsIbClick(final MediaBrowserCompat.MediaItem item, View animatingView) {
            PopupMenu popup = new PopupMenu(getActivity(), animatingView);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_playlist_options, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menuItemDelete:
                            mPresenter.onDeletePlaylistClick(item);
                            break;
                    }

                    return false;
                }
            });
            popup.show();
        }

        @Override
        public void OnClick() {

        }
    };
    private MediaListHeaderOnClickListener autoPlaylistAddClickListener =
            new MediaListHeaderOnClickListener() {
                @Override
                public void onActionClick() {
                    Log.d(TAG, "addPlaylist:clicked");
                    mPresenter.onAddPlaylistBtnClick();
                }

                @Override
                public void OnClick() {

                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:called");

        MusicPlayerHolderComponent mMusicPlayerHolderComponent =
                DaggerMusicPlayerHolderComponent
                        .builder()
                        .musicPlayerHolderModule(new MusicPlayerHolderModule())
                        .applicationComponent(
                                ((MyApplication)getActivity().getApplicationContext())
                                        .getApplicationComponent())
                        .build();

        mMusicPlayerHolderComponent
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));

        mPresenter.onCreate(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:called");
        View view = inflater.inflate(R.layout.layout_media_list, container, false);
        ButterKnife.bind(this, view);


        if (itemsRv.getLayoutManager() == null) {
            itemsRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }

        mPresenter.onCreateView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop:called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:called");
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displayList(List<MediaBrowserCompat.MediaItem> itemList) {
        itemsRv.setAdapter(
                new MediaRecyclerViewAdapter(getActivity(),
                        new MediaListTypeFactory(), getVisitableList(itemList)));
    }

    /**
     * this method is defined in fragment because of attached onclicklistener
     * @param songList
     * @return
     */
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> songList) {
        List<BaseVisitable> autoVisitableList = new ArrayList<>();
        List<BaseVisitable> userVisitableList = new ArrayList<>();

        for (MediaBrowserCompat.MediaItem item : songList) {
            PlaylistVisitable visitable;

            Bundle extras = item.getDescription().getExtras();

            if (extras != null) {
                if (extras.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_TYPE)
                        == Playlist.Type.AUTO.hashCode()) {
                    visitable = new PlaylistVisitable(item, Playlist.Type.AUTO.hashCode());
                    visitable.setOnClickListener(playlistOnClickListener);
                    autoVisitableList.add(visitable);
                } else if (extras.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_TYPE)
                        == Playlist.Type.USER.hashCode()){
                    visitable = new PlaylistVisitable(item, Playlist.Type.USER.hashCode());
                    visitable.setOnClickListener(playlistOnClickListener);
                    userVisitableList.add(visitable);
                } else {
                    Log.w(TAG, "getVisitableList:invalid playlist type");
                }
            } else {
                Log.w(TAG, "getVisitableList:extras in null");
            }
        }

        List<BaseVisitable> visitableList = new ArrayList<>();
        visitableList.add(new MediaListHeaderVisitable("Auto"));
        visitableList.addAll(autoVisitableList);

        MediaListHeaderVisitable visitable = new MediaListHeaderVisitable("User", true,
                "CREATE");
        visitable.setOnClickListener(autoPlaylistAddClickListener);

        visitableList.add(visitable);
        visitableList.addAll(userVisitableList);

        return visitableList;
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollListToTop() {
        itemsRv.smoothScrollToPosition(0);
    }

    @Override
    public void showCreatePlaylistDialog() {
        CreatePlaylistFragment fragment = new CreatePlaylistFragment();
        fragment.show(getChildFragmentManager(), "createPlaylistDialogFragment");
    }
}
