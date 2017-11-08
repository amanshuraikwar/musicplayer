package app.sonu.com.musicplayer.ui.albumcover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.list.adapter.AlbumCoverPagerAdapter;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.util.LogHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 4/10/17.
 */

public class AlbumCoverFragment extends BaseFragment<AlbumCoverMvpPresenter> implements AlbumCoverMvpView {

    private static final String TAG = LogHelper.getLogTag(AlbumCoverFragment.class);

    @BindView(R.id.albumCoverVp)
    ViewPager albumCoverVp;

    private AlbumCoverPagerAdapter mAdapter;
    private int mCurrentQueueIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_cover, container, false);
        ButterKnife.bind(this, view);

        albumCoverVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected:called");
                if (mCurrentQueueIndex != position) {
                    mPresenter.onPageSelected(mAdapter.getQueueItem(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageSelected:called");
            }
        });

        mPresenter.onCreateView();
        return view;
    }

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

    @Override
    public void displayQueue(List<MediaSessionCompat.QueueItem> queue) {
        mAdapter = new AlbumCoverPagerAdapter(getChildFragmentManager(), queue);
        albumCoverVp.setAdapter(mAdapter);
        albumCoverVp.invalidate();
        updateQueueIndex(mCurrentQueueIndex);
    }

    @Override
    public boolean updateQueueIndex(int index) {
        if (mAdapter != null) {
            mCurrentQueueIndex = index;
            if (albumCoverVp.getCurrentItem() != index) {
                albumCoverVp.setCurrentItem(index);
            }
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
}
