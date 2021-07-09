package com.flow.center.dubbo.impl;

import com.alibaba.fastjson.JSON;
import com.flow.center.cmd.*;
import com.flow.center.dubbo.ICommandRSV;
import com.flow.center.util.ActivitiUtil;
import com.google.common.collect.Maps;
import com.yjcloud.idol.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.cmd.yjcloud.AddMultiInstanceExecutionCmd;
import org.activiti.engine.impl.cmd.yjcloud.ChangeAssigneeCmd;
import org.activiti.engine.impl.cmd.yjcloud.CopyTaskCmd;
import org.activiti.engine.impl.cmd.yjcloud.DeleteMultiInstanceExecutionCmd;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.assertj.core.util.Lists;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author coco
 * @date 2020-08-06 14:26
 **/
@Service
@Slf4j
public class CommandRSVImpl implements ICommandRSV {

    @Resource
    private ManagementService managementService;
    @Resource
    private TaskService taskService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;


    @Override
    public BaseResponse withdrawCommand(String taskId, String userId, String reason) {
        try {
            if (StringUtils.isNotBlank(reason)) {
                taskService.addComment(taskId, reason);
            }
            WithdrawCmd withdrawCmd = new WithdrawCmd(taskId, userId);
            managementService.executeCommand(withdrawCmd);

        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("撤回成功!");
    }



    @Override
    public BaseResponse restartCommand(String taskId, String userId) {
        try {
            taskService.setAssignee(taskId, userId);
            RestartCmd restartCmd = new RestartCmd(taskId);
            managementService.executeCommand(restartCmd);
        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("重新发起成功!");
    }


    @Override
    public BaseResponse refuseCommand(String taskId, String reason) {

        try {
            if (StringUtils.isNotBlank(reason)) {
                taskService.addComment(taskId, reason);
            }
            RefuseCmd refuseCmd = new RefuseCmd(taskId);
            managementService.executeCommand(refuseCmd);

        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("拒绝成功!");
    }

    @Override
    public BaseResponse agreeCommand(String taskId, String reason) {

        try {
            if (StringUtils.isNotBlank(reason)) {
                taskService.addComment(taskId, reason);
            }
            AgreeTaskCmd agreeTaskCmd = new AgreeTaskCmd(taskId);
            managementService.executeCommand(agreeTaskCmd);
        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("审批成功!");
    }

    @Override
    public BaseResponse agreeCommand(String taskId, String reason, Map<String, Object> param) {
        try {
            if (StringUtils.isNotBlank(reason)) {
                taskService.addComment(taskId, reason);
            }
            if (Objects.isNull(param)) {
                param = Maps.newHashMap();
            }
            AgreeTaskCmd agreeTaskCmd = new AgreeTaskCmd(taskId, param, false);
            managementService.executeCommand(agreeTaskCmd);
        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("审批成功!");
    }


    @Override
    public BaseResponse copyTaskCommand(String taskId, String userId, String activityName) {

        Task task = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        if (Objects.isNull(task)) {
            return BaseResponse.buildErrorResponse().code(5000).message("task is no active");
        }
        UserTask userTask = (UserTask) ActivitiUtil.getFlowElement(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        Object behavior = userTask.getBehavior();
        if (behavior instanceof ParallelMultiInstanceBehavior || behavior instanceof SequentialMultiInstanceBehavior) {
            return BaseResponse.buildErrorResponse().code(5000).message("task is ParallelMultiInstanceBehavior or SequentialMultiInstanceBehavior ");
        }
        try {
            CopyTaskCmd copyTaskCmd = new CopyTaskCmd(taskId, userId, activityName);
            managementService.executeCommand(copyTaskCmd);
        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("临时增加用户任务成功!");
    }

    @Override
    public BaseResponse addActivityCommand(String taskId, List<String> assigneeList) {
        //判断当前任务是不是会签。不是会签的话，就调用copy方法
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        if (userTask.getLoopCharacteristics() == null) {
            log.info("task:[" + task.getId() + "] 不是会签任务，转为copyTaskCommand执行");
            return copyTaskCommand(taskId, assigneeList.get(0), task.getName());
        } else {
            try {
                AddMultiInstanceExecutionCmd addMultiInstanceExecutionCmd = new AddMultiInstanceExecutionCmd(taskId, assigneeList);
                managementService.executeCommand(addMultiInstanceExecutionCmd);
            } catch (Exception e) {
                log.error("加签成功命令发生异常：{}", e.getMessage(), e);
                return BaseResponse.buildErrorResponse().message("发生异常!");
            }
        }


        return BaseResponse.buildSuccessResponse().message("加签成功!");
    }

    @Override
    public BaseResponse removeActivityCommand(String taskId, List<String> assigneeList) {

        try {
            DeleteMultiInstanceExecutionCmd deleteMultiInstanceExecutionCmd = new DeleteMultiInstanceExecutionCmd(taskId, assigneeList);
            managementService.executeCommand(deleteMultiInstanceExecutionCmd);

        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("减签成功!");
    }

    @Override
    public BaseResponse jumpAnyActivityCommand(String taskId, String activityId) {

        List<Task> list = taskService.createTaskQuery().active().taskId(taskId).list();
        if (CollectionUtil.isEmpty(list)) {
            return BaseResponse.buildErrorResponse().code(5000).message("task is no active");
        }
        String processDefinitionId = list.get(0).getProcessDefinitionId();

        return executeJumpAnyActivityCmd(processDefinitionId, taskId, activityId);
    }

    @Override
    public BaseResponse jump2BeforeUserTaskCommand(String taskId, Long jumpCount) {
        List<Task> list = taskService.createTaskQuery().active().taskId(taskId).list();
        if (CollectionUtil.isEmpty(list)) {
            return BaseResponse.buildErrorResponse().code(5000).message("task is no active");
        }
        String procInstId = list.get(0).getProcessInstanceId();
        //获取历史的所有task的节点id
        List<HistoricTaskInstance> hisTaskInstList = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).orderByHistoricTaskInstanceStartTime().asc().list();

        List<String> hisTaskSidList = Lists.newArrayList();
        String recentNodeId = null;
        for (HistoricTaskInstance historicTaskInstance : hisTaskInstList) {
            String sid = historicTaskInstance.getTaskDefinitionKey();
            if (StringUtils.isBlank(recentNodeId)) {
                recentNodeId = sid;
                hisTaskSidList.add(recentNodeId);
            } else {
                if (!sid.equals(recentNodeId)) {
                    recentNodeId = sid;
                    hisTaskSidList.add(recentNodeId);
                }
            }
        }

        log.info("jump2BeforeUserTaskCommand hisTaskSidList:{}", JSON.toJSONString(hisTaskSidList));
        if (null == jumpCount) {
            jumpCount = 1L;
        }
        //获取要跳转的的节点下标
        int jumpIndex = (int) (hisTaskSidList.size() - jumpCount - 1);

        String processDefinitionId = list.get(0).getProcessDefinitionId();

        String jumpActivityId = hisTaskSidList.get(jumpIndex);
        //支持会签
        if (jumpIndex == 0) {
            jumpActivityId = recentNodeId;
        }
        return executeJumpAnyActivityCmd(processDefinitionId, taskId, jumpActivityId);
    }

    @Override
    public BaseResponse jump2BeforeUserTaskCommand(String taskId, String reason, Long jumpCount) {
        if (StringUtils.isNotBlank(reason)) {
            taskService.addComment(taskId, reason);
        }
        return jump2BeforeUserTaskCommand(taskId, jumpCount);
    }

    private BaseResponse executeJumpAnyActivityCmd(String processDefinitionId, String taskId, String jumpActivityId) {
        FlowElement flowElement = ActivitiUtil.getFlowElement(processDefinitionId, jumpActivityId);
        if (Objects.isNull(flowElement)) {
            return BaseResponse.buildErrorResponse().code(5000).message("activityId is no found");
        }
        RefuseCmd refuseCmd = new RefuseCmd(taskId, (FlowNode) flowElement);
        managementService.executeCommand(refuseCmd);
        return BaseResponse.buildSuccessResponse().message("节点跳转成功!");
    }


    @Override
    public BaseResponse changeAssginee(String taskId, String newUserId) {
        try {
            ChangeAssigneeCmd changeAssigneeCmd = new ChangeAssigneeCmd(taskId, newUserId);
            managementService.executeCommand(changeAssigneeCmd);
        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("移交审批成功!");
    }

    @Override
    public BaseResponse quickEndCommand(String taskId, String userId, String reason) {
        //1。换人
        try {
            if (StringUtils.isNotBlank(userId)) {
                ChangeAssigneeCmd changeAssigneeCmd = new ChangeAssigneeCmd(taskId, userId);
                managementService.executeCommand(changeAssigneeCmd);
            }
        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }

        //2。快速通过
        try {
            if (StringUtils.isNotBlank(reason)) {
                taskService.addComment(taskId, reason);
            }
            QuickEndCmd quickEndCmd = new QuickEndCmd(taskId);
            managementService.executeCommand(quickEndCmd);
        } catch (Exception e) {
            return BaseResponse.buildErrorResponse().message("发生异常!");
        }
        return BaseResponse.buildSuccessResponse().message("审批成功!");
    }

}
