package com.flow.center.dubbo.impl;

import com.flow.center.dto.TaskDTO;
import com.flow.center.dubbo.ITaskRSV;
import com.flow.center.util.BeanUtil;
import com.yjcloud.idol.common.response.BaseResponse;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.task.Task;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskRSVImpl implements ITaskRSV {
    @Resource
    private TaskService taskService;
    @Override
    public BaseResponse<TaskDTO> queryByAssignee(String assignee, String processId) {
        List<Task> list = taskService.createTaskQuery().taskAssignee(assignee).processInstanceId(processId).list();
        if(CollectionUtil.isNotEmpty(list)){
            return BaseResponse.buildSuccessResponse().result(BeanUtil.convertObject(list.get(0),TaskDTO.class));
        }
        return BaseResponse.buildErrorResponse();
    }
}
