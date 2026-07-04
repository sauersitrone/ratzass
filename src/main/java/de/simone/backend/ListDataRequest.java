/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class ListDataRequest {
    public static final int DEFAULT_LIMIT = 22;
    public static final int DEFAULT_PAGE = 0;

    public int limit = DEFAULT_LIMIT;
    public int page = DEFAULT_PAGE;

    /**
     * original query for view or sub list of elements
     */
    public String query;

    /**
     * query from user. will be appended to query to build a complexer query
     */
    public String partialWhere;

    /**
     * this sort can be original sort or intersected sort from user.
     */
    public String sortBy;

    /**
     * this parameter override {@link #page} and {@link #limit} parameters. used
     * when the data to collect is to export
     */
    @JsonIgnore
    public boolean toExport = false;

    public ListDataRequest() {
        // do nothing
    }

    public ListDataRequest(String query, String sortBy) {
        this.query = query;
        this.sortBy = sortBy;
    }

    public ListDataRequest copy() {
        String string = Json.encode(this);
        ListDataRequest dataRequest = ((JsonObject) Json.decodeValue(string)).mapTo(ListDataRequest.class);
        return dataRequest;
    }
}
