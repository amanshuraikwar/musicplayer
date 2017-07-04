package app.sonu.com.musicplayer.ui.fileview;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import app.sonu.com.musicplayer.MyApplication;
import app.sonu.com.musicplayer.R;
import app.sonu.com.musicplayer.base.list.BaseVisitable;
import app.sonu.com.musicplayer.base.ui.BaseFragment;
import app.sonu.com.musicplayer.di.component.DaggerUiComponent;
import app.sonu.com.musicplayer.di.module.UiModule;

import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.sonu.com.musicplayer.ui.fileview.list.FileViewListTypeFactory;
import app.sonu.com.musicplayer.ui.fileview.list.FileViewRecycleViewAdapter;
import app.sonu.com.musicplayer.ui.fileview.list.onclicklisteners.FolderOnClickListener;
import app.sonu.com.musicplayer.ui.fileview.list.visitables.FileVisitable;
import app.sonu.com.musicplayer.ui.fileview.list.visitables.FolderVisitable;
import app.sonu.com.musicplayer.utils.FileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sonu on 30/6/17.
 */

public class FileViewFragment extends BaseFragment<FileViewMvpPresenter> implements
        FileViewMvpView {

    String TAG = FileViewFragment.class.getSimpleName();

    FolderOnClickListener folderOnClickListener = new FolderOnClickListener() {
        @Override
        public void onFolderClick(String pathOfFolder) {
            mPresenter.folderItemOnClick(pathOfFolder);
        }

        @Override
        public void OnClick() {

        }
    };

    @BindView(R.id.filesRv)
    RecyclerView filesRv;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        filesRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume:is presenter's view null="+(mPresenter.getMvpView()==null));
    }

    @Override
    public void showFileList(List<File> fileList) {
        filesRv.setAdapter(
                new FileViewRecycleViewAdapter(getVisitableList(fileList),
                        new FileViewListTypeFactory()));
        filesRv.invalidate();
    }

    private List<BaseVisitable> getVisitableList(List<File> fileList) {
        List<BaseVisitable> visitableList = new ArrayList<>();

        for (File file : fileList) {
            if(file.isDirectory()) {
                FolderVisitable folderVisitable = new FolderVisitable(file);
                folderVisitable.setOnClickListener(folderOnClickListener);
                visitableList.add(folderVisitable);
            } else {
                FileVisitable fileVisitable = new FileVisitable(file);
                fileVisitable.setPlayDuration(
                        FileUtil.getAudioFileDuration(
                                fileVisitable.getCoreFile().getPath(), getActivity()));
                visitableList.add(new FileVisitable(file));
            }
        }

        return visitableList;
    }
}
