package com.appin;

//activity for bookmark page

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.appin.data.BookMarkDBHelper;
import com.appin.data.AppInfoContrast;
import com.appin.utilities.ToastUtils;

public class BookMarkActivity extends AppCompatActivity implements BookMarkListAdapter.BookMarkListItemClickHandler{
    private RecyclerView mBookMarkRecylerView;
    private BookMarkListAdapter mBookMarkAdapter;
    private SQLiteDatabase mDB;

    private final static String TAG = BookMarkActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        Log.v(TAG,"Bookmarks");
        setTitle("收藏夹");

        mBookMarkRecylerView = (RecyclerView) findViewById(R.id.rv_bookmarks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mBookMarkRecylerView.setLayoutManager(layoutManager);
        mBookMarkRecylerView.setHasFixedSize(true);



        BookMarkDBHelper dbHelper = new BookMarkDBHelper(this);
        mDB = dbHelper.getWritableDatabase();

        Cursor cursor = getBookMarkData();

        mBookMarkAdapter = new BookMarkListAdapter(this,cursor,this);
        mBookMarkRecylerView.setAdapter(mBookMarkAdapter);



        showBookmarks();

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
            }
        }).attachToRecyclerView(mBookMarkRecylerView);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = getBookMarkData();

        mBookMarkAdapter = new BookMarkListAdapter(this,cursor,this);
        mBookMarkRecylerView.setAdapter(mBookMarkAdapter);

    }


    private void showBookmarks() {
        mBookMarkRecylerView.setVisibility(View.VISIBLE);
    }

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
    //implement clicking in bookmark list
    @Override
    public void onClick(ContentValues data) {
        Log.v(TAG,"BookMark clicked");
        Intent intent = new Intent(this, AppPageActivity.class);
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
        intent.putExtra(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE,data.getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE));
        startActivity(intent);
    }

    private void removeFromBookmark(long id){
        String whereClause = AppInfoContrast.AppInfoEntry._ID+"=?";
        String[] whereArgs={Long.toString(id)};
        mDB.delete(AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME, whereClause,whereArgs);
        ToastUtils.unBookMarked(getApplicationContext());
    }
}
