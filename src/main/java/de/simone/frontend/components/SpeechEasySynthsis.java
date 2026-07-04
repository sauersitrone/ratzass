/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;

import elemental.json.JsonArray;

@Tag("speech-kit")
@JavaScript("context://speak-easy-synthesis.js")
public class SpeechEasySynthsis extends Component {

  private List<Voice> voices;

  public record Voice(String lang, String name) {}

  public SpeechEasySynthsis() {
    super();
    this.voices = new ArrayList<>();
    setId("SpeechEasySynthsis");
    loadVoices();
  }

  public List<Voice> getVoices() {
    return voices;
  }

  private void loadVoices() {
    getElement()
        .executeJs("return getVoices()")
        .then(
            e -> {
              JsonArray array = (JsonArray) e;
              for (int i = 0; i < array.length(); i++) {
                String[] langAndVoice = array.getString(i).split(":");
                voices.add(new Voice(langAndVoice[0], langAndVoice[1]));
              }
            });
  }

  public void setVoice(String voice) {
    //
  }

  public void speak(String text) {
    getElement().executeJs("speak($0)", text);
  }
}
