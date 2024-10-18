package org.example.utils;

import org.example.annotations.Id;

import java.util.Arrays;

public class AnnotationUtils<T> {
    protected boolean isIdPresent(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .anyMatch(f -> {
                    try {
                        f.setAccessible(true);
                        return f.get(entity) != null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to access field: " +  e.getMessage());
                    }
                });
    }

    protected void setIdUsingAnnotations(T entity, long id) {
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f ->  f.isAnnotationPresent(Id.class))
                .forEach(f -> {
                    try {
                        f.setAccessible(true);
                        f.set(entity, id);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error occurred while setting a field. " + e.getMessage());
                    }
                });
    }

    protected Long getIdUsingAnnotations(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f ->  f.isAnnotationPresent(Id.class))
                .findFirst()
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return (Long) f.get(entity);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error occurred while accessing the field: " + e.getMessage());
                    }
                }).orElse(null);
    }
}
