package com.flow.center.dubbo.impl;

import com.flow.center.dubbo.IFlowFormRSV;
import com.flow.center.util.ActivitiUtil;
import com.google.common.collect.Maps;
import com.yjcloud.idol.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author coco
 * @date 2020-08-21 10:49
 **/
@Service
@Slf4j
public class FlowFormRSVImpl implements IFlowFormRSV {

    @Resource
    private FormService formService;


    @Override
    public Map<String, String> getTaskButtons(String taskId) {
        Map<String, String> button = Maps.newHashMap();
        try {
            TaskFormData taskFormData = formService.getTaskFormData(taskId);
            if (Objects.nonNull(taskFormData)) {
                List<FormProperty> formProperties = taskFormData.getFormProperties();

                if (CollectionUtils.isNotEmpty(formProperties)) {

                    button = formProperties.stream()
                            .map(formProperty -> {
                                if (null == formProperty.getName()) {
                                    formProperty.setName("");
                                }
                                return formProperty;
                            })
                            .collect(Collectors.toMap(FormProperty::getVariableName, FormProperty::getName));
                }
            }
        }catch (Exception e){
            log.info("No task found for taskId",e.getMessage(),e);
        }
        return button;
    }

    @Override
    public BaseResponse<String> queryBeforeTaskDefKey(String processDefinitionId, String jumpActivityId) {
        FlowElement flowElement = ActivitiUtil.getFlowElement(processDefinitionId, jumpActivityId);
        if (Objects.isNull(flowElement)) {
            return BaseResponse.buildErrorResponse().code(5000).message("activityId is no found");
        }
        return BaseResponse.buildBaseResponse().success(true).result(flowElement.getId());
    }
}
