package app.sonu.com.musicplayer.ui.artists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
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
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.ui.list.onclicklistener.ArtistOnClickListener;
import app.sonu.com.musicplayer.ui.list.visitable.ArtistVisitable;
import app.sonu.com.musicplayer.ui.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.ui.list.MediaRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 1/8/17.
 */

public class ArtistsFragment extends BaseFragment<ArtistsMvpPresenter> implements ArtistsMvpView {

    private static final String TAG = ArtistsFragment.class.getSimpleName();
    public static final String TAB_TITLE = "Artists";

    private ArtistOnClickListener artistOnClickListener = new ArtistOnClickListener() {
        @Override
        public void onArtistClick(MediaBrowserCompat.MediaItem item) {
            Log.d(TAG, "onArtistClick:item="+item);
            mPresenter.onArtistClicked(item);
        }

        @Override
        public void OnClick() {

        }
    };

    @BindView(R.id.artistsRv)
    RecyclerView artistsRv;

    @BindView(R.id.parentSrl)
    SwipeRefreshLayout parentSrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerUiComponent.builder()
                .uiModule(new UiModule(getActivity()))
                .applicationComponent(((MyApplication) getActivity().getApplicationContext())
                        .getApplicationComponent())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null=" + (mPresenter == null));

        mPresenter.onCreate(getActivity());
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:called");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:called");
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        ButterKnife.bind(this, view);

        if (artistsRv.getLayoutManager() == null) {
            artistsRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }

        parentSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.onRefresh();
            }
        });
        mPresenter.onCreateView();
        return view;
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
        artistsRv.setAdapter(
                new MediaRecyclerViewAdapter(getVisitableList(itemList),
                        new MediaListTypeFactory()));
        artistsRv.invalidate();
    }

    @Override
    public void startLoading() {
        parentSrl.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        parentSrl.setRefreshing(false);
    }

    /**
     * this method is defined in fragment because of attached onclicklistener
     * @param artistList
     * @return
     */
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> artistList) {
        List<BaseVisitable> visitableList = new ArrayList<>();
        for (MediaBrowserCompat.MediaItem item : artistList) {
            ArtistVisitable artistVisitable = new ArtistVisitable(item);
            artistVisitable.setOnClickListener(artistOnClickListener);
            visitableList.add(artistVisitable);
        }

        return visitableList;
    }
}
