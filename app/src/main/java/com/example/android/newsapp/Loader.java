package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

import static com.example.android.newsapp.Query.fetchingData;

public class Loader extends AsyncTaskLoader<ArrayList<NewsData>> {

    private String url;

    public Loader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<NewsData> loadInBackground() {
        if (url == null) {
            return null;
        }
        return fetchingData(url);
    }
}
