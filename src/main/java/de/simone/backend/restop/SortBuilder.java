/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.restop;

import org.apache.commons.lang3.StringUtils;

import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

public class SortBuilder {

    private SortBuilder() {

    }

    public static Sort build(String sortBy) {
        Sort sort = Sort.by();
        if (StringUtils.isBlank(sortBy)) {
            return sort;
        }
        final String[] sortingColumnsAndDirections = sortBy.split(",");
        for (String sortingColumnsAndDirection : sortingColumnsAndDirections) {
            // if no direction is precent, add asc
            if (!sortingColumnsAndDirection.contains(":"))
                sortingColumnsAndDirection += ":asc";
            String[] columnAndDirection = sortingColumnsAndDirection.split(":");
            String column = columnAndDirection[0].trim();
            String direction = columnAndDirection[1].trim();
            if (!column.isEmpty()) {
                sort = sort.and(column,
                        "desc".equalsIgnoreCase(direction) ? Direction.Descending : Direction.Ascending);
            }
        }
        return sort;
    }
}
