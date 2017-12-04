package com.appin.data;
// database contract for convenience
import android.provider.BaseColumns;

public class AppInfoContrast {

    public static final class AppInfoEntry implements BaseColumns {
        public static final String BOOK_MARK_TABLE_NAME = "BOOKMARKS";
        public static final String COLUMN_APP_TITLE = "appName";
        public static final String COLUMN_APP_URL = "appUrl";
        public static final String COLUMN_APP_ABSTRACT = "appAbstract";
        public static final String COLUMN_APP_TYPE = "appType";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }

}
