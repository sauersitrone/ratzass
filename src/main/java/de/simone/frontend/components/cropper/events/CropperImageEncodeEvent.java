package de.simone.frontend.components.cropper.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DebounceSettings;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.dom.DebouncePhase;

import de.simone.frontend.components.cropper.Cropper;

@DomEvent(
    value = "cropper-image-encode",
    debounce = @DebounceSettings(timeout = 250, phases = DebouncePhase.TRAILING))
public class CropperImageEncodeEvent extends ComponentEvent<Cropper> {

  private String imageUri;
  private String mimeType;
  private double encoderQuality;
  private CropData cropData;

  public CropperImageEncodeEvent(
      Cropper source,
      boolean fromClient,
      @EventData("event.detail.x") int x,
      @EventData("event.detail.y") int y,
      @EventData("event.detail.width") int width,
      @EventData("event.detail.height") int height,
      @EventData("event.detail.rotate") int rotate,
      @EventData("event.detail.scaleX") int scaleX,
      @EventData("event.detail.scaleY") int scaleY,
      @EventData("event.detail.imageUri") String imageUri,
      @EventData("event.detail.mimeType") String mimeType,
      @EventData("event.detail.encoderQuality") double encoderQuality) {
    super(source, fromClient);

    this.imageUri = imageUri;
    this.mimeType = mimeType;
    this.encoderQuality = encoderQuality;
    this.cropData = new CropData(x, y, width, height, rotate, scaleX, scaleY);
  }

  public CropData getCropData() {
    return cropData;
  }

  /**
   * Returns the encoded image uri.
   *
   * @return {@link String}
   */
  public String getImageUri() {
    return this.imageUri;
  }

  /**
   * Returns the MIME-Type the image has been encoded with.
   *
   * @return {@link String}
   */
  public String getMimeType() {
    return this.mimeType;
  }

  /**
   * Returns the encoder quality the image has been encoded with.
   *
   * @return double
   */
  public double getEncoderQuality() {
    return this.encoderQuality;
  }

  public static class CropData {
    public int x = Integer.MAX_VALUE; // use x=Integer.MAX_VALUE as a marker for pojo initialization
    public int y;
    public int width;
    public int height;
    public int rotate;
    public int scaleX;
    public int scaleY;
    public boolean roundBox;

    public CropData() {
      //
    }

    public CropData(int x, int y, int width, int height, int rotate, int scaleX, int scaleY) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.rotate = rotate;
      this.scaleX = scaleX;
      this.scaleY = scaleY;
    }
  }
}
