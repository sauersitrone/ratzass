/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TVerticalLayout extends VerticalLayout {

    public TVerticalLayout(Component... components) {
        super(components);
        setPadding(false);
        // setSpacing(false); // this component is used mostly as container for other forms components
        setMargin(false);
        setWidthFull();
    }    
}