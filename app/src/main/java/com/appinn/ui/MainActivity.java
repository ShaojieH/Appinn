package com.appinn.ui;

//activity for the main page

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appinn.R;
import com.appinn.adapters.AppPageListAdapter;
import com.appinn.data.AppInfoContrast;
import com.appinn.data.Constants;
import com.appinn.utilities.NetworkUtils;
import com.appinn.utilities.ParseHomePageUtils;
import com.appinn.utilities.ParseSearchResultUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AppPageListAdapter.AppSearchResultListItemClickListener,
        AppPageListAdapter.AppSearchResultListOnBottomReachedListener{


    private RecyclerView mSearchResultRecyclerView;
    private RecyclerView mHomepageListRecyclerView;
    private AppPageListAdapter mSearchResultAdapter;
    private AppPageListAdapter mHomepageListAdapter;

    private SearchView mSearchView;

    private TextView mErrorMessageDisplay;
    private final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mLoadingIndicator;

    private int currentSearchResultPage;
    private int currentHomepagePage;
    private ArrayList<ContentValues> listOfSearchResults = new ArrayList<>();
    private ArrayList<ContentValues> listOfHomePageResults = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        initData();
        getHomePage(1);

    }

    private int currentAdapterState; // 0 : homepage, 1 : searchResult;

    private void initLayout(){
        setContentView(R.layout.activity_main);

        mSearchResultRecyclerView = (RecyclerView) findViewById(R.id.rv_searchResults);

        LinearLayoutManager searchListLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mSearchResultRecyclerView.setLayoutManager(searchListLayoutManager);
        mSearchResultRecyclerView.setHasFixedSize(true);

        mSearchResultAdapter = new AppPageListAdapter(this, this);
        mSearchResultRecyclerView.setAdapter(mSearchResultAdapter);
        mHomepageListRecyclerView = (RecyclerView) findViewById(R.id.rv_homepagelist);

        LinearLayoutManager homepageListLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mHomepageListRecyclerView.setLayoutManager(homepageListLayoutManager);
        mHomepageListRecyclerView.setHasFixedSize(true);
        mHomepageListAdapter = new AppPageListAdapter(this, this);
        mHomepageListRecyclerView.setAdapter(mHomepageListAdapter);
        mHomepageListRecyclerView.setVisibility(View.VISIBLE);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_main_page_loading_indicator);

        setTitle(getResources().getString(R.string.homepage));
    }

    private void initData(){
        currentSearchResultPage = 0;
        currentHomepagePage = 1;
        currentAdapterState = 0;

    }
    @Override
    public void onClick(ContentValues data) {
        Intent intent = new Intent(this, AppPageActivity.class);
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE));
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT, data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT));
        //Log.v(TAG, "linkAbstract = " + data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT));
        startActivity(intent);
    }

    @Override
    public void onBottomReached(int position) {
        if(currentAdapterState==1){
            makeSearchQuery(++currentSearchResultPage);
        }else if(currentAdapterState==0){
            getHomePage(++currentHomepagePage);
        }
    }

    private void getHomePage(int page){
        String homepage = Constants.HOME_PAGE + "/page/" + Integer.toString(page) + "/";
        URL homepageURL = null;
        try{
            homepageURL = new URL(homepage);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        Log.v(TAG,"Getting home page list");
        new getHomepageTask().execute(homepageURL);
    }

    private void makeSearchQuery(int page){
        String query = mSearchView.getQuery().toString();
        URL searchUrl = NetworkUtils.buildSearchUrl(query,Integer.toString(page));
        Log.v(TAG,"Making Query, url: "+searchUrl);
        new searchQueryTask().execute(searchUrl);
    }

    private void openBookmark(){
        Intent intent = new Intent(this,BookMarkActivity.class);
        startActivity(intent);
    }

    private void openAboutPage(){
        Intent intent = new Intent(this,AboutActivity.class);
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
        mHomepageListRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
    private void hideErrorMessage(){
        Log.v(TAG,"Hiding error message");
        mSearchResultRecyclerView.setVisibility(View.VISIBLE);
        if(currentAdapterState==0){
            mHomepageListRecyclerView.setVisibility(View.VISIBLE);
        }

        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }
    private class searchQueryTask extends AsyncTask<URL, Void, String>{
        @Override
        protected void onPreExecute() {
            progressbarFadeIn();
            hideErrorMessage();
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
            progressbarFadeOut();

            if(!TextUtils.isEmpty(searchResults)){
                listOfSearchResults.addAll(ParseSearchResultUtils.parseSearchResult(searchResults));
                mSearchResultAdapter.setAppData(listOfSearchResults);
                mSearchResultAdapter.notifyDataSetChanged();
                showSearchResultsView();
                hideErrorMessage();
            }else{
                showErrorMessage();
            }
        }

    }

    public class getHomepageTask extends AsyncTask<URL, Void, String>{

        @Override
        protected void onPreExecute() {
            progressbarFadeIn();
            hideErrorMessage();
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
        protected void onPostExecute(String str) {
            progressbarFadeOut();
            Log.v(TAG,"Getting home page html\n\n");
            if(!TextUtils.isEmpty(str)){
                ArrayList<ContentValues> result = ParseHomePageUtils.parseHomePage(str);
                listOfHomePageResults.addAll(result);
                mHomepageListAdapter.setAppData(listOfHomePageResults);
                hideErrorMessage();
            }else{
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentAdapterState = 1;
                currentSearchResultPage = 0;
                listOfSearchResults.clear();
                makeSearchQuery(0);
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
        switch (item.getItemId()){
            case R.id.action_bookmarks:{
                openBookmark();
                return true;
            }case R.id.action_about:{
                openAboutPage();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void progressbarFadeIn(){
        Animation fadeInAnimation = new AlphaAnimation(0.f, 1.f);
        fadeInAnimation.setDuration(500);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Log.v(TAG,"progress bar fading in");
        mLoadingIndicator.startAnimation(fadeInAnimation);
    }

    private void progressbarFadeOut(){
        Log.v(TAG,"progress bar fading out");

        Animation fadeOutAnimation = new AlphaAnimation(1.f, 0.f);
        fadeOutAnimation.setDuration(500);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLoadingIndicator.startAnimation(fadeOutAnimation);
    }
}
