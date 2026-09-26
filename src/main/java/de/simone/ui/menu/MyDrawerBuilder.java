package de.simone.ui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import de.simone.Main;
import de.simone.ui.forms.BehaviorTreeView;
import de.simone.ui.forms.CombatCenterView;
import de.simone.ui.forms.CommandQueueView;
import de.simone.ui.forms.FormSetting;
import de.simone.ui.forms.LogisticCenterView;
import de.simone.ui.forms.StarCraftMapView;
import de.simone.ui.forms.UnitsCenterView;
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

public class MyDrawerBuilder extends SimpleDrawerBuilder {

    private ModelUser user;
    private static MyDrawerBuilder instance;
    private static List<Form> starcraftForms = new ArrayList<>();

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
        // String iconName = user.getRole() == ModelUser.Role.ADMIN ? "avatar_male.svg"
        // : "avatar_female.svg";

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
                .setDescription("Version " + Main.APP_VERSION);
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
                new Item("Logistic center", "local_shipping.svg", LogisticCenterView.class),
                new Item("Units center", "people_size_decrease.svg", UnitsCenterView.class),
                new Item("Behavior Tree", "account_tree.svg", BehaviorTreeView.class),
                new Item("StarCraft Map", "map.svg", StarCraftMapView.class),
                new Item("Combat Center", "swords.svg", CombatCenterView.class),
                new Item("Command Queue", "record_voice_over.svg", CommandQueueView.class),
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
            // System.out.println("Drawer menu selected " + Arrays.toString(index));
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

            // is one of starCraft forms?
            Optional<Form> optional = starcraftForms.stream().filter(f -> f.getClass().equals(formClass)).findFirst();
            if (optional.isPresent()) {
                FormManager.showForm(optional.get());
            } else {
            //     // else, standar show
                FormManager.showForm(AllForms.getForm(formClass));
            }
        });

        simpleMenuOption.setMenus(items)
                .setBaseIconPath("ui/drawer/icon")
                .setIconScale(0.45f);

        // i need to instantiates all starcraft form to recive all events, if not,
        // previous event will be lost if the user dont click the form on time
        starcraftForms.add(new LogisticCenterView());
        starcraftForms.add(new UnitsCenterView());
        starcraftForms.add(new BehaviorTreeView());
        // starcraftForms.add(new StarCraftMapView());
        starcraftForms.add(new CombatCenterView());
        starcraftForms.add(new CommandQueueView());

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
