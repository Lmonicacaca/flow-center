package com.flow.center.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskDTO implements Serializable {
    private static final long serialVersionUID = -5569820068477685188L;
    protected String owner;
    protected int assigneeUpdatedCount; // needed for v5 compatibility
    protected String originalAssignee; // needed for v5 compatibility
    protected String assignee;
    protected String parentTaskId;

    protected String name;
    protected String localizedName;
    protected String description;
    protected String localizedDescription;
    protected Date createTime; // The time when the task has been created
    protected Date dueDate;
    protected String category;

    protected boolean isIdentityLinksInitialized;
    protected String executionId;
    protected String processInstanceId;
    protected String processDefinitionId;
    protected String taskDefinitionKey;
    protected String formKey;

    protected boolean isCanceled;
    protected String eventName;
    protected boolean forcedUpdate;

    protected Date claimTime;
    protected String id;
    protected int revision = 1;

    protected boolean isInserted;
    protected boolean isUpdated;
    protected boolean isDeleted;
}
