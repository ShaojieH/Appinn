package com.appinn.utilities;

import android.content.ContentValues;

import com.appinn.data.AppInfoContrast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 解析每个应用页面相关
 */
public class ParseAppPageUtils {

    /**
     *将url转为amp版
     * @param url   原始url
     * @return  便于解析的url(实际为手机访问用界面)
     */
    public static String makeUrl(String url){
        if(!url.endsWith("amp/")){
            url = url + "amp/";
        }
        return url;
    }

    /**
     *解析app页面
     * @param url   访问的页面
     * @return  app页面解析后的结果, contentValues
     */
    public static ContentValues parseAppUrl(String url){
        url = makeUrl(url);
        try {
            URL parsedUrl = new URL(url);
            try {
                return parseResponse(NetworkUtils.getResponseFromHttpUrl(parsedUrl));
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    //parse the response with jsoup and delete parts that won't be needed

    /**
     *用jsoup解析html
     * @param response  返回结果
     * @return  应用信息
     */
    private static ContentValues parseResponse(String response){

        Document doc = Jsoup.parse(response);

        doc.select("div[class=sticky_social]").remove();
        doc.select("div[class=nav_container]").remove();
        doc.select("div[id=headerwrap]").remove();
        doc.select("amp-sidebar[id=sidebar]").remove();
        doc.select("div[class=amp-ad-wrapper amp_ad_1]").remove();
        doc.select("div[class=amp-wp-article-featured-image amp-wp-content featured-image-content]").remove();
        doc.select("div[class=amp-wp-content post-pagination-meta]").remove();
        doc.select("div[class=amp-wp-content amp_author_area ampforwp-meta-taxonomy]").remove();
        doc.select("div[class=amp-wp-content post-pagination-meta ampforwp-social-icons-wrapper ampforwp-social-icons]").remove();
        doc.select("div[id=footer]").remove();
        doc.select("div[class=comment-button-wrapper]").remove();
        String title = doc.select("h1[class=amp-wp-title]").text();
        // TODO String type = doc.select()......
        String parsedResponse = doc.toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Response",parsedResponse);
        contentValues.put(AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE,title);
        return contentValues;
    }
}
