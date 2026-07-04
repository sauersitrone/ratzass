/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.restop;

/**
 * contains metadata about the resource
 * <ul>
 * <li>count is the total number of entities corresponding to the filters
 * <li>limit is the limit applied to the data retrieved (useful in case of
 * default values)
 * <li>page the page id. zero based
 * <li>sort is the sorting applied to the data retrieved (useful in case of
 * default
 * values)
 */
public class Meta {
    public long count;
    public int limit;
    public int page;
    public String sort;
    public int pages;

    public Meta(long count, int limit, int page) {
        this.count = count;
        this.limit = limit;
        this.page = page;
        this.pages = 1;
        if (limit > -1)
            this.pages = (int) count / limit;
    }

}
