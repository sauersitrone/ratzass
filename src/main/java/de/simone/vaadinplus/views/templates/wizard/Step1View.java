package de.simone.vaadinplus.views.templates.wizard;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PermitAll
@PageTitle("Step 1 - Wizard")
@Route(value = "1", layout = WizardLayout.class)
public class Step1View extends Div {

    public Step1View() {
        addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);

        H2 heading = new H2("Step 1");
        add(heading);
    }


}
