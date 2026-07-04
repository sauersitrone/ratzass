package de.simone.vaadinplus.views.components;

import de.simone.MainLayout;
import de.simone.vaadinplus.components.IconDialog;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.vaadin.lineawesome.LineAwesomeIcon;

@PermitAll
@PageTitle("Dialogs")
@Route(value = "dialogs", layout = MainLayout.class)
public class DialogsView extends ComponentView {

    public DialogsView() {
        addClassNames(AlignItems.START, Padding.Top.LARGE);

        add(
                new Button("Left-icon dialog", e -> createLeftIconDialog().open()),
                new Button("Top-icon dialog", e -> createTopIconDialog().open())
        );
    }

    private Dialog createLeftIconDialog() {
        IconDialog dialog = new IconDialog(
                LineAwesomeIcon.CHECK_SOLID,
                "Zippity doo dah",
                "Your wibbly-wobbly task has been triumphantly accomplished, leaving everyone flabbergasted!"
        );
        dialog.setIconTheme(IconDialog.Theme.SUCCESS);
        dialog.setWidth(480, Unit.PIXELS);
        return dialog;
    }

    private Dialog createTopIconDialog() {
        IconDialog dialog = new IconDialog(
                LineAwesomeIcon.INFO_CIRCLE_SOLID,
                "Zippity doo dah",
                "Your wibbly-wobbly task has been triumphantly accomplished, leaving everyone flabbergasted!"
        );
        dialog.setIconPosition(IconDialog.Position.TOP);
        dialog.setIconTheme(IconDialog.Theme.PRIMARY);
        dialog.setWidth(400, Unit.PIXELS);
        return dialog;
    }

}
