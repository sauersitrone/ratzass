/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend;

import java.util.Collections;
import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import de.simone.TGroupedProperty;
import de.simone.TranslationProvider;
import de.simone.UIUtils;
import io.quarkus.logging.Log;
import jakarta.servlet.http.HttpServletResponse;

@AnonymousAllowed
@Route("Hello404")
@StyleSheet("hello404Effect.css")
public class Hello404 extends HelloPage implements HasErrorParameter<NotFoundException> {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getStyle().set("background", "#00131c");

        Image image = new Image("/omg.png", "ohhh my goshhh");
        image.setHeight("550px");
        setVisualComponent(image);

        // randomly select a 404 msg
        List<TGroupedProperty> msgs = TranslationProvider.getGroupProperties("hello.404");
        Collections.shuffle(msgs);
        Span span = UIUtils.getSpan(msgs.get(0).value, false);

        setContent(span, homeAnchor);
        mainLayout.addClassName(LumoUtility.Background.BASE);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        Log.warn("404 page caught and exception: " + parameter.getCaughtException());
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
