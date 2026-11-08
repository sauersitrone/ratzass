package de.simone.ui.utils;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.util.LoggingFacade;

import de.simone.ui.themes.PanelThemes;
import raven.modal.drawer.menu.MenuOption.MenuOpenMode;

public class TPreferences {
    public static final String PREFERENCES_ROOT_PATH = "/raven-flatlaf-demo";
    public static final String KEY_LAF = "laf";
    public static final String KEY_LAF_THEME = "lafTheme";
    public static final String KEY_ACCENT_COLOR = "accent";
    public static final String KEY_DRAWER_OPEN = "drawer.open";
    public static final String KEY_RECENT_SEARCH = "recentSearch";
    public static final String KEY_RECENT_SEARCH_FAVORITE = "recentSearchFavorite";
    public static final String KEY_DIVIDER_PREFIX = "divider.";
    public static final String KEY_WINDOW_X = "window.x";
    public static final String KEY_WINDOW_Y = "window.y";
    public static final String KEY_WINDOW_W = "window.width";
    public static final String KEY_WINDOW_H = "window.height";
    public static final String KEY_WINDOW_STATE = "window.state";
    public static final String RESOURCE_PREFIX = "res:";
    public static final String THEME_UI_KEY = "__RaVen.flatlaf.demo.theme";
    public static Color accentColor;

    private static Preferences state;
    
    public static void init() {
        state = Preferences.userRoot().node(PREFERENCES_ROOT_PATH);
    }

    public static Preferences getState() {
        return state;
    }

    public static MenuOpenMode getDrawerOpenMode() {
        int ordinal = state.getInt(KEY_DRAWER_OPEN, MenuOpenMode.FULL.ordinal());
        return MenuOpenMode.values()[ordinal];
    }

    public static void setDrawerOpenMode(MenuOpenMode mode) {
        state.putInt(KEY_DRAWER_OPEN, mode.ordinal());
    }


