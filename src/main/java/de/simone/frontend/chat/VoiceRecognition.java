/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.chat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;    
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.shared.Registration;

/**
 * VoiceRecognition is a Vaadin wrapper component for the Web Speech API's
 * SpeechRecognition feature.
 * It provides voice-to-text functionality with optional internal control
 * buttons for starting,
 * stopping, and clearing the recorded speech.
 */
@Tag("voice-recognition")
@JsModule("./misc/voice-recognition.js")
public class VoiceRecognition extends Component {

    public VoiceRecognition() {
        // Used for Vaadin to create the element
    }

    /**
     * Sets whether the recognition should continue listening even if the user
     * pauses while speaking.
     *
     * @param continuous A boolean indicating whether recognition should be
     *                   continuous.
     */
    public void setContinuous(boolean continuous) {
        getElement().setProperty("continuous", continuous);
    }

    /**
     * Returns whether the recognition is continuous.
     *
     * @return A boolean indicating whether recognition is continuous.
     */
    public boolean isContinuous() {
        return getElement().getProperty("continuous", true);
    }

    /**
     * Gets the transcribed speech text.
     *
     * @return A string containing the transcribed speech.
     */
    @Synchronize("speech")
    public String getSpeech() {
        return getElement().getProperty("speech", "");
    }

    /**
     * Sets whether to use the internal control buttons for starting, stopping, and
     * clearing speech.
     *
     * @param useInternalButtons A boolean indicating whether to use internal
     *                           control buttons.
     */
    public void setUseInternalButtons(boolean useInternalButtons) {
        getElement().setProperty("useInternalButtons", useInternalButtons);
    }

    /**
     * Returns whether the component is using the internal control buttons.
     *
     * @return A boolean indicating whether the component is using internal control
     *         buttons.
     */
    @Synchronize("useInternalButtons")
    public boolean isUseInternalButtons() {
        return getElement().getProperty("useInternalButtons", true);
    }

    /**
     * Starts the speech recognition process.
     */
    public void start() {
        getElement().callJsFunction("start");
    }

    /**
     * Stops the speech recognition process.
     */
    public void stop() {
        getElement().callJsFunction("stop");
    }

    /**
     * Aborts the speech recognition process.
     */
    public void abort() {
        getElement().callJsFunction("abort");
    }

    /**
     * Adds a listener for the start event.
     *
     * @param listener The listener to be added.
     * @return A registration for the listener.
     */
    public Registration addStartListener(ComponentEventListener<StartEvent> listener) {
        return addListener(StartEvent.class, listener);
    }

    /**
     * Adds a listener for the error event.
     *
     * @param listener The listener to be added.
     * @return A registration for the listener.
     */
    public Registration addErrorListener(ComponentEventListener<ErrorEvent> listener) {
        return addListener(ErrorEvent.class, listener);
    }

    /**
     * Adds a listener for the end event.
     *
     * @param listener The listener to be added.
     * @return A registration for the listener.
     */
    public Registration addEndListener(ComponentEventListener<EndEvent> listener) {
        return addListener(EndEvent.class, listener);
    }

    /**
     * Adds a listener for the result event, which is triggered when speech is
     * transcribed.
     * 
     * @param listener The listener to be added.
     * @return A registration for the listener.
     */
    public Registration addResultListener(ComponentEventListener<ResultEvent> listener) {
        return getElement().addEventListener("speechResult", componentEvent -> {
            String speech = componentEvent.getEventData().getString("event.detail.speech");
            listener.onComponentEvent(new ResultEvent(this, speech));
        }).addEventData("event.detail.speech");
    }

    /**
     * StartEvent is triggered when the speech recognition process starts.
     */
    public static class StartEvent extends ComponentEvent<VoiceRecognition> {
        public StartEvent(VoiceRecognition source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    /**
     * ErrorEvent is triggered when an error occurs during the speech recognition
     * process.
     */
    public static class ErrorEvent extends ComponentEvent<VoiceRecognition> {
        public ErrorEvent(VoiceRecognition source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    /**
     * EndEvent is triggered when the speech recognition process ends.
     */
    public static class EndEvent extends ComponentEvent<VoiceRecognition> {
        public EndEvent(VoiceRecognition source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    /**
     * ResultEvent is triggered when speech is transcribed and contains the
     * transcribed text.
     */
    public static class ResultEvent extends ComponentEvent<VoiceRecognition> {
        private String speech;

        public ResultEvent(VoiceRecognition source, String speech) {
            super(source, true);
            this.speech = speech;
        }

        /**
         * Gets the transcribed speech text from the event.
         *
         * @return A string containing the transcribed speech.
         */
        public String getSpeech() {
            return speech;
        }
    }
}