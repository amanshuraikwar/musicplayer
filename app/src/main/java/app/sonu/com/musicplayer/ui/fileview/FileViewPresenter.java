package app.sonu.com.musicplayer.ui.fileview;

import java.io.File;
import java.util.List;

import app.sonu.com.musicplayer.base.ui.BasePresenter;
import app.sonu.com.musicplayer.data.DataManager;

/**
 * Created by sonu on 30/6/17.
 */

public class FileViewPresenter extends BasePresenter<FileViewMvpView> implements FileViewMvpPresenter {

    public FileViewPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onStart() {
        mMvpView.showFileList(getFileList());
    }

    @Override
    public List<File> getFileList() {
        return mDataManager.getFileList();
    }

    @Override
    public List<File> getFileList(String pathOfFolder) {
        return mDataManager.getFileList(pathOfFolder);
    }

    @Override
    public void folderItemOnClick(String pathOfFolder) {
        mMvpView.showFileList(getFileList(pathOfFolder));
    }
}
