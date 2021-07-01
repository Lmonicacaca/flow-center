package com.flow.center.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Component
@Slf4j
public class Notice implements Serializable {
    public void callManager(DelegateTask task) {
        String assignee = (String)task.getVariable("manager");
        log.info(assignee + " 有一个新的待办");
    }

    public void callEmployee(DelegateTask task) {
        String assignee = (String)task.getVariable("employee");
        log.info(assignee + " 有一个新的待办");
    }


    public void callComplete(DelegateExecution execution) {
        log.info(execution.getProcessDefinitionId() + " 流程完成");
    }
}
