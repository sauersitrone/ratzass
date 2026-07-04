/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.simone.UIUtils;

public class Card extends VerticalLayout {

    private HorizontalLayout headerLayout;
    private HorizontalLayout footerLayout;
    private VerticalLayout bodyLayout;

    public Card() {
        this(null, null, null);
    }

    public Card(Component header, Component body, Component footer) {
        // header
        this.headerLayout = new HorizontalLayout();
        headerLayout.setWidth("100%");
        if (header != null)
            headerLayout.add(header);

        // body
        this.bodyLayout = new VerticalLayout();
        bodyLayout.addClassNames(LumoUtility.Flex.AUTO);
        
        if (body != null)
            bodyLayout.add(body);

        // footer
        this.footerLayout = UIUtils.getFooter();
        if (footer != null)
            footerLayout.add(footer);

        add(headerLayout, bodyLayout, footerLayout);
        setPadding(false);
        setSpacing(false);
    }

    public HorizontalLayout getHeaderLayout() {
        return headerLayout;
    }

    public VerticalLayout getBodyLayout() {
        return bodyLayout;
    }

    public void setFooter(Component... components) {
        footerLayout.removeAll();
        footerLayout.add(components);
    }

    public void setHeader(Component... components) {
        headerLayout.removeAll();
        headerLayout.add(components);
        headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
    }

    public void setBody(Component... components) {
        bodyLayout.removeAll();
        bodyLayout.add(components);
    }

    public void hideFooter() {
        remove(footerLayout);
    }

    public void showFooter() {
        footerLayout.getElement().setAttribute("open", true);
    }

    public void hide() {
        getElement().setAttribute("open", false);
    }

    public void show() {
        getElement().setAttribute("open", true);
    }

    public HorizontalLayout getFooterLayout() {
        return footerLayout;
    }
}
