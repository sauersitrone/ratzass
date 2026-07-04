/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */

package de.simone;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

@Unremovable
@ApplicationScoped
@VaadinServiceEnabled
public class TranslationProvider implements I18NProvider {

    public static final String RESOURCE_BUNDLE_NAME = "vaadinapp";

    private static final List<Locale> providedLocales = Arrays.asList(Locale.ENGLISH, Locale.GERMAN, Locale.of("es"));

    public static String[] getLanguages() {
        return new String[] { Locale.ENGLISH.getLanguage(), Locale.GERMAN.getLanguage() };
    }

    /**
     * convert the duration (PT5M) to human readable time (5 Minute)
     * 
     * @param duration
     * @return duration text
     */
    public static String getReadeableDuration(String duration) {
        String dur1 = duration.substring(2);
        String i = dur1.substring(0, dur1.length() - 1);
        String tu = dur1.substring(dur1.length() - 1);
        String humRead = i + " " + TranslationProvider.getGroupProperty("durations", tu).value;
        return humRead;
    }

    /**
     * Utility method that return true if the translation was found in the
     * properties files or false
     * if the text is null or star with prefix "!{"
     * 
     * @param text - the translatet text
     * @return true if ok
     */
    public static boolean isValidTranslation(String text) {
        if (text == null)
            return false;
        return !text.startsWith("!{");
    }

    public static Locale getLocale() {
        // if this method is invoqued by cromjob, the set the locale to default locale
        if (VaadinSession.getCurrent() == null)
            return Locale.getDefault();

        return VaadinSession.getCurrent().getLocale();
    }

    public static TGroupedProperty getGroupProperty(String group, String elementKey) {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, getLocale());
        for (String bundleKey : bundle.keySet()) {
            if (bundleKey.startsWith(group)) {
                TGroupedProperty prp = new TGroupedProperty(bundleKey, bundle.getString(bundleKey));
                if (prp.key.equals(elementKey))
                    return prp;
            }
        }
        return new TGroupedProperty();
    }

    public static String getString(String group, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, getLocale());
        for (String bundleKey : bundle.keySet()) {
            if (bundleKey.startsWith(group)) {
                TGroupedProperty prp = new TGroupedProperty(bundleKey, bundle.getString(bundleKey));
                if (prp.key.equals(key))
                    return prp.value;
            }
        }
        return "!{" + key + "}";
    }

    public static String getString(Enum<?> enume) {
        String group = enume.getClass().getSimpleName();
        String key = enume.name();
        return getString(group, key);
    }

    public static String getTranslation(String key, Object... params) {
        Locale locale = getLocale();
        String msg = "!{" + locale.getLanguage() + ": " + key + "}!";
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);
            msg = bundle.getString(key);
            msg = MessageFormat.format(msg, params);
        } catch (Exception e) {
            // Log.error(e);
        }
        return msg;
    }

    /**
     * return a by position in the group ordered list of the properties gruped by
     * group
     * 
     * @param group - the group
     * @return the ordered list
     */
    public static List<TGroupedProperty> getGroupProperties(String group) {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, getLocale());
        TreeMap<Integer, TGroupedProperty> map = new TreeMap<>();
        for (String bundleKey : bundle.keySet()) {
            if (bundleKey.startsWith(group)) {
                TGroupedProperty prp = new TGroupedProperty(bundleKey, bundle.getString(bundleKey));
                map.put(prp.order, prp);
            }
        }
        List<TGroupedProperty> list = new ArrayList<>(map.values());
        return list;
    }

    public static List<String> getKeys(String group) {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, getLocale());
        TreeMap<Integer, String> map = new TreeMap<>();
        for (String bundleKey : bundle.keySet()) {
            if (bundleKey.startsWith(group)) {
                TGroupedProperty prp = new TGroupedProperty(bundleKey, bundle.getString(bundleKey));
                map.put(prp.order, prp.key);
            }
        }
        List<String> list = new ArrayList<>(map.values());
        return list;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return providedLocales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        String msg = "!{" + locale.getLanguage() + ": " + key + "}!";
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);
            msg = bundle.getString(key);
            msg = MessageFormat.format(msg, params);
        } catch (Exception e) {
            // Log.error(e);
        }
        return msg;
    }

}
