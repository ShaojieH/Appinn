package com.appinn.utilities;

// parse search results

import android.content.ContentValues;
import android.util.Log;

import com.appinn.data.AppInfoContrast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class ParseSearchResultUtils {

    final static private String TAG = ParseHomePageUtils.class.getSimpleName();
    public static boolean isAppUrlValid(String appUrl){
        int count = appUrl.length()-appUrl.replace("/","").length();
        if(appUrl.contains("?")||
                appUrl.contains("/tag/")||
                appUrl.contains("/category/")||
                appUrl.contains("/page/"))   {
            Log.v(TAG,"url invalid");
            return false;
        }
        else if(count==4||(appUrl.contains("amp")&&count==5)){
            Log.v(TAG,"url valid");
            return true;
        }
        Log.v("Parser","url invalid");
        return false;
    }

    //iterate through result html and return all valid app url
    public static ArrayList<ContentValues> parseSearchResult(String html){
        try{
            Log.v(TAG,"Parsing search result");
            ArrayList<ContentValues> parsedResults = new ArrayList<>();
            Document doc = Jsoup.parse(html);
            //Log.v(TAG,"html is: "+html);
            Elements results = doc.select("a[class=result]");

            if(results.isEmpty())   Log.v(TAG,"results is empty");
            for(Element result:results){

                String linkUrl = result.select("a[href]").attr("href");
                Log.v(TAG,"Start parsing: "+linkUrl.trim());
                if (isAppUrlValid(linkUrl)){

                    String linkTitle = result.select("div[class=result-title]").text();
                    String linkAbstract = result.select("div[class=result-abstract]").text();

                    ContentValues singleResult = new ContentValues();

                    singleResult.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE,linkTitle);
                    singleResult.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_URL,linkUrl.trim());
                    singleResult.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT,linkAbstract);
                    parsedResults.add(singleResult);
                }
            }
            return parsedResults;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}


