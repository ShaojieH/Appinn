package com.appinn.adapters;


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


/**
 * 应用列表adapter
 */
public class AppPageListAdapter extends RecyclerView.Adapter<AppPageListAdapter.AppViewHolder>{

    private ArrayList<ContentValues> mAppData;

    final private AppSearchResultListItemClickListener mOnClickListener;
    final private AppSearchResultListOnBottomReachedListener mOnBottomReachedListener;

    /**
     * 点击事件接口
     */
    public interface AppSearchResultListItemClickListener{
        void onClick(ContentValues data);
    }

    /**
     * 滚动到底部接口
     */
    public interface AppSearchResultListOnBottomReachedListener{
        void onBottomReached(int position);
    }

    /**
     * 构造函数
     * @param clickListener 接口实现者
     * @param bottomReachedListener 接口实现者
     */
    public AppPageListAdapter(AppSearchResultListItemClickListener clickListener, AppSearchResultListOnBottomReachedListener bottomReachedListener){
        mOnClickListener = clickListener;
        mOnBottomReachedListener = bottomReachedListener;
    }

    /**
     * 创建 view holder
     * @param parent    parent view
     * @param viewType  类型
     * @return  app view holder
     */
    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.app_result_list_item,parent,false);

        return new AppViewHolder(view);
    }

    /**
     * 绑定 view holder
     * @param holder    view holder
     * @param position  位置
     */
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

    /**
     * 获取数据数量
     * @return  数据数量
     */
    @Override
    public int getItemCount() {
        if(mAppData==null){
            return 0;
        }
        else {
            return mAppData.size();
        }
    }

    /**
     * 设置数据
     * @param appData
     */
    public void setAppData(ArrayList<ContentValues> appData){
        mAppData = appData;
        notifyDataSetChanged();
    }


    /**
     * 内部类，view holder
     */
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
