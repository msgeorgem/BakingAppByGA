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

public class ThumbNailLoader extends AsyncTask<Void, Void, Bitmap> {


    private ImageView mImageView = null;
    private String mUrl;
    
    /**
     * Query URL
     */
    
    ThumbNailLoader(Context context, String Url, ImageView imageview)
    {
        ThumbNailLoader.this.context = context;
        mUrl = Url;
        mImageView = imageview;
    }
    @Override
    protected void onPreExecute()
    {
        AssetManager assetMgr = context.getAssets();

        try {
            in = assetMgr.open(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * This is on a background thread.
     */

    @Override
    protected Bitmap doInBackground(Void... arg0) {
        
        Log.i(LOG_TAG, "loadInBackground");
        // Perform the network request, parse the response, and extract a list of news.
        Bitmap singleBitmap = null;
        Bitmap thumbImage = null;
        try {
            singleBitmap = RetrieveVideoFrame.retrieveVideoFrameFromVideo(in);
            in.close();
            thumbImage = ThumbnailUtils.extractThumbnail(singleBitmap, 90, 90);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return thumbImage;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }
}
