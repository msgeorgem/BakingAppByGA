package com.example.miner01.bakingappbyga;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static android.graphics.Color.rgb;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.MainViewHolder> {

    private final OnItemClickListener listener;
    private ArrayList<String[]> mListStepsAdapter;
    public static int selectedIndex = -9;

    Picasso picassoInstance;


    public RecipeDetailAdapter(ArrayList<String[]> listSteps, OnItemClickListener listener) {
        mListStepsAdapter = listSteps;
        this.listener = listener;
    }

    public static void setSelectedIndex(int ind) {
        selectedIndex = ind;
    }

    @Override
    public int getItemCount() {
        return mListStepsAdapter.size();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_step_detail, parent, false);
        MainViewHolder vh = new MainViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder viewHolder, int position) {
        viewHolder.bind(mListStepsAdapter.get(position), listener);
        // Get the {@link News} object located at this position in the list
        final String[] currentStep = mListStepsAdapter.get(position);

        viewHolder.stepNo.setText(String.format(Locale.ENGLISH, "%s: %s", DetailActivity.mStep,
                currentStep[1]));
        viewHolder.detailStepTextView.setText(currentStep[2]);


        viewHolder.thumbnailURL = currentStep[4];
        viewHolder.thumbnail.setTag(currentStep[4]);
        Context context = viewHolder.itemView.getContext();

        Uri path = Uri.parse(viewHolder.thumbnailURL);
        if (currentStep[4].equals("")) {
            viewHolder.pgsBar.setVisibility(View.GONE);
            viewHolder.thumbnail.setImageResource(R.drawable.default_thumb);
        } else {
            try {
                ThumbNailLoader task = new ThumbNailLoader(context,viewHolder.pgsBar);
                task.execute(viewHolder.thumbnail);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        Log.i("selectedIndex", String.valueOf(selectedIndex));
        if (position == selectedIndex) {
            viewHolder.wholeView.setBackgroundColor(rgb(63, 81, 181));
            viewHolder.stepNo.setTextColor(rgb(255, 255, 255));
            viewHolder.detailStepTextView.setTextColor(rgb(255, 255, 255));
        } else {
            viewHolder.wholeView.setBackgroundColor(rgb(255, 255, 255));
            viewHolder.stepNo.setTextColor(rgb(128, 128, 128));
            viewHolder.detailStepTextView.setTextColor(rgb(128, 128, 128));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String[] item);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        TextView stepNo;
        ProgressBar pgsBar;
        TextView detailStepTextView;
        String thumbnailURL;
        ImageView thumbnail;
        View wholeView;

        private MainViewHolder(View view) {
            super(view);
            this.stepNo = view.findViewById(R.id.step_no);
            this.detailStepTextView = view
                    .findViewById(R.id.detail_step);
            this.wholeView = view.findViewById(R.id.step_layout);
            this.thumbnail = view.findViewById(R.id.step_thumbnail);
            this.pgsBar = view.findViewById(R.id.pBar);
        }

        public void bind(final String[] item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
