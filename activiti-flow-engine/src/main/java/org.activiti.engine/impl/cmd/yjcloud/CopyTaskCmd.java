package org.activiti.engine.impl.cmd.yjcloud;

import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.constant.TaskStatus;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;


/**
 * @author coco
 * 复制一个当前的节点添加到下一步流程中，复制的节点 候选组会被清空，成为一个空节点（未指派任何人处理），当前节点会被完成。
 * @date 2020-03-31 09:46
 **/
public class CopyTaskCmd extends NeedsActiveTaskCmd<Void> {

    /**
     * 复制出的节点任务拥有人的id
     */
    private String userId;

    /**
     * 复制出的节点任务的名称，如果没有，则默认跟被复制的节点一致
    */
    private String activityName;


    public CopyTaskCmd(String taskId,String userId,String activityName) {
        super(taskId);
        this.validateParams(taskId,userId);
        this.userId = userId;
        this.activityName = activityName;
    }

    public CopyTaskCmd(String taskId,String userId) {
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
    protected Void execute(CommandContext commandContext, TaskEntity taskEntity) {
        String executionId = taskEntity.getExecutionId();
        if (StringUtils.isBlank(executionId)){
            return null;
        }
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        FlowNode currentFlowElement = (FlowNode)executionEntity.getCurrentFlowElement();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        //ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        //目前支持用户任务 UserTask的复制
        if (currentFlowElement instanceof UserTask) {
            TaskEntity taskEntity1 = taskEntityManager.create();
            BeanUtils.copyProperties(taskEntity,taskEntity1);
            taskEntity1.setId(null);
            taskEntity1.setExecutionId(taskEntity.getExecutionId());
            taskEntity1.setAssignee(this.userId);
            if (StringUtils.isNotBlank(this.activityName)) {
                taskEntity1.setName(this.activityName);
            }
            taskEntityManager.insert(taskEntity1,executionEntity);
            commandContext.getHistoryManager().recordTaskStatusChange(taskId, TaskStatus.AGREE);
            taskEntityManager.deleteTask(taskEntity, (String)"用户增加临时节点", false, false);
            commandContext.getHistoryManager().recordActivityEnd(executionEntity,"用户增加临时节点");
            List<TaskEntity> tasksByExecutionId = commandContext.getTaskEntityManager().findTasksByExecutionId(executionEntity.getId());
            if (CollectionUtil.isNotEmpty(tasksByExecutionId)) {
                TaskEntity taskEntity2 = tasksByExecutionId.get(0);
                UserTask userTask = new UserTask();
                BeanUtils.copyProperties(currentFlowElement,userTask);
                userTask.setAssignee(this.userId);
                if (StringUtils.isNotBlank(this.activityName)) {
                    userTask.setName(this.activityName);
                }
                //executionEntity.setCurrentFlowElement(currentFlowElement1);
                commandContext.getHistoryManager().recordActivityStart(executionEntity,userTask);
                commandContext.getHistoryManager().recordTaskId(taskEntity2);
                commandContext.getHistoryManager().recordTaskAssignment(taskEntity2);

            }



        } else {
            throw new ActivitiIllegalArgumentException("the type of currentFlowElement is not UserTask");
        }
        return null;
    }
}
