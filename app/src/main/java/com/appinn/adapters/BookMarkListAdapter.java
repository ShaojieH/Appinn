package com.appinn.adapters;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appinn.ui.BookMarkActivity;
import com.appinn.R;
import com.appinn.data.AppInfoContrast;


/**
 * 收藏夹列表adapter
 */
public class BookMarkListAdapter extends RecyclerView.Adapter<BookMarkListAdapter.BookMarkViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    final private BookMarkListItemClickHandler mOnClickListener;

    final private static String TAG = BookMarkActivity.class.getSimpleName();

    /**
     * 点击事件接口
     */
    public interface BookMarkListItemClickHandler{
        void onClick(ContentValues data);
    }

    /**
     * 构造函数
     * @param context   context
     * @param cursor    cursor
     * @param listener  监听器
     */
    public BookMarkListAdapter(Context context, Cursor cursor,BookMarkListItemClickHandler listener) {
        this.mContext = context;
        this.mCursor = cursor;
        mOnClickListener = listener;
    }

    /**
     * view holder
     * @param parent    parent view
     * @param viewType  类型
     * @return  view holder
     */
    @Override
    public BookMarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.app_result_list_item, parent, false);
        return new BookMarkViewHolder(view);
    }

    /**
     * 绑定 view holder
     * @param holder view holder
     * @param position 位置
     */
    @Override
    public void onBindViewHolder(BookMarkViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        String appTitle = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE));
        String appUrl = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
        String appAbstract = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT));
        //TODO String appType = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_TYPE));

        long id = mCursor.getLong(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry._ID));

        holder.appTitleTextView.setText(appTitle);
        holder.appAbstractTextView.setText(appAbstract);
        holder.itemView.setTag(id);
    }

    /**
     * 获取数据总量
     * @return  总量
     */
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * 改变数据时的行为
     * @param newCursor 新的cursor
     */
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    /**
     * 内部类， view holder
     */
    class BookMarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView appTitleTextView;
        TextView appAbstractTextView;

        public BookMarkViewHolder(View itemView) {
            super(itemView);
            appTitleTextView = (TextView) itemView.findViewById(R.id.tv_app_title);
            appAbstractTextView = (TextView) itemView.findViewById(R.id.tv_app_abstract);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.v(TAG,"Bookmark clicked in viewholder");
            int adapterPosition = getAdapterPosition();
            ContentValues appData = new ContentValues();
            if(mCursor.moveToPosition(adapterPosition)){
                DatabaseUtils.cursorRowToContentValues(mCursor,appData);
            }
            mOnClickListener.onClick(appData);
        }
    }
}