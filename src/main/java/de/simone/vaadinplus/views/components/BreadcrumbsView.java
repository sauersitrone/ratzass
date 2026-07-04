package de.simone.vaadinplus.views.components;

import de.simone.MainLayout;
import de.simone.frontend.HomeView;
import de.simone.vaadinplus.components.Breadcrumb;
import de.simone.vaadinplus.components.BreadcrumbItem;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PermitAll
@PageTitle("Breadcrumbs")
@Route(value = "breadcrumbs", layout = MainLayout.class)
public class BreadcrumbsView extends ComponentView {

    public BreadcrumbsView() {
        addClassNames(Padding.Top.LARGE);

        add(new Breadcrumb(
                new BreadcrumbItem("Home", HomeView.class),
                new BreadcrumbItem("Breadcrumbs", BreadcrumbsView.class)
        ));
    }

}
