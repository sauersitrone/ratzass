/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */

package de.simone;

public class TGroupedProperty {

    public String bundleKey;
    public String group;
    public int order;
    public String key;
    public String value;
    public boolean isDisabled;

    public TGroupedProperty() {
        group = "";
        this.order = -1;
        key = "";
        value = "";
        isDisabled = true;
    }

    public static String[] parseBundleKey(String bundleKey) {
        String[] prp = bundleKey.split("-");
        return prp;
    }

    public TGroupedProperty(String bundleKey, String value) {
        this.bundleKey = bundleKey;
        String[] prp = parseBundleKey(bundleKey);
        this.group = prp[0];
        this.order = Integer.parseInt(prp[1]);
        this.key = prp[2];
        this.value = value;
        if (order == 0)
            isDisabled = true;
    }

    @Override
    public String toString() {
        return key + " = " + value;
    }
}
