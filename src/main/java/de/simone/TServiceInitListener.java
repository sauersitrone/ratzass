/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.auth.NavigationAccessControl;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TServiceInitListener implements VaadinServiceInitListener {

    private NavigationAccessControl navigationAccessControl;

    public TServiceInitListener() {
        navigationAccessControl = new NavigationAccessControl();
    }

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        // i18n
        System.getProperties().setProperty("vaadin.i18n.provider", TranslationProvider.class.getName());

        // security: intercept attempts to enter all views.
        // serviceInitEvent.getSource().addUIInitListener(
        //         uiInitEvent -> uiInitEvent.getUI().addBeforeEnterListener(navigationAccessControl));
    }

}
