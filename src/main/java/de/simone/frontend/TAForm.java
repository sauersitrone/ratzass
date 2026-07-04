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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import de.simone.UIUtils;
import de.simone.ViewUtils;
import de.simone.backend.TAEntity;
import de.simone.backend.TAService;
import de.simone.vaadinplus.components.Layout;
import de.simone.vaadinplus.components.Layout.GridColumns;
import de.simone.vaadinplus.components.Sidebar;
import io.quarkus.logging.Log;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.ws.rs.core.Response;

public abstract class TAForm<E extends TAEntity> extends VerticalLayout
        implements ComponentEventListener<ClickEvent<Button>>, BeforeLeaveObserver, HastEntity<E> {

    public static final String DIALOG = "DIALOG";
    public static final String DETAIL = "DETAIL";

    private HorizontalLayout footerLayout;
    private TAEntity taEntity;
    private List<String> onSuccessTasks;
    private boolean confirmBeforeLeave;
    private boolean hasChanged = false;
    private List<Component> bodyComponents;

    protected String view = DIALOG;
    protected BeanValidationBinder<E> binder;
    protected String selectedComponentId; // work variables during onComponentEvent(ClickEvent<Button>)
    protected Class<E> taEntityClass;
    protected TAService<E> taService;

    public Sidebar sidebar;

    protected TAForm(Class<? extends TAService<E>> taServiceClass, Class<E> taEntityClass) {
        this.confirmBeforeLeave = true;

        if (taServiceClass != null)
            this.taService = CDI.current().select(taServiceClass).get();

        this.taEntityClass = taEntityClass;
        this.bodyComponents = new ArrayList<>();

        List<Button> footerComponents = ViewUtils.getCRUDButtons(this);
        footerLayout = UIUtils.getFooter();
        footerLayout.add(footerComponents.toArray(new Button[0]));

        add(footerLayout);

        setAlignItems(Alignment.STRETCH);
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setSizeFull();
    }

    protected Layout addBodyComponets(Component... childrens) {
        return addBodyComponets(null, false, Layout.GridColumns.COLUMNS_2, childrens);
    }

    protected Layout addBodyComponets(String titleId, Component... childrens) {
        return addBodyComponets(titleId, false, Layout.GridColumns.COLUMNS_2, childrens);
    }

    protected Layout addBodyComponets(String titleId, GridColumns columns, Component... childrens) {
        return addBodyComponets(titleId, false, columns, childrens);
    }

    protected Layout addBodyComponets(String titleId, boolean withHelp, GridColumns columns, Component... childrens) {
        Layout layout = UIUtils.getLayout();
        layout.setColumns(columns);
        if (titleId != null) {
            Component title = UIUtils.getH2(titleId, withHelp);
            layout.add(title);
            layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title);

            // if this component ist NOt the first on the list, add a large gap to separete
            // this titled layout fron the previous
            if (!bodyComponents.isEmpty()) {
                layout.addClassNames(Margin.Top.XLARGE);
            }
        }

        layout.add(childrens);
        bodyComponents.add(layout);
        return layout;
    }

    public void onSuccess(String... onSuccessTasks) {
        this.onSuccessTasks = Arrays.asList(onSuccessTasks);
    }

    public void setVisibleFooterLayout(boolean visible) {
        this.footerLayout.setVisible(visible);
    }

    public void setFooterComponents(List<Button> buttons) {
        footerLayout.removeAll();
        footerLayout.add(buttons.toArray(new Button[0]));
    }

    public List<Component> getBodyComponents() {
        return bodyComponents;
    }

    public List<Component> getFooterComponents() {
        List<Component> components = new ArrayList<>(footerLayout.getChildren().toList());
        if (taEntity.isNewEntity())
            components.removeIf(c -> TActions.DELETE_FORM.equals(c.getId().orElse("null")));
        footerLayout.setVisible(false);
        return components;
    }

    public Component getFooterComponent(String id) {
        Optional<Component> optional = footerLayout.getChildren().filter(c -> c.getId().orElse("null").equals(id))
                .findAny();
        if (!optional.isPresent())
            return null;
        return optional.get();
    }

    public void removeFooterComponents(String... componentsId) {
        for (String id : componentsId) {
            Component component = getFooterComponent(id);
            footerLayout.remove(component);
        }
    }

    @Override
    public boolean isValid() {
        BinderValidationStatus<?> binderStatus = binder.validate();

        if (binderStatus.hasErrors()) {
            Optional<? extends HasValue<?, ?>> firstErrorField = binderStatus.getFieldValidationErrors().stream()
                    .map(BindingValidationStatus::getField).findFirst();

            firstErrorField.ifPresent(field -> {
                Component component = (Component) field;
                ViewUtils.scrollToComponent(component);
            });
        }

        return binderStatus.isOk();
    }

    @Override
    public E getEntity() {
        E e = binder.getBean();
        return e;
    }

    @Override
    public void setEntity(E entity) {
        try {
            this.taEntity = entity;
            binder = new BeanValidationBinder<>(taEntityClass);
            binder.bindInstanceFields(this);
            binder.setBean(entity);
            binder.addValueChangeListener(
                    e -> {
                        if (e.isFromClient()) {
                            hasChanged = true;
                        }
                    });

        } catch (Exception e) {
            Log.error("", e);
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        try {
            if (isValid() && confirmBeforeLeave && hasChanged) {
                ContinueNavigationAction action = event.postpone();
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader(getTranslation("ConfirmDialog.leaveEdit.title"));
                dialog.setText(getTranslation("ConfirmDialog.leaveEdit.message"));
                dialog.setCancelButton(
                        getTranslation("ConfirmDialog.leaveEdit.leave"), e -> action.proceed());
                dialog.setConfirmButton(
                        getTranslation("ConfirmDialog.leaveEdit.stay"), e -> dialog.close());
                dialog.setCancelable(true);
                dialog.open();
            }
        } catch (Exception e) {
            Log.error("", e);
        }
    }

    protected void leaveWithoutConfirm() {
        confirmBeforeLeave = false;
        sidebar.close();
        UI.getCurrent().refreshCurrentRoute(false);
    }

    @Override
    public void onComponentEvent(ClickEvent<Button> event) {
        selectedComponentId = event.getSource().getId().orElse("null");
        confirmBeforeLeave = true;

        if (selectedComponentId.equals(TActions.SAVE_FORM) && isValid()) {
            E entity = getEntity();
            Response response = taService.save(entity);

            if (response.getStatus() < 400)
                leaveWithoutConfirm();

            if (UIUtils.showNotification(response)) {
                if (onSuccessTasks != null) {

                }
            }
        }

        if (selectedComponentId.equals(TActions.UPDATE_FORM) && isValid()) {
            E entity = getEntity();
            Response response = taService.save(entity);
            if (UIUtils.showNotification(response))
                hasChanged = false;
        }

        if (selectedComponentId.equals(TActions.CANCEL_FORM)
                || selectedComponentId.equals(TActions.OK_FORM)) {
            leaveWithoutConfirm();
        }

        if (selectedComponentId.equals(TActions.DELETE_FORM)) {
            // do nothing on delete when the entity is new
            E entity = getEntity();
            if (entity.isNewEntity())
                return;

            ConfirmDialog dialog = UIUtils.getDeleteDialog(taEntity.getClass(), "ConfirmDialog.delete");
            dialog.addConfirmListener(
                    e -> {
                        Response response = taService.delete(entity.id);
                        if (response.getStatus() < 400)
                            leaveWithoutConfirm();
                        UIUtils.showNotification(response);
                    });
            dialog.open();
        }
    }
}
