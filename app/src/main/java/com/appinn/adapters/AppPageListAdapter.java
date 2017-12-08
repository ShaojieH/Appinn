package com.appinn.adapters;

//activity for search result page
//recycler view is used
import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appinn.R;
import com.appinn.data.AppInfoContrast;

import java.util.ArrayList;


public class AppPageListAdapter extends RecyclerView.Adapter<AppPageListAdapter.AppViewHolder>{

    private ArrayList<ContentValues> mAppData;

    final private AppSearchResultListItemClickListener mOnClickListener;
    final private AppSearchResultListOnBottomReachedListener mOnBottomReachedListener;
    //handle clicking

    public interface AppSearchResultListItemClickListener{
        void onClick(ContentValues data);
    }
    public interface AppSearchResultListOnBottomReachedListener{
        void onBottomReached(int position);
    }
    public AppPageListAdapter(AppSearchResultListItemClickListener clickListener, AppSearchResultListOnBottomReachedListener bottomReachedListener){
        mOnClickListener = clickListener;
        mOnBottomReachedListener = bottomReachedListener;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.app_result_list_item,parent,false);

        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        String appTitle = mAppData.get(position).getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE);
        String appAbstract = mAppData.get(position).getAsString(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT);

        holder.mAppTitleTextView.setText(appTitle);
        holder.mAppAbstractTextView.setText(appAbstract);

        if(position == getItemCount()-1){
            mOnBottomReachedListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        if(mAppData==null){
            return 0;
        }
        else {
            return mAppData.size();
        }
    }

    public void setAppData(ArrayList<ContentValues> appData){
        mAppData = appData;
        notifyDataSetChanged();
    }



    public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mAppTitleTextView;
        private TextView mAppAbstractTextView;

        private AppViewHolder(View v){
            super(v);
            mAppTitleTextView = (TextView) v.findViewById(R.id.tv_app_title);
            mAppAbstractTextView = (TextView) v.findViewById(R.id.tv_app_abstract);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            ContentValues appData = mAppData.get(adapterPosition);
            mOnClickListener.onClick(appData);
        }
    }
}
