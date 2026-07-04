package de.simone.frontend.components.cropper.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;

import de.simone.frontend.components.cropper.Cropper;

@DomEvent("cropper-ready")
public class CropperReadyEvent extends ComponentEvent<Cropper> {

  public CropperReadyEvent(Cropper source, boolean fromClient) {
    super(source, fromClient);
  }
}
