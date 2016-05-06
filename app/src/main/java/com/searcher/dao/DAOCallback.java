package com.searcher.dao;

public interface DAOCallback<Item> {
    void onRespone(Item item, int code);
}
