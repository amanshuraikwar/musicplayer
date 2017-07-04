package app.sonu.com.musicplayer.ui.main;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BaseActivity;
import app.sonu.com.musicplayer.data.db.model.Song;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends BaseActivity<MainMvpPresenter> implements MainMvpView{

    String TAG = MainActivity.class.getSimpleName();
    private ActionBarDrawerToggle mDrawerToggle;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(app.sonu.com.musicplayer.R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DaggerUiComponent.builder()
                .uiModule(new UiModule(this))
                .applicationComponent(((MyApplication)getApplicationContext())
                        .getApplicationComponent())
                .build()
                .inject(this);

        ButterKnife.bind(this);

//        Song song = new Song();
//        song.setName("abcd");
//        song.setPath("abcd");
//        song.save();
//
//        List<Song> allSongs = DataSupport.findAll(Song.class);
//
//        for (Song s : allSongs) {
//            Log.d(TAG, "onCreate:querying songs="+s);
//        }

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
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
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

