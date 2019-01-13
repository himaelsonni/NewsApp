package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<NewsData>> {
    private static final int LOADER_ID = 0;
    private static final String requestedUrl =
            "https://content.guardianapis.com/search";
    private ListAdapter adapter;
    private ProgressBar progressBar;
    private TextView CAUTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        CAUTION = findViewById(R.id.alertText);
        ArrayList<NewsData> resultList = new ArrayList<>();
        ListView listView = findViewById(R.id.list);
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        adapter = new ListAdapter(this, resultList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsData currentNews = (NewsData) adapter.getItem(i);
                Uri uri = Uri.parse(currentNews.getURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        listView.setEmptyView(progressBar);

        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
        } else {
            CAUTION.setText(R.string.no_internet);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader<ArrayList<NewsData>> onCreateLoader(int i, Bundle bundle) {
        Uri uri = Uri.parse(requestedUrl);
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("q", getString(R.string.query));
        builder.appendQueryParameter("section", getString(R.string.section_const));
        builder.appendQueryParameter("show-tags", getString(R.string.show_tags));
        builder.appendQueryParameter("api-key", "your-key");


        return new com.example.android.newsapp.Loader(MainActivity.this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<NewsData>> loader, ArrayList<NewsData> newsData) {
        CAUTION.setText(R.string.no_news);
        progressBar.setVisibility(View.GONE);
        adapter.clear();
        if (newsData != null && !newsData.isEmpty()) {
            CAUTION.setVisibility(View.GONE);
            adapter.addAll(newsData);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<NewsData>> loader) {
        CAUTION.setVisibility(View.GONE);
        adapter.clear();
    }
}
