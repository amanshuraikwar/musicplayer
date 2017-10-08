package app.sonu.com.musicplayer.ui.playingqueue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
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
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.AlbumCoverPagerAdapter;
import app.sonu.com.musicplayer.list.adapter.QueueRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.QueueItemOnClickListener;
import app.sonu.com.musicplayer.list.visitable.QueueItemVisitable;
import app.sonu.com.musicplayer.ui.albumcover.AlbumCoverFragment;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.ui.musicplayer.MusicPlayerFragment;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 5/10/17.
 */

public class PlayingQueueFragment extends BaseFragment<PlayingQueueMvpPresenter>
        implements PlayingQueueMvpView {

    private static final String TAG = LogHelper.getLogTag(PlayingQueueFragment.class);

    private QueueRecyclerViewAdapter mQueueAdapter;
    private int mCurrentQueueIndex;
    private LinearLayoutManager mQueueLayoutManager;

    private QueueItemOnClickListener mQueueItemOnClickListener = new QueueItemOnClickListener() {
        @Override
        public void onQueueItemClick(MediaSessionCompat.QueueItem item) {
            Log.d(TAG, "onQueueItemClick:called");
            mPresenter.onQueueItemClick(item);
        }

        @Override
        public void OnClick() {
            //nothing
        }
    };

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playing_queue, container, false);
        ButterKnife.bind(this, view);

        if (itemsRv.getLayoutManager() == null) {
            mQueueLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            itemsRv.setLayoutManager(mQueueLayoutManager);
        }

        mPresenter.onCreateView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MusicPlayerFragment)getParentFragment())
                .getMusicPlayerHolderComponent()
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));

        mPresenter.onCreate(getActivity());
    }

    @Override
    public void onMetadataChanged() {
        mPresenter.setScrollView(itemsRv);
    }

    @Override
    public void displayQueue(List<MediaSessionCompat.QueueItem> queue) {
        mQueueAdapter = new QueueRecyclerViewAdapter(getVisitableList(queue),
                new MediaListTypeFactory());
        itemsRv.setAdapter(mQueueAdapter);

        updateQueueIndex(mCurrentQueueIndex);
    }

    @Override
    public boolean updateQueueIndex(int index) {
        if (mQueueAdapter != null) {
            mCurrentQueueIndex = index;
            mQueueAdapter.updateQueueIndex(index);
            mQueueLayoutManager.scrollToPositionWithOffset(index, 0);
            return true;
        } else {
            mCurrentQueueIndex = index;
            return false;
        }
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private List<BaseVisitable> getVisitableList(List<MediaSessionCompat.QueueItem> queue) {
        List<BaseVisitable> visitableList = new ArrayList<>();
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            QueueItemVisitable visitable = new QueueItemVisitable(item);
            visitable.setOnClickListener(mQueueItemOnClickListener);
            visitable.setIndexToDisplay(index);
            visitableList.add(visitable);
            index++;
        }

        return visitableList;
    }
}
