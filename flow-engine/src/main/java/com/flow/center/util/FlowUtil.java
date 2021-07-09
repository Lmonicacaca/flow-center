package com.flow.center.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公共转换类
 */
@Slf4j
@Component
public class FlowUtil {


    public static final String buildAppKey(String appId,String courtId) {
        String concat = "a".concat(appId);
        if (StringUtils.isNotEmpty(courtId)) {
            concat.concat("-").concat(courtId);
        }
        return concat;
    }

    /**
     * 根据流程定义Id获取appId
     * @param processDefinitionId
     * @return
     */
    public Long getAppIdByProcDefId(String processDefinitionId) {
        try {
            if (StringUtils.isEmpty(processDefinitionId)) {
                return null;
            }
            String procDefId = processDefinitionId.substring(processDefinitionId.indexOf("a") + 1,
                    processDefinitionId.indexOf("-"));

            return StringUtils.isBlank(procDefId) ? null : Long.valueOf(procDefId);
        } catch (Exception e) {
            log.error("getAppIdByProcDefId error:", e);
            return null;
        }
    }

    public <T> T convertObject(Object sourceObject, Class<T> targetClass) {
        try {
            if (ObjectUtils.isEmpty(sourceObject)) {
                return null;
            }
            T t = targetClass.newInstance();
            BeanUtils.copyProperties(sourceObject, t);
            return t;
        } catch (Exception e) {
            log.info("convert object error,sourceObj:{},targetClass:{}", JSON.toJSONString(sourceObject), targetClass.getName(), e);
        }
        return null;
    }
    public <T> List<T> convertObjectList(List<Object> sourceObjectList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceObjectList)) {
            return null;
        }
        List<T> result = sourceObjectList.stream().map(sourceObject -> {
            return convertObject(sourceObject,targetClass);
        }).collect(Collectors.toList());
        return result;
    }

}
