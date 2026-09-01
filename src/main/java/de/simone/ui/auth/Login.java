package de.simone.ui.auth;

import com.formdev.flatlaf.FlatClientProperties;

import de.simone.ui.component.LabelButton;
import de.simone.ui.menu.MyDrawerBuilder;
import de.simone.ui.model.ModelUser;
import de.simone.ui.system.Form;
import de.simone.ui.system.FormManager;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

import javax.swing.*;

public class Login extends Form {

    Image bgImage;

    public Login() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al right center"));
        bgImage = new ImageIcon(getClass().getResource("/ui/images/starcraft_2.jpg")).getImage();
        createLogin();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void createLogin() {
        JPanel panelLogin = new JPanel(new MigLayout());

        JPanel loginContent = new JPanel(new MigLayout("fillx,wrap,insets 35 35 25 35", "[fill,100]"));

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel lbDescription = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +12;");

        loginContent.add(lbTitle);
        loginContent.add(lbDescription);

        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JCheckBox chRememberMe = new JCheckBox("Remember Me");
        JButton cmdLogin = new JButton("Login") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };

        // style
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        panelLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]border:5,5,5,5,shade($Panel.background,10%),,0;" +
                "[dark]border:5,5,5,5,tint($Panel.background,5%),,0;" +
                "[light]background:shade($Panel.background,3%);" +
                "[dark]background:tint($Panel.background,2%);");

        loginContent.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");

        txtUsername.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;" +
                "showRevealButton:true;");

        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:4,10,4,10;" +
                "arc:12;");

        loginContent.add(new JLabel("Username"), "gapy 25");
        loginContent.add(txtUsername);

        loginContent.add(new JLabel("Password"), "gapy 10");
        loginContent.add(txtPassword);
        loginContent.add(chRememberMe, "grow 0");
        loginContent.add(cmdLogin, "gapy 20");
        loginContent.add(createInfo());

        panelLogin.add(loginContent);
        add(panelLogin);

        // event
        cmdLogin.addActionListener(e -> {
            String userName = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());
            ModelUser user = getUser(userName, password);
            MyDrawerBuilder.getInstance().setUser(user);
            FormManager.login();
        });
    }

    private JPanel createInfo() {
        JPanel panelInfo = new JPanel(new MigLayout("wrap,al center", "[center]"));
        panelInfo.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");

        panelInfo.add(new JLabel("Don't remember your account details?"));
        panelInfo.add(new JLabel("Contact us at"), "split 2");
        LabelButton lbLink = new LabelButton("help@info.com");

        panelInfo.add(lbLink);

        // event
        lbLink.addOnClick(() -> {

        });
        return panelInfo;
    }

    private ModelUser getUser(String user, String password) {

        // just testing.
        // input any user and password is admin by default
        // user='staff' password='123' if we want to test validation menu for role staff

        if (user.equals("staff") && password.equals("123")) {
            return new ModelUser("Justin White", "justinwhite@gmail.com", ModelUser.Role.STAFF);
        }
        return new ModelUser("Ratzass", "StarCraft II boot", ModelUser.Role.ADMIN);
    }
}
