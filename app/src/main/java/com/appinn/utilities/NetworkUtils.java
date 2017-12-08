package com.appinn.utilities;

//network utilities

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.appinn.data.Constants.*;

public class NetworkUtils {



    private final static String PARAM_QUERY = "q";


    private final static String UNKNOWNS_QUERY = "s";
    private final static String UNKNOWNS = "5999676002387380177";   //don't know what this is

    private final static String PAGE_QUERY = "p";

    private final static  String SOURCE_QUERY = "source";
    private final static  String SOURCE = HOME_PAGE;

    private final static String TAG = NetworkUtils.class.getSimpleName();

    // url builder
    public static URL buildSearchUrl(String SearchQuery, String searchResultPage) {
        Uri builtUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, SearchQuery)
                .appendQueryParameter(UNKNOWNS_QUERY, UNKNOWNS)
                .appendQueryParameter(SOURCE_QUERY,SOURCE)
                .appendQueryParameter(PAGE_QUERY,searchResultPage)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //read raw response from http url
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.v(TAG,"Starting getting response from" + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=utf-8");    //set charset header
            InputStream in = urlConnection.getInputStream();
            //convert input stream to string
            Scanner scanner = new Scanner(in);
            // "\\A", actually "\A", is a regex expression which matches the beginning of the input
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                //scan all the way down
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
