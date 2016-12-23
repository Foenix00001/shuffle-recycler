package org.foenix.shufflerecycler;

import android.app.IntentService;
import android.content.Intent;

import org.foenix.shufflerecycler.model.Item;
import org.foenix.shufflerecycler.database.DataProviderHelper;


/**
 * Created by Foenix on 22.12.2016.
 */

public class SaveDataService extends IntentService {
    private DataProviderHelper mDataProviderHelper;

    public SaveDataService() {
        super("SaveDataThread");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDataProviderHelper = new DataProviderHelper(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Item item = (Item) intent.getSerializableExtra("item");
        mDataProviderHelper.updateMoveItem(item);
    }

}
