package app.sonu.com.musicplayer.ui.medialists;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import app.sonu.com.musicplayer.R;

import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.list.adapter.MediaListsViewPagerAdapter;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.ui.base.musicplayerholder.MusicPlayerHolderFragment;
import app.sonu.com.musicplayer.ui.albums.AlbumsFragment;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsFragment;
import app.sonu.com.musicplayer.ui.artists.ArtistsFragment;
import app.sonu.com.musicplayer.ui.playlists.PlaylistsFragment;
import app.sonu.com.musicplayer.ui.search.SearchActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 15/9/17.
 */

public class MediaListsFragment
        extends BaseFragment<MediaListsMvpPresenter>
        implements MediaListsMvpView {

    private static final String TAG = MediaListsFragment.class.getSimpleName();

    private MediaListsViewPagerAdapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.vp)
    ViewPager vp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:called");

        ((MusicPlayerHolderFragment)getParentFragment())
                .getMusicPlayerHolderComponent()
                .fragmentComponentBuilder()
                .fragmentModule(new FragmentModule())
                .build()
                .inject(this);

        mPresenter.onCreate(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:called");

        View view = inflater.inflate(R.layout.fragment_medialists,
                container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //setting up media browser tabs
        if (mAdapter == null) {
            Log.d(TAG, "adapter is null:called");
            mAdapter =
                    new MediaListsViewPagerAdapter(getChildFragmentManager());
            mAdapter.addFragment(new AllSongsFragment(), AllSongsFragment.TAB_TITLE);
            mAdapter.addFragment(new AlbumsFragment(), AlbumsFragment.TAB_TITLE);
            mAdapter.addFragment(new ArtistsFragment(), ArtistsFragment.TAB_TITLE);
            mAdapter.addFragment(new PlaylistsFragment(), PlaylistsFragment.TAB_TITLE);
            vp.setAdapter(mAdapter);
        }

        vp.clearOnPageChangeListeners();

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPresenter.onPageSelected(position);
//                if (position == 3) {
//                    fab.show();
//                } else {
//                    fab.hide();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        toolbar.inflateMenu(R.menu.main_activity_options_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuItemSearch:
                        getActivity().startActivity(new Intent(getActivity(), SearchActivity.class));
                }
                return false;
            }
        });

        fab.hide();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setMedialistSelected(int position) {
        vp.setCurrentItem(position);
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
    }
}
