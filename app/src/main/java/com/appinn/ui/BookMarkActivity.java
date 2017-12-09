package com.appinn.ui;

//activity for bookmark page

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.appinn.R;
import com.appinn.adapters.BookMarkListAdapter;
import com.appinn.data.BookMarkDBHelper;
import com.appinn.data.AppInfoContrast;
import com.appinn.utilities.ToastUtils;

/**
 * 收藏夹Activity
 */
public class BookMarkActivity extends AppCompatActivity implements BookMarkListAdapter.BookMarkListItemClickHandler{
    private RecyclerView mBookMarkRecyclerView;
    private BookMarkListAdapter mBookMarkAdapter;
    private SQLiteDatabase mDB;
    private TextView bookmarkEmptyHint;
    private final static String TAG = BookMarkActivity.class.getSimpleName();

    /**
     * 初始化
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initLayout();
        initData();
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化布局
     */
    private void initLayout(){
        setContentView(R.layout.activity_book_mark);
        Log.v(TAG,"Bookmarks");
        setTitle(getResources().getString(R.string.bookmark));

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        bookmarkEmptyHint = (TextView) findViewById(R.id.tv_bookmark_empty_hint);
        mBookMarkRecyclerView = (RecyclerView) findViewById(R.id.rv_bookmarks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mBookMarkRecyclerView.setLayoutManager(layoutManager);
        mBookMarkRecyclerView.setHasFixedSize(true);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        BookMarkDBHelper dbHelper = new BookMarkDBHelper(this);
        mDB = dbHelper.getWritableDatabase();
        Cursor cursor = getBookMarkData();
        mBookMarkAdapter = new BookMarkListAdapter(this,cursor,this);
        mBookMarkRecyclerView.setAdapter(mBookMarkAdapter);

        showBookmarks();
        checkIsEmpty();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                removeFromBookmark(id);
                mBookMarkAdapter.swapCursor(getBookMarkData());
                checkIsEmpty();
            }
        }).attachToRecyclerView(mBookMarkRecyclerView);

    }

    /**
     * 重载从其他Activity返回到此页面时的行为
     */
    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = getBookMarkData();
        mBookMarkAdapter = new BookMarkListAdapter(this,cursor,this);
        mBookMarkRecyclerView.setAdapter(mBookMarkAdapter);
        checkIsEmpty();
    }

    /**
     * 展示收藏夹
     */
    private void showBookmarks() {
        mBookMarkRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取收藏夹数据
     * @return  用于访问数据库的cursor
     */
    private Cursor getBookMarkData(){
        Log.v(TAG,"Getting bookmark info");
        return mDB.query(
                AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AppInfoContrast.AppInfoEntry.COLUMN_TIMESTAMP
        );
    }

    /**
     * 实现recycler view adapter 的点击事件接口
     * @param data  点击到的数据
     */
    @Override
    public void onClick(ContentValues data) {
        Log.v(TAG,"BookMark clicked");
        Intent intent = new Intent(this, AppPageActivity.class);
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE));
        startActivity(intent);
    }

    /**
     * 从收藏夹中删除
     * @param id    序号
     */
    private void removeFromBookmark(long id){
        String whereClause = AppInfoContrast.AppInfoEntry._ID+"=?";
        String[] whereArgs={Long.toString(id)};
        mDB.delete(AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME, whereClause,whereArgs);
        ToastUtils.unBookMarked(getApplicationContext());
    }

    /**
     * 重载菜单选项被选中时的行为
     * @param item  选项
     * @return  是否成功处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 检查收藏夹是否为空
     */
    private void checkIsEmpty(){
        if(mBookMarkAdapter.getItemCount()==0){
            bookmarkEmptyHint.setVisibility(View.VISIBLE);
            Log.v(TAG,"empty");
        }else{
            Log.v(TAG,"not empty");
            bookmarkEmptyHint.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 重载结束时的行为
     */
    @Override
    protected void onDestroy() {
        if(mDB!=null)
            mDB.close();
        super.onDestroy();
    }
}
