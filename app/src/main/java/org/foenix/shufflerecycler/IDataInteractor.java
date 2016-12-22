package org.foenix.shufflerecycler;

import org.foenix.shufflerecycler.model.Item;

import java.util.List;

/**
 * Created by Foenix on 16.12.2016.
 */

public interface IDataInteractor {
    List<Item> loadDataList();
    void updateMoveItem(Item item);
}
