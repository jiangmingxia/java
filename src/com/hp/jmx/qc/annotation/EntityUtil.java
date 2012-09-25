package com.hp.jmx.qc.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityUtil {

    private static final Logger log = LoggerFactory.getLogger(EntityUtil.class);
    
    private static final ConcurrentHashMap<Class<?>, String> entityTypeMap = new ConcurrentHashMap<Class<?>, String>();

    private static final ConcurrentHashMap<Class<?>, Map<String, Method>> toQCFieldInfoCache = new ConcurrentHashMap<Class<?>, Map<String, Method>>();

    private static final ConcurrentHashMap<Class<?>, Map<String, Method>> fromQCFieldInfoCache = new ConcurrentHashMap<Class<?>, Map<String, Method>>();

    private static final ConcurrentHashMap<Class<?>, Method[]> entityMethodsCache = new ConcurrentHashMap<Class<?>, Method[]>();
    
    @SuppressWarnings("unchecked")
    public static String getEntityType(Class clazz) {
        if (entityTypeMap.containsKey(clazz)) 
            return entityTypeMap.get(clazz);
        else {
            String type = null;
            if (clazz.isAnnotationPresent(EntityType.class)) {
                EntityType entityType = (EntityType) clazz.getAnnotation(EntityType.class);
                type = entityType.value();
                if (type != null)
                    entityTypeMap.putIfAbsent(clazz, type);
            }
            return type;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Method> getToQCFieldInfo(Class clazz) {
        if (toQCFieldInfoCache.containsKey(clazz)) 
            return toQCFieldInfoCache.get(clazz);
        else {
            Map<String, Method> toQCFieldInfoMap = new HashMap<String, Method>();
            for (Field field :clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(EntityField.class)) {
                    EntityField entityField = field.getAnnotation(EntityField.class);
                    String qcFieldName = entityField.value();
                    Method method = null;
                    try {
                        method = clazz.getDeclaredMethod(getterName(field.getName()));
                    } catch (SecurityException e) {
                        log.error("Error happend when getting the method name." + field.getName());
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        log.info("Fail to get getter method, try to get is method.");
                        try {
                            method = clazz.getDeclaredMethod(isSetterName(field.getName()));
                        } catch (SecurityException e1) {
                            log.error("Error happend when getting the method name." + field.getName());
                            throw new RuntimeException(e);
                        } catch (NoSuchMethodException e1) {
                            log.error("Can't find any getter method." + field.getName());
                            throw new RuntimeException("Can't find any getter method." + field.getName(), e1);
                        }
                    }
                    toQCFieldInfoMap.put(qcFieldName, method);
                }
            }
            if (toQCFieldInfoMap.size() > 0) {
                toQCFieldInfoCache.putIfAbsent(clazz, toQCFieldInfoMap);
                return toQCFieldInfoMap;
            }
            return null;
        }
    }

    public static Map<String, Method> getFromQCFieldInfo(Class<?> clazz) {
        if (fromQCFieldInfoCache.containsKey(clazz)) 
            return fromQCFieldInfoCache.get(clazz);
        else {
            Map<String, Method> fromQCFieldInfoMap = new HashMap<String, Method>();
            for (Field field :clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(EntityField.class)) {
                    EntityField entityField = field.getAnnotation(EntityField.class);
                    String qcFieldName = entityField.value();
                    
                    String setterMethodName = setterName(field.getName());
                    Method method = null;
                    for (Method temp : getMethods(clazz)) {
                        if (setterMethodName.equals(temp.getName()) && temp.getParameterTypes() != null && temp.getParameterTypes().length == 1) {
                            method = temp;
                            break;
                        }
                    }
                    if (method == null)
                        logAndException("Can't find any setter method." + field.getName());
                    fromQCFieldInfoMap.put(qcFieldName, method);
                }
            }
            if (fromQCFieldInfoMap.size() > 0) {
                fromQCFieldInfoCache.putIfAbsent(clazz, fromQCFieldInfoMap);
                return fromQCFieldInfoMap;
            }
            return null;
        }
    }
    
    private static Method[] getMethods(Class<?> clazz) {
        if (entityMethodsCache.containsKey(clazz)) 
            return entityMethodsCache.get(clazz);
        else {
            Method[] methods = clazz.getMethods();
            entityMethodsCache.putIfAbsent(clazz, methods);
            return methods;
        }
        
    }
    
    private static String getterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
    
    private static String setterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
    
    private static String isSetterName(String fieldName) {
        return "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
    
    private static void logAndException(String message) {
        log.error(message);
        throw new RuntimeException(message);
    }
}
