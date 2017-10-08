package app.sonu.com.musicplayer.ui.base.medialist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 4/10/17.
 */

public abstract class MediaListFragment<MvpPresenter extends MediaListMvpPresenter>
        extends BaseFragment<MvpPresenter> implements MediaListMvpView{

    private static final String TAG = LogHelper.getLogTag(MediaListFragment.class);

    @BindView(R.id.itemsRv)
    RecyclerView tempItemsRv;

    protected RecyclerView itemsRv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // make sure to override this and inject dependencies and call presenter.onCreate()
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_media_list, container, false);
        ButterKnife.bind(this, view);

        itemsRv = tempItemsRv;

        if (itemsRv.getLayoutManager() == null) {
            itemsRv.setLayoutManager(getLayoutManager());
        }

        mPresenter.onCreateView();

        return view;
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:called");
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displayMediaList(List<MediaBrowserCompat.MediaItem> itemList) {
        itemsRv.setAdapter(
                new MediaRecyclerViewAdapter(getVisitableList(itemList), new MediaListTypeFactory()));
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * converts mediaitem list to visitable list
     * @param mediaList input list
     * @return visitable list
     */
    protected abstract List<BaseVisitable> getVisitableList(
            List<MediaBrowserCompat.MediaItem> mediaList);
}
