package org.foenix.shufflerecycler;

import android.app.IntentService;
import android.content.Intent;

import org.foenix.shufflerecycler.model.Item;
import org.foenix.shufflerecycler.database.DataProviderHelper;


/**
 * Created by Foenix on 22.12.2016.
 */

public class SaveDataService extends IntentService {
    private IDataInteractor dataInteractor;

    public SaveDataService() {
        super("SaveDataThread");
        dataInteractor = new DataProviderHelper();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Item item = (Item) intent.getSerializableExtra("item");
        dataInteractor.updateMoveItem(item);
        //Log.i(TAG, item.toString() + " item processed in " + Thread.currentThread().getName());
    }

}
