package de.simone;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.FlatClientProperties;

import de.simone.ui.layout.ResponsiveLayout;
import de.simone.ui.layout.ResponsiveLayout.JustifyContent;
import de.simone.ui.system.Form;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;
import raven.modal.option.Location;
import raven.modal.toast.option.ToastBorderStyle;
import raven.modal.toast.option.ToastLocation;
import raven.modal.toast.option.ToastOption;
import raven.modal.toast.option.ToastStyle;

public class UIUtils {

    public static String formatTime(int frames) {
        int seconds = frames / 24;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static JTextArea getConsoleTextArea() {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setBackground(Color.BLACK);
        jTextArea.setForeground(Color.WHITE);
        jTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        // jTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return jTextArea;
    }

    public static JCheckBox getCheckBox(String text, boolean selected, ActionListener listener) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setSelected(selected);
        checkBox.addActionListener(listener);
        return checkBox;
    }

    public static JCheckBox getPropertyCheckBox(String text, boolean selected, ActionListener listener) {
        JCheckBox checkBox = getCheckBox(text, selected, listener);
        checkBox.addChangeListener(e -> {
            Config.save();
        });
        return checkBox;
    }

    public static JSlider getSlider(int min, int max, int value, ChangeListener listener) {
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing((max - min) / 10);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.addChangeListener(listener);
        return slider;
    }

    public static JPanel getControlPanel(String title, JComponent... components) {
        ResponsiveLayout layout = new ResponsiveLayout(JustifyContent.START);
        JPanel panel = new JPanel(layout);
        for (JComponent component : components) {
            panel.add(component);
        }
        panel.setBorder(new TitledBorder(title));
        return panel;
    }

    public static void showToast(Form form, Toast.Type type, String text) {
        ToastOption option = Toast.createOption();
        Location h = Location.LEADING;
        Location v = Location.BOTTOM;
        ToastStyle.BackgroundType backgroundType = ToastStyle.BackgroundType.DEFAULT;
        ToastBorderStyle.BorderType borderType = ToastBorderStyle.BorderType.OUTLINE;
        option.setAnimationEnabled(true)
                .setPauseDelayOnHover(true)
                .setAutoClose(true)
                .setCloseOnClick(true);

        option.getLayoutOption()
                .setLocation(ToastLocation.from(h, v));
        option.getStyle().setBackgroundType(backgroundType)
                .setShowIcon(true)
                .setShowLabel(true)
                .setShowCloseButton(true)
                .getBorderStyle()
                .setBorderType(borderType)        ;

        Toast.show(form, type, text, option);
    }

    public static JPanel getInVerticalPanel(JComponent... components) {
        JPanel panel = new JPanel(new MigLayout("wrap,top", "[fill]"));
        for (JComponent component : components) {
            panel.add(component);
        }
        return panel;
    }

    public static JPanel getHeader(String titleText, String descriptionText) {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap", "[fill]"));
        JLabel title = new JLabel(titleText);
        JTextPane text = new JTextPane();
        text.setText(descriptionText);
        text.setEditable(false);
        text.setBorder(BorderFactory.createEmptyBorder());
        title.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +3");
        panel.add(title);
        panel.add(text, "width 500");
        return panel;
    }

}
