package org.activiti.engine.impl.cmd.yjcloud;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.cmd.NeedsActiveExecutionCmd;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.apache.commons.lang3.StringUtils;

/**
 * @author coco
 * @date 2020-07-26 15:17
 * 更换审批人
 **/
public class ChangeAssigneeCmd extends NeedsActiveTaskCmd<Void> {


    private String userId;

    public ChangeAssigneeCmd(String taskId,String userId) {
        super(taskId);
        this.validateParams(taskId,userId);
        this.userId = userId;
    }

    protected void validateParams(String taskId,String userId) {
        if (StringUtils.isBlank(taskId)) {
            throw new ActivitiIllegalArgumentException("taskId is null");
        } else if (StringUtils.isBlank(userId)) {
            throw new ActivitiIllegalArgumentException("userId is required when copy a flowNode");
        }
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity currentTask) {
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        currentTask.setOwner(currentTask.getAssignee());
        taskEntityManager.changeTaskAssignee(currentTask,userId);
        taskEntityManager.update(currentTask);
        return null;
    }
}
