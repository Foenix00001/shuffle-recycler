package org.foenix.shufflerecycler.database;

import android.content.Context;
import android.database.Cursor;

import org.foenix.shufflerecycler.IDataInteractor;
import org.foenix.shufflerecycler.model.Item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Foenix on 16.12.2016.
 */

public class DataProviderHelper implements IDataInteractor {
    private Context mContext;
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
        mContext.getContentResolver().update(ItemsTable.getMoveItemUri(item.getId(),item.getPrev()), null,null,null);
    }
}
