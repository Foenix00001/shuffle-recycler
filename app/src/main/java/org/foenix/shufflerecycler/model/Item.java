package org.foenix.shufflerecycler.model;

import java.io.Serializable;

/**
 * Created by Foenix on 12.12.2016.
 */

public class Item implements Serializable{
    private String description;
    private long id;
    private long id_prev;
    private long id_next;

    public String getDescription() {
        return description;
    }

    public Item(long id, String description, long id_prev, long id_next) {
        this.id = id;
        this.description = description;
        this.id_prev = id_prev;
        this.id_next = id_next;
    }

    public long getNext() {
        return id_next;
    }

    public long getPrev() {
        return id_prev;
    }

    public long getId() {
        return id;
    }

    public void setNext(long id) {
        id_next = id;
    }

    public void setPrev(long id) {
        id_prev = id;
    }

    public String toString() {
        return "_id = " + id + "; id_next = " + id_next + "; id_prev = " + id_prev + "; description = " + description + ";";
    }
}
