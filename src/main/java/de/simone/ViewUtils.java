/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import de.simone.backend.TAEntity;
import de.simone.frontend.TAForm;
import de.simone.frontend.TAView;
import de.simone.frontend.TActions;
import de.simone.vaadinplus.components.Header;
import de.simone.vaadinplus.components.Layout;
import de.simone.vaadinplus.components.Sidebar;
import de.simone.vaadinplus.utilities.Font;
import io.quarkus.logging.Log;

public class ViewUtils {
    public static final String SEARCH_FIELD = "searchField";
    public static final String AFTER_OF = "afterOf";
    public static final String BUTTON_VARIANT = "buttonVariant";
    public static final String ICON_NAME = "iconName";
    public static final String PAGINATION_LAYOUT = "paginationLayout";
    public static final String ENTITY_ID = "entityId";
    public static final String IS_EDIT_ACTION = "isEditAction";

    public static SvgIcon getAfterIcon(SvgIcon icon, String id, String after) {
        icon.setId(id);
        icon.getElement().setProperty(ViewUtils.AFTER_OF, after);
        return icon;
    }

    public static TextField getSearchField() {
        TextField search = new TextField();
        Button clear = new Button(VaadinIcon.CLOSE.create());
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        clear.addClickListener(e -> search.clear());
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setPlaceholder(search.getTranslation("search.textfield"));
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.EAGER);
        search.setId(ViewUtils.SEARCH_FIELD);
        return search;
    }

    public static List<Component> getHeaderToolBar(ComponentEventListener<ClickEvent<Button>> listener,
            SvgIcon... afterComponets) {
        List<Component> actionsComponent = new ArrayList<>();

        TextField search = getSearchField();
        search.getElement().setProperty(IS_EDIT_ACTION, false);
        actionsComponent.add(search);

        Button add = new Button(LineAwesomeIcon.PLUS_SOLID.create());
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add.setId(TActions.NEW_ACTION);
        add.setTooltipText(TranslationProvider.getTranslation(TActions.NEW_ACTION));
        add.addClickListener(listener);
        add.getElement().setProperty(IS_EDIT_ACTION, false);
        actionsComponent.add(add);

        addAfterOf(actionsComponent, TActions.NEW_ACTION, -1L, listener, afterComponets);

        return actionsComponent;
    }

    public static List<Button> getCRUDButtons(
            ComponentEventListener<ClickEvent<Button>> listener) {
        return ViewUtils.getCRUDButtons(listener, TActions.DELETE_FORM, TActions.SHOW_CODE, TActions.CANCEL_FORM,
                TActions.SAVE_FORM);
    }

    public static List<Button> getFormFooterButtons(ComponentEventListener<ClickEvent<Button>> listener,
            String... actionIds) {
        ArrayList<Button> footer = new ArrayList<>();

        for (String actionId : actionIds) {
            Button button = new Button();
            button.setText(button.getTranslation(actionId));
            button.setId(actionId);
            if (listener != null)
                button.addClickListener(listener);
            footer.add(button);
        }
        return footer;
    }

    public static List<Button> getCRUDButtons(
            ComponentEventListener<ClickEvent<Button>> listener, String... actions) {
        List<Button> footer = getFormFooterButtons(listener, actions);

        Optional<Button> optional = footer.stream().filter(b -> b.getId().orElse("null").equals(TActions.DELETE_FORM))
                .findAny();
        if (optional.isPresent()) {
            Button delete = optional.get();
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.getStyle().set("margin-inline-end", "auto");
        }

        optional = footer.stream().filter(b -> b.getId().orElse("null").equals(TActions.SAVE_FORM))
                .findAny();
        if (optional.isPresent()) {
            Button save = optional.get();
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }

        optional = footer.stream().filter(b -> b.getId().orElse("null").equals(TActions.OK_FORM))
                .findAny();
        if (optional.isPresent()) {
            Button save = optional.get();
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }

        return footer;
    }

    /**
     * return an array of aplicable actions for individual items. the standar list
     * return only edit and delete standar actions.
     * 
     * @param id             - the item id
     * @param listener       - the instance of TAView who process the action
     * @param afterComponets - list of icons to add to the list
     * @return the actions
     */
    public static Component[] getItemActions(Long id, TAView<?> listener, boolean isItemEditable,
            SvgIcon... afterComponets) {
        List<Component> components = new ArrayList<>();

        Button watch = new Button(LineAwesomeIcon.SEARCH_SOLID.create());
        watch.setId(TActions.WATCH_ACTION);
        watch.getElement().setProperty(ENTITY_ID, "" + id);
        watch.setTooltipText(TranslationProvider.getTranslation(TActions.WATCH_ACTION));
        watch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        watch.addClickListener(listener);
        if (!isItemEditable) {
            components.add(watch);
            return components.toArray(new Component[0]);
        }

        Button edit = new Button(LineAwesomeIcon.PEN_SOLID.create());
        edit.setId(TActions.EDIT_ACTION);
        edit.getElement().setProperty(ENTITY_ID, "" + id);
        edit.setTooltipText(TranslationProvider.getTranslation(TActions.EDIT_ACTION));
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        edit.addClickListener(listener);
        components.add(edit);

        // component marked as afterOF = EDIT_ACTION
        addAfterOf(components, TActions.EDIT_ACTION, id, listener, afterComponets);

        Button delete = new Button(LineAwesomeIcon.TRASH_ALT.create());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        delete.setId(TActions.DELETE_ACTION);
        delete.getElement().setProperty(ENTITY_ID, "" + id);
        delete.setTooltipText(TranslationProvider.getTranslation(TActions.DELETE_ACTION));
        delete.getElement().setProperty(IS_EDIT_ACTION, true);
        delete.addClickListener(listener);
        components.add(delete);

        // component marked as afterOF = DELETE_ACTION
        addAfterOf(components, TActions.DELETE_ACTION, id, listener, afterComponets);

        return components.toArray(new Component[0]);
    }

    private static void addAfterOf(List<Component> components, String afterOf, Long id,
            ComponentEventListener<ClickEvent<Button>> listener,
            SvgIcon... afterComponets) {
        for (SvgIcon icon : afterComponets) {

            if (icon.getElement().getProperty(AFTER_OF, "").equals(afterOf)) {
                String iName = icon.getElement().getProperty(ViewUtils.ICON_NAME);
                SvgIcon icon2 = new SvgIcon(iName);
                Button button = new Button(icon2, listener);
                // temp: add the ternariy manualy
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY,
                        ButtonVariant.valueOf(icon.getElement().getProperty(ViewUtils.BUTTON_VARIANT)));
                String ids = icon.getId().orElse("null");
                button.setId(ids);
                button.getElement().setProperty(ENTITY_ID, "" + id);
                button.setTooltipText(TranslationProvider.getTranslation(ids));
                components.add(button);
            }
        }
    }

    public static Header getHeader(Class<? extends Component> clazz) {
        String clsName = UIUtils.getSimpleClassName(clazz);
        Header header = new Header(TranslationProvider.getTranslation(clsName));
        header.setGap(Layout.Gap.MEDIUM);
        header.setHeadingFontSize(Font.Size.XXLARGE);
        header.removeClassName(Border.BOTTOM);

        Paragraph description = new Paragraph(TranslationProvider.getTranslation(clsName + ".tt"));
        description.addClassNames(Margin.Vertical.NONE);
        header.setDetails(description);

        return header;
    }

    public static Header getFromHeader(Class<? extends Component> clazz, List<Button> actions) {
        Header header = getHeader(clazz);
        header.setActions(actions.toArray(new Component[0]));
        return header;
    }

    public static Header getViewHeader(Class<? extends Component> clazz, TAView<?> listener) {
        Header header = getHeader(clazz);

        // Actions
        List<Component> components = getHeaderToolBar(listener);
        header.setActions(components.toArray(new Component[0]));

        return header;
    }


    public static record AditionalAction(
            String actionName, String collectionName, String iconName, boolean isEnabled) {
    }

    public static record RecordActions(MenuBar menuBar, List<Component> components) {
    }

    /**
     * show a form that is not direct related with new/edit. usefull for aditionals
     * operations like chage password in a user admin environtment
     * 
     * @param taForm the form to show
     */
    public static void showShortCustomForm(TAForm<?> taForm, Sidebar sidebar) {
        String titleText = TranslationProvider.getTranslation(UIUtils.getSimpleClassName(taForm));
        String description = TranslationProvider.getTranslation(UIUtils.getSimpleClassName(taForm) + ".tt");
        taForm.sidebar = sidebar;

        // silent hide description if not found
        if (description.startsWith("!{"))
            description = null;

        sidebar.setWidth(400, Unit.PIXELS);
        sidebar.createHeader(titleText, description);
        sidebar.setContent(taForm.getBodyComponents());
        sidebar.setFooter(taForm.getFooterComponents());
        sidebar.open();
    }

    public static void showMediumCustomForm(TAForm<?> taForm, Sidebar sidebar) {
        showShortCustomForm(taForm, sidebar);
        sidebar.setWidth(580, Unit.PIXELS);
    }

    public static <E extends TAEntity> TAForm<E> showForm(Class<? extends TAForm<E>> formClass, Sidebar sidebar,
            E entity) {
        try {
            TAForm<E> taForm = formClass.getDeclaredConstructor().newInstance();
            taForm.sidebar = sidebar;
            taForm.setEntity(entity);

            String entityName = TranslationProvider.getTranslation(entity.getClass().getSimpleName());

            String title = entity.isNewEntity()
                    ? TranslationProvider.getTranslation("Sidebar.title.new", entityName)
                    : TranslationProvider.getTranslation("Sidebar.title.edit", entityName);

            sidebar.setWidth(580, Unit.PIXELS);
            sidebar.createHeader(title, null);
            sidebar.setContent(taForm.getBodyComponents());
            sidebar.setFooter(taForm.getFooterComponents());
            sidebar.open();
            return taForm;
        } catch (Exception e) {
            Log.error("", e);
        }
        return null;
    }

    public static void scrollToComponent(Component component) {
        UI.getCurrent().getPage().executeJs("arguments[0].scrollIntoView({behavior:'smooth'});", component);
    }

    public static boolean isMobile() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isIPhone();
    }
}
