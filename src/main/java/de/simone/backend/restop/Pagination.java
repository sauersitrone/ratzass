/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.restop;

import java.util.List;

public class Pagination<E> {

    public Meta meta;
    public Links links;
    public List<E> data;
    public String errorMessage;
    
    public Pagination(Meta meta, List<E> data) {
        this(meta, null, data);
    }

    public Pagination(Meta meta, Links links, List<E> data) {
        this.meta = meta;
        this.links = links;
        this.data = data;
    }
}
