package org.activiti.engine.impl.cmd.yjcloud;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author coco
 *  会签 加签
 * @date 2020-07-03 11:28
 **/
public class AddMultiInstanceExecutionCmd implements Command<String>,CountersigningVariables {

    private static final Logger LOG = LoggerFactory.getLogger(AddMultiInstanceExecutionCmd.class);

    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * 审核人
     */
    private List<String> assigneeList;

    /**
     * 任务执行人
     */
    private String assignee;

    public AddMultiInstanceExecutionCmd(String taskId, List<String> assigneeList) {

        super();

        if (ObjectUtils.isEmpty(assigneeList)) {
            throw new RuntimeException("assigneeList 不能为空!");
        }

        this.taskId = taskId;
        this.assigneeList = assigneeList;
    }

    public AddMultiInstanceExecutionCmd(String taskId, List<String> assigneeList, String assignee) {

        super();

        if (ObjectUtils.isEmpty(assigneeList)) {
            throw new RuntimeException("assigneeList 不能为空!");
        }

        this.taskId = taskId;
        this.assigneeList = assigneeList;
        this.assignee = assignee;
    }

    @Override
    public String execute(CommandContext commandContext) {

        RepositoryService repositoryService = commandContext.getProcessEngineConfiguration().getRepositoryService();
        TaskService taskService = commandContext.getProcessEngineConfiguration().getTaskService();
        RuntimeService runtimeService = commandContext.getProcessEngineConfiguration().getRuntimeService();

        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        if (userTask.getLoopCharacteristics() == null) {
            LOG.error("task:[" + task.getId() + "] 不是会签节任务");
        }

        /**
         *  获取父级
         */
        ExecutionEntityImpl parentNode = execution.getParent();

        /**
         *  获取流程变量
         */
        int nrOfInstances = (int) runtimeService.getVariable(parentNode.getId(), NUMBER_OF_INSTANCES);
        int nrOfActiveInstances = (int) runtimeService.getVariable(parentNode.getId(), NUMBER_OF_ACTIVE_INSTANCES);

        /**
         *  获取管理器
         */
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

        Object behavior = userTask.getBehavior();
        if (behavior instanceof ParallelMultiInstanceBehavior) {
            LOG.info("task:[" + task.getId() + "] 并行会签 加签 任务");
            /**
             *  设置循环标志变量
             */
            runtimeService.setVariable(parentNode.getId(), NUMBER_OF_INSTANCES, nrOfInstances + assigneeList.size());
            runtimeService.setVariable(parentNode.getId(), NUMBER_OF_ACTIVE_INSTANCES, nrOfActiveInstances + assigneeList.size());

            /**
             *  新建任务列表
             */
            for (String assignee : this.assigneeList) {

                /**
                 *  创建 子 execution
                 */
                ExecutionEntity newExecution = executionEntityManager.createChildExecution(parentNode);

                newExecution.setActive(true);
                newExecution.setVariableLocal(LOOP_COUNTER, nrOfInstances);
                newExecution.setVariableLocal(ASSIGNEE_USER, assignee);
                newExecution.setCurrentFlowElement(userTask);

                /**
                 * 任务总数 +1
                 */
                nrOfInstances++;

                /**
                 * 推入时间表序列
                 */
                Context.getAgenda().planContinueMultiInstanceOperation(newExecution);
            }
        } else if (behavior instanceof SequentialMultiInstanceBehavior) {
            LOG.info("task:[" + task.getId() + "] 串行会签 加签 任务");
            /**
             *  是否需要替换审批人
             */
            boolean changeAssignee = false;
            if (StringUtils.isEmpty(assignee)) {
                assignee = task.getAssignee();
                changeAssignee = true;
            }
            /**
             *  当前任务执行位置
             */
            int loopCounterIndex = -1;

            for (int i = 0; i < assigneeList.size(); i++) {

                String temp = assigneeList.get(i);
                if (assignee.equals(temp)) {
                    loopCounterIndex = i;
                }
            }

            if (loopCounterIndex == -1) {
                throw new RuntimeException("任务审批人不存在于任务执行人列表中");
            }

            /**
             *  修改当前任务执行人
             */
            if (changeAssignee) {
                taskService.setAssignee(taskId, assignee);
                execution.setVariableLocal(ASSIGNEE_USER, assignee);
            }

            /**
             *  修改 计数器位置
             */
            execution.setVariableLocal(LOOP_COUNTER, loopCounterIndex);

            /**
             *  修改全局变量
             */
            Map<String, Object> variables = new HashMap<>(3);
            variables.put(NUMBER_OF_INSTANCES, assigneeList.size());
            variables.put(NUMBER_OF_COMPLETED_INSTANCES, loopCounterIndex);
            variables.put(ASSIGNEE_LIST, assigneeList);

            runtimeService.setVariables(parentNode.getId(), variables);
        }

        return "加签成功";
    }
}
