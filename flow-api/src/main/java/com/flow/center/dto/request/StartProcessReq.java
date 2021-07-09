package com.flow.center.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @desc:
 * @Author: lvjing
 * @Date: 2021/4/6 9:48 上午
 */
@Data
public class StartProcessReq implements Serializable {
    /**
     *  发起人id
     */
    protected String userId;
    /**
     * 流程id
     */
    protected String flowId;
    /**
     * 租户id
     */
    protected String tenantId;
    /**
     * 流程业务key
     */
    protected String businessKey;

    /**
     * 流程名称
     */
    protected String procName;
    /**
     * 流程变量
     */
    protected Map<String, Object> variables;

    /**
     * 当前租户无流程时，是否使用上层租户流程即xxxx:-1的流程
     */
    protected boolean useParentFlow = false;

    public String getProcessDefinitionKey() {
        if (null == flowId || "".equals(flowId) || null == tenantId || "".equals(tenantId)) {
            return null;
        }
        return "a".concat(flowId).concat("-").concat(tenantId);
    }
}
