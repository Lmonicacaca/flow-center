package com.flow.center.cmd;

import com.flow.center.util.ActivitiUtil;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.constant.ApprovalStatus;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.cmd.yjcloud.JumpAnyActivityCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * @author coco
 * @date 2020-08-06 18:01
 **/
public class WithdrawCmd extends NeedsActiveTaskCmd<Void> {

    private String userId;

    public WithdrawCmd(String taskId, String userId) {
        super(taskId);
        this.userId = userId;
    }
    @Override
    protected Void execute(CommandContext commandContext, TaskEntity currentTask) {
        FlowNode end = ActivitiUtil.findEndActivity(currentTask.getProcessDefinitionId());
        JumpAnyActivityCmd jumpAnyActivityCmd = new JumpAnyActivityCmd(currentTask.getId(),userId,currentTask.getName(),end);
        commandContext.getCommandExecutor().execute(jumpAnyActivityCmd);
        commandContext.getHistoryManager().recordTaskStatusChange(taskId, ApprovalStatus.WITHDRAW);
        commandContext.getHistoryManager().recordProcessInstanceEnd(currentTask.getProcessInstanceId(), ApprovalStatus.WITHDRAW);
        return null;
    }

}
