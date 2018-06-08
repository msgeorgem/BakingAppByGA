package com.example.miner01.bakingappbyga;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.FileObserver;
import android.util.Log;
import android.widget.ImageView;

import com.example.miner01.bakingappbyga.Utils.RetrieveVideoFrame;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import static com.example.miner01.bakingappbyga.StepsFragment.LOG_TAG;

/**
 * Created by Marcin on 2017-09-12.
 */

public class ThumbNailLoader extends AsyncTask<ImageView, Void, Bitmap> {


    private Context context;

    private ImageView mImageView = null;
    private String mUrl;
    InputStream in;
    private int mPosition;

    /**
     * Query URL
     */

//    ThumbNailLoader(Context context, String Url, int position) {
//        this.context = context;
//        mUrl = Url;
//        mPosition = position;
//    }


    /**
     * This is on a background thread.
     */

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        mImageView = imageViews[0];
        mUrl = (String) mImageView.getTag();
        Log.i(LOG_TAG, "loadInBackground");
        // Perform the network request, parse the response, and extract a list of news.
        Bitmap singleBitmap = null;
        Bitmap thumbImage = null;
        try {
            singleBitmap = RetrieveVideoFrame.retrieveVideoFrameFromVideo(mUrl);
            Log.i("Bitmap Loader", String.valueOf(singleBitmap.getWidth()));
            thumbImage = ThumbnailUtils.extractThumbnail(singleBitmap, 90, 90);
            Log.i("Bitmap Loader", String.valueOf(thumbImage.getWidth()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return thumbImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (mImageView != null) {
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                Drawable placeholder = mImageView.getContext().getResources().getDrawable(R.drawable.default_thumb);
                mImageView.setImageDrawable(placeholder);
            }
        }
    }
}
