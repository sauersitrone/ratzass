package de.simone.vaadinplus.components;

import java.util.List;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxShadow;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Position;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import de.simone.TranslationProvider;
import de.simone.UIUtils;

public class Sidebar extends Section implements   HasTheme {

    private Header header;
    private H2 title;
    private Span description;
    private Layout content;
    private Footer footer;
    private Div glassPane;

    public Sidebar() {
        this("", "");

    }

    public Sidebar(String title, Component... components) {
        this(title, null, components);
    }

    public Sidebar(String title, String description, Component... components) {
        addClassNames(Background.BASE, BoxShadow.MEDIUM, Display.FLEX, FlexDirection.COLUMN, Overflow.HIDDEN,
                Position.FIXED, "bottom-0", "top-0", "transition-all", "z-10");
        setMaxWidth(100, Unit.PERCENTAGE);

        setWidth(480, Unit.PIXELS);

        this.header = new Header();
        this.header.addClassNames(Border.BOTTOM, Display.FLEX, JustifyContent.BETWEEN,
                Padding.End.MEDIUM, Padding.Start.LARGE, Padding.Vertical.MEDIUM);
        add(this.header);

        createContent(components);

        this.footer = new Footer();
        this.footer.addClassNames(Background.CONTRAST_5, Display.FLEX, Gap.SMALL, JustifyContent.END,
                Padding.Horizontal.MEDIUM, Padding.Vertical.SMALL);
        add(this.footer);

        this.glassPane = new Div();
        glassPane.addClassName("sitrone-modal-background");
        glassPane.setVisible(false);
        add(glassPane);

        close();
    }

    public void createHeader(String title, String description) {
        this.title = new H2(title);
        this.title.addClassNames(FontSize.XLARGE);

        Layout layout = new Layout(this.title);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setGap(Layout.Gap.SMALL);

        if (description != null) {
            this.description = new Span(description);
            this.description.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
            layout.add(this.description);
        }

        Button close = new Button(LineAwesomeIcon.TIMES_SOLID.create(), e -> close());
        close.addClassNames(Margin.Vertical.NONE);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.setAriaLabel("Close sidebar");
        close.setTooltipText("Close sidebar");

        header.removeAll();
        this.header.add(layout, close);

        if (description == null) {
            this.header.addClassNames(AlignItems.CENTER);
        }
    }

    private void createContent(Component... components) {
        this.content = new Layout(components);
        this.content.addClassNames(Flex.GROW, Padding.Bottom.MEDIUM, Padding.Horizontal.LARGE, Padding.Top.SMALL);
        this.content.setFlexDirection(Layout.FlexDirection.COLUMN);
        add(this.content);
    }

    /**
     * Terry: the the glass panel with icon and text for this sidebar
     * 
     * @param visible - the visibility
     * @param textKey - the text to show
     */
    public void setGlassPaneVisible(boolean visible, String textKey) {
        glassPane.setVisible(visible);
        glassPane.setText("textKey");
        if (!visible)
            return;

        Image image = UIUtils.getImageIcon(UIUtils.SPINNER, "40Px");
        glassPane.removeAll();
        Div text = new Div(TranslationProvider.getTranslation(textKey));
        text.addClassNames(LumoUtility.TextColor.PRIMARY_CONTRAST, LumoUtility.FontSize.XLARGE,
                LumoUtility.Margin.Left.MEDIUM);
        glassPane.add(image, text);
    }

    public void setContent(List<Component> components) {
        content.removeAll();
        content.add(components);
        content.setOverflow(de.simone.vaadinplus.components.Layout.Overflow.AUTO);

    }

    public void setFooter(List<Component> components) {
        footer.removeAll();
        footer.add(components);
    }

    // TODO: Refocus the component that opened the sidebar after closing
    public void close() {
        addClassNames("-end-full");
        removeClassName("end-0");
        setEnabled(false);
    }

    public void open() {
        addClassNames("end-0");
        removeClassNames("-end-full");
        setEnabled(true);
    }

    public void addHeaderThemeName(String theme) {
        this.header.getElement().getThemeList().add(theme);
    }

    public void removeHeaderThemeName(String theme) {
        this.header.getElement().getThemeList().remove(theme);
    }

    public Layout getContent() {
        return this.content;
    }

    public Footer getFooter() {
        return this.footer;
    }

    public void setHeaderVisible(boolean visible) {
        this.header.setVisible(visible);
    }
}
