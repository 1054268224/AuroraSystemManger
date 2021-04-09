package com.cydroid.softmanager.utils;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static void CopyFileFromAssets(Context myContext, String ASSETS_NAME, String savePath,
            String saveName) {
        String filename = savePath + "/" + saveName;

        File dir = new File(savePath);
        if (!dir.exists()) {
            if (!dir.mkdir())
                return;
        }
        dir.setExecutable(true, false);
        dir.setReadable(true, false);
        dir.setWritable(true);

        InputStream is = null;
        FileOutputStream fos = null;

        if (!(new File(filename)).exists()) {
            try {
                is = myContext.getResources().getAssets().open(ASSETS_NAME);
                fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                try {
                    if (fos != null)
                        fos.close();
                    if (is != null)
                        is.close();
                } catch (IOException e1) {
                }
                e.printStackTrace();
            }
        }
        File file = new File(filename);
        file.setExecutable(true, false);
        file.setReadable(true, false);
        file.setWritable(true);

    }

    public static boolean isExistsFile(String filePath) {
        File dir = new File(filePath);
        return dir.exists();
    }

    public static void recurDelete(File file) {
        if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                if (!file.delete()) {
                    Log.d(TAG, "file:" + file.getAbsolutePath() + " deleted failed!");
                }
                return;
            }
        }

        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                if (!file.delete()) {
                    Log.d(TAG, "file:" + file.getAbsolutePath() + " deleted failed!");
                }
                return;
            }

            for (File f : childFile) {
                recurDelete(f);
            }
            if (!file.delete()) {
                Log.d(TAG, "file:" + file.getAbsolutePath() + " deleted failed!");
            }
        }
    }

    public final static String[] WHITE_LIST_FILE_KEY = new String[] {"ThemePark"};
    public final static String[] WHITE_LIST_PATH_KEY = new String[] {"cyee"};

    public static boolean isWhiteListFile(String path) {
        boolean result = false;
        if (path == null || path.equals("")) {
            return false;
        }
        for (String key : WHITE_LIST_FILE_KEY) {
            if (path.contains(key)) {
                result = true;
                break;
            }
        }

        for (String key : WHITE_LIST_PATH_KEY) {
            String[] dirs = path.split("/");
            for (String dir : dirs) {
                if (dir.equals(key)) {
                    return true;
                }
            }
        }
        return result;
    }
}
