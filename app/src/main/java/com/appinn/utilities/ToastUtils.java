package com.appinn.utilities;

import android.content.Context;
import android.widget.Toast;

import com.appinn.R;


/**
 * 提示消息相关
 */
public class ToastUtils{
    private static Toast currentToast;

    /**
     * 生成提示信息
     * @param context   context
     * @param msg   要展示的信息
     */
    public static void showToast(Context context, String msg){
        if(currentToast!=null){
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    /**
     * 已添加到书签提示
     * @param context   context
     */
    public static void bookMarked(Context context){
        showToast(context,context.getResources().getString(R.string.added_to_bookmark));
    }

    /**
     * 已删除书签提示
     * @param context   context
     */
    public static void unBookMarked(Context context){
        showToast(context,context.getResources().getString(R.string.deleted_from_bookmark));
    }

}
