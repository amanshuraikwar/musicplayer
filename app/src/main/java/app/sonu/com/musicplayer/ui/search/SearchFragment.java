
package app.sonu.com.musicplayer.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.list.MediaListTypeFactory;
import app.sonu.com.musicplayer.list.adapter.MediaRecyclerViewAdapter;
import app.sonu.com.musicplayer.list.base.BaseVisitable;
import app.sonu.com.musicplayer.list.onclicklistener.AlbumSearchResultOnClickListener;
import app.sonu.com.musicplayer.list.onclicklistener.ArtistSearchResultOnClickListener;
import app.sonu.com.musicplayer.list.onclicklistener.SongSearchResultOnClickListener;
import app.sonu.com.musicplayer.list.visitable.AlbumSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.ArtistSearchResultVisitable;
import app.sonu.com.musicplayer.list.visitable.MediaListHeaderPaddedVisitable;
import app.sonu.com.musicplayer.list.visitable.SongSearchResultVisitable;
import app.sonu.com.musicplayer.ui.base.BaseFragment;
import app.sonu.com.musicplayer.util.SearchHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 21/9/17.
 */

public class SearchFragment extends BaseFragment<SearchMvpPresenter> implements SearchMvpView {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @BindView(R.id.itemsRv)
    RecyclerView itemsRv;

    @BindView(R.id.backIb)
    ImageButton backIb;

    @BindView(R.id.clearIb)
    ImageButton clearIb;

    @BindView(R.id.searchQueryEt)
    EditText searchQueryEt;

    private SongSearchResultOnClickListener mSongSearchResultOnClickListener =
            new SongSearchResultOnClickListener() {
                @Override
                public void onSearchResultClick(MediaBrowserCompat.MediaItem item) {
                    mPresenter.onSongClicked(item);
                }

                @Override
                public void OnClick() {

                }
            };

    private AlbumSearchResultOnClickListener mAlbumSearchResultOnClickListener =
            new AlbumSearchResultOnClickListener() {
                @Override
                public void onSearchResultClick(MediaBrowserCompat.MediaItem item, View animatingView) {
                    mPresenter.onAlbumClicked(item, animatingView);
                }

                @Override
                public void OnClick() {

                }
            };

    private ArtistSearchResultOnClickListener mArtistSearchResultOnClickListener =
            new ArtistSearchResultOnClickListener() {
                @Override
                public void onSearchResultClick(MediaBrowserCompat.MediaItem item, View animatingView) {
                    mPresenter.onArtistClicked(item, animatingView);
                }

                @Override
                public void OnClick() {

                }
            };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:called");

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:called");
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        if (itemsRv.getLayoutManager() == null) {
            itemsRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        }

        mPresenter.onCreateView();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                    clearIb.setVisibility(View.GONE);
                } else {
                    clearIb.setVisibility(View.VISIBLE);
                }

                mPresenter.onSearchQueryTextChange(s.toString());
            }
        });

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onBackIbClick();
            }
        });

        clearIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQueryEt.setText("");
            }
        });

        itemsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchQueryEt.getWindowToken(), 0);
                }
            }
        });
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
                new MediaRecyclerViewAdapter(getActivity(),
                        new MediaListTypeFactory(), getVisitableList(itemList)));
    }

    /**
     * this method is defined in fragment because of attached onclicklistener
     * @param itemList
     * @return
     */
    private List<BaseVisitable> getVisitableList(List<MediaBrowserCompat.MediaItem> itemList) {
        List<BaseVisitable> visitableList = new ArrayList<>();

        String currentItemHeader = "";
        for (MediaBrowserCompat.MediaItem item : itemList) {

            String itemHeader = SearchHelper.getSearchItemHeader(item.getDescription().getMediaId());

            if (itemHeader == null) {
                Log.w(TAG, "getVisitableList:item type is null");
                continue;
            }

            if (!currentItemHeader.equals(itemHeader)) {
                currentItemHeader = itemHeader;
                visitableList.add(new MediaListHeaderPaddedVisitable(itemHeader));
            }

            BaseVisitable visitable = null;

            switch (itemHeader) {
                case SearchHelper.SONGS_HEADER:
                    visitable = new SongSearchResultVisitable(item);
                    visitable.setOnClickListener(mSongSearchResultOnClickListener);
                    break;
                case SearchHelper.ALBUMS_HEADER:
                    visitable = new AlbumSearchResultVisitable(item);
                    visitable.setOnClickListener(mAlbumSearchResultOnClickListener);
                    break;
                case SearchHelper.ARTISTS_HEADER:
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

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollListToTop() {
        itemsRv.smoothScrollToPosition(0);
    }

    @Override
    public void close() {
        getActivity().finish();
    }
}