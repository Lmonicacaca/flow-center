package com.flow.center.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ProcessInstanceDTO implements Serializable {
    private static final long serialVersionUID = -4868007842442319093L;
    protected String id;
    protected String name;
    protected String description;
    protected String localizedName;
    protected String localizedDescription;
    protected Date lockTime;
    protected boolean isActive;
    protected boolean isScope;
    protected boolean isConcurrent;
    protected boolean isEnded;
    protected boolean isEventScope;
    protected boolean isMultiInstanceRoot;
    protected boolean isCountEnabled;
    protected String deleteReason;
    protected String startUserId;
    protected Date startTime;
    protected int eventSubscriptionCount;
    protected int taskCount;
    protected int jobCount;
    protected int timerJobCount;
    protected int suspendedJobCount;
    protected int deadLetterJobCount;
    protected int variableCount;
    protected int identityLinkCount;
    protected String processDefinitionId;
    protected String processDefinitionKey;
    protected String processDefinitionName;
    protected Integer processDefinitionVersion;
    protected String deploymentId;
    protected String activityId;
    protected String activityName;
    protected String processInstanceId;
    protected String businessKey;
    protected String parentId;
    protected String superExecutionId;
    protected String rootProcessInstanceId;
    protected boolean forcedUpdate;
}
