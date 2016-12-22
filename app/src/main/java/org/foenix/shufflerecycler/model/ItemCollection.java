package org.foenix.shufflerecycler.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by Foenix on 20.12.2016.
 */

public class ItemCollection {
    private List<Item> mList;

    public ItemCollection(List<Item> items) {
        mList = items;
    }

    public void setData(List<Item> list) {
        mList = list;
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        Item item = removeItem(fromPosition);
        insertItem(toPosition, item);
    }

    public List<Item> getData() {
        return mList;
    }

    public int getItemCount() {
        return (mList == null) ? 0 : mList.size();
    }

    public void clearData() {
        mList = Collections.<Item>emptyList();
    }

    public Item removeItem(int position) {
        Item item = mList.get(position);
        if (position == 0) {
            mList.get(position + 1).setPrev(0);
            return mList.remove(position);
        }
        if (position == getItemCount() - 1) {
            mList.get(position - 1).setNext(0);
            return mList.remove(position);
        }
        mList.get(position - 1).setNext(item.getNext());
        mList.get(position + 1).setPrev(item.getPrev());
        return mList.remove(position);
    }

    public void insertItem(int position, Item item) {
        mList.add(position, item);
        if (position == 0) {
            item.setPrev(0);
            item.setNext(mList.get(position).getId());
            mList.get(position + 1).setPrev(item.getId());
            return;
        }
        if (position == getItemCount() - 1) {
            mList.get(position - 1).setNext(item.getId());
            item.setNext(0);
            item.setPrev(mList.get(position - 1).getId());
            return;
        }
        mList.get(position - 1).setNext(item.getId());
        mList.get(position).setPrev(item.getId());
        item.setPrev(mList.get(position - 1).getId());
        item.setNext(mList.get(position).getId());
    }
}
