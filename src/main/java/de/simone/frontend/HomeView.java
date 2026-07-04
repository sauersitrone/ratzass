/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.simone.MainLayout;
import de.simone.UIUtils;
import de.simone.vaadinplus.components.Highlight;
import de.simone.vaadinplus.components.Layout;
import de.simone.vaadinplus.components.Sidebar;
import de.simone.vaadinplus.utilities.Color;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;

@Route(value = "home", layout = MainLayout.class)
public class HomeView extends Div {

    private Sidebar sidebar;


    public HomeView() {
        this.sidebar = new Sidebar();
    }

    @PostConstruct
    void init() {
        Layout highlights = getHighlights();
        add(highlights, sidebar);
        addClassNames(LumoUtility.Background.CONTRAST_20);
        setSizeFull();
        // resourceCenter.init(this);
    }

    private Layout getHighlights() {
        Highlight engagement = new Highlight(
                UIUtils.createIcon(LineAwesomeIcon.RUNNING_SOLID, Color.Background.PRIMARY_10, Color.Text.PRIMARY),
                getTranslation("preview.engagement"), "10/15");

        Highlight frequency = new Highlight(
                UIUtils.createIcon(LineAwesomeIcon.ARROW_ALT_CIRCLE_DOWN, Color.Background.SUCCESS_10,
                        Color.Text.SUCCESS),
                getTranslation("preview.frequency"), "Daily");

        Highlight tamagotchi = new Highlight(
                UIUtils.createIcon(LineAwesomeIcon.ROBOT_SOLID, Color.Background.ERROR_10, Color.Text.ERROR),
                getTranslation("preview.tamagotchi"), "Happy");

        Highlight book = new Highlight(
                UIUtils.createIcon(LineAwesomeIcon.BOOK_SOLID, Color.Background.CONTRAST_10, Color.Text.BODY),
                getTranslation("preview.book"), "10%");

        Layout layout = UIUtils.getLayout(engagement, frequency, tamagotchi, book);
        return layout;

    }
}