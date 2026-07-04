/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;

import io.quarkus.logging.Log;

@NpmPackage(value = "@google/model-viewer", version = "v4.1.0")
@JsModule("@google/model-viewer/dist/model-viewer.min.js")
@Tag("model-viewer")
public class TamagotchiViewer extends Component implements HasSize {

    SpeechEasySynthsis speech;

    TTimer timer;
    Registration timerRegistration;

    public TamagotchiViewer(String src) {
        // set attributes of the model viewer
        setId("change-speed-demo");
        getElement().setAttribute("src", src);
        getElement().setAttribute("alt", "3D Modell");
        getElement().setAttribute("auto-rotate", false);
        getElement().setAttribute("camera-controls", false);
        getElement().setAttribute("style", "width: 50%; height: 50%");
        getElement().setAttribute("autoplay", true);
        getElement().setAttribute("shadow-intensity", "1");

        timer = new TTimer(2);
        speech = new SpeechEasySynthsis();
    }

    public void animate(String animation) {
        getElement().setAttribute("animation-name", animation);
        Log.info("Animate " + animation);
    }

    public void respond(String text, String animation) {
        animate(animation);
        speech.speak(text);

        if (timerRegistration != null)
            timerRegistration.remove();
        timerRegistration = timer.addTimerEndEvent(
                e -> {
                    // pause
                    if (timer.isRunning())
                        timer.start();
                });

        timer.start();
    }
}
