package org.activiti.engine.impl.cmd.yjcloud;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.util.ProcThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.activiti.engine.impl.cmd.yjcloud.CountersigningVariables.NUMBER_OF_COMPLETED_INSTANCES;

/**
 * @author coco
 * @date 2020-07-26 15:36
 * 跳转到任意节点
 **/
public class JumpAnyActivityCmd extends NeedsActiveTaskCmd<Void> {


    private String userId;

    private String activityName;

    /**
     * 目标节点
     */
    private FlowNode targetNode;

    private Map<String,Object> param;



    public JumpAnyActivityCmd(String taskId,String userId,String activityName,FlowNode targetNode, Map<String,Object> param) {
        super(taskId);
        if (targetNode == null) {
            throw new ActivitiIllegalArgumentException("targetNode is null");
        }
        this.activityName = activityName;
        this.userId = userId;
        this.targetNode = targetNode;
        this.param = param;
    }

    public JumpAnyActivityCmd(String taskId,String userId,String activityName,FlowNode targetNode) {
        super(taskId);
        if (targetNode == null) {
            throw new ActivitiIllegalArgumentException("targetNode is null");
        }
        this.activityName = activityName;
        this.targetNode = targetNode;
        this.userId = userId;
    }

    public JumpAnyActivityCmd(String taskId,FlowNode targetNode) {
        super(taskId);
        if (targetNode == null) {
            throw new ActivitiIllegalArgumentException("targetNode is null");
        }
        this.targetNode = targetNode;
    }

    @Override
    protected Void execute(final CommandContext commandContext, TaskEntity currentTask) {
        //String processDefinitionId = currentTask.getProcessDefinitionId();
        String executionId = currentTask.getExecutionId();
        if (param != null) {
            commandContext.getProcessEngineConfiguration()
                    .getTaskService().setVariables(taskId,param);
        }

        BpmnModel bpmnModel = commandContext.getProcessEngineConfiguration().getRepositoryService().getBpmnModel(currentTask.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        UserTask userTask = (UserTask) process.getFlowElement(currentTask.getTaskDefinitionKey());


        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        if (StringUtils.isNotBlank(userId)) {
            taskEntityManager.changeTaskAssignee(currentTask,userId);
        }
        if (StringUtils.isNotBlank(activityName)) {
            currentTask.setName(activityName);
            commandContext.getHistoryManager().recordTaskNameChange(taskId,activityName);

            //taskEntityManager.update(currentTask,false);
        }

        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        FlowNode currentFlowElement = (FlowNode)executionEntity.getCurrentFlowElement();
        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(currentFlowElement.getOutgoingFlows());
        //清理活动方向
        currentFlowElement.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("jumpline");
        newSequenceFlow.setSourceFlowElement(currentFlowElement);
        newSequenceFlow.setSourceRef(currentFlowElement.getId());
        newSequenceFlow.setTargetRef(targetNode.getId());
        //指向撤回请求
        newSequenceFlow.setTargetFlowElement(targetNode);
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowElement.setOutgoingFlows(newSequenceFlowList);


        //ActivitiUtil.addComment(currentTask.getId(),currentTask.getProcessInstanceId(),"申请人员撤回其请求");
        //完成任务
        Object behavior = userTask.getBehavior();
        //判断是否是会签
        if ( behavior instanceof ParallelMultiInstanceBehavior || behavior instanceof SequentialMultiInstanceBehavior) {
            HistoricActivityInstanceQueryImpl historicActivityInstanceQuery = new HistoricActivityInstanceQueryImpl().processInstanceId(currentTask.getProcessInstanceId()).unfinished();
            List<HistoricActivityInstance> unfinishedActivityList = commandContext.getHistoricActivityInstanceEntityManager().findHistoricActivityInstancesByQueryCriteria(historicActivityInstanceQuery,null);


            if (!CollectionUtils.isEmpty(unfinishedActivityList)) {
                //当前完成的节点
                List<HistoricActivityInstanceEntity> updateCollect = new ArrayList<>();
                //当前节点未完成的其他节点删除掉
                List<HistoricActivityInstanceEntity> deleteCollect = new ArrayList<>();

                for (HistoricActivityInstance historicActivityInstance : unfinishedActivityList) {
                    HistoricActivityInstanceEntity entity = (HistoricActivityInstanceEntity) historicActivityInstance;
                    if (taskId.equals(entity.getTaskId())) {
                        updateCollect.add(entity);
                    } else {
                        deleteCollect.add(entity);
                    }
                }
                //当前需要更新完成时间的
        /*    if (!CollectionUtils.isEmpty(updateCollect)) {
                for (HistoricActivityInstanceEntity historicActivityInstanceEntity : updateCollect) {
                    Date date = new Date();
                    historicActivityInstanceEntity.setEndTime(date);
                    historicActivityInstanceEntity.setDurationInMillis(date.getTime() - historicActivityInstanceEntity.getStartTime().getTime());
                    commandContext.getHistoricActivityInstanceEntityManager().update(historicActivityInstanceEntity);
                }
            }*/
                RuntimeService runtimeService = commandContext.getProcessEngineConfiguration().getRuntimeService();
                ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(currentTask.getExecutionId()).singleResult();
                ExecutionEntityImpl parentNode = execution.getParent();
                int nrOfCompletedInstances = (int) runtimeService.getVariable(parentNode.getId(), NUMBER_OF_COMPLETED_INSTANCES);

                if (!CollectionUtils.isEmpty(deleteCollect)) {
                    for (HistoricActivityInstanceEntity historicActivityInstanceEntity : deleteCollect) {
/*                    ExecutionEntityImpl temp = (ExecutionEntityImpl)
                            runtimeService.createExecutionQuery().executionId(historicActivityInstanceEntity.getExecutionId()).singleResult();*/
                        commandContext.getHistoricActivityInstanceEntityManager().delete(historicActivityInstanceEntity);
                        commandContext.getHistoricTaskInstanceEntityManager().delete(historicActivityInstanceEntity.getTaskId());
                    }
                    runtimeService.setVariable(parentNode.getId(), NUMBER_OF_COMPLETED_INSTANCES, nrOfCompletedInstances + deleteCollect.size());
                }
            }
        }
        ProcThreadLocalUtil.setNodeJump(true);
        commandContext.getProcessEngineConfiguration().getTaskService().complete(currentTask.getId());
        //恢复原方向
        currentFlowElement.setOutgoingFlows(oriSequenceFlows);
        ProcThreadLocalUtil.clearNodeJumpThreadLocal();

        return null;
    }
}
