package com.flow.center.dubbo;

import com.yjcloud.idol.common.response.BaseResponse;

/**
 * @desc:流程公共方法
 * @Author: lvjing
 * @Date: 2020/11/16 2:11 下午
 */
public interface IActivitiRSV {

    /**
     * 获取发起人节点id
     * @param appId
     * @param tenantId
     * @return
     */
    BaseResponse<String> getFirstUserTaskNodeId(String appId, Long tenantId);

    /**
     * 获取发起人节点id
     * @param procDefId
     * @return
     */
    BaseResponse<String> getFirstUserTaskNodeId(String procDefId);

    /**
     * 获取圆圈圈节点
     * @param flowId
     * @param tenantId
     * @return
     */
    BaseResponse<String> findFirstActivity(String flowId, Long tenantId);

}
