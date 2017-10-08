package app.sonu.com.musicplayer.ui.createplaylist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.di.component.DaggerMusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.component.MusicPlayerHolderComponent;
import app.sonu.com.musicplayer.di.module.FragmentModule;
import app.sonu.com.musicplayer.di.module.MusicPlayerHolderModule;
import app.sonu.com.musicplayer.ui.base.BaseDialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 23/9/17.
 */

public class CreatePlaylistFragment extends BaseDialogFragment<CreatePlaylistMvpPresenter>
        implements CreatePlaylistMvpView {

    private static final String TAG = CreatePlaylistFragment.class.getSimpleName();

    @BindView(R.id.playlistTitleEt)
    EditText playlistTitleEt;

    @BindView(R.id.createBtn)
    Button createBtn;

    @BindView(R.id.cancelBtn)
    Button cancelBtn;

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
        View view = inflater.inflate(R.layout.fragment_create_playlist, container, false);
        ButterKnife.bind(this, view);

        playlistTitleEt.requestFocus();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCreateBtnClick(playlistTitleEt.getText().toString());
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCancelBtnClick();
            }
        });

        mPresenter.onCreateView();

        return view;
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void close() {
        dismiss();
    }
}
