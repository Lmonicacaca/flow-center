package com.flow.center.dubbo.impl;

import com.flow.center.dto.ProcessInstanceDTO;
import com.flow.center.dto.request.ProcessInstanceRequest;
import com.flow.center.dto.request.StartProcessReq;
import com.flow.center.dubbo.IRuntimeRSV;
import com.flow.center.util.ActivitiUtil;
import com.flow.center.util.FlowUtil;
import com.yjcloud.idol.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class RuntimeRSVImpl implements IRuntimeRSV {
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private IdentityService identityService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private FlowUtil flowUtil;

    @Override
    public BaseResponse<Object> getVariable(String executionId, String variableName) {
        Object value = null;
        try {
            // 变量取得
            value = runtimeService.getVariable(executionId, variableName);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }

        return BaseResponse.buildBaseResponse().success(true).result(value);
    }

    @Override
    public BaseResponse<Map<String, Object>> getVariables(String executionId) {
        Map<String, Object> valueMap = null;
        try {
            // 变量取得
            valueMap = runtimeService.getVariables(executionId);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }

        return BaseResponse.buildBaseResponse().success(true).result(valueMap);
    }

    @Override
    public BaseResponse setVariable(String executionId, String variableName, Object value) {
        try {
            // 变量设定
            runtimeService.setVariable(executionId, variableName, value);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }

        return BaseResponse.buildBaseResponse().success(true);
    }

    @Override
    public BaseResponse setVariables(String executionId, Map<String, ?> variables) {

        try {
            // 变量设定
            runtimeService.setVariables(executionId, variables);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
        return BaseResponse.buildBaseResponse().success(true);
    }

    @Override
    public BaseResponse<List<String>> getActiveActivityIds(String executionId) {

        // 返回值
        List<String> activeActivityIds = null;

        try {
            activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }

        return BaseResponse.buildBaseResponse().success(true).result(activeActivityIds);
    }


    @Override
    public BaseResponse<Boolean> isFinished(String processInstanceId) {
        return BaseResponse.buildSuccessResponse().result(ActivitiUtil.isFinished(processInstanceId));
    }


}
