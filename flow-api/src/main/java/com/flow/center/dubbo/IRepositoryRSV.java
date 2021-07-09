package com.flow.center.dubbo;

import com.yjcloud.idol.common.response.BaseResponse;

import java.util.List;
import java.util.Map;

/**
 * @author coco
 * @date 2020-08-20 18:24
 **/
public interface IRepositoryRSV {

    /**
     * 获取所有SequequeFlow的id
     * @param appId
     * @param courtId
     * @return
     */
    List<String> allSequequeFlowsId(String appId, Long courtId);

    /**
     * 获取流程最新的版本
     * @param appId
     * @param courtId
     * @return
     */
    Long getModelVersion(String appId, Long courtId);


    /**
     * 判断流程定义是否已部署
     * @param appId
     * @param courtId
     * @return true:存在该流程定义，false：不存在该流程定义
     */
    BaseResponse<Boolean> checkProcessDefinition(String appId, Long courtId);

    /**
     * 获取流程中所有用户节点的节点名称
     * @param appId
     * @param courtId
     * @return
     */
    BaseResponse<Map<String, String>> getUserTaskNodeNameMap(String appId, Long courtId);
}
