package de.simone.gui;

import java.awt.FlowLayout;
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

import net.miginfocom.swing.MigLayout;

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
        jTextArea.setBackground(java.awt.Color.BLACK);
        jTextArea.setForeground(java.awt.Color.WHITE);
        jTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        return jTextArea;
    }

    public static JCheckBox getCheckBox(String text, boolean selected, ActionListener listener) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setSelected(selected);
        checkBox.addActionListener(listener);
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

    public static JPanel getControlPanel( String title, JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JComponent component : components) {
            panel.add(component);
        }
        panel.setBorder(new TitledBorder(title));
        return panel;
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
