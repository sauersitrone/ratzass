/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */

package de.simone;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.PropertyChangeEvent;
import com.vaadin.flow.dom.PropertyChangeListener;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Position;

import de.simone.frontend.HomeView;
import de.simone.frontend.components.TTimer;
import de.simone.vaadinplus.components.Badge;
import de.simone.vaadinplus.utilities.BadgeVariant;
import de.simone.vaadinplus.views.components.AppBarsView;
import de.simone.vaadinplus.views.components.BreadcrumbsView;
import de.simone.vaadinplus.views.components.CheckboxesView;
import de.simone.vaadinplus.views.components.DialogsView;
import de.simone.vaadinplus.views.components.EmptyStatesView;
import de.simone.vaadinplus.views.components.GridsView;
import de.simone.vaadinplus.views.components.HeadersView;
import de.simone.vaadinplus.views.components.HighlightsView;
import de.simone.vaadinplus.views.components.InputGroupsView;
import de.simone.vaadinplus.views.components.KeyValuePairsView;
import de.simone.vaadinplus.views.components.ListsView;
import de.simone.vaadinplus.views.components.MenuBarsView;
import de.simone.vaadinplus.views.components.NavRailView;
import de.simone.vaadinplus.views.components.NotificationsView;
import de.simone.vaadinplus.views.components.RadioButtonsView;
import de.simone.vaadinplus.views.components.SearchDialogsView;
import de.simone.vaadinplus.views.components.SidebarsView;
import de.simone.vaadinplus.views.components.StatusesView;
import de.simone.vaadinplus.views.components.SteppersView;
import de.simone.vaadinplus.views.components.TabsView;
import de.simone.vaadinplus.views.components.TopNavView;
import de.simone.vaadinplus.views.templates.CheckoutView;
import de.simone.vaadinplus.views.templates.CustomersView;
import de.simone.vaadinplus.views.templates.DashboardView1;
import de.simone.vaadinplus.views.templates.DashboardView2;
import de.simone.vaadinplus.views.templates.FilesView;
import de.simone.vaadinplus.views.templates.HotelsView;
import de.simone.vaadinplus.views.templates.ProductDetailsView;
import de.simone.vaadinplus.views.templates.ProductListView;
import de.simone.vaadinplus.views.templates.ProfileView;
import de.simone.vaadinplus.views.templates.ShoppingCartView;
import de.simone.vaadinplus.views.templates.ValidationView;
import de.simone.vaadinplus.views.templates.wizard.Step1View;

public class MainLayout extends AppLayout implements BeforeEnterObserver, PropertyChangeListener {

    private TTimer timer;
    private HorizontalLayout toolBar;
    private List<MenuItem> languageItems;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        H1 viewTitle = new H1();
        viewTitle.addClassNames(FontSize.LARGE);

        this.toolBar = new HorizontalLayout();
        toolBar.setJustifyContentMode(JustifyContentMode.END);
        toolBar.setAlignItems(Alignment.CENTER);
        toolBar.addClassName(LumoUtility.Margin.Right.LARGE);

        HorizontalLayout horizontalLayout = new HorizontalLayout(toggle, viewTitle, toolBar);
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        horizontalLayout.setFlexGrow(1, viewTitle);
        horizontalLayout.getStyle().set("overflow", "auto");

        Badge messageBadge = new Badge();
        messageBadge.addClassNames("end-xs", Position.ABSOLUTE, "top-xs");
        messageBadge.addThemeVariants(
                BadgeVariant.SUCCESS, BadgeVariant.PILL, BadgeVariant.PRIMARY, BadgeVariant.SMALL);

        Button messageButton = new Button(LineAwesomeIcon.COMMENTS.create());
        messageButton.addClassNames(Margin.Start.AUTO);
        messageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        messageButton.setAriaLabel("View messages (4)");
        messageButton.setSuffixComponent(messageBadge);
        messageButton.setTooltipText("View messages (4)");

        Badge notificationsBadge = new Badge();
        notificationsBadge.addClassNames("end-xs", Position.ABSOLUTE, "top-xs");
        notificationsBadge.addThemeVariants(
                BadgeVariant.ERROR, BadgeVariant.PILL, BadgeVariant.PRIMARY, BadgeVariant.SMALL);

        Button notificationsButton = new Button(LumoIcon.BELL.create());
        notificationsButton.addClassNames(Margin.Start.XSMALL);
        notificationsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        notificationsButton.setAriaLabel("View notifications (2)");
        notificationsButton.setSuffixComponent(notificationsBadge);
        notificationsButton.setTooltipText("View notifications (2)");

