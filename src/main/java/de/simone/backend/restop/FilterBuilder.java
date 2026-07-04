/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.restop;


import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

public class FilterBuilder {

    public static final String QUERY_PARAM_LIMIT = "limit";
    public static final String DEFAULT_VALUE_LIMIT = "25";
    public static final String QUERY_PARAM_PAGE = "page";
    public static final String DEFAULT_VALUE_PAGE = "0";
    public static final String QUERY_PARAM_SORT = "sortBy";
    public static final String DEFAULT_VALUE_SORT = "id:Ascending";
    public static final String QUERY_PARAM_PARENT = "parent";
    public static final String DEFAULT_VALUE_PARENT = "";

    private final UriInfo uriInfo;
    private final MultivaluedMap<String, String> multivaluedMap;
    private String where = "";

    FilterBuilder(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        this.multivaluedMap = null;
    }

    FilterBuilder(MultivaluedMap<String, String> multivaluedMap) {
        this.uriInfo = null;
        this.multivaluedMap = multivaluedMap;
    }

    public static FilterBuilder withUriInfo(UriInfo uriInfo) {
        return new FilterBuilder(uriInfo);
    }

    public static FilterBuilder withMultiValuedMap(MultivaluedMap<String, String> multivaluedMap) {
        return new FilterBuilder(multivaluedMap);
    }

    public FilterBuilder andWhere(String where) {
        this.where = where;
        return this;
    }

    /**
     * old changes befor elasticesearch
     * 
     * @return
     */
    public Filter build() {
        final StringBuilder queryBuilder = new StringBuilder();
        final Map<String, Object> parameters = new HashMap<>();
        if (!where.isEmpty()) {
            queryBuilder.append(where);
        }
        MultivaluedMap<String, String> map = multivaluedMap == null ? uriInfo.getQueryParameters(true) : multivaluedMap;
        map.forEach((key, values) -> {
            if (!(QUERY_PARAM_LIMIT.equals(key) || QUERY_PARAM_PAGE.equals(key)
                    || QUERY_PARAM_SORT.equals(key) || QUERY_PARAM_PARENT.equals(key))) {
                if (queryBuilder.length() != 0)
                    queryBuilder.append(" and ");
                queryBuilder.append(key);
                queryBuilder.append(" in :");
                queryBuilder.append(key);
                parameters.put(key, values);
            }
        });
        return Filter.withQuery(queryBuilder.toString()).andParameters(parameters);
    }

    /**
     * original code
     * 
     * @return
     */
    public Filter buildOld() {
        final StringBuilder queryBuilder = new StringBuilder();
        final Map<String, Object> parameters = new HashMap<>();
        if (!where.isEmpty()) {
            String[] whereConditions = where.split(",");
            for (String whereCondition : whereConditions) {
                String[] nameAndValue = whereCondition.split(":");
                String name = nameAndValue[0].trim();
                // Object value = name.equals("campaign_id") ?
                // Long.parseLong(nameAndValue[1].trim()) : nameAndValue[1].trim();
                Object value = name.equals("campaign_id") ? Long.parseLong(nameAndValue[1].trim())
                        : nameAndValue[1].trim();
                if (!name.isEmpty()) {
                    if (queryBuilder.length() != 0)
                        queryBuilder.append(" and ");
                    queryBuilder.append(name);
                    queryBuilder.append(" = :");
                    queryBuilder.append(name);
                    parameters.put(name, value);
                }
            }
        } else {
            uriInfo.getQueryParameters(true).forEach((key, values) -> {
                if (!(QUERY_PARAM_LIMIT.equals(key) || QUERY_PARAM_PAGE.equals(key)
                        || QUERY_PARAM_SORT.equals(key) || QUERY_PARAM_PARENT.equals(key))) {
                    if (queryBuilder.length() != 0)
                        queryBuilder.append(" and ");
                    queryBuilder.append(key);
                    queryBuilder.append(" in :");
                    queryBuilder.append(key);
                    parameters.put(key, values);
                }
            });
        }
        return Filter.withQuery(queryBuilder.toString()).andParameters(parameters);
    }
}
