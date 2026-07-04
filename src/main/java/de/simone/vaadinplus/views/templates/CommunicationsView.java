package de.simone.vaadinplus.views.templates;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.simone.MainLayout;

@PageTitle("Communications")
@Route(value = "communications", layout = MainLayout.class)
public class CommunicationsView extends Main {

    public CommunicationsView() {
        // Sidebar
        // - Title
        // - Search
        // - List
        // Content
        // - Header
        // - Different types of messages
    }

}