        // user avatar component & menu
        this.timer = UIUtils.getTimer(this, 10);
        this.languageItems = new ArrayList<>();
    }

    private void addDrawerContent() {
        MainLayoutNavHeader header = new MainLayoutNavHeader();

        SideNav nav = createComponentNavigation();
        // Layout layout = new Layout(nav, createTemplatesNavigation());
        // layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        // layout.setGap(Layout.Gap.MEDIUM);

        Scroller scroller = new Scroller(nav);
        scroller.setSizeFull();
        addToDrawer(header, scroller);
        // addToDrawer(header, nav);
    }

    private SideNav createComponentNavigation() {
        SideNav nav = new SideNav();

        // nav.addItem(new SideNavItem(getTranslation("HomeView"), HomeView.class,
        // LineAwesomeIcon.HOME_SOLID.create()));
        nav.addItem(new SideNavItem(getTranslation("SauerDashBoard"), HomeView.class,
                LineAwesomeIcon.CHART_PIE_SOLID.create()));

        // settings
        SideNavItem config = new SideNavItem(getTranslation("mainLayout.config.menu"));
        nav.addItem(config);

        // vaadin+
        SideNavItem vaadinp = new SideNavItem("vaadin+");
        vaadinp.addItem(
                new SideNavItem("App bars", AppBarsView.class, LineAwesomeIcon.BARS_SOLID.create()));
        vaadinp.addItem(
                new SideNavItem("Breadcrumbs", BreadcrumbsView.class, LineAwesomeIcon.BREAD_SLICE_SOLID.create()));
        vaadinp.addItem(
                new SideNavItem("Checkboxes", CheckboxesView.class, LineAwesomeIcon.CHECK_SQUARE.create()));
        vaadinp.addItem(new SideNavItem("Dialogs", DialogsView.class, LineAwesomeIcon.WINDOWS.create()));
        vaadinp.addItem(new SideNavItem("Empty states", EmptyStatesView.class, LineAwesomeIcon.FILE.create()));
        vaadinp.addItem(new SideNavItem("Grids", GridsView.class, LineAwesomeIcon.LIST_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Headers", HeadersView.class, LineAwesomeIcon.HEADING_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Highlights", HighlightsView.class, LineAwesomeIcon.CHART_LINE_SOLID.create()));
        vaadinp.addItem(
                new SideNavItem("Input groups", InputGroupsView.class, LineAwesomeIcon.TERMINAL_SOLID.create()));
        vaadinp.addItem(
                new SideNavItem("Key-value pairs", KeyValuePairsView.class, LineAwesomeIcon.KEY_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Lists", ListsView.class, LineAwesomeIcon.LIST_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Menu bars", MenuBarsView.class, LineAwesomeIcon.ELLIPSIS_V_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Navigation rail", NavRailView.class, LineAwesomeIcon.BARS_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Notifications", NotificationsView.class, LineAwesomeIcon.BELL.create()));
        vaadinp.addItem(
                new SideNavItem("Radio buttons", RadioButtonsView.class, LineAwesomeIcon.CHECK_CIRCLE_SOLID.create()));
        vaadinp.addItem(
                new SideNavItem("Search dialogs", SearchDialogsView.class, LineAwesomeIcon.SEARCH_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Sidebar", SidebarsView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Statuses", StatusesView.class, LineAwesomeIcon.INFO_CIRCLE_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Steppers", SteppersView.class, LineAwesomeIcon.WALKING_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Tabs", TabsView.class, LineAwesomeIcon.EXCHANGE_ALT_SOLID.create()));
        vaadinp.addItem(new SideNavItem("Top nav", TopNavView.class, LineAwesomeIcon.BARS_SOLID.create()));
        nav.addItem(vaadinp);

        SideNavItem templates = new SideNavItem("Templates");
        templates.addItem(new SideNavItem("Checkout", CheckoutView.class, LineAwesomeIcon.CREDIT_CARD.create()));
        templates.addItem(new SideNavItem("Customers", CustomersView.class, LineAwesomeIcon.HANDSHAKE.create()));
        templates.addItem(
                new SideNavItem("Dashboard º1", DashboardView1.class, LineAwesomeIcon.CHART_LINE_SOLID.create()));
        templates.addItem(
                new SideNavItem("Dashboard º2", DashboardView2.class, LineAwesomeIcon.CHART_LINE_SOLID.create()));
        templates.addItem(new SideNavItem("Files", FilesView.class, LineAwesomeIcon.FILE.create()));
        templates.addItem(new SideNavItem("Hotels", HotelsView.class, LineAwesomeIcon.HOTEL_SOLID.create()));
        templates.addItem(new SideNavItem("Product details", ProductDetailsView.class,
                LineAwesomeIcon.INFO_CIRCLE_SOLID.create()));
        templates.addItem(
                new SideNavItem("Product list", ProductListView.class, LineAwesomeIcon.TH_LARGE_SOLID.create()));
        templates.addItem(new SideNavItem("Profile", ProfileView.class, LineAwesomeIcon.USER.create()));
        templates.addItem(
                new SideNavItem("Shopping cart", ShoppingCartView.class, LineAwesomeIcon.SHOPPING_CART_SOLID.create()));
        templates.addItem(
                new SideNavItem("Validation form", ValidationView.class, LineAwesomeIcon.CHECK_CIRCLE.create()));
        templates.addItem(new SideNavItem("Wizard (WIP)", Step1View.class, LineAwesomeIcon.HAT_WIZARD_SOLID.create()));
        nav.addItem(templates);

        return nav;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // boolean deviceIsBlocked = SecurityUtils.logoutIfBlocked();

        // UI.getCurrent().getPage().addJavaScript("/webauthn.js", LoadMode.LAZY);
        setId(getClass().getName());
        // currentUser = SecurityUtils.getLoggedUser();
        timer.start();
    }

    public void changeTheme(String newTheme) {
        // if theme is null, swich theme, otherwise, set the theme

        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        themeList.clear();
        if (newTheme.equals(Lumo.DARK))
            themeList.add(Lumo.DARK);
        if (newTheme.equals(Lumo.LIGHT))
            themeList.add(Lumo.LIGHT);
    }

    public void changeLanguage(String newLanguage) {
        languageItems.forEach(
                mi -> {
                    String lang = mi.getId().orElse("0");
                    mi.setChecked(lang.equals(newLanguage));
                });

        UI.getCurrent().getSession().setLocale(Locale.of(newLanguage));
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        // checkNewMessages();
    }

    /**
     * Returns the current {@link #MainLayout()}
     * 
     * @return the MainLayout
     */
    public static MainLayout getInstance(Component component) {
        Optional<Component> optional = component.getParent();
        MainLayout mainLayout = null;
        while (optional.isPresent()) {
            Component p = optional.get();
            if (p instanceof MainLayout layout) {
                mainLayout = layout;
            }
            optional = p.getParent();
        }
        return mainLayout;
    }
}
