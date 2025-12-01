package com.glidingpath.finch.util;

import com.glidingpath.finch.constants.FinchConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class FinchMappingUtil {
    
    private FinchMappingUtil() {
        // Prevent instantiation
    }
    
    /**
     * Safe date parsing with error handling
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateString, FinchConstants.DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("{} {}", FinchConstants.ERROR_DATE_PARSE, dateString);
            return null;
        }
    }
    
    /**
     * Safe integer to long conversion
     */
    public static Long toLong(Integer value) {
        return value != null ? value.longValue() : null;
    }
    
    /**
     * Generic list mapping utility
     */
    public static <T, R> List<R> mapList(List<T> source, java.util.function.Function<T, R> mapper) {
        if (source == null) {
            return null;
        }
        return source.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
    
    /**
     * Generic object mapping utility with null safety
     */
    public static <T, R> R mapObject(T source, java.util.function.Function<T, R> mapper) {
        return source != null ? mapper.apply(source) : null;
    }
    
    /**
     * Safe object copying with BeanUtils
     */
    public static <T> T copyProperties(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            log.error("Failed to copy properties from {} to {}", source.getClass(), targetClass, e);
            return null;
        }
    }
} 