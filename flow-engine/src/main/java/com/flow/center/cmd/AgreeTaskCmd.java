package com.flow.center.cmd;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.compatibility.Activiti5CompatibilityHandler;
import org.activiti.engine.impl.cmd.AbstractCompleteTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.util.Activiti5Util;

import java.util.Map;

/**
 * @author coco
 * @date 2020-08-10 19:42
 **/
@Slf4j
public class AgreeTaskCmd extends AbstractCompleteTaskCmd {

    private static final long serialVersionUID = 1L;
    protected Map<String, Object> variables;
    protected Map<String, Object> transientVariables;
    protected boolean localScope;

    public AgreeTaskCmd(String taskId) {
        super(taskId);
    }

    public AgreeTaskCmd(String taskId, Map<String, Object> variables) {
        super(taskId);
        this.variables = variables;
    }

    public AgreeTaskCmd(String taskId, Map<String, Object> variables, boolean localScope) {
        this(taskId, variables);
        this.localScope = localScope;
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity task) {
        // Backwards compatibility
        if (task.getProcessDefinitionId() != null) {
            if (Activiti5Util.isActiviti5ProcessDefinitionId(commandContext, task.getProcessDefinitionId())) {
                Activiti5CompatibilityHandler activiti5CompatibilityHandler = Activiti5Util.getActiviti5CompatibilityHandler();
                activiti5CompatibilityHandler.completeTask(task, variables, localScope);
                return null;
            }
        }

        if (variables != null) {
            if (localScope) {
                task.setVariablesLocal(variables);
            } else if (task.getExecutionId() != null) {
                task.setExecutionVariables(variables);
            } else {
                task.setVariables(variables);
            }
        }

        if (transientVariables != null) {
            if (localScope) {
                task.setTransientVariablesLocal(transientVariables);
            } else {
                task.setTransientVariables(transientVariables);
            }
        }

        executeTaskComplete(commandContext, task, variables, localScope);

 /*       FlowNode endActivity = ActivitiUtil.findEndActivity(task.getProcessDefinitionId());

        List<String> nextActivityId = ActivitiUtil.findNextActivityId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());

        if (nextActivityId.contains(endActivity.getId())) {
            commandContext.getHistoryManager().recordProcessStatusChange(task.getProcessInstanceId(), ApprovalStatus.FINISHED);
        }*/
        return null;
    }

    @Override
    protected String getSuspendedTaskException() {
        return "Cannot complete a suspended task";
    }
}
