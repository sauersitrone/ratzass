package de.simone.ui.menu;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import de.simone.command.LogisticCenter;
import de.simone.gui.BehaviorTreeView;
import de.simone.gui.CommandQueueView;
import de.simone.gui.EnvView;
import de.simone.gui.LogView;
import de.simone.gui.LogisticCenterView;
import de.simone.gui.StarCraftMapView;
import de.simone.ui.Demo;
import de.simone.ui.forms.*;
import de.simone.ui.model.ModelUser;
import de.simone.ui.system.AllForms;
import de.simone.ui.system.Form;
import de.simone.ui.system.FormManager;
import raven.extras.AvatarIcon;
import raven.modal.drawer.DrawerPanel;
import raven.modal.drawer.item.Item;
import raven.modal.drawer.item.MenuItem;
import raven.modal.drawer.menu.MenuOption;
import raven.modal.drawer.menu.MenuStyle;
import raven.modal.drawer.renderer.DrawerStraightDotLineStyle;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.footer.LightDarkButtonFooter;
import raven.modal.drawer.simple.footer.SimpleFooterData;
import raven.modal.drawer.simple.header.SimpleHeader;
import raven.modal.drawer.simple.header.SimpleHeaderData;
import raven.modal.option.Option;
import raven.modal.utils.FlatLafStyleUtils;

import javax.swing.*;
import java.util.Arrays;

public class MyDrawerBuilder extends SimpleDrawerBuilder {

    private static MyDrawerBuilder instance;
    private ModelUser user;

    public static MyDrawerBuilder getInstance() {
        if (instance == null) {
            instance = new MyDrawerBuilder();
        }
        return instance;
    }

    public ModelUser getUser() {
        return user;
    }

    public void setUser(ModelUser user) {
        boolean updateMenuItem = this.user == null || this.user.getRole() != user.getRole();

        this.user = user;

        // set user to menu validation
        MyMenuValidation.setUser(user);

        // setup drawer header
        SimpleHeader header = (SimpleHeader) getHeader();
        SimpleHeaderData data = header.getSimpleHeaderData();
        AvatarIcon icon = (AvatarIcon) data.getIcon();
        // String iconName = user.getRole() == ModelUser.Role.ADMIN ? "avatar_male.svg" : "avatar_female.svg";

        // icon.setIcon(new FlatSVGIcon("ui/drawer/image/" + iconName, 100, 100));
        icon.setIcon(new ImageIcon(getClass().getResource("/ui/drawer/image/avatar.jpg")));
        data.setTitle(user.getUserName());
        data.setDescription(user.getMail());
        header.setSimpleHeaderData(data);

        if (updateMenuItem) {
            rebuildMenu();
        }
    }

