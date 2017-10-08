package app.sonu.com.musicplayer.ui.albums;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.GridLayoutManager;
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
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.ui.base.BaseFragment;

import app.sonu.com.musicplayer.list.onclicklistener.AlbumOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumVisitable;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsFragment extends BaseFragment<AlbumsMvpPresenter> implements AlbumsMvpView {

    private static final String TAG = AlbumsFragment.class.getSimpleName();
    public static final String TAB_TITLE = "Albums";

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    private AlbumOnClickListener albumOnClickListener = new AlbumOnClickListener() {
        @Override
        public void onAlbumClick(MediaBrowserCompat.MediaItem item, View animatingView) {
            Log.d(TAG, "onAlbumClick:currentAlbum=" + item+" "+this);
            mPresenter.onAlbumClicked(item, animatingView);
        }

        @Override
        public void OnClick() {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_media_list, container, false);
        ButterKnife.bind(this, view);

        if (itemsRv.getLayoutManager() == null) {
            RecyclerView.LayoutManager layoutManager =
                    new GridLayoutManager(getActivity().getApplicationContext(), 2);
            itemsRv.setLayoutManager(layoutManager);
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
                new MediaRecyclerViewAdapter(getVisitableList(itemList),
                        new MediaListTypeFactory()));
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollListToTop() {
        itemsRv.smoothScrollToPosition(0);
    }

    /**
     * converts mediaitem list to visitable list
     * @param albumList input list
     * @return output visitable list
     */
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> albumList) {
        List<BaseVisitable> visitableList = new ArrayList<>();

        for (MediaBrowserCompat.MediaItem item : albumList) {
            AlbumVisitable albumVisitable = new AlbumVisitable(item);
            albumVisitable.setOnClickListener(albumOnClickListener);
            visitableList.add(albumVisitable);
        }

        return visitableList;
    }
}
