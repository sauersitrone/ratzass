/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.PropertyChangeListener;
import com.vaadin.flow.shared.Registration;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Tag("simple-timer")
@JsModule("./misc/simple-timer.js")
public class TTimer extends Component implements HasSize, HasStyle {

  private static final long serialVersionUID = 1L;
  private static final int START_TIME_S = 60;
  private static final String DISPLAY = "display";
  private static final String INLINE = "inline";
  private static final String CURRENT_TIME = "currentTime";

  /** Creates a timer with a start time of 60 */
  public TTimer() {
    this(START_TIME_S);
  }

  /**
   * Creates a timer using the start time passed in the constructor
   *
   * @param startTime value in seconds for the start time
   */
  public TTimer(final Number startTime) {
    getElement().getStyle().set(DISPLAY, INLINE);
    setStartTime(startTime);

    // terry
    setVisible(false);
  }

  /**
   * Sets the start time
   *
   * @param startTime value in seconds for the start time
   */
  public void setStartTime(final Number startTime) {
    getElement().setProperty("startTime", startTime.doubleValue());
    getElement().setProperty(CURRENT_TIME, startTime.doubleValue());
    reset();
  }

  /**
   * Changes the behavior to count up or down Default is false for count down
   *
   * @param countUp
   */
  public void setCountUp(final boolean countUp) {
    getElement().setProperty("countUp", countUp);
    reset();
  }

  /**
   * Enables showing fractions of a second
   *
   * @param fractions
   */
  public void setFractions(final boolean fractions) {
    getElement().setProperty("fractions", fractions);
  }

  /**
   * Enables showing minutes
   *
   * @param minutes
   */
  public void setMinutes(final boolean minutes) {
    getElement().setProperty("minutes", minutes);
  }

  /**
   * Enables showing hours and minutes
   *
   * @param hours
   */
  public void setHours(final boolean hours) {
    getElement().setProperty("hours", hours);
  }

  /** Starts or stops the timer if it is already started */
  public void start() {
    getElement().callJsFunction("start");
  }

  /** Stops the timer, does nothing if already stopped */
  public void pause() {
    getElement().callJsFunction("pause");
  }

  /** Resets the current value to the start time */
  public void reset() {
    getElement().callJsFunction("ready");
  }

  /**
   * Returns the status of the timer
   *
   * @return
   */
  @Synchronize(property = "isRunning", value = "is-running-changed")
  public boolean isRunning() {
    return getElement().getProperty("isRunning", false);
  }

  /**
   * Returns the last known value of the timer. The value is updated when the
   * CurrentTimeChangeListener executes.
   *
   * @return current value in seconds
   */
  @Synchronize("is-running-changed")
  public BigDecimal getCurrentTime() {
    return BigDecimal.valueOf(getElement().getProperty(CURRENT_TIME, 0d));
  }

  /**
   * Returns the current value of the timer.
   *
   * @return a pending result that completes after retrieving the timer value.
   */
  public CompletableFuture<BigDecimal> getCurrentTimeAsync() {
    return getElement()
        .executeJs("return this.currentTime")
        .toCompletableFuture(Double.class)
        .thenApply(BigDecimal::valueOf);
  }

  /**
   * Adds a property change listener for the {@code currentTime} property
   *
   * @return current value in seconds
   */
  public Registration addCurrentTimeChangeListener(
      PropertyChangeListener listener, long time, TimeUnit timeUnit) {
    int millis = (int) Math.min(timeUnit.toMillis(time), Integer.MAX_VALUE);
    if (listener == null) {
      listener = ev -> {};
    }
    return getElement()
        .addPropertyChangeListener(CURRENT_TIME, "current-time-changed", listener)
        .throttle(millis);
  }

  /** Event that gets triggered when the timer reaches 0 */
  @DomEvent("simple-timer-end")
  public static class TimerEndedEvent extends ComponentEvent<TTimer> {

    public TimerEndedEvent(final TTimer source, final boolean fromClient) {
      super(source, fromClient);
    }
  }

  /**
   * Adds a timer ended listener that will be triggered when the timer reaches 0
   *
   * @param listener
   * @return
   */
  public Registration addTimerEndEvent(final ComponentEventListener<TimerEndedEvent> listener) {
    return addListener(TimerEndedEvent.class, listener);
  }

  @Override
  public boolean isVisible() {
    return getStyle().get(DISPLAY).equals(INLINE);
  }

  @Override
  public void setVisible(boolean visible) {
    getStyle().set(DISPLAY, visible ? INLINE : "none");
  }
}
