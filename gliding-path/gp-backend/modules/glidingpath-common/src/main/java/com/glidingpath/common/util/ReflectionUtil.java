package com.glidingpath.common.util;

import java.lang.reflect.Field;

public class ReflectionUtil {
    public static Object getField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field '" + fieldName + "' on " + obj.getClass().getName(), e);
        }
    }
} 