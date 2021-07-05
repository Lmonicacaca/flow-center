package com.flow.center.cmd;

import com.flow.center.util.ActivitiUtil;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.constant.ApprovalStatus;
import org.activiti.engine.constant.TaskStatus;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.cmd.yjcloud.JumpAnyActivityCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * 快速结束
 */
public class QuickEndCmd extends NeedsActiveTaskCmd<Void> {

    public QuickEndCmd(String taskId) {
        super(taskId);
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity currentTask) {
        FlowNode end = ActivitiUtil.findEndActivity(currentTask.getProcessDefinitionId());
        JumpAnyActivityCmd jumpAnyActivityCmd = new JumpAnyActivityCmd(currentTask.getId(),currentTask.getAssignee(),currentTask.getName(),end);
        commandContext.getCommandExecutor().execute(jumpAnyActivityCmd);

        commandContext.getHistoryManager().recordTaskStatusChange(taskId, TaskStatus.AGREE);
        commandContext.getHistoryManager().recordProcessInstanceEnd(currentTask.getProcessInstanceId(), ApprovalStatus.FINISHED);
        return null;
    }
}
