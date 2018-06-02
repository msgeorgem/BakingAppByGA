package com.example.miner01.bakingappbyga;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static android.graphics.Color.rgb;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.MainViewHolder> {


    private final OnItemClickListener listener;
    private ArrayList<String[]> mListStepsAdapter;
    public static int selectedIndex = -9;


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

//    public void clear() {
//        mListStepsAdapter.clear();
//        notifyDataSetChanged();
//    }

    public interface OnItemClickListener {
        void onItemClick(String[] item);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        TextView stepNo;
        TextView detailStepTextView;
        View wholeView;


        private MainViewHolder(View view) {
            super(view);
            this.stepNo = view.findViewById(R.id.step_no);
            this.detailStepTextView = view
                    .findViewById(R.id.detail_step);
            this.wholeView = view.findViewById(R.id.step_layout);

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
