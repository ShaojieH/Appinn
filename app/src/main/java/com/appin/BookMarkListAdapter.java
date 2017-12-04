package com.appin;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appin.data.AppInfoContrast;



public class BookMarkListAdapter extends RecyclerView.Adapter<BookMarkListAdapter.BookMarkViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    final private BookMarkListItemClickHandler mOnClickListener;

    final private static String TAG = BookMarkActivity.class.getSimpleName();

    //handle clicking
    public interface BookMarkListItemClickHandler{
        void onClick(ContentValues data);
    }

    public BookMarkListAdapter(Context context, Cursor cursor,BookMarkListItemClickHandler listener) {
        this.mContext = context;
        this.mCursor = cursor;
        mOnClickListener = listener;
    }

    @Override
    public BookMarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.app_result_list_item, parent, false);
        return new BookMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookMarkViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        // Update the view holder with the information needed to display
        String appTitle = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE));
        //String appAbstract = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT));
        String appUrl = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL));
        //TODO String appType = mCursor.getString(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry.COLUMN_APP_TYPE));

        long id = mCursor.getLong(mCursor.getColumnIndex(AppInfoContrast.AppInfoEntry._ID));


        holder.appTitleTextView.setText(appTitle);
        holder.appAbstractTextView.setText("简介");
        holder.itemView.setTag(id);
    }


    @Override
    public int getItemCount() {


        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }


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