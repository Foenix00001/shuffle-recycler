package org.foenix.shufflerecycler;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.foenix.shufflerecycler.database.DataProviderHelper;
import org.foenix.shufflerecycler.model.Item;

import java.util.List;

/**
 * Created by Foenix on 23.12.2016.
 */

public class ItemLoader extends AsyncTaskLoader {
    private IDataInteractor mDataInteractor;

    public ItemLoader(Context context) {
        super(context);
        mDataInteractor = new DataProviderHelper(context.getApplicationContext());
    }

    @Override
    public List<Item> loadInBackground() {
        return mDataInteractor.loadDataList();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
