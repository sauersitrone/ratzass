package de.simone;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

@StyleSheet("navbarHeader.css")
public class MainLayoutNavHeader extends VerticalLayout {

    public MainLayoutNavHeader() {
        UIUtils.setCompatStyle(this);
        addClassNames(LumoUtility.Background.BASE);
        // getStyle().set("background-color", "lightblue"); ;

        Image logo = UIUtils.getImage("icons/favicon.png", UIUtils.MEDIUM_IMAGE_SIZE);
        logo.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.AlignSelf.CENTER);


        H3 appName = new H3("Sauer Sitrone");
        appName.addClassNames(LumoUtility.Margin.Top.SMALL, LumoUtility.AlignSelf.CENTER, LumoUtility.TextColor.SUCCESS);

        Span text = new Span("Ein digitaler Freund, der dich liebt.");
        text.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.AlignSelf.CENTER);

        VerticalLayout textContainer = UIUtils.getCompactVerticalLayout(logo, appName, text);
        textContainer.setHeight("160px");

        Span background = new Span();
        background.setWidthFull();
        String html = FileUtils.readResourceFile("navbarHeader.html");
        background.getElement().setProperty("innerHTML", html);
        background.getStyle().set("z-index", "2").set("position", "relative");
        add(textContainer, background);
    }
}
