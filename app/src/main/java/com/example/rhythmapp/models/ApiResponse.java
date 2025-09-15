package com.example.rhythmapp.models;

import java.util.List;

public class ApiResponse<T> {
    public List<T> items;
    public int count;

    public List<T> getItems() {
        return items;
    }
    public int getCount() {
        return count;
    }
}


