package com.flow.center.dubbo;


import com.flow.center.dto.CommentDTO;
import com.flow.center.dto.TaskDTO;
import com.yjcloud.idol.common.response.BaseResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITaskRSV {


    /**
     * 获取用户代办数量
     * taskStatus = "approvaling"
     * @param assignee
     * @return
     */
    BaseResponse<Long> getUnfinishTaskCountByAssignee(String assignee);

    /**
     * 通过processInstanceId获取task，只返回第一条数据
     * @param processInstanceId
     * @return
     */
    BaseResponse<TaskDTO> getTaskByProcessInstanceId(String processInstanceId);

    /**
     * 通过processInstanceId获取task,返回ru_task中所有数据
     * @param processInstanceId
     * @return
     */
    BaseResponse<List<TaskDTO>> getTaskListByProcessInstanceId(String processInstanceId);

    /**
     * 通过条件查询获取task
     * @param processInstanceId
     * @param userId
     * @param courtId
     * @param key
     * @return
     */
    BaseResponse<TaskDTO> getTaskByQuery(String processInstanceId, Long userId, Long courtId, String key);


    /**
     * 获取task的comment
     * @param taskId
     * @return
     */
    BaseResponse<List<CommentDTO>> getTaskComments(String taskId);

    /**
     * 新增comment
     * @param taskId
     * @param processInstanceId
     * @param message
     */
    BaseResponse addComment(String taskId, String processInstanceId, String message);

    /**
     * 设置variable
     * @param taskId
     * @param variableName
     * @param value
     */
    BaseResponse setVariable(String taskId, String variableName, Object value);


    /**
     * 获取variable
     * @param taskId
     * @param variableName
     * @return
     */
    BaseResponse<Object> getVariable(String taskId, String variableName);

    /**
     * 获取variable
     * @param taskId
     * @param variableName
     * @param clz
     * @param <T>
     * @return
     */
    <T> BaseResponse<T> getVariable(String taskId, String variableName, Class<T> clz);

    /**
     * 设置variableLocal
     * @param taskId
     * @param variableName
     * @param value
     */
    BaseResponse setVariableLocal(String taskId, String variableName, Object value);

    /**
     * 设置assignee
     * @param taskId
     * @param userId
     */
    BaseResponse setAssignee(String taskId, String userId);

    /**
     * 通过条件获取tasks
     * @param startTime
     * @param endTime
     * @param appId
     * @param processIds
     * @param userId
     * @param courtId
     * @param pageNum
     * @param pageSize
     * @return
     */
    BaseResponse<List<TaskDTO>> getTaskList(Date startTime, Date endTime, String appId, List<String> processIds,
                                            Long userId, Long courtId, int pageNum, int pageSize);

    /**
     * 通过条件获取count
     * @param startTime
     * @param endTime
     * @param appId
     * @param processIds
     * @param userId
     * @param courtId
     * @return
     */
    BaseResponse<Long> getTaskCount(Date startTime, Date endTime, String appId, List<String> processIds, Long userId, Long courtId);

    /**
     * 获取当前用户tasks，active
     * @param processIds
     * @param userId
     * @param appId
     * @return
     */
    BaseResponse<List<TaskDTO>> getTaskListByUser(List<String> processIds, Long userId, String appId);

    /**
     * 通过processInstanceId获取tasks
     * @param processInstanceId
     * @return
     */
    BaseResponse<List<TaskDTO>> getTasksByProcessInstanceId(String processInstanceId);

    /**
     * 通过taskId获取task
     * @param taskId
     * @return
     */
    BaseResponse<TaskDTO> getTaskById(String taskId);


    /**
     * 判断当前user是否有待办任务
     * @param processInstanceId
     * @param userId
     * @return
     */
    BaseResponse<Boolean> checkUserIfHasActiveTask(String processInstanceId, Long userId);

    /**
     *  是否是当前审批人
     * @param businessKey
     * @since pma-1.0
     * @return true：是，false：不是
     */
    BaseResponse<Boolean> checkAssignee(String businessKey, String userId);

    /**
     *  是否是当前审批人
     * @param checkMap key:businessKey, value:userId
     * @since pma-1.0
     * @return Map<String, Boolean></> key：businessKey value: (true：是，false：否)
     */
    BaseResponse<Map<String, Boolean>> checkAssignees(Map<String, String> checkMap);

    /**
     * 通过业务主键获取当前活动的task，只返回第一条数据
     * @param businessKeys
     * @since safety-1.0
     * @return Map<String, TaskDto></> key：businessKey value: 当前活动的第一条task
     */
    BaseResponse<Map<String,TaskDTO>> getTaskByBusinessKeys(List<String> businessKeys);

    
}
