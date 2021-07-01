package com.flow.center.dubbo.impl;

import com.flow.center.dubbo.ICommandRSV;
import com.yjcloud.idol.common.response.BaseResponse;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service
public class CommandRSVImpl implements ICommandRSV {
    @Resource
    private RuntimeService runtimeService;

    @Override
    public BaseResponse startProcess(String processId) {
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
        }catch (Exception e){
            return BaseResponse.buildErrorResponse();
        }


        return BaseResponse.buildSuccessResponse();
    }

    @Override
    public BaseResponse startProcess(String processId, Map<String, Object> params) {

        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId,params);
        }catch (Exception e){
            return BaseResponse.buildErrorResponse();
        }


        return BaseResponse.buildSuccessResponse();
    }
}