    private MyDrawerBuilder() {
        super(createSimpleMenuOption());
        LightDarkButtonFooter lightDarkButtonFooter = (LightDarkButtonFooter) getFooter();
        lightDarkButtonFooter.addModeChangeListener(isDarkMode -> {
            // event for light dark mode changed
        });
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        AvatarIcon icon = new AvatarIcon(new FlatSVGIcon("ui/drawer/image/avatar_male.svg", 100, 100), 50, 50, 3.5f);
        icon.setType(AvatarIcon.Type.MASK_SQUIRCLE);
        icon.setBorder(2, 2);

        changeAvatarIconBorderColor(icon);

        UIManager.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("lookAndFeel")) {
                changeAvatarIconBorderColor(icon);
            }
        });

        return new SimpleHeaderData()
                .setIcon(icon)
                .setTitle("Ratzass")
                .setDescription("StarCraft II boot");
    }

    private void changeAvatarIconBorderColor(AvatarIcon icon) {
        icon.setBorderColor(new AvatarIcon.BorderColor(UIManager.getColor("Component.accentColor"), 0.7f));
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("Swing Modal Dialog")
                .setDescription("Version " + Demo.DEMO_VERSION);
    }

    @Override
    public Option createOption() {
        Option option = super.createOption();
        option.setOpacity(0.3f);
        return option;
    }

    public static MenuOption createSimpleMenuOption() {
        // create simple menu option
        MenuOption simpleMenuOption = new MenuOption();

        MenuItem[] items = new MenuItem[] {
                new Item.Label("Ratzass"),
                new Item("Logistic center", "dashboard.svg", LogisticCenterView.class),
                new Item("Environment", "dashboard.svg", EnvView.class),
                new Item("Behavior Tree", "BehaviorTree2.svg", BehaviorTreeView.class),
                new Item("StarCraft Map", "dashboard.svg", StarCraftMapView.class),
                new Item("Log View", "dashboard.svg", LogView.class),
                new Item("Command Queue", "dashboard.svg", CommandQueueView.class),

                new Item.Label("MAIN"),
                new Item("Dashboard", "dashboard.svg", FormDashboard.class),
                new Item.Label("SWING UI"),
                new Item("Forms", "forms.svg")
                        .subMenu("Input", FormInput.class)
                        .subMenu("Table", FormTable.class)
                        .subMenu("Responsive Layout", FormResponsiveLayout.class),
                new Item("Components", "components.svg")
                        .subMenu("Modal", FormModal.class)
                        .subMenu("Toast", FormToast.class)
                        .subMenu("Date Time", FormDateTime.class)
                        .subMenu("Color Picker", FormColorPicker.class)
                        .subMenu("Avatar Icon", FormAvatarIcon.class)
                        .subMenu("Slide Pane", FormSlidePane.class),
                new Item("Swing Pack", "pack.svg")
                        .subMenu("Pagination", FormPagination.class)
                        .subMenu("MultiSelect", FormMultiSelect.class),
                new Item("Chat", "chat.svg"),
                new Item("Calendar", "calendar.svg"),
                new Item("Setting", "setting.svg", FormSetting.class),
                new Item("About", "about.svg"),
                new Item("Logout", "logout.svg")
        };

        simpleMenuOption.setMenuStyle(new MenuStyle() {

            @Override
            public void styleMenuItem(JButton menu, int[] index, boolean isMainItem) {
                boolean isTopLevel = index.length == 1;
                if (isTopLevel) {
                    // adjust item menu at the top level because it's contain icon
                    menu.putClientProperty(FlatClientProperties.STYLE, "" +
                            "margin:-1,0,-1,0;");
                }
            }

            @Override
            public void styleMenu(JComponent component) {
                component.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
            }
        });

        simpleMenuOption.getMenuStyle().setDrawerLineStyleRenderer(new DrawerStraightDotLineStyle());
        simpleMenuOption.setMenuValidation(new MyMenuValidation());

        simpleMenuOption.addMenuEvent((action, index) -> {
            System.out.println("Drawer menu selected " + Arrays.toString(index));
            Class<?> itemClass = action.getItem().getItemClass();
            if ("About".equals(action.getItem().getName())) {
                action.consume();
                FormManager.showAbout();
                return;
            } else if ("Logout".equals(action.getItem().getName())) {
                action.consume();
                FormManager.logout();
                return;
            }
            if (itemClass == null || !Form.class.isAssignableFrom(itemClass)) {
                action.consume();
                return;
            }
            Class<? extends Form> formClass = (Class<? extends Form>) itemClass;
            FormManager.showForm(AllForms.getForm(formClass));
        });

        simpleMenuOption.setMenus(items)
                .setBaseIconPath("ui/drawer/icon")
                .setIconScale(0.45f);

        return simpleMenuOption;
    }

    @Override
    public int getOpenDrawerAt() {
        return 1000;
    }

    @Override
    public boolean openDrawerAtScale() {
        return false;
    }

    @Override
    public void build(DrawerPanel drawerPanel) {
        drawerPanel.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
        FlatLafStyleUtils.appendStyle(drawerPanel, "border:0,0,0,1,$Separator.foreground;");
    }

    private static String getDrawerBackgroundStyle() {
        return "background:$Menu.background;";
    }
}
