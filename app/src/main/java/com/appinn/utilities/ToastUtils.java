package com.appinn.utilities;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.appinn.R;


//globol toasts function are stored here
public class ToastUtils extends Application{
    private static Toast currentToast;

    public static void showToast(Context context, String msg){
        if(currentToast!=null){
            currentToast.cancel();
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT);
    }

    public static void bookMarked(Context context){
        if(currentToast!=null){
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context,context.getResources().getString(R.string.added_to_bookmark),Toast.LENGTH_LONG);
        currentToast.show();
    }

    public static void unBookMarked(Context context){
        if(currentToast!=null){
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context,context.getResources().getString(R.string.deleted_from_bookmark),Toast.LENGTH_LONG);
        currentToast.show();
    }

}
