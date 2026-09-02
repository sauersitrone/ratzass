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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.simone.command.CombatCenter;
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Thread printingHook = new Thread(() -> System.out.println("In the middle of a shutdown"));
        Runtime.getRuntime().addShutdownHook(printingHook);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                RUtils.endStarcraftProcess();
                System.out.println("Main.Main().new WindowAdapter() {...}.windowClosed()");
                System.exit(0);
            }
        });
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        Drawer.installDrawer(this, MyDrawerBuilder.getInstance());
        FormManager.install(this);
        setSize(new Dimension(1366, 800));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        Env.init();
        RUtils.startStarcraftProcess();

        CommandQueue.init();
        UnitsCenter.init();
        LogisticCenter.init();
        CombatCenter.init();

        DemoPreferences.init();
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("ui.icons.themes");
        UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        DemoPreferences.setupLaf();
        EventQueue.invokeLater(() -> new Demo().setVisible(true));

        RBWListener.init();

    }
}
