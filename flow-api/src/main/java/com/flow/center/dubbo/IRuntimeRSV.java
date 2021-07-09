package com.flow.center.dubbo;


import com.yjcloud.idol.common.response.BaseResponse;

import java.util.List;
import java.util.Map;

public interface IRuntimeRSV {

    /**
     * 获取单个流程变量
     * @param executionId
     * @param variableName
     * @return 流程变量
     */
    BaseResponse<Object> getVariable(String executionId, String variableName);

    /**
     * 获取流程变量列表
     * @param executionId
     * @return 流程变量列表
     */
    BaseResponse<Map<String, Object>> getVariables(String executionId);

    /**
     * 设置流程变量
     * @param executionId
     * @param variableName
     * @param value
     * @return
     */
    BaseResponse setVariable(String executionId, String variableName, Object value);

    /**
     * 设置流程变量列表
     * @param executionId
     * @param variables
     * @return
     */
    BaseResponse setVariables(String executionId, Map<String, ? extends Object> variables);

    /**
     * 获取当前活跃用户数
     * @param executionId
     * @return 当前活跃用户数
     */
    BaseResponse<List<String>> getActiveActivityIds(String executionId);

    /**
     * 流程实例是否结束
     * @param processInstanceId
     * @return
     */
    BaseResponse<Boolean> isFinished(String processInstanceId);


}
