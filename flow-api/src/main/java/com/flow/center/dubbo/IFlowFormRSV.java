package com.flow.center.dubbo;

import com.yjcloud.idol.common.response.BaseResponse;

import java.util.Map;

/**
 * @author coco
 * @date 2020-08-20 18:25
 **/
public interface IFlowFormRSV {

    Map<String,String> getTaskButtons(String taskId);

    /**
     * 查询当前任务节点 taskDefKey
     * @param processDefinitionId
     * @param jumpActivityId
     * @return
     */
    BaseResponse<String> queryBeforeTaskDefKey(String processDefinitionId, String jumpActivityId);
}
