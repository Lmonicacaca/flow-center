package org.activiti.engine.impl.cmd.yjcloud;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author coco
 * 进行会签减签
 * @date 2020-07-03 11:47
 **/
public class DeleteMultiInstanceExecutionCmd implements Command<String>, CountersigningVariables {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteMultiInstanceExecutionCmd.class);
    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * 审核人
     */
    private List<String> assigneeList;

    public DeleteMultiInstanceExecutionCmd(String taskId, List<String> assigneeList) {

        super();

        if (ObjectUtils.isEmpty(assigneeList)) {
            throw new RuntimeException("assigneeList 不能为空!");
        }

        this.taskId = taskId;
        this.assigneeList = assigneeList;
    }

    @Override
    public String execute(CommandContext commandContext) {

        RepositoryService repositoryService = commandContext.getProcessEngineConfiguration().getRepositoryService();
        TaskService taskService = commandContext.getProcessEngineConfiguration().getTaskService();
        RuntimeService runtimeService = commandContext.getProcessEngineConfiguration().getRuntimeService();

        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);

        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());

        if (userTask.getLoopCharacteristics() == null) {
            // TODO
            LOG.error("task:[" + task.getId() + "] 不是会签节任务");
        }

        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        ExecutionEntityImpl parentNode = execution.getParent();

        /**
         *  获取任务完成数
         */
        int nrOfCompletedInstances = (int) runtimeService.getVariable(parentNode.getId(), NUMBER_OF_COMPLETED_INSTANCES);

        /**
         *  转换判断标识
         */
        Set<String> assigneeSet = new HashSet<>(assigneeList);
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

        Object behavior = userTask.getBehavior();
        /**
         *  进行并行任务 减签
         */
        if (behavior instanceof ParallelMultiInstanceBehavior) {

            LOG.info("task:[" + task.getId() + "] 并行会签 减签 任务");

            /**
             *  当前任务列表
             */
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstance().getProcessInstanceId()).list();

            List<Task> removeTaskList = new ArrayList<>(assigneeSet.size());
            List<Task> existTaskList = new ArrayList<>(taskList.size() - assigneeSet.size());


            for (Task obj : taskList) {
                if (assigneeSet.contains(obj.getAssignee())) {
                    removeTaskList.add(obj);
                    ExecutionEntityImpl temp = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(obj.getExecutionId()).singleResult();
                    executionEntityManager.deleteExecutionAndRelatedData(temp, "会签减签", true);

                } else {
                    existTaskList.add(obj);
                }
            }

            /**
             *  修改已完成任务变量,增加被删减任务
             */
            runtimeService.setVariable(parentNode.getId(), NUMBER_OF_COMPLETED_INSTANCES, nrOfCompletedInstances + removeTaskList.size());

        } else if (behavior instanceof SequentialMultiInstanceBehavior) {
            LOG.info("task:[" + task.getId() + "] 串行会签 减签 任务");

            Object obj = parentNode.getVariable(ASSIGNEE_LIST);
            if (obj == null || !(obj instanceof ArrayList)) {
                throw new RuntimeException("没有找到任务执行人列表");
            }


            ArrayList<String> sourceAssigneeList = (ArrayList) obj;
            List<String> newAssigneeList = new ArrayList<>();
            boolean flag = false;
            int loopCounterIndex = -1;
            String newAssignee = "";
            for (String temp : sourceAssigneeList) {
                if (!assigneeSet.contains(temp)) {
                    newAssigneeList.add(temp);
                }

                if (flag) {
                    newAssignee = temp;
                    flag = false;
                }

                if (temp.equals(task.getAssignee())) {

                    if (assigneeSet.contains(temp)) {
                        flag = true;
                        loopCounterIndex = newAssigneeList.size();
                    } else {
                        loopCounterIndex = newAssigneeList.size() - 1;
                    }
                }
            }

            /**
             *  修改计数器变量
             */
            Map<String, Object> variables = new HashMap<>();
            variables.put(NUMBER_OF_INSTANCES, newAssigneeList.size());
            variables.put(NUMBER_OF_COMPLETED_INSTANCES, loopCounterIndex > 0 ? loopCounterIndex - 1 : 0);
            variables.put(ASSIGNEE_LIST, newAssigneeList);
            runtimeService.setVariables(parentNode.getId(), variables);

            /**
             *  当前任务需要被删除，需要替换下一个任务审批人
             */
            if (!StringUtils.isEmpty(newAssignee)) {
                taskService.setAssignee(taskId, newAssignee);
                execution.setVariable(LOOP_COUNTER, loopCounterIndex);
                execution.setVariable(ASSIGNEE_USER, newAssignee);
            }
        }
        return "减签成功";
    }
}
