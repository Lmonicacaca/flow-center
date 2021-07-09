package com.flow.center.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
public class ProcessInstanceRequest implements Serializable {
    private static final long serialVersionUID = 4051221042884348554L;
    protected String processInstanceId;
    protected Set<String> processInstanceIds;
    protected Date startedBefore;
    protected Date startedAfter;
    protected String processDefinitionKey;

    // 是否获取active的流程实例 true:是，false:否
    protected Boolean activeFlag = false;

    // 排序（ProcessInstanceId） true:升序，false：降序
    protected Boolean orderByProcessInstanceId = null;

}
