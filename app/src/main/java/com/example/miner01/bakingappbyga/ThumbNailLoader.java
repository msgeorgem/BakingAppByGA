package com.example.miner01.bakingappbyga;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.FileObserver;
import android.util.Log;

import com.example.miner01.bakingappbyga.Utils.RetrieveVideoFrame;

/**
 * Created by Marcin on 2017-09-12.
 */

public class ThumbNailLoader extends AsyncTask<String, Void, Bitmap> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = ThumbNailLoader.class.getName();
    private Bitmap mData;
    private FileObserver mFileObserver;
    /**
     * Query URL
     */
    private String mUrl;


    /**
     * This is on a background thread.
     */

    @Override
    protected Bitmap doInBackground(String... string) {
        if (mUrl == null) {
            return null;
        }
        Log.i(LOG_TAG, "loadInBackground");
        // Perform the network request, parse the response, and extract a list of news.
        Bitmap singleBitmap = null;
        Bitmap thumbImage = null;
        try {
            singleBitmap = RetrieveVideoFrame.retrieveVideoFrameFromVideo(mUrl);
            thumbImage = ThumbnailUtils.extractThumbnail(singleBitmap, 90, 90);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return thumbImage;
    }
}
