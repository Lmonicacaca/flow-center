package com.flow.center.dubbo.impl;

import com.flow.center.cmd.WithdrawCmd;
import com.flow.center.dubbo.IProcessRSV;
import com.yjcloud.idol.common.response.BaseResponse;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lilin
 * @date 2021-07-05
 * 流程相关service，所有的校验没有做，实际开发需加上
 */
@Service
public class ProcessRSVImpl implements IProcessRSV {
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private ManagementService managementService;

    @Override
    public BaseResponse startProcess(String processId) {
        try {
            runtimeService.startProcessInstanceById(processId);
        }catch (Exception e){
            return BaseResponse.buildErrorResponse();
        }


        return BaseResponse.buildSuccessResponse();
    }

    @Override
    public BaseResponse startProcess(String processId, Map<String, Object> params) {

        try {
            runtimeService.startProcessInstanceById(processId,params);
        }catch (Exception e){
            return BaseResponse.buildErrorResponse();
        }


        return BaseResponse.buildSuccessResponse();
    }
}
