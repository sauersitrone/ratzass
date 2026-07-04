package de.simone.vaadinplus.views.components;

import de.simone.MainLayout;
import de.simone.vaadinplus.components.EmptyState;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import org.vaadin.lineawesome.LineAwesomeIcon;

@PermitAll
@PageTitle("Empty states")
@Route(value = "empty-states", layout = MainLayout.class)
public class EmptyStatesView extends ComponentView {

    public EmptyStatesView() {
        EmptyState emptyState = new EmptyState("Empty in events", "Create an event and it will show up here", "New event", null);
        emptyState.setIcon(LineAwesomeIcon.CALENDAR_PLUS.create());
        add(emptyState);
    }

}
