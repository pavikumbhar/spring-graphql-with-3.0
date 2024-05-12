package com.pavikumbhar.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.GenericTypeResolver;

@UtilityClass
public class ObjectUtil {

    public static Class<?>[] getGenericType(Class<?> classInstance, Class<?> classToGetGenerics) {
        return GenericTypeResolver.resolveTypeArguments(classInstance, classToGetGenerics);
    }

    public static Class<?> getGenericType(Class<?> classInstance, Class<?> classToGetGenerics, int genericPosition) {
        Class<?>[] typeArguments = getGenericType(classInstance, classToGetGenerics);
        if (typeArguments != null && typeArguments.length >= genericPosition) {
            return typeArguments[genericPosition];
        }
        throw new IllegalArgumentException("Could not determine generic type for interface " + classInstance.getName());
    }

}


