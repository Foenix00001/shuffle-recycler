package org.foenix.shufflerecycler.database;

import android.net.Uri;

/**
 * Created by Foenix on 12.12.2016.
 */

public class ItemsTable {
    public static final String DATABASE_TABLE = "items";
    public static final Uri CONTENT_URI = Uri.parse("content://" + DbContentProvider.AUTHORITY + "/" + DATABASE_TABLE);
    public static final Uri CONTENT_URI_ALL = Uri.parse("content://" + DbContentProvider.AUTHORITY + "/" + DATABASE_TABLE + "/all");
    public static final String KEY_ROWID = "_id";
    public static final String ROW_DESCRIPTION = "description";
    public static final String ROW_ID_PREV = "id_prev";//link to next row id is redundant;
    public static final String FIELDS[] = {KEY_ROWID, ROW_DESCRIPTION, ROW_ID_PREV};
    public static final String DATABASE_TABLE_ALIAS = "items2";
    public static final String ROW_ID_NEXT_ALIAS = "id_next";
    public static final String JOIN_QUERY = "select " + DATABASE_TABLE + "." + KEY_ROWID + ", "
            + DATABASE_TABLE + "." + ROW_DESCRIPTION + ","
            + DATABASE_TABLE + "." + ROW_ID_PREV + ","
            + DATABASE_TABLE_ALIAS + "." + KEY_ROWID + " as " + ROW_ID_NEXT_ALIAS
            + " from  " + DATABASE_TABLE
            + " left outer join " + DATABASE_TABLE + "  " + DATABASE_TABLE_ALIAS
            + " on " + DATABASE_TABLE + "." + KEY_ROWID + "=" + DATABASE_TABLE_ALIAS + "." + ROW_ID_PREV;
    public static final int ID_PATH_POSITION = 1;
    public static final int ID_PREV_PATH_POSITION = 2;
    public static Uri getMoveItemUri(long id, long id_prev) {
        return CONTENT_URI.buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(Long.toString(id_prev))
                .build();
    }
}
