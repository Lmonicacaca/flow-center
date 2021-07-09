package com.flow.center.dubbo;

import com.yjcloud.idol.common.response.BaseResponse;

import java.util.Map;

/**
 * @author coco
 * @date 2020-08-06 10:06
 **/
public interface IProcessRSV {

    /**
     * 启动流程
     * @param processId
     * @return
     */
    BaseResponse startProcess(String processId);

    /**
     * 启动流程，带参数
     * @param processId
     * @param params
     * @return
     */
    BaseResponse startProcess(String processId, Map<String,Object> params);

}
