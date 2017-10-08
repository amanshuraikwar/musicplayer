package app.sonu.com.musicplayer.ui.miniplayer;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.ui.base.BaseFragment;

import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by sonu on 4/8/17.
 */

public class MiniPlayerFragment extends BaseFragment<MiniPlayerMvpPresenter>
        implements MiniPlayerMvpView {

    private static final String TAG = MiniPlayerFragment.class.getSimpleName();

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            mPresenter.updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;

    @BindView(R.id.miniPlayerSongTitleTv)
    TextView miniPlayerSongTitleTv;

    @BindView(R.id.miniPlayerPlayPauseIv)
    ImageView miniPlayerPlayPauseIv;

    @BindView(R.id.miniPlayerMpb)
    MaterialProgressBar miniPlayerMpb;

    @BindView(R.id.miniPlayerNavUpIv)
    ImageView miniPlayerNavUpIv;

    @BindView(R.id.shuffleNotifyIv)
    ImageView shuffleNotifyIv;

    @BindView(R.id.repeatNotifyIv)
    ImageView repeatNotifyIv;

    @OnClick(R.id.miniPlayerNavUpIv)
    void onNavUpClicked() {
        Log.d(TAG, "navUp:clicked");
        mPresenter.onNavUpClick();
    }

    @OnClick(R.id.miniPlayerRl)
    void onMiniPlayerRlClick() {
        Log.d(TAG, "navUp:clicked");
        mPresenter.onNavUpClick();
    }

    @OnClick(R.id.miniPlayerPlayPauseIv)
    void onPlayPauseIbClick(){
        Log.d(TAG, "playPauseIb onClick:called");
        mPresenter.playPauseButtonOnClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);
        ButterKnife.bind(this, view);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void displaySong(String title, String albumArtPath) {
        miniPlayerSongTitleTv.setText(title);

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (albumArtPath != null) {
            Glide.with(getActivity())
                    .asBitmap()
                    .load(albumArtPath)
                    .apply(options)
                    .into(miniPlayerNavUpIv);
        } else {
            Glide.with(getActivity()).clear(miniPlayerNavUpIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                miniPlayerNavUpIv.setImageDrawable(
                        getActivity().getDrawable(R.drawable.default_song_art));
            } else {
                miniPlayerNavUpIv.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.default_song_art));
            }
        }
    }

    @Override
    public void showPauseIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_white_48dp, null));
        } else {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_white_48dp));
        }
    }

    @Override
    public void showPlayIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_arrow_white_48dp, null));
        } else {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_arrow_white_48dp));
        }
    }

    @Override
    public void setSeekBarPosition(int position) {
        miniPlayerMpb.setProgress(position);
    }

    @Override
    public void updateDuration(long dur) {
        Log.d(TAG, "updateDuration:called");
        int duration = (int) dur;
        miniPlayerMpb.setMax(duration);

        Log.i(TAG, "updateDuration:total="+DateUtils.formatElapsedTime(duration/1000));
    }

    @Override
    public void scheduleSeekbarUpdate() {
        Log.d(TAG, "scheduleSeekbarUpdate:called");
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            Log.d(TAG, "scheduleSeekbarUpdate:isnotshutdown");
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    @Override
    public void resetSeekbar() {
        miniPlayerMpb.setProgress(0);
    }

    @Override
    public void setShuffleModeEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shuffleNotifyIv
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_shuffle_black_12dp));
        } else {
            shuffleNotifyIv.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_shuffle_black_12dp));
        }
    }

    @Override
    public void setShuffleModeDisabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shuffleNotifyIv
                    .setImageDrawable(null);
        } else {
            shuffleNotifyIv.setImageDrawable(null);
        }
    }

    @Override
    public void setRepeatModeNone() {
        repeatNotifyIv.setVisibility(View.GONE);
    }

    @Override
    public void setRepeatModeAll() {
        repeatNotifyIv.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatNotifyIv
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_black_12dp));
        } else {
            repeatNotifyIv.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_black_12dp));
        }
    }

    @Override
    public void setRepeatModeOne() {
        repeatNotifyIv.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatNotifyIv
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_one_black_12dp));
        } else {
            repeatNotifyIv.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_one_black_12dp));
        }
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
