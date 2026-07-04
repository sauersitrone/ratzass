/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */

package de.simone.frontend.components;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.simone.TranslationProvider;
import de.simone.UIUtils;

public class SiaTaskItem extends HorizontalLayout {

    private VerticalLayout column;
    private Span primaryLabel;

    public SiaTaskItem(String task, boolean completed) {
        addClassName(LumoUtility.LineHeight.SMALL);

        String primary = TranslationProvider.getTranslation("task." + task + ".title");
        String secondary = TranslationProvider.getTranslation("task." + task + ".description");
        this.column = getPrimaryAndSecondaryComlumn(primary, secondary);

        // Span icon = null;
        SvgIcon icon = null;
        if (completed) {
             icon =        LineAwesomeIcon.CHECK_CIRCLE.create();
            // icon = UIUtils.getLaIcon("la-2x", "las la-check-circle");
            icon.addClassName(LumoUtility.TextColor.SUCCESS);
            primaryLabel.addClassName(LumoUtility.TextColor.SUCCESS);
        } else {
            // icon = UIUtils.getLaIcon("la-2x", "las la-circle");
            icon =         LineAwesomeIcon.CIRCLE.create();
        }
        icon.addClassName(LumoUtility.IconSize.LARGE);
        add(icon, column);
    }

    private VerticalLayout getPrimaryAndSecondaryComlumn(String primary, String secondary) {
        primaryLabel = new Span(primary);
        Span secondaryLabel = UIUtils.getSpan(secondary, true);
        secondaryLabel.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        VerticalLayout column2 = UIUtils.getCompactVerticalLayout(primaryLabel, secondaryLabel);
        primaryLabel.setWidthFull();
        secondaryLabel.setWidthFull();
        return column2;
    }
}
