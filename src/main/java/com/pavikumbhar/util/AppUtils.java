package com.pavikumbhar.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class AppUtils {
    /**
     * Copies properties from one object to another
     * @destination
     */
    public static void copyNonNullProperties(Object source, Object destination, String... ignoreProperties){
        Set<String> ignorePropertiesSet = getNullPropertyNames(source);
        Collections.addAll(ignorePropertiesSet, ignoreProperties);
        BeanUtils.copyProperties(source, destination,  ignorePropertiesSet.toArray(new String[0]));
    }

    public static void copyProperties(Object source, Object destination, String... ignoreProperties){
        BeanUtils.copyProperties(source, destination,  ignoreProperties);
    }

    public static Set<String> getNullPropertyNames(Object source) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        return Arrays.stream(pds)
                .map(PropertyDescriptor::getName)
                .filter(propertyName -> beanWrapper.getPropertyValue(propertyName) == null)
                .collect(Collectors.toSet());
    }


    public static <S, T> List<T> copyProperties(List<S> sourceList, Class<T> targetClass) {
        List<T> targetList = new ArrayList<>();
        for (S source : sourceList) {
            try {
                T target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                targetList.add(target);
            } catch (Exception e) {
                log.error("copyProperties : error while converting list " );
                throw new RuntimeException("Failed to copy properties", e);
            }
        }
        return targetList;
    }
}
