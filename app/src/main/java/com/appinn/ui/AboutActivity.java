package com.appinn.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.appinn.R;
import com.appinn.data.Constants;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * 关于页面Activity
 */
public class AboutActivity extends AppCompatActivity {

    private final String TAG = AboutActivity.class.getSimpleName();

    /**
     * 初始化
     * @param savedInstanceState    已保存的信息
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
    }

    /**
     * 初始化布局
     */
    private void initLayout(){
        setTitle(getResources().getString(R.string.about));

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo1)
                .setDescription(getResources().getString(R.string.about_page_description))
                .addItem(new Element().setTitle(getResources().getString(R.string.version)))
                .addGroup(getResources().getString(R.string.contact_with_me))
                .addWebsite(Constants.HOME_PAGE)
                .addEmail(getResources().getString(R.string.email))
                .create();
        setContentView(aboutPage);
    }

    /**
     * 重载返回键行为
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG,"Back button pressed");
        finish();
    }

    /**
     * 重载菜单选项
     * @param item  菜单
     * @return  是否处理成功
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
