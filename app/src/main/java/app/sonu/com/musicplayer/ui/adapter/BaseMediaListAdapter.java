package app.sonu.com.musicplayer.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.mediaplayer.MediaBrowserManager;
import app.sonu.com.musicplayer.ui.base.BaseMvpPresenter;
import app.sonu.com.musicplayer.ui.base.BaseMvpView;

/**
 * Created by sonu on 7/11/17.
 */

public class BaseMediaListAdapter<MvpPresenter extends BaseMediaListMvpPresenter>
        extends BaseRecyclerViewAdapter<MediaListTypeFactory>
        implements BaseMediaListMvpView {

    @Inject
    protected MvpPresenter mPresenter;

    private MediaListBuilder mListBuilder;
    private String mMediaId;

    // don't forget to inject the dependencies
    public BaseMediaListAdapter(@NonNull FragmentActivity activity,
                                @NonNull MediaListTypeFactory typeFactory,
                                @NonNull String mediaId,
                                @NonNull MediaListBuilder listBuilder) {
        super(activity, typeFactory);

        mListBuilder = listBuilder;
        mMediaId = mediaId;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mPresenter.onAttach(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mPresenter.onDetach();
    }

    @Override
    public void displayMediaItems(List<MediaBrowserCompat.MediaItem> itemList) {
        setVisitableList(mListBuilder.getVisitableList(itemList));
        notifyDataSetChanged();
    }

    @Override
    public String getMediaId() {
        return mMediaId;
    }
}
