package com.kanawish.gl.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *
 */
public class FileUtils {

    private FileUtils() {
    }

    /**
     * Always good to check your basics:
     * http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
     * <p>
     * but since we're on android...
     * http://stackoverflow.com/questions/9095610/android-fileinputstream-read-txt-file-to-string
     */
    public static String loadStringFromAsset(Context context, String assetName) throws IOException {
        InputStream input = context.getAssets().open(assetName);

        return readFile(new InputStreamReader(input));
    }

    /**
     * Will wrap the provided reader in a BufferedReader, and return the contents
     * of this string as a single String.
     *
     * @param inputReader
     * @return file contents
     * @throws IOException in case of issues.
     */
    public static String readFile(Reader inputReader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return stringBuilder.toString();
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
