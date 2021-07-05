package com.flow.center.cmd;

import com.flow.center.util.ActivitiUtil;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.constant.ApprovalStatus;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.cmd.yjcloud.JumpAnyActivityCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

import java.util.Objects;

/**
 * 拒绝
 **/
public class RefuseCmd extends NeedsActiveTaskCmd<Void> {

    /**
     * 有传参数则跳转到指定节点
     */
    private FlowNode flowNode;

    public RefuseCmd(String taskId) {
        super(taskId);
    }

    public RefuseCmd(String taskId, FlowNode flowNode) {
        super(taskId);
        this.flowNode = flowNode;
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity currentTask) {
        boolean back2Start = false;
        if (Objects.isNull(flowNode)) {
            back2Start = true;
            flowNode = ActivitiUtil.findFirstUserTask(currentTask.getProcessDefinitionId());
        }

        JumpAnyActivityCmd jumpAnyActivityCmd = new JumpAnyActivityCmd(currentTask.getId(), flowNode);
        commandContext.getCommandExecutor().execute(jumpAnyActivityCmd);

        commandContext.getHistoryManager().recordTaskStatusChange(taskId, ApprovalStatus.REFUSE);

        if (back2Start) {
            commandContext.getHistoryManager().recordProcessStatusChange(currentTask.getProcessInstanceId(), ApprovalStatus.REFUSE);
        }
        return null;
    }
}
