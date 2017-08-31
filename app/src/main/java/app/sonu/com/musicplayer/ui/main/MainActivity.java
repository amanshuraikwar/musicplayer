package app.sonu.com.musicplayer.ui.main;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BaseActivity;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.mediaplayernew.musicsource.MusicProviderSource;
import app.sonu.com.musicplayer.ui.about.AboutActivity;
import app.sonu.com.musicplayer.ui.album.AlbumFragment;
import app.sonu.com.musicplayer.ui.albums.AlbumsFragment;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsFragment;
import app.sonu.com.musicplayer.ui.artist.ArtistFragment;
import app.sonu.com.musicplayer.ui.artists.ArtistsFragment;
import app.sonu.com.musicplayer.ui.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.ui.list.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.ui.list.onclicklistener.AlbumSearchResultOnClickListener;
import app.sonu.com.musicplayer.ui.list.onclicklistener.ArtistSearchResultOnClickListener;
import app.sonu.com.musicplayer.ui.list.onclicklistener.SearchResultOnClickListener;
import app.sonu.com.musicplayer.ui.list.onclicklistener.SongOnClickListener;
import app.sonu.com.musicplayer.ui.list.onclicklistener.SongSearchResultOnClickListener;
import app.sonu.com.musicplayer.ui.list.visitable.AlbumSearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.ArtistSearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SearchItemTypeTitleVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SongSearchResultVisitable;
import app.sonu.com.musicplayer.ui.list.visitable.SongVisitable;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends BaseActivity<MainMvpPresenter>
        implements MainMvpView, SlidingUpPaneCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AlbumFragment mAlbumFragment;
    private ArtistFragment mArtistFragment;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.slidingUpPaneLayout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    @BindView(R.id.mediaListsVp)
    ViewPager mediaListVp;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.miniBarRl)
    View miniBarRl;

    @BindView(R.id.upperFragmentFl)
    FrameLayout upperFragmentFl;

    @BindView(R.id.searchResultsRv)
    RecyclerView searchResultsRv;

    @BindView(R.id.searchBackIb)
    ImageButton searchBackIb;

    @BindView(R.id.searchClearIb)
    ImageButton searchClearIb;

    @BindView(R.id.searchQueryEt)
    EditText searchQueryEt;

    @BindView(R.id.searchViewParentLl)
    View searchViewParentLl;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    private SongSearchResultOnClickListener mSongSearchResultOnClickListener = new SongSearchResultOnClickListener() {
        @Override
        public void onSearchResultClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onSongSearchResultClick(item);
        }

        @Override
        public void OnClick() {

        }
    };

    private AlbumSearchResultOnClickListener mAlbumSearchResultOnClickListener = new AlbumSearchResultOnClickListener() {
        @Override
        public void onSearchResultClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onAlbumSearchResultClick(item);
        }

        @Override
        public void OnClick() {

        }
    };

    private ArtistSearchResultOnClickListener mArtistSearchResultOnClickListener = new ArtistSearchResultOnClickListener() {
        @Override
        public void onSearchResultClick(MediaBrowserCompat.MediaItem item) {
            mPresenter.onArtistSearchResultClick(item);
        }

        @Override
        public void OnClick() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:called");

        setContentView(app.sonu.com.musicplayer.R.layout.activity_main);

        DaggerUiComponent.builder()
                .uiModule(new UiModule(this))
                .applicationComponent(((MyApplication)getApplicationContext())
                        .getApplicationComponent())
                .build()
                .inject(this);

        ButterKnife.bind(this);

        mPresenter.onCreate(this);

        //setting toolbar as actionbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //setting hamburger icon for drawer
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_grey_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));

        //setting up media browser tabs
        MediaBrowserPagerAdapter adapter = new MediaBrowserPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllSongsFragment(), AllSongsFragment.TAB_TITLE);
        adapter.addFragment(new AlbumsFragment(), AlbumsFragment.TAB_TITLE);
        adapter.addFragment(new ArtistsFragment(), ArtistsFragment.TAB_TITLE);
        mediaListVp.setAdapter(adapter);

        //making tabs work with viewpager
        tabLayout.setupWithViewPager(mediaListVp);

        if (searchResultsRv.getLayoutManager() == null) {
            searchResultsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }

        searchQueryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    searchClearIb.setVisibility(View.GONE);
                } else {
                    searchClearIb.setVisibility(View.VISIBLE);
                }

                mPresenter.onSearchQueryTextChange(s.toString());
            }
        });

        searchBackIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQueryEt.setText("");
                searchViewParentLl.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchQueryEt.getWindowToken(), 0);
            }
        });

        searchViewParentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQueryEt.setText("");
                searchViewParentLl.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchQueryEt.getWindowToken(), 0);
            }
        });

        searchClearIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQueryEt.setText("");
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuItemAbout:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                //limiting method calls
                if ((slideOffset <= 0.1) || (slideOffset >= 0.9)) {
//                    Log.i(TAG, "onPanelSlide:called slideOffset="+slideOffset);
                    mPresenter.onSlidingUpPanelSlide(slideOffset);
                }
            }

            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                //todo figure out better way
                //to handle unexpected behavior with nested sliding up pane layout
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                    slidingUpPanelLayout.setDragView(miniBarRl);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    slidingUpPanelLayout.setDragView(dragView);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_options_menu, menu);
//        MenuItem item = menu.findItem(R.id.search);
//        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Log.d(TAG, "onOptionsItemSelected:called");

        switch (id) {

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.search:
                Log.d(TAG, "onOptionsItemSelected:called for search");
                searchViewParentLl.setVisibility(View.VISIBLE);
                searchQueryEt.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchQueryEt, 0);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setSlidingUpPaneCollapsed() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void setSlidingUpPaneExpanded() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void setSlidingUpPaneHidden() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public boolean isSlidingUpPaneHidden() {
        return slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN;
    }

    @Override
    public void hideMiniPlayer() {
        miniBarRl.setVisibility(GONE);
    }

    @Override
    public void showMiniPlayer() {
        miniBarRl.setVisibility(View.VISIBLE);
    }

    @Override
    public void startAlbumFragment(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "startAlbumFragment:called");

        if (mAlbumFragment == null) {
            mAlbumFragment = new AlbumFragment();
        }

        Bundle args = new Bundle();
        args.putParcelable("mediaItem", item);
        mAlbumFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(AlbumFragment.BACK_STACK_TAG, 0);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.upperFragmentFl, mAlbumFragment);
        fragmentTransaction.addToBackStack(AlbumFragment.BACK_STACK_TAG);
        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState:called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void startArtistFragment(MediaBrowserCompat.MediaItem item) {
        if (mArtistFragment == null) {
            mArtistFragment = new ArtistFragment();
        }

        Bundle args = new Bundle();
        args.putParcelable("mediaItem", item);
        mArtistFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(ArtistFragment.BACK_STACK_TAG, 0);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.upperFragmentFl, mArtistFragment);
        fragmentTransaction.addToBackStack(ArtistFragment.BACK_STACK_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void displaySearchResults(List<MediaBrowserCompat.MediaItem> itemList) {
        searchResultsRv.setAdapter(
                new MediaRecyclerViewAdapter(getVisitableList(itemList),
                        new MediaListTypeFactory()));
        searchResultsRv.invalidate();
    }

    @Override
    public void displayToast(String message) {
        //todo
    }

    @Override
    public void hideSearchView() {
        searchViewParentLl.setVisibility(View.GONE);
    }

    /**
     * this method is defined in fragment because of attached onclicklistener
     * @param itemList
     * @return
     */
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> itemList) {
        List<BaseVisitable> visitableList = new ArrayList<>();
        String currentItemType = "";
        for (MediaBrowserCompat.MediaItem item : itemList) {

            String itemType = item.getDescription()
                    .getExtras()
                    .getString(MusicProviderSource.CUSTOM_METADATA_KEY_SEARCH_ITEM_TYPE);

            if (itemType == null) {
                Log.w(TAG, "getVisitableList:item type is null");
                continue;
            }

            if (!currentItemType.equals(itemType)) {
                currentItemType = itemType;
                visitableList.add(new SearchItemTypeTitleVisitable(itemType));
            }

            BaseVisitable visitable = null;

            switch (itemType) {
                case MusicProviderSource.SEARCH_RESULT_ITEM_TYPE_SONG:
                    visitable = new SongSearchResultVisitable(item);
                    visitable.setOnClickListener(mSongSearchResultOnClickListener);
                    break;
                case MusicProviderSource.SEARCH_RESULT_ITEM_TYPE_ALBUM:
                    visitable = new AlbumSearchResultVisitable(item);
                    visitable.setOnClickListener(mAlbumSearchResultOnClickListener);
                    break;
                case MusicProviderSource.SEARCH_RESULT_ITEM_TYPE_ARTIST:
                    visitable = new ArtistSearchResultVisitable(item);
                    visitable.setOnClickListener(mArtistSearchResultOnClickListener);
                    break;
            }

            if (visitable != null) {
                visitableList.add(visitable);
            } else {
                Log.w(TAG, "getVisitableList:item type is invalid");
            }

        }

        return visitableList;
    }

    //todo figure out better way
    private View dragView;
    private View antiDragView;
    @Override
    public void setDragView(View view) {
        dragView = view;
    }

    @Override
    public void setDragViewNow(View view) {
        dragView = view;
        slidingUpPanelLayout.setDragView(dragView);
    }

    @Override
    public void setAntiDragViewNow(View view) {
        slidingUpPanelLayout.setAntiDragView(view);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed:called");
        if (searchViewParentLl.getVisibility()==View.VISIBLE) {
            searchViewParentLl.setVisibility(View.GONE);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }
}

