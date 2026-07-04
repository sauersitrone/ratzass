/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.frontend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import de.simone.backend.AlphanumComparator;
import de.simone.backend.TAEntity;

public class TActions {

    public record EndPoint(String name, String group) {
    }

    private record EntityViewPair(Class<? extends TAEntity> entityClass, Class<? extends TAView<?>> viewClass) {
    }

    public static final String SHOW_CODE = "action.showCode";

    public static final String ADULT_CHAT_LOG = "action.adultChatLog";
    public static final String ADULT_STATISTICS = "action.adultTriages";
    public static final String ADULT_REMINDERS = "action.adultReminders";
    public static final String ADULT_RELATIVES = "action.adultRelatives";
    public static final String ADULT_TAMAGOTCHI = "action.adultTamagotchi";

    public static final String TIMELINE_EVENTS = "action.timelineEvents";
    public static final String TIMELINE_TEST = "action.timelineTest";

    public static final String RSS_ITEMS = "action.rssItems";
    public static final String ALL_RSS_ITEMS = "action.allRssItems";
    public static final String DELETE_RSS_FEEDS = "action.deleteRssFeeds";

    public static final String LOAD_MEDIA = "action.loadMedia";

    public static final String TELEGRAM_TEST = "action.telegramTest";
    public static final String WHATSAPP_TEST = "action.whatsappTest";
    public static final String EMAIL_TEST = "action.emailTest";
    public static final String SMS_TEST = "action.smsTest";
    public static final String NEWSLETTERS = "action.newsletters";

    public static final String NEW_ACTION = "action.new";
    public static final String EXPORT_CSV_ACTION = "action.exportCsv";
    public static final String EDIT_ACTION = "action.edit";
    public static final String WATCH_ACTION = "action.watch";
    public static final String DELETE_ACTION = "action.delete";
    public static final String MORE_ACTIONS = "action.more";

    public static final String OK_FORM = "action.okForm";
    public static final String CANCEL_FORM = "action.cancelForm";
    public static final String SAVE_FORM = "action.saveForm";
    public static final String UPDATE_FORM = "action.updateForm";
    public static final String DELETE_FORM = "action.deleteForm";

    public static final String PASSWORD_RESET = "action.passwordReset";
    public static final String DEVICES_LIST = "action.devicesList";

    TActions() {
        //
    }

    public static List<EndPoint> getActions() {
        List<EndPoint> actions = new ArrayList<>();

        List<EntityViewPair> entityViewPairs = new ArrayList<>();

        for (EntityViewPair entityViewPair : entityViewPairs) {
            // add the view as an endpoint, e.g. "Books" for "BooksView"
            EndPoint endpoint = new EndPoint(entityViewPair.viewClass.getSimpleName(),
                    entityViewPair.entityClass().getSimpleName());
            actions.add(endpoint);

            // add item actions, e.g. "Book.new", "Book.edit", "Book.delete" for "Book"
            // entity
            List<EndPoint> itemActions = getCRUDActions(entityViewPair.entityClass());
            actions.addAll(itemActions);
        }

        AlphanumComparator alphanumComparator = new AlphanumComparator();
        Comparator<EndPoint> comparator = (o1, o2) -> alphanumComparator.compare(o1.name(), o2.name());
        actions = actions.stream().sorted(comparator).toList();
        return actions;
    }

    /**
     * returns all simple class names instaces of @link{TAView) without the "View"
     * suffix. This is
     * used to generate action names for views, e.g. "Books" for "BooksView"
     * 
     * @return the class names
     */
    public static Set<String> getViewClassNames() {
        Reflections reflections = new Reflections("de.simone");
        Set<Class<?>> allClasses = new HashSet<>(
                reflections.getSubTypesOf(TAView.class).stream().collect(Collectors.toSet()));
        Set<String> strings = allClasses.stream().map(c -> c.getSimpleName().replace("View", ""))
                .collect(Collectors.toSet());
        strings.removeIf(s -> s.endsWith("_Subclass"));
        return strings;
    }

    private static List<EndPoint> getActions(Class<? extends TAEntity> entityClazz, String... actions) {
        String prefix = entityClazz.getSimpleName() + ".";
        List<EndPoint> actions2 = new ArrayList<>();
        for (String action : actions) {
            actions2.add(new EndPoint(prefix + action, entityClazz.getSimpleName()));
        }
        return actions2;
    }

    private static List<EndPoint> getCRUDActions(Class<? extends TAEntity> entityClazz) {
        return getActions(entityClazz, NEW_ACTION, EDIT_ACTION, DELETE_ACTION);
    }
}
