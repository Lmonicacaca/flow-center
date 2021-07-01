package com.flow.center.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
public class BeanUtil {

    /**
     * T transfer R
     *
     * @param sourceObject
     * @param targetClass
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> T convertObject(R sourceObject, Class<T> targetClass, String... ignoreProperties) {
        try {
            if (ObjectUtils.isEmpty(sourceObject)) {
                throw new RuntimeException("sourceObject must not null");
            }
            T t = targetClass.newInstance();
            BeanUtils.copyProperties(sourceObject, t, ignoreProperties);
            return t;
        } catch (Exception e) {
            log.error("convert object error,targetClass:{}", targetClass.getName(), e);
            throw new RuntimeException("convert object error", e);
        }
    }

    /**
     * T transfer R
     *
     * @param sourceObject
     * @param targetClass
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> T convertObject(R sourceObject, Class<T> targetClass) {
        try {
            if (ObjectUtils.isEmpty(sourceObject)) {
                throw new RuntimeException("sourceObject must not null");
            }
            T t = targetClass.newInstance();
            BeanUtils.copyProperties(sourceObject, t);
            return t;
        } catch (Exception e) {
            log.error("convert object error,targetClass:{}", targetClass.getName(), e);
            throw new RuntimeException("convert object error", e);
        }
    }

    /**
     * list<T>  convert to list<R>
     *
     * @param sourceObjectList
     * @param targetClass
     * @param <T>              输入类型
     * @param <R>              输出 类型
     * @return list<R>
     */
    public static <T, R> List<R> convertObjectList(Collection<T> sourceObjectList, Class<R> targetClass) {
        if (CollectionUtils.isEmpty(sourceObjectList)) {
            return null;
        }
        List<R> result = sourceObjectList.stream().map(sourceObject -> {
            return convertObject(sourceObject, targetClass);
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * T transfer R
     *
     * @param sourceObject 输入
     * @param targetClass  输出
     * @param consumer     对目标对象进行额外操作：比如赋值等
     * @param <T>          输入类型
     * @param <R>          输出 类型
     * @return R
     */
    public static <T, R> R convertObject(T sourceObject, Class<R> targetClass, BiConsumer<T, R> consumer) {
        try {
            if (ObjectUtils.isEmpty(sourceObject)) {
                throw new RuntimeException("sourceObject must not null");
            }
            R r = targetClass.newInstance();
            BeanUtils.copyProperties(sourceObject, r);
            if (null != consumer) {
                consumer.accept(sourceObject, r);
            }
            return r;
        } catch (Exception e) {
            log.error("convert object error,targetClass:{}", targetClass.getName(), e);
            throw new RuntimeException("convert object error", e);
        }
    }

    /**
     * list<T>  convert to list<R>
     *
     * @param sourceObjectList 输入
     * @param targetClass      输入
     * @param <T>              输入类型
     * @param <R>              输出 类型
     * @param consumer         对目标对象进行额外操作：比如赋值等
     * @return list<R>
     */
    public static <T, R> List<R> convertObjectList(Collection<T> sourceObjectList, Class<R> targetClass, BiConsumer<T, R> consumer) {
        if (CollectionUtils.isEmpty(sourceObjectList)) {
            throw new RuntimeException("sourceObjectList must not null");
        }
        return sourceObjectList.stream().map(sourceObject -> convertObject(sourceObject, targetClass, consumer)).collect(Collectors.toList());
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static <T> Map<String, Object> objectToMap(T obj, Class<T> clazz) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = null;

                value = field.get(obj);
                if (Objects.nonNull(value)) {
                    map.put(fieldName, value);
                }
            }
        } catch (IllegalAccessException e) {
            log.info("objectToMap error,obj:{}", obj, e);
        }
        return map;
    }

    public static <T> List<Map<String, Object>> objectToMapList(List<T> list, Class<T> clazz) {
        return list.stream().map(v -> {
            return objectToMap(v, clazz);
        }).collect(Collectors.toList());
    }

    /**
     * list分页展示
     * @param page
     * @param pageSize
     * @param list
     * @return
     */
    public static <T> List<T> getListPage(int page, int pageSize, List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        int totalCount = list.size();
        page = page - 1;
        int fromIndex = page * pageSize;
        //分页不能大于总数
        if (fromIndex >= totalCount) {
            log.info("页数或分页大小不正确!");
        }

        int toIndex = ((page + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        log.info(String.format("formIndex:%s,toIndex:%s", fromIndex, toIndex));
        return list.subList(fromIndex, toIndex);
    }


}
