package de.simone.vaadinplus.components;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.IconSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import de.simone.TranslationProvider;
import de.simone.UIUtils;
import de.simone.frontend.TActions;
import de.simone.vaadinplus.utilities.HeadingLevel;

public class EmptyState extends Layout {

    private Component icon;
    private Component heading;
    private Paragraph description;
    private Button button;

    // TERRY
    public EmptyState() {
        this("", "", "", null);
    }

    // TERRY
    public EmptyState(Class<? extends Component> clazz) {
        this(TranslationProvider.getTranslation(UIUtils.getSimpleClassName(clazz)),
                TranslationProvider.getTranslation(UIUtils.getSimpleClassName(clazz) + ".tt"), "", null);

        String icon = TranslationProvider.getTranslation(UIUtils.getSimpleClassName(clazz) + ".icon");
        SvgIcon icon2 = new SvgIcon("line-awesome/svg/" + icon + ".svg");
        setIcon(icon2);
        this.button.setVisible(false);
    }

    public EmptyState(String title, String description, String cta,
            ComponentEventListener<ClickEvent<Button>> listener) {
        this(title, HeadingLevel.H2, description, cta, listener);
    }

    public EmptyState(String title, HeadingLevel level, String description, String cta,
            ComponentEventListener<ClickEvent<Button>> listener) {
        addClassNames(Padding.XLARGE);
        setAlignItems(AlignItems.CENTER);
        setFlexDirection(FlexDirection.COLUMN);
        setJustifyContent(JustifyContent.CENTER);

        setHeading(title, level);

        this.description = new Paragraph(description);
        this.description.addClassNames(FontSize.SMALL, TextColor.SECONDARY, Margin.Bottom.NONE);

        this.button = new Button(cta, LineAwesomeIcon.PLUS_SOLID.create());
        this.button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.button.addClassName(Margin.Top.LARGE);

        // Terry: add the listener and set the id for the new button
        if (listener != null)
            button.addClickListener(listener);

        button.setId(TActions.NEW_ACTION);

        add(this.heading, this.description, this.button);
    }

    public void setHeading(String title, HeadingLevel level) {
        Component heading = level.getComponent(title);
        if (this.heading != null) {
            replace(this.heading, heading);
        }
        this.heading = heading;
        this.heading.addClassNames(FontSize.LARGE);
    }

    public void setIcon(Component icon) {
        if (this.icon != null) {
            replace(this.icon, icon);
        } else {
            icon.addClassNames(IconSize.LARGE, Margin.Bottom.MEDIUM, TextColor.SECONDARY);
            addComponentAsFirst(icon);
        }
        this.icon = icon;
    }

    // Terry
    public void setDescription(String description) {
        this.description.setText(description);
    }

    // Terry
    public Button getAction() {
        return button;
    }
}
