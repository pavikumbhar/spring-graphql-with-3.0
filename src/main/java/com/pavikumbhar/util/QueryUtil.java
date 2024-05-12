package com.pavikumbhar.util;

import com.pavikumbhar.common.repository.GenericRepository;
import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryUtil {


    /**
     * @return the @Entity class of the given repository param.
     * @see ObjectUtil#getGenericType(Class, Class, int)
     * @see ObjectUtil#getGenericType(Class, Class)
     */
    public static Class<?> getRepositoryEntityType(Class<?> repositoryClass) {
        return ObjectUtil.getGenericType(repositoryClass, GenericRepository.class, 0);
    }

}