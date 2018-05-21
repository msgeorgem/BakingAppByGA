package com.example.miner01.bakingappbyga;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeMainAdapter extends RecyclerView.Adapter<RecipeMainAdapter.MainViewHolder> {


    private final OnItemClickListener listener;
    private ArrayList<Recipes> mListAdapter;


    public RecipeMainAdapter(ArrayList<Recipes> listRecipes, OnItemClickListener listener) {
        mListAdapter = listRecipes;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mListAdapter.size();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_recipe_main, parent, false);
        MainViewHolder vh = new MainViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder viewHolder, int position) {
        viewHolder.bind(mListAdapter.get(position), listener);
        // Get the {@link News} object located at this position in the list
        final Recipes currentRecipe = mListAdapter.get(position);

        viewHolder.recipeTextView.setText(currentRecipe.getName());
    }

    public interface OnItemClickListener {
        void onItemClick(Recipes item);
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView recipeTextView;


        private MainViewHolder(View view) {
            super(view);
            this.recipeTextView = view
                    .findViewById(R.id.content);
        }

        public void bind(final Recipes item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

}
