package app.sonu.com.musicplayer.ui.main;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BaseActivity;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.ui.albums.AlbumsFragment;
import app.sonu.com.musicplayer.ui.allsongs.AllSongsFragment;
import app.sonu.com.musicplayer.ui.artists.ArtistsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends BaseActivity<MainMvpPresenter>
        implements MainMvpView, SlidingUpPaneCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

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

        //setting toolbar as actionbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //setting hamburger icon for drawer
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_charcoal_24dp);
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

        //todo remove after designing drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

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
                    slidingUpPanelLayout.setDragView(miniBarRl);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    slidingUpPanelLayout.setDragView(dragView);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                //todo uncomment after designing drawer
                //drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.search:
                //todo implement
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
        miniBarRl.setVisibility(View.GONE);
    }

    @Override
    public void showMiniPlayer() {
        miniBarRl.setVisibility(View.VISIBLE);
    }

    //todo figure out better way
    private View dragView;
    @Override
    public void setDragView(View view) {
        dragView = view;
    }

    @Override
    public void setDragViewNow(View view) {
        dragView = view;
        slidingUpPanelLayout.setDragView(dragView);
    }
}

