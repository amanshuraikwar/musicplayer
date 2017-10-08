package app.sonu.com.musicplayer.ui.addsongstoplaylists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.SongPlaylistOnClickListener;
import app.sonu.com.musicplayer.list.visitable.SongPlaylistVisitable;
import app.sonu.com.musicplayer.mediaplayer.MusicService;
import app.sonu.com.musicplayer.model.Playlist;
import app.sonu.com.musicplayer.ui.base.BaseDialogFragment;
import app.sonu.com.musicplayer.ui.createplaylist.CreatePlaylistFragment;
import app.sonu.com.musicplayer.util.LogHelper;
import app.sonu.com.musicplayer.util.MediaMetadataHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 23/9/17.
 */

public class AddSongsToPlaylistsFragment extends BaseDialogFragment<AddSongsToPlaylistsMvpPresenter>
        implements AddSongsToPlaylistsMvpView {

    private static final String TAG = LogHelper.getLogTag(AddSongsToPlaylistsFragment.class);

    @BindView(R.id.doneBtn)
    Button doneBtn;

    @BindView(R.id.createNewPlaylistBtn)
    Button createNewPlaylistBtn;

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    private SongPlaylistOnClickListener onClickListener = new SongPlaylistOnClickListener() {
        @Override
        public void onPlaylistClick(MediaBrowserCompat.MediaItem playlist) {
            mPresenter.onPlaylistClick(playlist);
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
        View view = inflater.inflate(R.layout.fragment_add_songs_to_playlists, container, false);
        ButterKnife.bind(this, view);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDoneBtnClick();
            }
        });

        createNewPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCreateNewPlaylistBtnClick();
            }
        });

        itemsRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPresenter.onCreateView(getArguments().getString(MusicService.KEY_SONG_ID));

        return view;
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void close() {
        dismiss();
    }

    @Override
    public void displayList(List<MediaBrowserCompat.MediaItem> itemList) {
        itemsRv.setAdapter(
                new MediaRecyclerViewAdapter(getVisitableList(itemList),
                        new MediaListTypeFactory()));
    }

    @Override
    public void startCreatePlaylistFragment() {
        CreatePlaylistFragment fragment = new CreatePlaylistFragment();
        fragment.show(getChildFragmentManager(), "createPlaylistDialogFragment");
    }

    @Override
    public void addPlaylistToRv(MediaBrowserCompat.MediaItem item) {
        // todo add dynamic list support
    }

    @Override
    public void removePlaylistFromRv(MediaBrowserCompat.MediaItem item) {
        // todo add dynamic list support
    }

    @Override
    public void setPlaylistChecked(String playlistMediaId) {
        // todo add dynamic list support
    }

    @Override
    public void setPlaylistUnchecked(String playlistMediaId) {
        // todo add dynamic list support
    }

    /**
     * this method is defined in fragment because of attached onclicklistener
     * @param itemList
     * @return
     */
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> itemList) {
        List<BaseVisitable> visitableList = new ArrayList<>();

        for (MediaBrowserCompat.MediaItem item : itemList) {
            Bundle extras = item.getDescription().getExtras();
            if (extras != null) {
                // only displaying user playlists
                if (extras.getLong(MediaMetadataHelper.CUSTOM_METADATA_KEY_PLAYLIST_TYPE)
                        == Playlist.Type.USER.hashCode()) {
                    SongPlaylistVisitable visitable = new SongPlaylistVisitable(item);
                    visitable.setOnClickListener(onClickListener);
                    visitableList.add(visitable);
                }
            } else {
                Log.w(TAG, "getVisitableList:extras is null");
            }
        }

        return visitableList;
    }
}
