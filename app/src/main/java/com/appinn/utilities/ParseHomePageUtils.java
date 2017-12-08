package com.appinn.utilities;

import android.content.ContentValues;
import android.util.Log;

import com.appinn.data.AppInfoContrast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class ParseHomePageUtils {

    private static final String TAG = ParseHomePageUtils.class.getSimpleName();

    public static ArrayList<ContentValues> parseHomePage(String html){
        try{
            ArrayList<ContentValues> parsedResults = new ArrayList<>();
            Document doc = Jsoup.parse(html);
            //Log.v(TAG,"html is: "+html);
            Elements results = doc.select("div[class=spost post]");
            if(results.isEmpty())   Log.v(TAG,"results is empty");
            for(Element result:results){

                String linkUrl = result.select("h2[class=entry-title]").select("a[href]").attr("href");
                //Log.v(TAG,"Start parsing: "+linkUrl.trim());

                String linkTitle = result.select("h2[class=entry-title]").select("a[title]").attr("title");
                //Log.v(TAG,"Title : "+linkTitle);
                String linkAbstract = result.select("div[class=entry-content]").text();
                //Log.v(TAG,"Abstract : "+linkAbstract);
                ContentValues singleResult = new ContentValues();

                singleResult.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE,linkTitle);
                singleResult.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,linkUrl.trim());
                singleResult.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT,linkAbstract);
                parsedResults.add(singleResult);
            }
            return parsedResults;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
