package com.appinn.ui;

//activity for a single app page

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.appinn.R;
import com.appinn.data.AppInfoContrast;
import com.appinn.data.BookMarkDBHelper;
import com.appinn.utilities.ParseAppPageUtils;
import com.appinn.utilities.ParseSearchResultUtils;
import com.appinn.utilities.ToastUtils;

/**
 * 应用页面activity
 */
public class AppPageActivity extends AppCompatActivity {

    private WebView mAppWebView;

    private WebViewClient mWebViewClient;

    private SQLiteDatabase mDb;

    private String mAppUrl;
    private String mAppTitle;
    private String mAppAbstract;

    private Menu mMenu;

    private ProgressBar mLoadingIndicator;

    private final static String TAG = AppPageActivity.class.getSimpleName();

    /**
     * 初始化
     * @param savedInstanceState    已保存的信息
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG,"New app page creating");
        super.onCreate(savedInstanceState);

        initLayout();
        initData();
        startLoadingPage();

    }

    /**
     * 初始化布局
     */
    private void initLayout(){
        setContentView(R.layout.activity_app_page);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }else {
            ToastUtils.showToast(getApplicationContext(),getResources().getString(R.string.error_message));
        }

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_app_page_loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);

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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        };
        mAppWebView.setWebViewClient(mWebViewClient);
        mAppWebView.setWebChromeClient(new WebChromeClient());
        mAppWebView.getSettings().setDomStorageEnabled(true);
        mAppWebView.getSettings().setJavaScriptEnabled(true);
        mAppWebView.getSettings().setLoadsImagesAutomatically(true);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity != null){
            if(intentThatStartedThisActivity.hasExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL)){
                mAppUrl = ParseAppPageUtils.makeUrl(intentThatStartedThisActivity.getStringExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
            }
            if(intentThatStartedThisActivity.hasExtra("oldTitle")){
                setTitle(intentThatStartedThisActivity.getStringExtra("oldTitle"));
            }
            if(intentThatStartedThisActivity.hasExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT)){
                mAppAbstract = intentThatStartedThisActivity.getStringExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT);
            }else{
                mAppAbstract = getResources().getString(R.string.default_app_abstract);
            }
        }

    }

    /**
     * 开始载入页面
     */
    private void startLoadingPage(){
        if(!TextUtils.isEmpty(mAppUrl))
            new parseAppPage().execute(mAppUrl);
    }

    /**
     * 解析页面的异步任务
     */
    public class parseAppPage extends AsyncTask<String,Void,ContentValues>{

        @Override
        protected void onPreExecute() {
            progressbarFadeIn();
            super.onPreExecute();
        }

        @Override
        protected ContentValues doInBackground(String... params) {
            return ParseAppPageUtils.parseAppUrl(params[0]);
        }

        @Override
        protected void onPostExecute(ContentValues contentValues) {
            if (contentValues!=null){
                mAppWebView.loadDataWithBaseURL(null,contentValues.getAsString("Response"),"text/html","UTF-8",null);
                mAppTitle = contentValues.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE);
                Log.v(TAG, "mAppAbstract = " + mAppAbstract);
                //TODO mAppType = contentValues.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TYPE);
                setTitle(mAppTitle);
            }
            progressbarFadeOut();
            webViewFadeIn();
        }
    }

    /**
     * 初始化菜单
     * @param menu  菜单
     * @return  是否成功
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apppage,menu);
        mMenu = menu;
        refreshBookMarkButtons();
        return true;
    }

    /**
     * 菜单中选项被选中后的行为
     * @param item  菜单选项
     * @return  是否处理成功
     */
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

    /**
     * 刷新书签情况
     */
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

    /**
     * 加入到书签
     */
    private void putToBookMark(){
        BookMarkDBHelper dbHelper = new BookMarkDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE, mAppTitle);
        cv.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,mAppUrl);
        cv.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT, mAppAbstract);
        ToastUtils.bookMarked(this);
        /*long returnVal = */mDb.insert(AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME,null,cv);
        refreshBookMarkButtons();
        //return returnVal;
    }

    /**
     * 从书签中删除
     */
    private void removeFromBookMark(){
        ToastUtils.unBookMarked(this);
        String whereClause = AppInfoContrast.AppInfoEntry.COLUMN_APP_URL+"=?";
        String[] whereArgs={mAppUrl};
        /*long returnVal = */mDb.delete(AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME, whereClause,whereArgs);
        refreshBookMarkButtons();
        //return returnVal;
    }

    /**
     * 检查是否在书签中
     * @return  检查结果
     */
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

    /**
     * 重载返回键
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG,"Back button pressed");
        finish();
    }

    /**
     * 进度条渐入
     */
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

    /**
     * 进度条渐出
     */
    private void progressbarFadeOut(){
        Log.v(TAG,"progress bar fading out");

        Animation fadeOutAnimation = new AlphaAnimation(1.f, 0.f);
        fadeOutAnimation.setDuration(1000);
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

    /**
     * webview 渐入
     */
    private void webViewFadeIn(){
        Log.v(TAG,"web view fading out");
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
        mAppWebView.startAnimation(fadeInAnimation);
    }

    /**
     * 重载结束时的行为
     */
    @Override
    protected void onDestroy() {
        if(mDb!=null)
            mDb.close();
        super.onDestroy();
    }
}
