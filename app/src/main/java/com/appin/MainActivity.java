package com.appin;

//activity for the main page

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appin.data.AppInfoContrast;
import com.appin.data.Constants;
import com.appin.utilities.NetworkUtils;
import com.appin.utilities.ParseHomePageUtils;
import com.appin.utilities.ParseSearchResultUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AppPageListAdapter.AppSearchResultListItemClickListener{


    private RecyclerView mSearchResultRecyclerView;
    private RecyclerView mHomepageListRecyclerView;
    private AppPageListAdapter mSearchResultAdapter;
    private AppPageListAdapter mHomepageListAdapter;

    private SearchView mSearchView;

    private TextView mErrorMessageDisplay;
    private final String TAG = MainActivity.class.getSimpleName();
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
        initListeners();
        showHomePage();

    }
    //implement clicking in search result list

    private void initLayout(){
        setContentView(R.layout.activity_main);

        mSearchResultRecyclerView = (RecyclerView) findViewById(R.id.rv_searchResults);

        LinearLayoutManager searchListLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mSearchResultRecyclerView.setLayoutManager(searchListLayoutManager);
        mSearchResultRecyclerView.setHasFixedSize(true);

        mSearchResultAdapter = new AppPageListAdapter(this);
        mSearchResultRecyclerView.setAdapter(mSearchResultAdapter);

        mHomepageListRecyclerView = (RecyclerView) findViewById(R.id.rv_homepagelist);

        LinearLayoutManager homepageListLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mHomepageListRecyclerView.setLayoutManager(homepageListLayoutManager);
        mHomepageListRecyclerView.setHasFixedSize(true);
        mHomepageListAdapter = new AppPageListAdapter(this);
        mHomepageListRecyclerView.setAdapter(mHomepageListAdapter);
        mHomepageListRecyclerView.setVisibility(View.VISIBLE);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        setTitle(getResources().getString(R.string.homepage));
    }

    private void initListeners(){

    }

    @Override
    public void onClick(ContentValues data) {
        Intent intent = new Intent(this, AppPageActivity.class);
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE));
        startActivity(intent);
    }

    private void showHomePage(){
        String homepage = Constants.HOME_PAGE;
        URL homepageURL = null;
        try{
            homepageURL = new URL(homepage);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"Getting home page list");
        new getHomepageTask().execute(homepageURL);
    }

    private void makeSearchQuery(){
        String query = mSearchView.getQuery().toString();
        URL searchUrl = NetworkUtils.buildSearchUrl(query,"0");
        Log.v(TAG,"Making Query, url: "+searchUrl);
        new searchQueryTask().execute(searchUrl);
    }

    private void openBookmark(){
        Intent intent = new Intent(this,BookMarkActivity.class);
        startActivity(intent);
    }

    private void showSearchResultsView() {
        Log.v(TAG,"Showing results");
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mSearchResultRecyclerView.setVisibility(View.VISIBLE);
        mHomepageListRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        Log.v(TAG,"Showing error message");
        mSearchResultRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private class searchQueryTask extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            Log.v(TAG,"Doing in background");
            URL searchUrl = urls[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            }catch (IOException e){
                e.printStackTrace();
            }
            return searchResults;
        }
        @Override
        protected void onPostExecute(String searchResults) {
            Log.v(TAG,"On post execute");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }
            },500);

            if(searchResults!=null&& !searchResults.equals("")){
                ArrayList<ContentValues> listOfSearchResults = ParseSearchResultUtils.parseSearchResult(searchResults);
                mSearchResultAdapter.setAppData(listOfSearchResults);
                showSearchResultsView();
            }else{
                showErrorMessage();
            }
        }
    }

    public class getHomepageTask extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL homepageUrl = params[0];
            String homePageResults = null;
            try{
                homePageResults = NetworkUtils.getResponseFromHttpUrl(homepageUrl);
            }catch (IOException e){
                e.printStackTrace();
            }
            return homePageResults;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v(TAG,"Getting home page html\n\n");
            ArrayList<ContentValues> listOfHomepageApps = ParseHomePageUtils.parseHomePage(s);
            mHomepageListAdapter.setAppData(listOfHomepageApps);
            mHomepageListRecyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeSearchQuery();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_bookmarks){
            openBookmark();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
