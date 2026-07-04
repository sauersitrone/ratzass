/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.components;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;

@Tag("audio")
public class Audio extends HtmlContainer {

    private static final PropertyDescriptor<String, String> srcDescriptor = PropertyDescriptors
            .attributeWithDefault("src", "");

    public Audio() {
        super();
        getElement().setProperty("controls", true);
    }

    public Audio(String src) {
        setSrc(src);
        getElement().setProperty("controls", true);
    }

    public String getSrc() {
        return get(srcDescriptor);
    }

    public void setSrc(String src) {
        set(srcDescriptor, src);
    }
}

        