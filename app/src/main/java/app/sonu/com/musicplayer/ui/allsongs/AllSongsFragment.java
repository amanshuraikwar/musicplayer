package app.sonu.com.musicplayer.ui.allsongs;

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
import app.sonu.com.musicplayer.list.base.BaseListItemOnClickListener;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.list.onclicklistener.MediaItemSquareOnClickListener;
import app.sonu.com.musicplayer.list.onclicklistener.MediaListHeaderOnClickListener;
import app.sonu.com.musicplayer.list.visitable.HorizontalMediaListVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaItemSquareVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderVisitable;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.mediaplayer.playlist.PlaylistsSource;
import app.sonu.com.musicplayer.ui.adapter.MediaListBuilder;
import app.sonu.com.musicplayer.ui.addsongstoplaylists.AddSongsToPlaylistsFragment;
import app.sonu.com.musicplayer.ui.base.BaseFragment;

import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.list.visitable.ShuffleAllSongsVisitable;
import app.sonu.com.musicplayer.list.visitable.SongVisitable;
import app.sonu.com.musicplayer.util.MediaIdHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 2/7/17.
 */

public class AllSongsFragment extends BaseFragment<AllSongsMvpPresenter> implements AllSongsMvpView{

    private static final String TAG = AllSongsFragment.class.getSimpleName();
    public static final String TAB_TITLE = "Songs";

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    private MediaItemSquareOnClickListener mediaItemSquareOnClickListener = new MediaItemSquareOnClickListener() {
        @Override
        public void onClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onSongClicked(item);
        }

        @Override
        public void OnClick() {

        }
    };

    private SongOnClickListener songOnClickListener = new SongOnClickListener() {
        @Override
        public void onSongClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onSongClicked(item);
        }

        @Override
        public void onOptionsIbClick(final MediaBrowserCompat.MediaItem item, View optionsIb) {
            PopupMenu popup = new PopupMenu(getActivity(), optionsIb);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_song_options, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menuItemAddToPlaylist:
                            mPresenter.onAddToPlaylistClick(item);
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

    private MediaListHeaderOnClickListener mediaListHeaderOnClickListener = new MediaListHeaderOnClickListener() {
        @Override
        public void onActionClick() {
            mPresenter.onShuffleAllClick();
        }

        @Override
        public void OnClick() {

        }
    };

    private BaseListItemOnClickListener shuffleAllOnClickListener = new BaseListItemOnClickListener() {
        @Override
        public void OnClick() {
            mPresenter.onShuffleAllClick();
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
        List<BaseVisitable> visitableList = new ArrayList<>();

        visitableList.add(new MediaListHeaderVisitable("Favourites"));

        visitableList.add(new HorizontalMediaListVisitable(
                MediaIdHelper.createHierarchyAwareMediaId(null,
                        MediaIdHelper.MEDIA_ID_PLAYLISTS, PlaylistsSource.FAVORITES_PLAYLIST_ID),
                new MediaListBuilder() {
            @Override
            public List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> itemList) {
                List<BaseVisitable> visitableList = new ArrayList<>();

                for (MediaBrowserCompat.MediaItem item : itemList) {
                    MediaItemSquareVisitable visitable1 =
                            new MediaItemSquareVisitable(item);
                    visitable1.setOnClickListener(mediaItemSquareOnClickListener);
                    visitableList.add(visitable1);
                }

                return visitableList;
            }
        }));

        MediaListHeaderVisitable visitable = new MediaListHeaderVisitable("All Songs",
                true, "SHUFFLE");
        visitable.setOnClickListener(mediaListHeaderOnClickListener);
        visitableList.add(visitable);

        for (MediaBrowserCompat.MediaItem item : songList) {
            SongVisitable songVisitable = new SongVisitable(item);
            songVisitable.setOnClickListener(songOnClickListener);
            visitableList.add(songVisitable);
        }

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
    public void showAddToPlaylistsDialog(String songId) {
        AddSongsToPlaylistsFragment fragment = new AddSongsToPlaylistsFragment();
        Bundle b = new Bundle();
        b.putString(MusicService.KEY_SONG_ID, songId);
        fragment.setArguments(b);
        fragment.show(getChildFragmentManager(), "AddSongsToPlaylistsFragment");
    }
}
