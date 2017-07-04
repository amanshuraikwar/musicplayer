package app.sonu.com.musicplayer.ui.fileview;

import java.io.File;
import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpPresenter;

/**
 * Created by sonu on 30/6/17.
 */

public interface FileViewMvpPresenter extends BaseMvpPresenter<FileViewMvpView> {
    List<File> getFileList();
    List<File> getFileList(String pathOfFolder);
    void folderItemOnClick(String pathOfFolder);
}
