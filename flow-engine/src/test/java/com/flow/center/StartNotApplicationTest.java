package com.flow.center;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class StartNotApplicationTest {
    @Test
    void doTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId("10001").list();
        Task task = list.get(0);
        log.info(task.getName());
        taskService.complete(task.getId());

    }

}
