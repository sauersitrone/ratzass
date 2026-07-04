package de.simone.vaadinplus.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

public class Preview extends Layout {

    public Preview(Component... components) {
        super(components);
        addClassNames(Background.BASE, Padding.MEDIUM);
        setBoxSizing(BoxSizing.BORDER);
        setFlexDirection(FlexDirection.COLUMN);
        setGap(Layout.Gap.MEDIUM);
    }

}
