/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.restop;

import java.util.Map;

public class Filter {
    private String query;
    private Map<String, Object> parameters;
    
    public static Filter withQuery(String query) {
        Filter filter = new Filter();
        filter.query = query;
        return filter;
    }

    public Filter andParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
