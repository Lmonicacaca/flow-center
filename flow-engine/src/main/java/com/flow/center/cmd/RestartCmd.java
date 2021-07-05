package com.flow.center.cmd;

import org.activiti.engine.constant.ApprovalStatus;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * 重新发起
 **/
public class RestartCmd extends NeedsActiveTaskCmd<Void> {

    private String userId;

    public RestartCmd(String taskId) {
        super(taskId);
    }
    @Override
    protected Void execute(CommandContext commandContext, TaskEntity currentTask) {

        commandContext.getHistoryManager().recordTaskStatusChange(taskId, ApprovalStatus.APPROVALING);
        commandContext.getHistoryManager().recordProcessInstanceEnd(currentTask.getProcessInstanceId(), ApprovalStatus.APPROVALING);
        return null;
    }

}
