package org.foenix.shufflerecycler.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.foenix.shufflerecycler.IDataInteractor;
import org.foenix.shufflerecycler.model.Item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Foenix on 16.12.2016.
 */

public class DataProviderHelper implements IDataInteractor {
    Context mContext;
    public DataProviderHelper(Context context){
        mContext = context;
    }
    public List<Item> loadDataList() {
        Cursor cursor = mContext.getContentResolver().query(ItemsTable.CONTENT_URI_ALL, null, null, null, null);
        HashMap<Long, Item> rawList = new HashMap<>();
        List<Item> result = new LinkedList<>();
        Item next_item = null;
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long id_prev = cursor.getLong((cursor.getColumnIndex(ItemsTable.ROW_ID_PREV)));
                Item curr_item = new Item(
                        cursor.getLong(cursor.getColumnIndex(ItemsTable.KEY_ROWID)),
                        cursor.getString((cursor.getColumnIndex(ItemsTable.ROW_DESCRIPTION))),
                        id_prev,
                        cursor.getLong((cursor.getColumnIndex(ItemsTable.ROW_ID_NEXT_ALIAS))));
                rawList.put(cursor.getLong(cursor.getColumnIndex(ItemsTable.KEY_ROWID)),
                        curr_item);
                if (id_prev == 0) {
                    next_item = curr_item;
                }
            }
            cursor.close();
            while (next_item != null) {
                result.add(next_item);
                next_item = rawList.get(next_item.getNext());
            }
        }
        return result;
    }

    @Override
    public void updateMoveItem(Item item) {
        SQLiteDatabase db = DbHelper.getInstance(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            String id = Long.toString(item.getId());

            String[] projection = new String[]{ItemsTable.ROW_ID_PREV};
            String selection = "_id = ?";
            String[] selectionArgs = new String[]{id};
            Cursor cursor = mContext.getContentResolver().query(ItemsTable.CONTENT_URI, projection, selection, selectionArgs, null);
            String id_prev = null;
            if (cursor != null) {
                cursor.moveToFirst();
                id_prev = Long.toString(cursor.getLong((cursor.getColumnIndex(ItemsTable.ROW_ID_PREV))));
                cursor.close();
            }
            ContentValues cv = new ContentValues();
            cv.put(ItemsTable.ROW_ID_PREV, id_prev);
            String where = ItemsTable.ROW_ID_PREV + " = ?";
            String[] whereArgs = new String[]{id};
            mContext.getContentResolver().update(ItemsTable.CONTENT_URI, cv, where, whereArgs);

            ContentValues cv1 = new ContentValues();
            cv1.put(ItemsTable.ROW_ID_PREV, id);
            String where1 = ItemsTable.ROW_ID_PREV + " = ?";
            String[] whereArgs1 = new String[]{Long.toString(item.getPrev())};
            mContext.getContentResolver().update(ItemsTable.CONTENT_URI, cv1, where1, whereArgs1);

            ContentValues cv2 = new ContentValues();
            cv2.put(ItemsTable.ROW_ID_PREV, Long.toString(item.getPrev()));
            String where2 = ItemsTable.KEY_ROWID + " = ?";
            String[] whereArgs2 = new String[]{id};
            mContext.getContentResolver().update(ItemsTable.CONTENT_URI, cv2, where2, whereArgs2);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
