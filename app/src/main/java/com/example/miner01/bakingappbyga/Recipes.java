package com.example.miner01.bakingappbyga;

import java.util.ArrayList;

public class Recipes {

    private String mId;
    private String mName;
    private ArrayList<String[]> mIngredients = null;
    private ArrayList<String[]> mSteps = null;

    /**
     * No args constructor for use in serialization
     */
    public Recipes() {
    }

    public Recipes(String id, String name, ArrayList<String[]> ingredients, ArrayList<String[]> steps) {

        this.mId = id;
        this.mName = name;
        this.mIngredients = ingredients;
        this.mSteps = steps;
    }

    public String getID() {
        return mId;
    }
    public String getName() {
        return mName;
    }
    public ArrayList<String[]> getIngredients() {
        return mIngredients;
    }
    public ArrayList<String[]> getSteps() {
        return mSteps;
    }




}
