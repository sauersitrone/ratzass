package de.simone;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.FontUtils;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import de.simone.command.CommandQueue;
import de.simone.command.UnitsCenter;
import de.simone.ui.Demo;
import de.simone.ui.menu.MyDrawerBuilder;
import de.simone.ui.system.FormManager;
import de.simone.ui.utils.DemoPreferences;
import raven.modal.Drawer;
import de.simone.command.LogisticCenter;

public class Main extends JFrame {

    public static final String DEMO_VERSION = "2.6.2-SNAPSHOT";

    public Main() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        Drawer.installDrawer(this, MyDrawerBuilder.getInstance());
        FormManager.install(this);
        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // initializeEsper();
        Env.init();
        CommandQueue.init();
        UnitsCenter.init();
        LogisticCenter.init();
        RBWListener listener = new RBWListener();

        RUtils.killStarcraftProcess();
        RUtils.killChaosLauncherProcess();
        // Make sure Chaoslauncher -> Settings -> "Run Starcraft on Startup" is checked
        RUtils.startChaosLauncherProcess();

        // gui
        DemoPreferences.init();
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("ui.icons.themes");
        UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        DemoPreferences.setupLaf();
        EventQueue.invokeLater(() -> new Demo().setVisible(true));

        listener.run();
    }
}
