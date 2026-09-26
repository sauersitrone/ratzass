package de.simone.ui.component;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.DefaultCaret;

import org.apache.commons.lang3.tuple.Triple;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.LoggingFacade;

import de.simone.Main;
import de.simone.RUtils;
import net.miginfocom.swing.MigLayout;
import raven.extras.SlidePane;
import raven.extras.SlidePaneTransition;

public class About extends JPanel {
    private List<Triple<String, String, String>> libs = new ArrayList<>();
    private SlidePane slidePane;
    private int currentSlideIndex;
    private Timer timer;

    public About() {
        populateLibraries();
        setLayout(new MigLayout("fillx,wrap,insets 5 30 5 30,width 500", "[fill,400::]", "[][]20[]"));

        JTextPane title = createText(Main.APP_DESCRIPTION);
        title.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +5");

        JTextPane description = createText("");
        description.setContentType("text/html");
        String text = Main.APP_LONG_DESCRIPTION + "<br>" +
                "For source code, visit the <a href='" + Main.APP_GITHUB + "'>GitHub Project.</a><br>" +
                "<p>This application can't be possible without the following libraries &amp; tools</p>";
        description.setText(text);
        description.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                showUrl(e.getURL());
            }
        });

        add(title);
        add(description);
        slidePane = new SlidePane();
        add(slidePane);
        nextSlide();

        timer = new Timer(8000, e -> nextSlide());
        timer.start();
    }

    private JTextPane createText(String text) {
        JTextPane textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createEmptyBorder());
        textPane.setText(text);
        textPane.setEditable(false);
        textPane.setCaret(new DefaultCaret() {
            @Override
            public void paint(Graphics g) {
            }
        });
        return textPane;
    }

    private void nextSlide() {
        Triple<String, String, String> triple = libs.get(currentSlideIndex++);
        JTextPane textPane = createText("");
        textPane.setEditable(false);
        textPane.setCaret(new DefaultCaret() {
            @Override
            public void paint(Graphics g) {
                //
            }
        });
        textPane.setContentType("text/html");
        String htmlText = String.format("<b>%s</b><br/>%s<br/><a href='%s'>%s</a><br/>", triple.getLeft(),
                triple.getMiddle(), triple.getRight(), triple.getRight());
        textPane.setText(htmlText);
        textPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                showUrl(e.getURL());
            }
        });

        ImageIcon icon = RUtils.getImageIcon("aboutIcons/" + triple.getLeft());
        if (icon != null) {
            icon = new ImageIcon(icon.getImage().getScaledInstance(68, 68, Image.SCALE_SMOOTH));
            JPanel iconPanel = new JPanel(new BorderLayout(10, 10));
            iconPanel.add(new JLabel(icon), BorderLayout.WEST);
            iconPanel.add(textPane, BorderLayout.CENTER);
            slidePane.addSlide(iconPanel, SlidePaneTransition.Type.FORWARD);
        } else {
            slidePane.addSlide(textPane, SlidePaneTransition.Type.FORWARD);
        }
        currentSlideIndex %= libs.size();
    }

    private void showUrl(URL url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(url.toURI());
                } catch (IOException | URISyntaxException e) {
                    LoggingFacade.INSTANCE.logSevere("Error browse url", e);
                }
            }
        }
    }

    private void populateLibraries() {
        libs.add(Triple.of("Modal-dialog", "Swing modal dialog component library for Java desktop apps.",
                "https://github.com/DJ-Raven/swing-modal-dialog"));
        libs.add(Triple.of("Flat Look and Feel",
                "FlatLaf is a modern open-source cross-platform Look and Feel for Java Swing desktop applications.",
                "https://github.com/JFormDesigner/FlatLaf"));
        libs.add(Triple.of("Commons Lang",
                "Apache utility library for strings, numbers, reflection, collections, and more.",
                "https://github.com/apache/commons-lang"));
        libs.add(Triple.of("Jenetics", "Java library for genetic algorithms and evolutionary computation.",
                "https://github.com/jenetics/jenetics"));
        libs.add(Triple.of("Commons Text", "Apache library for text processing, string manipulation, and escaping.",
                "https://github.com/apache/commons-text"));
        libs.add(Triple.of("JBWAPI", "Java wrapper for the Brood War API used in StarCraft AI bots.",
                "https://github.com/JavaBWAPI/JBWAPI"));
        libs.add(Triple.of("Libtiled", "Java binding/library for reading and working with TMX/Tiled map files.",
                "https://github.com/mapeditor/tiled"));
        libs.add(Triple.of("Lombok",
                "Java annotation processor that generates boilerplate code like getters/setters and builders.",
                "https://github.com/projectlombok/lombok"));
        libs.add(Triple.of("ENHSP", "Expressive Numeric Heuristic Planner for planning problems.",
                "https://github.com/hstairs/ENHSP"));
        libs.add(Triple.of("ASCII Table", "Java library for rendering tables in terminal/console output.",
                "https://github.com/freva/ascii-table"));
        libs.add(Triple.of("Gdx-ai", "Artificial intelligence library for libGDX game development.",
                "https://github.com/libGDX/gdx-ai"));
        libs.add(Triple.of("VScode", "Microsoft visual Studio Code.", "https://code.visualstudio.com/"));
        libs.add(Triple.of("Tablesaw", "Java dataframe and visualization library.",
                "https://github.com/jtablesaw/tablesaw"));
        libs.add(Triple.of("JDK 21", "Java Development Kit 21.", "https://jdk.java.net/21"));
        Collections.shuffle(libs);
    }

}