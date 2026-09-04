package de.simone.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.simone.ui.system.Form;

public class LogView extends Form {

    private JTextArea jTextArea;

    public LogView() {
        setLayout(new BorderLayout());

        JPanel header = UIUtils.getHeader("Logging Panel", "Displays log messages in real-time.");

        this.jTextArea = UIUtils.getConsoleTextArea();

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(jTextArea), BorderLayout.CENTER);

        attachLogbackAppender();
    }

    private void attachLogbackAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n");
        encoder.start();

        AppenderBase<ILoggingEvent> appender = new AppenderBase<>() {
            @Override
            protected void append(ILoggingEvent event) {
                String line = new String(encoder.encode(event));
                SwingUtilities.invokeLater(() -> jTextArea.append(line));
            }
        };
        appender.setContext(context);
        appender.start();

        context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).addAppender(appender);
    }

}
