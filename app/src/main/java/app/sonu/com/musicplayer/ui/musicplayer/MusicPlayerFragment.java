package app.sonu.com.musicplayer.ui.musicplayer;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BaseFragment;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.QueueRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.onclicklistener.QueueItemOnClickListener;
import app.sonu.com.musicplayer.list.visitable.QueueItemVisitable;
import app.sonu.com.musicplayer.ui.main.SlidingUpPaneCallback;
import app.sonu.com.musicplayer.util.ColorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sonu on 4/7/17.
 */

public class MusicPlayerFragment extends BaseFragment<MusicPlayerMvpPresenter>
        implements MusicPlayerMvpView {

    String TAG = MusicPlayerFragment.class.getSimpleName();

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

    private QueueRecyclerViewAdapter mQueueAdapter;

    private int mCurrentQueueIndex;
    private LinearLayoutManager mQueueLayoutManager;

    @BindView(R.id.musicPlayerParentLl)
    View musicPlayerParent;

    @BindView(R.id.playPauseIb)
    ImageButton playPauseIb;

    @BindView(R.id.skipNextIb)
    ImageButton skipNextIb;

    @BindView(R.id.skipPreviousIb)
    ImageButton skipPreviousButton;

    @BindView(R.id.songTitleMainTv)
    TextView songTitleMainTv;

    @BindView(R.id.songArtistMainTv)
    TextView songArtistMainTv;

    @BindView(R.id.songCurrentPositionSeekBar)
    SeekBar songCurrentPositionSeekBar;

    @BindView(R.id.elapsedTimeTv)
    TextView elapsedTimeTv;

    @BindView(R.id.totalTimeTv)
    TextView totalTimeTv;

    @BindView(R.id.musicPlayerSupl)
    SlidingUpPanelLayout musicPlayerSupl;

    @BindView(R.id.musicPlayerUpperHalfRl)
    View musicPlayerUpperHalfRl;

    @BindView(R.id.shuffleIb)
    ImageButton shuffleIb;

    @BindView(R.id.repeatIb)
    ImageButton repeatIb;

    @BindView(R.id.playingQueueRv)
    RecyclerView playingQueueRv;

    @BindView(R.id.titleTextTv)
    TextView titleTextTv;

    @BindView(R.id.bodyTextTv)
    TextView bodyTextTv;

    @BindView(R.id.backgroundTextTv)
    TextView backgroundTextTv;

    @BindView(R.id.topBarLl)
    View topBarLl;

    @BindView(R.id.topBarTitleTv)
    TextView topBarTitleTv;

    @BindView(R.id.moreOptionsIv)
    ImageView moreOptionsIv;

    @BindView(R.id.musicPlayerLowerHalfLl)
    View musicPlayerLowerHalfLl;

    @BindView(R.id.collapseIv)
    ImageView collapseIv;

    @BindView(R.id.heartIv)
    ImageView heartIv;

    @BindView(R.id.musicPlayerMainLl)
    View musicPlayerMainLl;

    @BindView(R.id.gotoAlbumIv)
    ImageView gotoAlbumIv;

    @BindView(R.id.gotoArtistIv)
    ImageView gotoArtistIv;

    @BindView(R.id.smallAlbumArtIv)
    ImageView smallAlbumArtIv;

    @BindView(R.id.smallAlbumArtCv)
    CardView smallAlbumArtCv;

    @OnClick(R.id.playPauseIb)
    void onPlayPauseIbClick(){
        Log.d(TAG, "playPauseIb onClick:called");
        mPresenter.playPauseButtonOnClick();
    }

    @OnClick(R.id.skipNextIb)
    void onSkipNextIbClick(){
        Log.d(TAG, "skipNextIb onClick:called");
        mPresenter.skipNextButtonOnClick();
    }

    @OnClick(R.id.skipPreviousIb)
    void onSkipPreviousIbClick(){
        Log.d(TAG, "skipPreviousIb onClick:called");
        mPresenter.skipPreviousButtonOnClick();
    }

    @OnClick(R.id.shuffleIb)
    void onShuffleIbClick() {
        mPresenter.onShuffleButtonClick();
    }

    @OnClick(R.id.repeatIb)
    void onRepeatIbClick() {
        mPresenter.onRepeatButtonClick();
    }

    @BindView(R.id.albumArtIv)
    ImageView albumArtIv;

    @OnClick(R.id.collapseIv)
    void onCollapseIvClick() {
        mPresenter.onCollapseIvClick();
    }

    private QueueItemOnClickListener mQueueItemOnClickListener = new QueueItemOnClickListener() {
        @Override
        public void onQueueItemClick(MediaSessionCompat.QueueItem item) {
            Log.d(TAG, "onQueueItemClick:called");
            mPresenter.onQueueItemClick(item);
        }

        @Override
        public void OnClick() {
            //nothing
        }
    };

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        ButterKnife.bind(this, view);

        mPresenter.onCreateView();

        musicPlayerSupl.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    smallAlbumArtCv.setVisibility(View.VISIBLE);
                } else {
                    smallAlbumArtCv.setVisibility(View.GONE);
                    if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        mQueueLayoutManager.scrollToPositionWithOffset(mCurrentQueueIndex, 0);
                    }
                }
            }
        });

        songCurrentPositionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                elapsedTimeTv.setText(DateUtils.formatElapsedTime(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPresenter.onSeekbarStopTrackingTouch(seekBar.getProgress());
            }
        });

        if (playingQueueRv.getLayoutManager() == null) {
            mQueueLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            playingQueueRv.setLayoutManager(mQueueLayoutManager);
        }

        heartIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onHeartIvClick();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void displaySong(String songTitle,
                            String songSubtitle,
                            String songDuration,
                            String albumArtPath) {
        songTitleMainTv.setText(songTitle);
        songArtistMainTv.setText(songSubtitle);

        Glide.with(getActivity()).clear(albumArtIv);

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

                            updateUiColor(resource);
                            return false;
                        }
                    })
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(albumArtIv);

            Glide.with(getActivity())
                    .load(albumArtPath)
                    .apply(options)
                    .into(smallAlbumArtIv);
        } else {
            Glide.with(getActivity()).clear(albumArtIv);
            Glide.with(getActivity()).clear(smallAlbumArtIv);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                albumArtIv.setImageDrawable(getActivity()
                        .getDrawable(R.drawable.default_song_art));
                smallAlbumArtIv.setImageDrawable(getActivity()
                        .getDrawable(R.drawable.default_song_art));
            } else {
                albumArtIv.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.default_song_art));
                smallAlbumArtIv.setImageDrawable(
                        getActivity()
                                .getResources()
                                .getDrawable(R.drawable.default_song_art));
            }
            updateUiColor(null);
        }

        totalTimeTv.setText(songDuration);

        ((SlidingUpPaneCallback) getActivity()).setAntiDragViewNow(musicPlayerLowerHalfLl);
        ((SlidingUpPaneCallback) getActivity()).setPaneLayout(musicPlayerSupl);
    }

    private void updateUiColor(Bitmap resource) {

        if (resource == null) {
            setUiColorWithSwatch(null);
        } else {
            ColorUtil.generatePalette(resource, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    setUiColorWithSwatch(ColorUtil.getColorSwatch(palette));
                }
            });
        }
    }

    private void setUiColorWithSwatch(Palette.Swatch swatch) {

        int backgroundColor = ColorUtil.getBackgroundColor(swatch);
        int titleColor = ColorUtil.getTitleColor(swatch);
        int bodyColor = ColorUtil.getBodyColor(swatch);

        titleTextTv.setBackgroundColor(titleColor);
        bodyTextTv.setBackgroundColor(bodyColor);
        backgroundTextTv.setBackgroundColor(backgroundColor);

        gotoAlbumIv.setColorFilter(titleColor,PorterDuff.Mode.SRC_IN);
        gotoArtistIv.setColorFilter(titleColor,PorterDuff.Mode.SRC_IN);

        topBarTitleTv.setTextColor(titleColor);
        musicPlayerLowerHalfLl.setBackgroundColor(backgroundColor);
        songTitleMainTv.setTextColor(titleColor);
        songArtistMainTv.setTextColor(bodyColor);

        elapsedTimeTv.setTextColor(bodyColor);
        totalTimeTv.setTextColor(bodyColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            songCurrentPositionSeekBar.setProgressBackgroundTintList(ColorStateList.valueOf(bodyColor));
            songCurrentPositionSeekBar.setProgressTintList(ColorStateList.valueOf(titleColor));
            songCurrentPositionSeekBar.setThumbTintList(ColorStateList.valueOf(titleColor));
        }
    }

    @Override
    public void showPlayIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_arrow_black_48dp, null));
        } else {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_play_arrow_black_48dp));
        }
    }

    @Override
    public void showPauseIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_black_48dp, null));
        } else {
            playPauseIb.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_pause_black_48dp));
        }
    }

    @Override
    public void setSeekBarPosition(int position) {
        songCurrentPositionSeekBar.setProgress(position);
    }

    @Override
    public void setElapsedTime(int position) {
        elapsedTimeTv.setText(DateUtils.formatElapsedTime(position/1000));
    }

    @Override
    public void updateDuration(long dur) {
        Log.d(TAG, "updateDuration:called");
        int duration = (int) dur;
        songCurrentPositionSeekBar.setMax(duration);
        totalTimeTv.setText(DateUtils.formatElapsedTime(duration/1000));

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
    public void setShuffleModeEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shuffleIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_shuffle_grey_24dp));
        } else {
            shuffleIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_shuffle_grey_24dp));
        }
    }

    @Override
    public void setShuffleModeDisabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shuffleIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_shuffle_light_grey_24dp));
        } else {
            shuffleIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_shuffle_light_grey_24dp));
        }
    }

    @Override
    public void displayQueue(List<MediaSessionCompat.QueueItem> queue) {
        mQueueAdapter = new QueueRecyclerViewAdapter(getVisitableList(queue),
                new MediaListTypeFactory());
        playingQueueRv.setAdapter(mQueueAdapter);
        playingQueueRv.invalidate();

        updateQueueIndex(mCurrentQueueIndex);
    }

    @Override
    public void setRepeatModeNone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_light_grey_24dp));
        } else {
            repeatIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_light_grey_24dp));
        }
    }

    @Override
    public void setRepeatModeAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_grey_24dp));
        } else {
            repeatIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_grey_24dp));
        }
    }

    @Override
    public void setRepeatModeOne() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            repeatIb
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_repeat_one_grey_24dp));
        } else {
            repeatIb.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_repeat_one_grey_24dp));
        }
    }

    @Override
    public void resetSeekbar() {
        songCurrentPositionSeekBar.setProgress(0);
    }

    @Override
    public boolean updateQueueIndex(int index) {
        if (mQueueAdapter != null) {
            mCurrentQueueIndex = index;
            mQueueAdapter.updateQueueIndex(index);
            mQueueLayoutManager.scrollToPositionWithOffset(index, 0);
            return true;
        } else {
            mCurrentQueueIndex = index;
            return false;
        }
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFavButtonEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            heartIv
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_heart_solid_light_pink_24dp));
        } else {
            heartIv.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_heart_solid_light_pink_24dp));
        }
    }

    @Override
    public void showFavButtonDisabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            heartIv
                    .setImageDrawable(
                            getActivity().getDrawable(R.drawable.ic_heart_outline_white_24dp));
        } else {
            heartIv.setImageDrawable(
                    getActivity()
                            .getResources()
                            .getDrawable(R.drawable.ic_heart_outline_white_24dp));
        }
    }

    private List<BaseVisitable> getVisitableList(List<MediaSessionCompat.QueueItem> queue) {
        List<BaseVisitable> visitableList = new ArrayList<>();
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            QueueItemVisitable visitable = new QueueItemVisitable(item);
            visitable.setOnClickListener(mQueueItemOnClickListener);
            visitable.setIndexToDisplay(index);
            visitableList.add(visitable);
            index++;
        }

        return visitableList;
    }
}
