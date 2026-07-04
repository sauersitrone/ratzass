/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend;

import java.util.ArrayList;
import java.util.List;

public class ListDataProvider<E> {

    public int page;
    public int limit;
    public String sortBy;
    public String partialWhere;
    public long count;
    public int pages;
    public String errorMessage;
    public List<E> data = new ArrayList<>();

    public ListDataProvider() {
        // do nothing
    }

    public ListDataProvider(List<E> data, long count, int page, int limit) {
        this.data = data;
        this.page = page;
        this.limit = limit;
        this.count = count;
        this.pages = (int) count / limit;
    }

    public ListDataProvider(List<E> data) {
        this.data = data;
    }
}
