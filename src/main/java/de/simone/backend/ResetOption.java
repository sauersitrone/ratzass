/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend;

public class ResetOption {
    public String action;
    public String target;
    public String name;
    public String description;

    public static final String EMAIL_ACTION = "EMAIL";
    public static final String PHONE_ACTION = "PHONE";
    public static final String TOTP_ACTION = "TOTP";
    public ResetOption() {

    }

    public ResetOption(String action, String target, String name, String description) {
        this.action = action;
        this.target = target;
        this.name = name;
        this.description = description;
    }
}
