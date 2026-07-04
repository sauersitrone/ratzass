/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.PropertyChangeEvent;
import com.vaadin.flow.dom.PropertyChangeListener;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;

import de.simone.TranslationProvider;
import de.simone.UIUtils;
import de.simone.ViewUtils;
import de.simone.backend.ListDataRequest;
import de.simone.backend.TAEntity;
import de.simone.backend.TAService;
import de.simone.frontend.components.TTimer;
import de.simone.vaadinplus.components.EmptyState;
import de.simone.vaadinplus.components.Header;
import de.simone.vaadinplus.components.Layout;
import de.simone.vaadinplus.components.Sidebar;
import de.simone.vaadinplus.components.list.ListItem2;
import jakarta.ws.rs.core.Response;

public abstract class TAView<E extends TAEntity> extends Main
        implements BeforeEnterObserver, ComponentEventListener<ClickEvent<Button>>,
        PropertyChangeListener {

    protected static final String AUTH_ERROR_MSG = "Auth.error";
    protected static final int SHORT = 20;
    protected static final int MEDIUM = 27;

    protected Grid<E> grid;
    protected de.simone.vaadinplus.components.list.List2 list;
    protected Sidebar sidebar;
    protected TAService<E> taService;
    protected Class<E> taEntityClass;
    protected String formUrl;
    protected String viewUrl;
    protected TextField searchField;
    protected AccessAnnotationChecker accessAnnotationChecker;
    protected EmptyState emptyState;
    protected TTimer timer;
    protected Header header;
    protected VaadinContext context;
    protected List<SvgIcon> aditionalActions;
    protected List<String> itemActionToRemove;
    protected int mobilFirstColumnTextLength = MEDIUM;
    protected boolean areItemsEditable = true;

    // work variables during onComponentEvent(ClickEvent<Button>)
    protected E selectedItem;
    // work variables during onComponentEvent(ClickEvent<Button>)
    protected String selectedComponentId;
    // work variable to store the current device status
    protected boolean deviceIsBlocked;

    private HastEntity<E> hastEntity;
    private Class<? extends TAForm<E>> formClass;
    private ListDataRequest listRequest;
    private String configDialogBundle;
    private String tipOftheDay;
    private String[] tasksToCheck;
    private Dialog configDialog;

    protected TAView() {
        accessAnnotationChecker = new AccessAnnotationChecker();
        timer = UIUtils.getTimer(this, 1);
        aditionalActions = new ArrayList<>();
        itemActionToRemove = new ArrayList<>();
        addClassNames(Display.FLEX, FlexDirection.COLUMN);

        setSizeFull();
    }

    /**
     * see
     * {@link #addAditionalAction(LineAwesomeIcon, String, String, ButtonVariant)}
     * 
     */
    protected void addAditionalAction(LineAwesomeIcon icon, String id, String after) {
        addAditionalAction(icon, id, after, ButtonVariant.LUMO_TERTIARY);
    }

    /**
     * allow to add more actions to this component. during render, the action will
     * be show on item elements or as global action.
     *
     * The position of the action is
     * determined by the "after" parameter, which specifies the action after which
     * this new action should be placed.
     *
     * @param icon  - the icon
     * @param id    - id of the component
     * @param after - the action name to put the action component associated with
     *              this icon
     * @return the icon
     */
    protected void addAditionalAction(LineAwesomeIcon icon, String id, String after, ButtonVariant variant) {
        String iName = "line-awesome/svg/" + icon.getSvgName() + ".svg";
        SvgIcon icon2 = new SvgIcon(iName);
        icon2.setId(id);
        icon2.getElement().setProperty(ViewUtils.ICON_NAME, iName);
        icon2.getElement().setProperty(ViewUtils.AFTER_OF, after);
        icon2.getElement().setProperty(ViewUtils.BUTTON_VARIANT, variant.name());
        Optional<SvgIcon> optional = aditionalActions.stream().filter(a -> a.getId().orElse("null").equals(id))
                .findAny();
        if (!optional.isPresent())
            aditionalActions.add(icon2);
    }

    public Component getToolBarComponent(String id) {
        Layout layout = header.getActionsLayout();
        Optional<Component> optional = layout.getChildren().filter(c -> c.getId().orElse("null").equals(id)).findAny();
        if (!optional.isPresent())
            return null;
        return optional.get();
    }

    public void removeItemAction(String componentsId) {
        itemActionToRemove.add(componentsId);
    }

    public void removeToolBarComponents(String... componentsId) {
        Layout layout = header.getActionsLayout();
        for (String id : componentsId) {
            Component component = getToolBarComponent(id);

            // in case of dople executioon of this method, the component could be already
            // removed
            if (component != null)
                layout.remove(component);
        }
    }

    protected void init(
            Class<E> entityClass,
            Class<? extends TAForm<E>> formClass,
            TAService<E> service) {

        this.context = VaadinService.getCurrent().getContext();
        this.taEntityClass = entityClass;
        this.taService = service;
        this.viewUrl = RouteUtil.resolve(context, getClass());
        this.formClass = formClass;
        this.formUrl = RouteUtil.resolve(context, formClass);

        sidebar = new Sidebar();
        header = getHeader(this.getClass(), this);

        String title = getTranslation(UIUtils.getSimpleClassName(this));
        String description = getTranslation(UIUtils.getSimpleClassName(this) + ".tt");
        String action = getTranslation(taEntityClass.getSimpleName());
        action += " " + getTranslation("addText");
        String icon = getTranslation(UIUtils.getSimpleClassName(this) + ".icon");
        emptyState = new EmptyState(title, description, action, this);
        SvgIcon icon2 = new SvgIcon("line-awesome/svg/" + icon + ".svg");
        emptyState.setIcon(icon2);
        emptyState.setVisible(false);

        removeAll();
        add(timer, header, grid, sidebar, emptyState);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (tipOftheDay != null) {
            // GOnboarding onboarding = UIUtils.getOnboarding(tipOftheDay, this);
            // onboarding.start();
            timer.pause();
        }
    }

    protected Component getFirstColumnRenderer(E entity, String primary, String secondary, String image,
            boolean withActions) {
        Image image2 = UIUtils.getImageIcon(image, "40px");
        return getFirstColumnRendererImpl(entity, primary, secondary, image2, withActions);
    }

    protected Component getFirstColumnRenderer(E entity, String primary, String secondary, boolean withActions) {
        return getFirstColumnRendererImpl(entity, primary, secondary, null, withActions);
    }

    protected Component getFirstColumnRendererImpl(E entity, String primary, String secondary, Component image,
            boolean withActions) {
        String primary2 = withActions ? StringUtils.abbreviate(primary, mobilFirstColumnTextLength) : primary;
        String secondary2 = withActions ? StringUtils.abbreviate(secondary, mobilFirstColumnTextLength) : secondary;
        ListItem2 listItem2 = new ListItem2(primary2, secondary2);
        if (image != null) {
            image.addClassName(LumoUtility.BorderRadius.SMALL);
            listItem2.setPrefix(image);
        }

        if (withActions) {
            Span span = getItemActions(entity);
            listItem2.setSuffix(span);
        }
        return listItem2;

    }

    protected void checkTasks(String... tasksToCheck) {
        this.tasksToCheck = tasksToCheck;
    }

    public void setItems(List<E> items) {
        setItems(items, null);
    }

    public void setItems(List<E> items, SerializablePredicate<E> filter) {
        GridListDataView<E> dataView = grid.setItems(items);
        searchField.addValueChangeListener(evt -> dataView.refreshAll());
        if (filter != null) {
            dataView.addFilter(filter);
        }

        emptyState.setVisible(items.isEmpty());
        grid.setVisible(!items.isEmpty());
        header.setVisible(!items.isEmpty());

    }

    public ListDataRequest getListRequest() {
        return listRequest;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        getUI().ifPresent(
                ui -> {
                    Page page = ui.getPage();
                    page.retrieveExtendedClientDetails(
                            details -> updateVisibleColumns(details.getBodyClientWidth()));
                    page.addBrowserWindowResizeListener(event -> updateVisibleColumns(event.getWidth()));
                });
    }

    private void updateVisibleColumns(int width) {
        boolean mobile = width < UIUtils.MOBILE_BREAKPOINT;
        List<Grid.Column<E>> columns = grid.getColumns();

        // "Mobile" column
        columns.get(0).setVisible(mobile);

        // "Desktop" columns
        for (int i = 1; i < columns.size(); i++) {
            Column<E> column = columns.get(i);
            // sub classes can alter visibility of a column
            boolean isVisible = column.getElement().getProperty(UIUtils.COLUMN_VISIBLE, true);
            column.setVisible(!mobile && isVisible);
        }
    }

    /**
     * return the current selected element in the list or grid or null if nothing
     * has be selected
     *
     * @return - the element
     */
    @SuppressWarnings("unchecked")
    protected E getSelectedItem(Long itemId) {
        ListDataProvider<E> dataProvider = (ListDataProvider<E>) grid.getDataProvider();
        for (E entity : dataProvider.getItems()) {
            if (entity.id.equals(itemId))
                return entity;
        }

        return null;
    }

    protected void setDialogElements(String bundle, HastEntity<E> body) {
        this.configDialogBundle = bundle;
        this.hastEntity = body;
    }

    protected void openConfigDialog(E entity) {
        hastEntity.setEntity(entity);
        Button acceptButton = UIUtils.getButton(configDialogBundle + ".accept", ButtonVariant.LUMO_PRIMARY);
        acceptButton.setId(TActions.SAVE_FORM);
        acceptButton.addClickListener(this);

        Button update = UIUtils.getButton(configDialogBundle + ".update", ButtonVariant.LUMO_TERTIARY);
        update.setId(TActions.UPDATE_FORM);
        update.addClickListener(this);

        configDialog = UIUtils.getDialog(
                configDialogBundle + ".title",
                null,
                Arrays.asList(update, acceptButton),
                configDialogBundle + ".cancel");

        configDialog.removeAll();
        configDialog.add((Component) hastEntity);
        configDialog.setWidth("90%");
        configDialog.setHeight("90%");
        configDialog.open();
    }

    protected boolean hasAccess(Class<?> cls) {
        boolean hasAccess = accessAnnotationChecker.hasAccess(cls);
        if (!hasAccess) {
            UIUtils.showErrorNotification(AUTH_ERROR_MSG);
        }
        return hasAccess;
    }

    /**
     * check if the current user are autorized to perform the action. if the user is
     * not autorized, this method show an error notification
     * 
     * @param action - the action
     * @return true o false
     */
    protected boolean isActionSecure(String action) {
        // if device is blocked, return silent
        if (deviceIsBlocked)
            return false;

        return true;
    }

    @Override
    public void onComponentEvent(ClickEvent<Button> event) {

        selectedComponentId = event.getSource().getId().orElse("null");
        String itemIdS = event.getSource().getElement().getProperty(ViewUtils.ENTITY_ID);
        Long itemId = itemIdS == null ? 0L : Long.parseLong(itemIdS);
        selectedItem = getSelectedItem(itemId);

        if (isActionSecure(TActions.NEW_ACTION)) {
            ViewUtils.showForm(formClass, sidebar, taService.get(0L));
        }

        if (isActionSecure(TActions.EDIT_ACTION) || isActionSecure(TActions.WATCH_ACTION)) {
            ViewUtils.showForm(formClass, sidebar, selectedItem);
        }

        if (selectedComponentId.equals(TActions.SAVE_FORM) && hastEntity.isValid()) {
            E entity = hastEntity.getEntity();
            Response response = taService.save(entity);
            if (UIUtils.showNotification(response)) {
                configDialog.close();
                UI.getCurrent().refreshCurrentRoute(false);
            }
        }

        if (selectedComponentId.equals(TActions.UPDATE_FORM) && hastEntity.isValid()) {
            E entity = hastEntity.getEntity();
            Response response = taService.save(entity);
            UIUtils.showNotification(response);
        }

        if (isActionSecure(TActions.SHOW_CODE)) {
//
        }

        if (isActionSecure(TActions.DELETE_ACTION)) {
            ConfirmDialog dialog = UIUtils.getDeleteDialog(taEntityClass, "ConfirmDialog.delete");
            dialog.addConfirmListener(
                    e -> {
                        Response response = taService.delete(itemId);
                        UI.getCurrent().refreshCurrentRoute(false);
                        UIUtils.showNotification(response);
                    });
            dialog.open();
        }
    }

    /**
     * return a span with the action buttons for an item. the actions are the
     * standar CRUD actions and the aditional actions added with
     * {@link #addAditionalAction(LineAwesomeIcon, String, String, ButtonVariant)}
     * method.
     * 
     * use this method if the items action muss be edited on demand, for
     * example to show or hide an action based on the item status.
     * 
     * @param entity - the entity
     * @return the span with the action
     */
    protected Span getItemActions(E entity) {
        Component[] components = ViewUtils.getItemActions(entity.id, this, areItemsEditable,
                aditionalActions.toArray(new SvgIcon[0]));

        // if an action must be removed for this item, remove it from the components
        // array
        List<Component> comps = new ArrayList<>(Arrays.asList(components));
        for (String aId : itemActionToRemove) {
            comps.removeIf(c -> c.getId().orElse("null").equals(aId));
            aditionalActions.removeIf(a -> a.getId().orElse("null").equals(aId));
        }

        components = comps.toArray(new Component[0]);
        Span span = new Span(components);

        return modifyItemActions(components, span);
    }

    /**
     * if there are more than 2 actions, this method put all the actions except the
     * first one in a popover and return a span with the first action and the
     * popover button
     * 
     * @param components - the components to show as actions
     * @param span       - the span with initials actions
     * @return - the span with the first action and the popover button
     */
    protected Span modifyItemActions(Component[] components, Span span) {
        // conunt only items actions (after edit). Table scope actions (after new) are
        // not relevant for this logic
        long actionCount = aditionalActions.stream()
                .filter(i -> i.getElement().getProperty(ViewUtils.AFTER_OF, "").equals(TActions.EDIT_ACTION)).count();

        if (actionCount > 1) {
            Button moreActions = new Button(LineAwesomeIcon.ELLIPSIS_V_SOLID.create());
            moreActions.setId(TActions.MORE_ACTIONS);
            moreActions.setTooltipText(TranslationProvider.getTranslation(TActions.MORE_ACTIONS));
            moreActions.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            Popover popover = UIUtils.getPopover(moreActions);
            popover.setPosition(PopoverPosition.START_TOP);
            popover.removeThemeVariants(PopoverVariant.LUMO_NO_PADDING);

            VerticalLayout layout = UIUtils.getCompactVerticalLayout();
            for (int i = 1; i < components.length; i++) {
                Button button = (Button) components[i];
                button.addClickListener(e -> popover.close());
                String id = button.getId().orElse("null");
                button.setText(getTranslation(id));
                button.setTooltipText(null);
                if (TActions.DELETE_ACTION.equals(id))
                    layout.add(new Hr());
                layout.add(button);
            }
            popover.add(layout);
            span = new Span(components[0], moreActions);
        }

        return span;
    }

    public Header getHeader(Class<? extends Component> clazz,
            ComponentEventListener<ClickEvent<Button>> listener) {
        header = UIUtils.getHeader(clazz);

        // Paragraph description = new
        // Paragraph(TranslationProvider.getTranslation(clsName + ".tt"));
        // description.addClassNames(Margin.Vertical.NONE);
        // header.setDetails(description);

        // Actions
        addAditionalAction(LineAwesomeIcon.CODE_SOLID, TActions.SHOW_CODE, TActions.NEW_ACTION);

        List<Component> list2 = ViewUtils.getHeaderToolBar(listener, aditionalActions.toArray(new SvgIcon[0]));
        header.setActions(list2.toArray(new Component[0]));
        this.searchField = (TextField) getToolBarComponent(ViewUtils.SEARCH_FIELD);

        return header;
    }
}