    public static void setupLaf() {
        // set look and feel
        try {
            String lafClassName = state.get(KEY_LAF, FlatDarculaLaf.class.getName());
            String rgbAccentColor = state.get(KEY_ACCENT_COLOR, null);
            if (rgbAccentColor != null) {
                accentColor = new Color(Integer.parseInt(rgbAccentColor), true);
                FlatLaf.setSystemColorGetter(name -> name.equals("accent") ? accentColor : null);
            }

            if (IntelliJTheme.ThemeLaf.class.getName().equals(lafClassName)) {
                String theme = state.get(KEY_LAF_THEME, "");
                if (theme.startsWith(RESOURCE_PREFIX)) {
                    IntelliJTheme.setup(PanelThemes.class.getResourceAsStream(
                            PanelThemes.THEMES_PACKAGE + theme.substring(RESOURCE_PREFIX.length())));
                } else {
                    FlatDarculaLaf.setup();
                }
                if (!theme.isEmpty()) {
                    UIManager.getLookAndFeelDefaults().put(THEME_UI_KEY, theme);
                }
            } else {
                UIManager.setLookAndFeel(lafClassName);
            }
        } catch (Exception e) {
            LoggingFacade.INSTANCE.logSevere(null, e);
            FlatDarculaLaf.setup();
        }
        UIManager.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("lookAndFeel")) {
                state.put(KEY_LAF, UIManager.getLookAndFeel().getClass().getName());
            }
        });
    }

    public static String[] getRecentSearch(boolean favorite) {
        String stringArr = state.get(favorite ? KEY_RECENT_SEARCH_FAVORITE : KEY_RECENT_SEARCH, null);
        if (stringArr == null || stringArr.trim().isEmpty())
            return null;
        return stringArr.trim().split(",");
    }

    public static void addRecentSearch(String value, boolean favorite) {
        String[] oldRecent = getRecentSearch(false);
        String[] oldFavorite = getRecentSearch(true);
        if (favorite) {
            if (oldRecent != null) {
                // remove from recent search
                List<String> list = new ArrayList<>(Arrays.asList(oldRecent));
                list.remove(value);
                state.put(KEY_RECENT_SEARCH, String.join(",", list));
            }
            if (oldFavorite != null) {
                List<String> list = new ArrayList<>(Arrays.asList(oldFavorite));
                list.remove(value);
                list.add(0, value);
                state.put(KEY_RECENT_SEARCH_FAVORITE, String.join(",", list));
            } else {
                state.put(KEY_RECENT_SEARCH_FAVORITE, value);
            }
        } else {
            if (oldFavorite != null) {
                List<String> list = new ArrayList<>(Arrays.asList(oldFavorite));
                if (list.contains(value)) {
                    return;
                }
            }
            if (oldRecent == null) {
                state.put(KEY_RECENT_SEARCH, value);
            } else {
                List<String> list = new ArrayList<>(Arrays.asList(oldRecent));
                list.remove(value);
                list.add(0, value);
                state.put(KEY_RECENT_SEARCH, String.join(",", list));
            }
        }
    }

    public static void removeRecentSearch(String value, boolean favorite) {
        String[] oldRecent = getRecentSearch(favorite);
        if (oldRecent != null) {
            List<String> list = new ArrayList<>(Arrays.asList(oldRecent));
            list.remove(value);
            state.put(favorite ? KEY_RECENT_SEARCH_FAVORITE : KEY_RECENT_SEARCH, String.join(",", list));
        }
    }

    public static int getDividerLocation(String key) {
        return state.getInt(KEY_DIVIDER_PREFIX + key, -1);
    }

    public static void saveDividerLocation(String key, int location) {
        state.putInt(KEY_DIVIDER_PREFIX + key, location);
    }

    /**
     * Restores size/position/state of the frame and keeps them saved while the
     * user changes them. Call before the frame is made visible.
     */
    public static void bindWindowState(JFrame frame) {
        if (state == null) {
            return;
        }
        int w = state.getInt(KEY_WINDOW_W, -1);
        int h = state.getInt(KEY_WINDOW_H, -1);
        if (w > 0 && h > 0) {
            Rectangle bounds = new Rectangle(state.getInt(KEY_WINDOW_X, 0), state.getInt(KEY_WINDOW_Y, 0), w, h);
            if (isOnScreen(bounds)) {
                frame.setBounds(bounds);
            } else {
                frame.setSize(w, h);
                frame.setLocationRelativeTo(null);
            }
        }
        // a minimized start would hide the app, so it is restored as normal
        if (state.getInt(KEY_WINDOW_STATE, Frame.NORMAL) == Frame.MAXIMIZED_BOTH) {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }

        ComponentAdapter boundsListener = new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                saveNormalBounds(frame);
            }

            @Override
            public void componentResized(ComponentEvent e) {
                saveNormalBounds(frame);
            }
        };
        frame.addComponentListener(boundsListener);
        frame.addWindowStateListener(e -> state.putInt(KEY_WINDOW_STATE, e.getNewState()));
    }

    private static void saveNormalBounds(JFrame frame) {
        // only the restored bounds are stored, not the maximized ones
        if (frame.getExtendedState() == Frame.NORMAL && frame.isShowing()) {
            Rectangle b = frame.getBounds();
            state.putInt(KEY_WINDOW_X, b.x);
            state.putInt(KEY_WINDOW_Y, b.y);
            state.putInt(KEY_WINDOW_W, b.width);
            state.putInt(KEY_WINDOW_H, b.height);
        }
    }

    /**
     * check if the given bounds are on any screen (maybe the screen is
     * disconnected)
     * 
     * @param bounds - the bounds
     * @return true if the bounds are on any screen
     */
    private static boolean isOnScreen(Rectangle bounds) {
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            if (gd.getDefaultConfiguration().getBounds().intersects(bounds)) {
                return true;
            }
        }
        return false;
    }

    public static void updateAccentColor(Color color) {
        if (color != null) {
            String rgb = color.getRGB() + "";
            state.put(KEY_ACCENT_COLOR, rgb);
        } else {
            state.remove(KEY_ACCENT_COLOR);
        }
    }
}
