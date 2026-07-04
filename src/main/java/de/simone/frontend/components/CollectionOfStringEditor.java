/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */

package de.simone.frontend.components;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.customfield.CustomFieldVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.simone.UIUtils;

public class CollectionOfStringEditor extends CustomField<Set<String>> {

    private Grid<String> grid;
    private TextField inputComponent;
    private Button addButton;
    private Set<String> set;
    private Span hint;
    private int setLimit = 3;

    public CollectionOfStringEditor(String fieldName, boolean withHelper) {

        this.set = new HashSet<>();
        this.addButton = new Button(VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_ICON);

        addButton.addClickListener(ev -> addToList());

        setLabel(getTranslation(fieldName));
        if (withHelper) {
            setHelperComponent(UIUtils.getHelperComponent(fieldName));
            addThemeVariants(CustomFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        }

        this.inputComponent = new TextField();
        inputComponent.setWidthFull();

        grid = new Grid<>(String.class, false);
        grid.addColumn(String::toString)
                .setHeader(getTranslation(fieldName))
                .setWidth("80%");
                // .setAutoWidth(true);

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, value) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
                    button.addClickListener(e -> this.removeFromList(value));
                    button.setIcon(VaadinIcon.TRASH.create());
                })).setTextAlign(ColumnTextAlign.END);

        grid.setWidthFull();
        grid.setMinHeight("20px");
        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);

        hint = new Span("0 " + getLabel());
        hint.addClassNames(LumoUtility.TextColor.SECONDARY);

        refreshGrid();
        HorizontalLayout horizontalL = UIUtils.getCompactHorizontalLayout(inputComponent, addButton);
        horizontalL.setSpacing(true);
        VerticalLayout verticalL = UIUtils.getCompactVerticalLayout(horizontalL, grid, hint);
        add(verticalL);

    }

    public void setLimit(int limit) {
        this.setLimit = limit;
    }

    @Override
    protected Set<String> generateModelValue() {
        return set;
    }

    @Override
    protected void setPresentationValue(Set<String> newPresentationValue) {
        this.set = newPresentationValue;
        grid.setItems(set);
        refreshGrid();
    }

    private void removeFromList(String value) {
        set.remove(value);
        refreshGrid();
    }

    private void addToList() {
        setInvalid(false);
        if (set.size() > setLimit) {
            setErrorMessage(getTranslation("CollectionEditor.limit", setLimit));
            setInvalid(true);
            return;
        }

        String value = inputComponent.getValue();
        if (value == null || set.contains(value))
            return;

        set.add(value);
        refreshGrid();
    }

    private void refreshGrid() {
        if (!set.isEmpty()) {
            grid.setVisible(true);
            hint.setVisible(false);
            grid.getDataProvider().refreshAll();
        } else {
            grid.setVisible(false);
            hint.setVisible(true);
        }
    }
}
