package com.catl.cal_index.utils;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author Johny
 */
public class BeanUtils {

    private static final Map<String, BeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

    private BeanUtils() {
    }

    /**
     * 对象复制
     *
     * @param source 被复制对象，为空会抛出异常
     * @param target 复制的结果，为空会抛出异常
     */
    public static void copyProperties(Object source, Object target) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(target, "target must not be null");

        BeanCopier beanCopier = getBeanCopier(source.getClass(), target.getClass());
        beanCopier.copy(source, target, null);
    }

    /**
     * 对象复制
     *
     * @param source 被复制对象，为空会抛出异常
     * @param clazz  复制类型，为空会抛出异常
     * @param <T>
     */
    public static <T> T copyObject(Object source, Class<T> clazz) {
        T result;
        try {
            result = clazz.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("fail to create instance of type" + clazz.getCanonicalName(), e);
        }

        copyProperties(source, result);
        return result;
    }

    /**
     * 复制列表
     *
     * @param list   被复制列表
     * @param classz 复制类型
     * @param <T>
     * @return
     */
    public static <T> List<T> copyList(List<?> list, Class<T> classz) {
        List<T> resultList = new LinkedList<>();
        if (CollectionUtils.isEmpty(list)) {
            return resultList;
        }
        for (Object obj1 : list) {
            resultList.add(copyObject(obj1, classz));
        }
        return resultList;
    }

    private static BeanCopier getBeanCopier(Class<?> source, Class<?> target) {
        String key = generateKey(source, target);
        return BEAN_COPIER_MAP.computeIfAbsent(key, x -> BeanCopier.create(source, target, false));
    }

    private static String generateKey(Class<?> source, Class<?> target) {
        return source.getCanonicalName().concat(target.getCanonicalName());
    }
}