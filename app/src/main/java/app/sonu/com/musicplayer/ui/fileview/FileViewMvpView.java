package app.sonu.com.musicplayer.ui.fileview;

import java.io.File;
import java.util.List;

import app.sonu.com.musicplayer.base.ui.BaseMvpView;

/**
 * Created by sonu on 30/6/17.
 */

public interface FileViewMvpView extends BaseMvpView {
    void showFileList(List<File> fileList);
}
