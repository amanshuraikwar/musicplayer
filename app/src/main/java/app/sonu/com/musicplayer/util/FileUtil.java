package app.sonu.com.musicplayer.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by sonu on 1/7/17.
 */

public class FileUtil {
    public static boolean fileIsMimeType(File file, String mimeType, MimeTypeMap mimeTypeMap) {
        if (mimeType == null || mimeType.equals("*/*")) {
            return true;
        } else {
            // get the file mime type
            String filename = file.toURI().toString();
            int dotPos = filename.lastIndexOf('.');
            if (dotPos == -1) {
                return false;
            }
            String fileExtension = filename.substring(dotPos + 1).toLowerCase();
            String fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension);
            if (fileType == null) {
                return false;
            }
            // check the 'type/subtype' pattern
            if (fileType.equals(mimeType)) {
                return true;
            }
            // check the 'type/*' pattern
            int mimeTypeDelimiter = mimeType.lastIndexOf('/');
            if (mimeTypeDelimiter == -1) {
                return false;
            }
            String mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter);
            String mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1);
            if (!mimeTypeSubtype.equals("*")) {
                return false;
            }
            int fileTypeDelimiter = fileType.lastIndexOf('/');
            if (fileTypeDelimiter == -1) {
                return false;
            }
            String fileTypeMainType = fileType.substring(0, fileTypeDelimiter);
            if (fileTypeMainType.equals(mimeTypeMainType)) {
                return true;
            }
        }
        return false;
    }

    public static int getAudioFileDuration(String path, Context context) {
        Uri uri = Uri.parse(path);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSecond = Integer.parseInt(durationStr);
        return millSecond;
    }
}
