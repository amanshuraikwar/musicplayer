package app.sonu.com.musicplayer.ui.miniplayer;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.ui.BaseFragment;
import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.utils.ColorUtil;
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

    @OnClick(R.id.miniPlayerNavUpIv)
    void onNavUpClicked() {
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

        DaggerUiComponent.builder()
                .uiModule(new UiModule(getActivity()))
                .applicationComponent(((MyApplication)getActivity().getApplicationContext())
                        .getApplicationComponent())
                .build()
                .inject(this);

        Log.d(TAG, "onCreate:is presenter null="+(mPresenter==null));

        mPresenter.onCreate(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy:called");
        mPresenter.onDestroy();
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
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Bitmap> target,
                                                    boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource,
                                                       Object model,
                                                       Target<Bitmap> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            miniPlayerMpb.setProgressTintList(
                                    ColorStateList.valueOf(
                                            ColorUtil.getColor(
                                                            ColorUtil.generatePalette(resource),
                                                            Color.DKGRAY)));
//                            miniPlayerPlayPauseIv
//                                    .setColorFilter(
//                                            ColorUtil.getColor(
//                                                    ColorUtil.generatePalette(resource),
//                                                    Color.DKGRAY),
//                                            PorterDuff.Mode.SRC_IN);
                            return false;
                        }
                    })
                    .into(miniPlayerNavUpIv);
        } else {
            Glide.with(getActivity()).clear(miniPlayerNavUpIv);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                miniPlayerNavUpIv.setImageDrawable(
                        getActivity().getDrawable(R.drawable.default_album_art_note));
            } else {
                miniPlayerNavUpIv.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.default_album_art_note));
            }
        }
    }

    @Override
    public void showPauseIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_grey_24dp, null));
        } else {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_grey_24dp));
        }
    }

    @Override
    public void showPlayIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_arrow_grey_24dp, null));
        } else {
            miniPlayerPlayPauseIv.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_arrow_grey_24dp));
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
}
