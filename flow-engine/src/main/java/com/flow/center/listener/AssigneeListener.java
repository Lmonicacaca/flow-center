package com.flow.center.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("assigneeListener")
@Slf4j
public class AssigneeListener implements TaskListener {
    @Resource
    private RuntimeService runtimeService;
    @Override
    public void notify(DelegateTask delegateTask) {
        Map<String,Object> params = new HashMap<>();
        params.put("assignee","张三");
        runtimeService.setVariables(delegateTask.getExecutionId(),params);

    }
}
