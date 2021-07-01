package com.flow.center.dubbo;

import com.yjcloud.idol.common.response.BaseResponse;

import java.util.Map;

/**
 * @author coco
 * @date 2020-08-06 10:06
 **/
public interface ICommandRSV {

    BaseResponse startProcess(String processId);

    BaseResponse startProcess(String processId, Map<String,Object> params);


}
