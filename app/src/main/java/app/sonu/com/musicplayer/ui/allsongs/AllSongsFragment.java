package app.sonu.com.musicplayer.ui.allsongs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BaseFragment;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.ui.allsongs.list.AllSongsListTypeFactory;
import app.sonu.com.musicplayer.ui.allsongs.list.AllSongsRecyclerViewAdapter;
import app.sonu.com.musicplayer.ui.allsongs.list.SongOnClickListener;
import app.sonu.com.musicplayer.ui.allsongs.list.SongVisitable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 2/7/17.
 */

public class AllSongsFragment extends BaseFragment<AllSongsMvpPresenter> implements AllSongsMvpView{

    private static final String TAG = AllSongsFragment.class.getSimpleName();

    private SongOnClickListener songOnClickListener = new SongOnClickListener() {
        @Override
        public void onSongClick(Song currentSong) {
            Log.d(TAG, "onSongClick:currentSong="+currentSong);
            mPresenter.onSongClick(currentSong);
        }

        @Override
        public void OnClick() {

        }
    };

    @BindView(R.id.songsRv)
    RecyclerView songsRv;

    @BindView(R.id.parentSrl)
    SwipeRefreshLayout parentSrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerUiComponent.builder()
                .uiModule(new UiModule(getActivity()))
                .applicationComponent(((MyApplication)getActivity().getApplicationContext())
                        .getApplicationComponent())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        songsRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        parentSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.onRefresh();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displayList(List<Song> songList) {
        songsRv.setAdapter(
                new AllSongsRecyclerViewAdapter(getVisitableList(songList),
                        new AllSongsListTypeFactory()));
        songsRv.invalidate();
    }

    /**
     * this method is defined in fragment because of attached onclicklistener
     * @param songList
     * @return
     */
    private List<BaseVisitable> getVisitableList(List<Song> songList) {
        List<BaseVisitable> visitableList = new ArrayList<>();
        for (Song song : songList) {
            SongVisitable songVisitable = new SongVisitable(song);
            songVisitable.setOnClickListener(songOnClickListener);
            visitableList.add(songVisitable);
        }

        return visitableList;
    }

    @Override
    public void startLoading() {
        parentSrl.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        parentSrl.setRefreshing(false);
    }
}
