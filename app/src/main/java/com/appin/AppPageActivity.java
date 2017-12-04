package com.appin;

//activity for a single app page

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.appin.data.AppInfoContrast;
import com.appin.data.BookMarkDBHelper;
import com.appin.utilities.ParseAppPageUtils;
import com.appin.utilities.ParseSearchResultUtils;
import com.appin.utilities.ToastUtils;

public class AppPageActivity extends AppCompatActivity {

    private WebView mAppWebView;

    private WebViewClient mWebViewClient;

    private SQLiteDatabase mDb;

    private String mAppUrl;
    private String mAppTitle;

    private Menu mMenu;

    private final static String TAG = AppPageActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG,"New app page creating");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_page);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }else {
            ToastUtils.showToast(getApplicationContext(),getResources().getString(R.string.error_message));
        }
        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity != null){
            if(intentThatStartedThisActivity.hasExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL)){
                mAppUrl = ParseAppPageUtils.makeUrl(intentThatStartedThisActivity.getStringExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
                new parseAppPage().execute(mAppUrl);
            }
            if(intentThatStartedThisActivity.hasExtra("oldTitle")){
                setTitle(intentThatStartedThisActivity.getStringExtra("oldTitle"));
            }
        }

    }
    public class parseAppPage extends AsyncTask<String,Void,ContentValues>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ContentValues doInBackground(String... params) {
            return ParseAppPageUtils.parseAppUrl(params[0]);
        }

        @Override
        protected void onPostExecute(ContentValues contentValues) {
            if (contentValues!=null){
                mAppWebView = (WebView) findViewById(R.id.wb_app_page);
                mWebViewClient = new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if(ParseSearchResultUtils.isAppUrlValid(url)){

                            Intent intent = new Intent(getApplicationContext(),AppPageActivity.class);
                            intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,url);
                            intent.putExtra("oldTitle", mAppTitle);
                            startActivity(intent);
                        }
                        return true;
                    }
                };
                mAppWebView.setWebViewClient(mWebViewClient);
                mAppWebView.getSettings().setJavaScriptEnabled(true);
                mAppWebView.getSettings().setLoadsImagesAutomatically(true);
                mAppWebView.loadDataWithBaseURL(null,contentValues.getAsString("Response"),"text/html","UTF-8",null);
                mAppTitle = contentValues.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE);
                //TODO mAppType = contentValues.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TYPE);
                setTitle(mAppTitle);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apppage,menu);
        mMenu = menu;
        refreshBookMarkButtons();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_put_to_bookmark:
                putToBookMark();
                return true;
            case R.id.action_remove_from_bookmark:
                removeFromBookMark();
                return true;
            case android.R.id.home:
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
    }
    private void refreshBookMarkButtons(){
        MenuItem addToBookmark = mMenu.findItem(R.id.action_put_to_bookmark);
        MenuItem removeFromBookmark = mMenu.findItem(R.id.action_remove_from_bookmark);
        if(checkIfBookmarked()){
            addToBookmark.setVisible(false);
            removeFromBookmark.setVisible(true);
        }else {
            addToBookmark.setVisible(true);
            removeFromBookmark.setVisible(false);
        }
    }
    private long putToBookMark(){
        BookMarkDBHelper dbHelper = new BookMarkDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE, mAppTitle);
        cv.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,mAppUrl);
        ToastUtils.bookMarked(this);
        long returnVal = mDb.insert(AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME,null,cv);
        refreshBookMarkButtons();
        return returnVal;

    }
    private long removeFromBookMark(){
        ToastUtils.unBookMarked(this);
        String whereClause = AppInfoContrast.AppInfoEntry.COLUMN_APP_URL+"=?";
        String[] whereArgs={mAppUrl};
        long returnVal = mDb.delete(AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME, whereClause,whereArgs);
        refreshBookMarkButtons();
        return returnVal;
    }

    private boolean checkIfBookmarked(){
        BookMarkDBHelper dbHelper = new BookMarkDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        String []columns = {AppInfoContrast.AppInfoEntry.COLUMN_APP_URL};
        String selection = AppInfoContrast.AppInfoEntry.COLUMN_APP_URL+"=?";
        String[] selectionArgs = {mAppUrl};
        Cursor cursor = mDb.query(AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME,
                columns,selection,selectionArgs,null,null,null);
        boolean position = cursor.getCount()>0;
        cursor.close();
        return position;
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG,"Back button pressed");
        finish();
    }
}
