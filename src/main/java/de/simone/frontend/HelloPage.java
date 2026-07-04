/*
 *   Copyright (c) 2026 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.simone.UIUtils;

public class HelloPage extends VerticalLayout {

    protected Anchor homeAnchor;
    protected HorizontalLayout mainLayout;
    protected HorizontalLayout footerLayout;
    
    private VerticalLayout contentArea;
    private Component visualComponent;

    public HelloPage() {
        setSizeFull();
        setPadding(false);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setAlignItems(FlexComponent.Alignment.CENTER);

        visualComponent = new Span();

        contentArea = new VerticalLayout();
        contentArea.setPadding(true);
        contentArea.setWidthFull();

        mainLayout = new HorizontalLayout(visualComponent, contentArea);
        mainLayout.addClassNames(LumoUtility.BorderRadius.MEDIUM, LumoUtility.BorderColor.CONTRAST_20);
        mainLayout.setWidth("50%");

        homeAnchor = new Anchor("", "null");
        Anchor anchor1 = new Anchor("https://www.simone.de/", getTranslation("aditionalLink.userContitions"));
        Anchor anchor2 = new Anchor("https://www.simone.de/", getTranslation("aditionalLink.data"));
        Anchor anchor4 = new Anchor("https://www.simone.de/", getTranslation("aditionalLink.support"));

        footerLayout = new HorizontalLayout(anchor1, anchor2, anchor4);
        footerLayout.setWidth("50%");
        footerLayout.setVisible(false); // temp: idk what to put in the footer, so I hide it for now

        add(mainLayout, footerLayout);
    }
    
    void setVisualComponent(Component component) {
        this.visualComponent = component;
        mainLayout.removeAll();
        mainLayout.add(visualComponent, contentArea);
    }

    void setContent(Component... components) {
        contentArea.removeAll();
        contentArea.add(components);
    }

    void setContentAreaBackground(String color) {
        contentArea.getStyle().set("background", color);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        checkLayout();
    }

    private void checkLayout() {
        getUI().ifPresent(ui -> ui.getPage().retrieveExtendedClientDetails(details -> {
            boolean mobile = details.getBodyClientWidth() < UIUtils.MOBILE_BREAKPOINT;
            if (mobile) {
                visualComponent.setVisible(false);
                mainLayout.setWidth("100%");
                footerLayout.setWidth("100%");
            }
        }));
    }
}
