package com.flow.center.dubbo.impl;

import com.flow.center.dubbo.IActivitiRSV;
import com.flow.center.util.ActivitiUtil;
import com.yjcloud.idol.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FlowElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import java.util.Objects;

/**
 * @desc:流程中一些公共方法
 * @Author: lvjing
 * @Date: 2020/11/16 2:13 下午
 */
@Service
@Slf4j
public class IActivitiRSVImpl implements IActivitiRSV {

    @Override
    public BaseResponse<String> getFirstUserTaskNodeId(String appId, Long tenantId) {
        if (StringUtils.isBlank(appId) || null == tenantId) {
            return BaseResponse.buildErrorResponse().code(4000).message("参数为空");
        }
        FlowElement flowFirstUserTask = ActivitiUtil.findFlowFirstUserTask(appId, tenantId);
        if (Objects.nonNull(flowFirstUserTask)) {
            return BaseResponse.buildSuccessResponse().result(flowFirstUserTask.getId());
        }
        return null;
    }

    @Override
    public BaseResponse<String> getFirstUserTaskNodeId(String procDefId) {
        if (StringUtils.isBlank(procDefId)) {
            return BaseResponse.buildErrorResponse().code(4000).message("参数为空");
        }
        FlowElement flowFirstUserTask = ActivitiUtil.findFirstUserTask(procDefId);
        if (Objects.nonNull(flowFirstUserTask)) {
            return BaseResponse.buildSuccessResponse().result(flowFirstUserTask.getId());
        }
        return null;
    }

    @Override
    public BaseResponse<String> findFirstActivity(String flowId, Long tenantId) {
        FlowElement firstActivity = ActivitiUtil.findFirstActivity(flowId, tenantId);
        if(null != firstActivity){
            return BaseResponse.buildSuccessResponse().result(firstActivity.getId());
        }
        return BaseResponse.buildErrorResponse();
    }

}
