package app.sonu.com.musicplayer.ui.allsongs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BaseFragment;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.ui.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.ui.list.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.ui.list.SongOnClickListener;
import app.sonu.com.musicplayer.ui.list.SongVisitable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 2/7/17.
 */

public class AllSongsFragment extends BaseFragment<AllSongsMvpPresenter> implements AllSongsMvpView{

    private static final String TAG = AllSongsFragment.class.getSimpleName();
    public static final String TAB_TITLE = "Songs";

    private SongOnClickListener songOnClickListener = new SongOnClickListener() {
        @Override
        public void onSongClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onSongClicked(item);
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
        Log.d(TAG, "onCreate:called");

        DaggerUiComponent.builder()
                .uiModule(new UiModule(getActivity()))
                .applicationComponent(((MyApplication)getActivity().getApplicationContext())
                        .getApplicationComponent())
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
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        ButterKnife.bind(this, view);

        mPresenter.onCreateView();

        if (songsRv.getLayoutManager() == null) {
            songsRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }

        parentSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.onRefresh();
            }
        });

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
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy:called");
        mPresenter.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:called");
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displayList(List<MediaBrowserCompat.MediaItem> itemList) {
        songsRv.setAdapter(
                new MediaRecyclerViewAdapter(getVisitableList(itemList),
                        new MediaListTypeFactory()));
        songsRv.invalidate();
    }

    /**
     * this method is defined in fragment because of attached onclicklistener
     * @param songList
     * @return
     */
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> songList) {
        List<BaseVisitable> visitableList = new ArrayList<>();
        for (MediaBrowserCompat.MediaItem item : songList) {
            SongVisitable songVisitable = new SongVisitable(item);
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

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
