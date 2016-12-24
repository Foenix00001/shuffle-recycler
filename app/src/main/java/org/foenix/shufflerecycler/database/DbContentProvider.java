package org.foenix.shufflerecycler.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by Foenix on 23.10.2016.
 */

public class DbContentProvider extends ContentProvider {
    public static final String AUTHORITY = "org.foenix.database.DbContentProvider";
    private static final int ITEMS = 10;
    private static final int ITEMS_ID = 11;
    private static final int ITEMS_ALL = 12;
    private static final int ITEMS_MOVE = 12;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;
        matcher.addURI(authority, "items", ITEMS);
        matcher.addURI(authority, "items/all", ITEMS_ALL);
        matcher.addURI(authority, "items/#", ITEMS_ID);
        matcher.addURI(authority, "items/#/#", ITEMS_MOVE);
        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int id;
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                cursor = db.query(ItemsTable.DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEMS_ID:
                id = Integer.parseInt(uri.getLastPathSegment());
                if (TextUtils.isEmpty(selection)) {
                    selection = ItemsTable.KEY_ROWID + " = " + id;
                } else {
                    selection = selection + " AND " + ItemsTable.KEY_ROWID + " = " + id;
                }
                cursor = db.query(ItemsTable.DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEMS_ALL:
                cursor = db.rawQuery(ItemsTable.JOIN_QUERY, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        //cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId;
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                rowId = db.insert(ItemsTable.DATABASE_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (rowId > 0) {
            Uri resUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(resUri, null);
            return resUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                count = db.delete(ItemsTable.DATABASE_TABLE, where, whereArgs);
                break;
            case ITEMS_ID:
                int id = Integer.parseInt(uri.getLastPathSegment());
                String finalWhere = ItemsTable.KEY_ROWID + "=" + id;
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.delete(ItemsTable.DATABASE_TABLE, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        switch (sUriMatcher.match(uri)) {
            case ITEMS:
                count = db.update(ItemsTable.DATABASE_TABLE, values, selection, selectionArgs);
                break;
            case ITEMS_ID:
                int _id = Integer.parseInt(uri.getLastPathSegment());
                finalWhere = ItemsTable.KEY_ROWID + "=" + _id;
                if (selection != null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.update(ItemsTable.DATABASE_TABLE, values, finalWhere, selectionArgs);
                break;
            case ITEMS_MOVE:
                String id = uri.getPathSegments().get(ItemsTable.ID_PATH_POSITION);
                String id_prev = uri.getPathSegments().get(ItemsTable.ID_PREV_PATH_POSITION);
                count = moveItem(db, id, id_prev);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * takes item with _id= id and "moves" it after item with _id=id_prev
     * @param db database
     * @param id _id of row to update
     * @param id_prev row _id after one current row should goes to
     * @return
     */
    private int moveItem(SQLiteDatabase db, String id, String id_prev) {
        int count = 0;
        db.beginTransaction();
        try {
            String sql = ItemsTable.UPDATE_QUERY;
            String[] selectionArgs = new String[]{id,id};
            count = count + db.rawQuery(sql, selectionArgs).getCount();

            ContentValues cv = new ContentValues();
            cv.put(ItemsTable.ROW_ID_PREV, id);
            String where = ItemsTable.ROW_ID_PREV + " = ?";
            String[] whereArgs = new String[]{id_prev};
            count = count + update(ItemsTable.CONTENT_URI, cv, where, whereArgs);

            cv.clear();
            cv.put(ItemsTable.ROW_ID_PREV, id_prev);
            where = ItemsTable.KEY_ROWID + " = ?";
            whereArgs = new String[]{id};
            count = count + update(ItemsTable.CONTENT_URI, cv, where, whereArgs);

            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }
}
