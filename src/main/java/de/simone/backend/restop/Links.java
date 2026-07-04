/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.restop;

/**
 * links set of links to easily move to other set of results consistently with
 * the request's limit and offset
 * <ul>
 * <li>first link to the first page
 * <li>prev link to the previous page (if available)
 * <li>next link to the next page (if available)
 * <li>last link to the last page
 */
public class Links {
    public final String first;
    public final String prev;
    public final String next;
    public final String last;

    protected Links(String first, String prev, String next, String last) {
        this.first = first;
        this.prev = prev;
        this.next = next;
        this.last = last;
    }
}
