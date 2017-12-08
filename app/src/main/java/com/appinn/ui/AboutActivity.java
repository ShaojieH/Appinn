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

public class AboutActivity extends AppCompatActivity {

    private final String TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
    }

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

    @Override
    public void onBackPressed() {
        Log.v(TAG,"Back button pressed");
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
