/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.simone.backend.TAEntity;
import io.quarkus.logging.Log;
import jakarta.validation.constraints.Size;

public class EntityUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Map<String, String> getVariablesByName(TAEntity entity) {
        Map<String, String> variables = new TreeMap<>();
        String clazz = entity.getClass().getSimpleName();
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            variables.put(
                    field.getName(), TranslationProvider.getTranslation(clazz + "." + field.getName()));
        }
        variables.remove("id");
        variables.remove("comment");

        Map<String, String> variables2 = new LinkedHashMap<>();
        variables2.putAll(variables);

        return variables2;
    }

    public static Map<String, Integer> getEntityFieldLength(Class<? extends TAEntity> clazz) {
        return getEntityFieldLength(clazz, false);
    }

    /**
     * return a Map with key=Field name and value= Field lenght. the lenght is
     * obteined form the @Size
     * anotation inside the entitiy passed as arguent
     *
     * @param clazz      - the entity class
     * @param withPrefix - with clazz.getSimpleName(). as prefix
     * @return the Map
     */
    public static Map<String, Integer> getEntityFieldLength(
            Class<? extends TAEntity> clazz, boolean withPrefix) {
        Map<String, Integer> variables = new TreeMap<>();
        String prefix = withPrefix ? clazz.getSimpleName() + "." : "";
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            Size size = field.getAnnotation(Size.class);
            if (size != null) {
                String name2 = prefix + name;
                variables.put(name2, size.max());
            }
        }
        return variables;
    }

    public static Map<String, String> getEntityFields(
            Class<? extends TAEntity> clazz, boolean withPrefix) {
        return getEntityFields(clazz, withPrefix, false);
    }

    /**
     * return the entity fields with prefix and brackets
     *
     * @param clazz - the class to scan
     * @return the map
     * @see EntityUtils#getEntityFields(Class, boolean, boolean)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getEntityFields(Class<? extends TAEntity>... clazz) {
        Map<String, String> variables = new TreeMap<>();
        for (Class<? extends TAEntity> class1 : clazz) {
            variables.putAll(getEntityFields(class1, true, true));
        }
        return variables;
    }

    /**
     * return a Map containing (allmost) all declared fields inside the clazz entity
     * argument. the key
     * = entity field name and the Value text description for the field
     *
     * @param clazz        - class to extract the fields of
     * @param withPrefix   - true to append the class name as prefix (suitable for
     *                     qute template)
     * @param withBrackets - true to enclose the key value between {}
     * @return the map
     */
    public static Map<String, String> getEntityFields(
            Class<? extends TAEntity> clazz, boolean withPrefix, boolean withBrackets) {
        Map<String, String> variables = new TreeMap<>();
        String prefix = withPrefix ? clazz.getSimpleName() + "." : "";
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            int mod = field.getModifiers();
            if (name.startsWith("$$_")
                    || Modifier.isStatic(mod)
                    || Modifier.isInterface(mod)
                    || Collection.class.isAssignableFrom(field.getType())
                    || TAEntity.class.isAssignableFrom(field.getType()))
                continue;

            String name2 = prefix + name;

            variables.put(
                    name2, TranslationProvider.getTranslation(clazz.getSimpleName() + "." + field.getName()));
        }

        // add or remove fields. this method is used for all entities. for fine tunning
        // go to QuteUtils.getEntityVariables
        variables.remove(prefix + "secondaryKey");

        variables.put(prefix + "id", TranslationProvider.getTranslation("TAEntity.id"));

        if (withBrackets) {
            Map<String, String> variables2 = new TreeMap<>();
            variables.forEach((k, v) -> variables2.put("{" + k + "}", v));
            return variables2;
        }
        return variables;
    }

    /**
     * return all entities. The return names is THE COMPLETE QUALIFIED NAME of the
     * entity class. for example de.simone.backend.User
     * 
     * @return the entities
     */
    public static Set<String> getEntities() {
        Reflections reflections = new Reflections("de.simone");
        Set<Class<?>> allClasses = new HashSet<>(
                reflections.getSubTypesOf(TAEntity.class).stream().collect(Collectors.toSet()));
        Set<String> strings = allClasses.stream().map(c -> c.getName().replace("View", ""))
                .collect(Collectors.toSet());
        return strings;
    }

    /**
     * return the json representation of the entity. the fields to exclude are
     * passed as argument
     * 
     * NOTE: In Jackson Databind, a method like calculateAmount() is only serialized
     * if Jackson treats it as a property getter. Jackson considers methods
     * following the JavaBean getter pattern (getX() or isX()) as properties.
     * So:
     * getAmount() → serialized as "amount"
     * calculateAmount() → NOT serialized by default
     * 
     * @param entity          - the entity
     * @param fieldsToExclude - the fileds to exclude
     * @return the json
     */
    public static String getJson(TAEntity entity, String... fieldsToExclude) {
        String json = null;
        try {
            SimpleFilterProvider filters = new SimpleFilterProvider();
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(fieldsToExclude);
            filters.addFilter("FieldFilter", filter);
            objectMapper.setFilterProvider(filters);
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
        } catch (Exception e) {
            json = String.format("""
                    {
                        "error": "Error converting entity to json",
                        "message": "%s"
                    }
                    """, e.getMessage());
            Log.error("", e);
        }
        return json;
    }
}
