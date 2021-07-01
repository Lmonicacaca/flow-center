package com.flow.center.dubbo;

import com.flow.center.dto.TaskDTO;
import com.yjcloud.idol.common.response.BaseResponse;

public interface ITaskRSV {
    BaseResponse<TaskDTO> queryByAssignee(String assignee, String processId);
}
