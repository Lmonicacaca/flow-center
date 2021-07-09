package com.flow.center.dubbo.impl;


import com.flow.center.dto.CommentDTO;
import com.flow.center.dto.TaskDTO;
import com.flow.center.dubbo.ITaskRSV;
import com.flow.center.util.FlowUtil;
import com.yjcloud.idol.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti5.engine.impl.javax.el.ExpressionFactory;
import org.activiti5.engine.impl.javax.el.ValueExpression;
import org.activiti5.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti5.engine.impl.juel.SimpleContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskRSVImpl implements ITaskRSV {
    @Resource
    TaskService taskService;
    @Resource
    FlowUtil flowUtil;
    @Resource
    HistoryService historyService;

    @Override
    public BaseResponse<Long> getUnfinishTaskCountByAssignee(String assignee) {

        Long count = null;
        try {
            count = taskService.createTaskQuery().active().taskAssignee(assignee)
                    .count();
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
        return BaseResponse.buildSuccessResponse().result(count);
    }

    @Override
    public BaseResponse<TaskDTO> getTaskByProcessInstanceId(String processInstanceId) {

        try {
            List<Task> tasks = taskService.createTaskQuery().active().processInstanceId(processInstanceId).list();
            if (CollectionUtils.isEmpty(tasks)) {
                return BaseResponse.buildSuccessResponse();
            }
            TaskDTO taskDto = flowUtil.convertObject(tasks.get(0), TaskDTO.class);
            return BaseResponse.buildBaseResponse().success(true).result(taskDto);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<TaskDTO>> getTaskListByProcessInstanceId(String processInstanceId) {

        try {
            List<Task> tasks = taskService.createTaskQuery().active().processInstanceId(processInstanceId).list();
            if (CollectionUtils.isEmpty(tasks)) {
                return BaseResponse.buildSuccessResponse();
            }
            List<TaskDTO> taskDtos = tasks.stream().map(task -> {
                TaskDTO taskDto = new TaskDTO();
                BeanUtils.copyProperties(task, taskDto);
                return taskDto;
            }).collect(Collectors.toList());
            return BaseResponse.buildBaseResponse().success(true).result(taskDtos);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<TaskDTO> getTaskByQuery(String processInstanceId, Long userId, Long courtId, String key) {

        try {
            TaskQuery query = taskService.createTaskQuery().active();
            if (!StringUtils.isEmpty(processInstanceId)) {
                query.processInstanceId(processInstanceId);
            }
            if (!ObjectUtils.isEmpty(userId)) {
                query.taskAssignee(String.valueOf(userId));
            }
            if (!ObjectUtils.isEmpty(courtId)) {
                query.taskTenantId(String.valueOf(courtId));
            }
            if (!StringUtils.isEmpty(key)) {
                query.taskDefinitionKey(key);
            }
            Task task = query.singleResult();
            TaskDTO taskDto = flowUtil.convertObject(task, TaskDTO.class);
            return BaseResponse.buildBaseResponse().success(true).result(taskDto);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<CommentDTO>> getTaskComments(String taskId) {

        try {
            List<Comment> comments = taskService.getTaskComments(taskId);
            List<CommentDTO> commentDtoList = comments.stream().map(comment -> flowUtil.convertObject(comment, CommentDTO.class))
                    .collect(Collectors.toList());
            return BaseResponse.buildBaseResponse().success(true).result(commentDtoList);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse addComment(String taskId, String processInstanceId, String message) {

        try {
            taskService.addComment(taskId, "comment", message);
            return BaseResponse.buildBaseResponse().success(true);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse setVariable(String taskId, String variableName, Object value) {

        try {
            taskService.setVariable(taskId, variableName, value);
            return BaseResponse.buildBaseResponse().success(true);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<Object> getVariable(String taskId, String variableName) {

        try {
            Object Object = taskService.getVariable(taskId, variableName);
            return BaseResponse.buildBaseResponse().success(true).result(Object);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public <T> BaseResponse<T> getVariable(String taskId, String variableName, Class<T> clz) {

        try {
            T result = taskService.getVariable(taskId, variableName, clz);
            return BaseResponse.buildBaseResponse().success(true).result(result);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse setVariableLocal(String taskId, String variableName, Object value) {

        try {
            taskService.setVariableLocal(taskId, variableName, value);
            return BaseResponse.buildBaseResponse().success(true);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }


    @Override
    public BaseResponse setAssignee(String taskId, String userId) {

        try {
            taskService.setAssignee(taskId, userId);
            return BaseResponse.buildBaseResponse().success(true);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<TaskDTO>> getTaskList(Date startTime, Date endTime, String appId, List<String> processIds,
                                                   Long userId, Long courtId, int pageNum, int pageSize) {
        try {
            TaskQuery query = getQuery(startTime, endTime, appId, courtId, processIds, userId);
            List<Task> tasks = query.orderByTaskCreateTime().desc().listPage((pageNum - 1) * pageSize, pageSize);
            List<TaskDTO> taskDtos = tasks.stream().map(task -> flowUtil.convertObject(task, TaskDTO.class))
                    .collect(Collectors.toList());
            return BaseResponse.buildBaseResponse().success(true).result(taskDtos);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<Long> getTaskCount(Date startTime, Date endTime, String appId, List<String> processIds, Long userId,
                                           Long courtId) {
        try {
            TaskQuery query = getQuery(startTime, endTime, appId, courtId, processIds, userId);
            Long count = query.orderByTaskCreateTime().desc().count();
            return BaseResponse.buildBaseResponse().success(true).result(count);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<TaskDTO>> getTaskListByUser(List<String> processIds, Long userId, String appId) {
        try {
            TaskQuery query = taskService.createTaskQuery().active();
            if (!StringUtils.isEmpty(appId)) {
                query.processDefinitionKeyLike("a" + appId + "%");
            }
            if (!CollectionUtils.isEmpty(processIds)) {
                query.processInstanceIdIn(processIds);
            }
            if (!ObjectUtils.isEmpty(userId)) {
                query.taskAssignee(userId.toString());
            }
            List<Task> tasks = query.orderByTaskCreateTime().desc().list();
            List<TaskDTO> taskDtos = tasks.stream().map(task -> flowUtil.convertObject(task, TaskDTO.class))
                    .collect(Collectors.toList());
            return BaseResponse.buildBaseResponse().success(true).result(taskDtos);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<TaskDTO>> getTasksByProcessInstanceId(String processInstanceId) {
        if (StringUtils.isEmpty(processInstanceId)) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message("processInstanceId is null");
        }
        try {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            List<TaskDTO> taskDtos = tasks.stream().map(task -> flowUtil.convertObject(task, TaskDTO.class))
                    .collect(Collectors.toList());
            return BaseResponse.buildBaseResponse().success(true).result(taskDtos);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }

    @Override
    public BaseResponse<TaskDTO> getTaskById(String taskId) {
        if (StringUtils.isEmpty(taskId)) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message("taskId is null");
        }
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            TaskDTO taskDto = flowUtil.convertObject(task, TaskDTO.class);
            return BaseResponse.buildBaseResponse().success(true).result(taskDto);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }

    }

    @Override
    public BaseResponse<Boolean> checkUserIfHasActiveTask(String processInstanceId, Long userId) {
        if (StringUtils.isEmpty(processInstanceId) || null == userId) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message("taskId is null");
        }
        try {
            TaskQuery query = taskService.createTaskQuery().active().processInstanceId(processInstanceId).taskAssignee(userId.toString());
            boolean flag = query.count() > 0 ? true : false;
            return BaseResponse.buildSuccessResponse().result(flag);
        } catch (Exception e) {
            return BaseResponse.buildBaseResponse().success(false).code(4000).message(e.getMessage());
        }
    }



    @Override
    public BaseResponse<Boolean> checkAssignee(String businessKey, String userId) {

        // 根据业务key取流程实例id
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(historicProcessInstance) || !"approvaling".equals(historicProcessInstance.getApprovalStatus())) {
            return BaseResponse.buildErrorResponse().code(4000).message("流程不存在或已经审批完成");
        }
        String processInstanceId = historicProcessInstance.getId();
        long count = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(userId).count();
        if (count > 0) {
            return BaseResponse.buildSuccessResponse().result(true);
        }

        return BaseResponse.buildSuccessResponse().result(false);
    }

    @Override
    public BaseResponse<Map<String, Boolean>> checkAssignees(Map<String, String> checkMap) {

        Map<String, Boolean> result = new LinkedHashMap<>();

        for (String keyPar : checkMap.keySet()) {
            String valuePar = checkMap.get(keyPar);
            BaseResponse<Boolean> valueRsv = checkAssignee(keyPar, valuePar);
            Boolean value = false;
            if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(valueRsv) && valueRsv.getSuccess()) {
                value = valueRsv.getResult();
            }
            result.put(keyPar, value);
        }
        return BaseResponse.buildSuccessResponse().result(result);
    }

    @Override
    public BaseResponse<Map<String,TaskDTO>> getTaskByBusinessKeys(List<String> businessKeys) {

        Map<String, TaskDTO> result = new LinkedHashMap<>();

        businessKeys.stream().forEach(businessKey -> {
            if (StringUtils.isEmpty(businessKey)) {
                return;
            }

            // 根据业务key取流程实例id
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
            if (ObjectUtils.isEmpty(historicProcessInstance) || !"approvaling".equals(historicProcessInstance.getApprovalStatus())) {
                log.info("该流程不存在或已经审批完成。");
                return;
            }
            String processInstanceId = historicProcessInstance.getId();
            List<Task> tasks = taskService.createTaskQuery().active().processInstanceId(processInstanceId).list();
            if (CollectionUtils.isEmpty(tasks)) {
                return;
            }
            TaskDTO taskDto = flowUtil.convertObject(tasks.get(0), TaskDTO.class);
            result.put(businessKey, taskDto);
        });

        return BaseResponse.buildSuccessResponse().result(result);
    }

    public static Boolean checkFormDataByRuleEl(String el, Map<String, Object> formData) {
        try {
            ExpressionFactory factory = new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            for (Object k : formData.keySet()) {
                if (formData.get(k) != null) {
                    //
                    context.setVariable(k.toString(), factory.createValueExpression(formData.get(k), formData.get(k).getClass()));
                }
            }
            ValueExpression e = factory.createValueExpression(context, el, Boolean.class);
            return (Boolean) e.getValue(context);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通用query对象构造
     *
     * @param startTime
     * @param endTime
     * @param appId
     * @param courtId
     * @param processIds
     * @param userId
     * @return
     */
    private TaskQuery getQuery(Date startTime, Date endTime, String appId, Long courtId, List<String> processIds,
                               Long userId) {
        TaskQuery query = taskService.createTaskQuery().active();
        if (endTime != null && startTime != null) {
            query.taskCreatedAfter(startTime).taskCreatedBefore(endTime);
        } else if (startTime == null && endTime != null) {
            query.taskCreatedBefore(endTime);

        } else if (startTime != null && endTime == null) {
            query.taskCreatedAfter(startTime);
        }
        if (!StringUtils.isEmpty(appId)) {
            query.processDefinitionKey("a" + appId + "-" + courtId);
        }
        if (!ObjectUtils.isEmpty(userId)) {
            query.taskAssignee(String.valueOf(userId));
        }
        if (!CollectionUtils.isEmpty(processIds)) {
            query.processInstanceIdIn(processIds);
        }
        return query;
    }
}
