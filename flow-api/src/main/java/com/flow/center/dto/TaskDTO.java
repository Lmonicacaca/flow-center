package com.flow.center.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskDTO implements Serializable {
    private static final long serialVersionUID = -1L;
    protected String owner;
    protected String assignee;
    protected String parentTaskId;

    protected String name;
    protected String localizedName;
    protected String description;
    protected String localizedDescription;
    protected Date createTime;
    protected Date dueDate;
    protected String category;

    protected Boolean isIdentityLinksInitialized;
    protected String executionId;
    protected String processInstanceId;
    protected String processDefinitionId;
    protected String taskDefinitionKey;
    protected String formKey;

    protected Boolean isCanceled;
    protected String eventName;
    protected Boolean forcedUpdate;

    protected Date claimTime;
    protected String id;

}
