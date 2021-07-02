package com.flow.center;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SpringBootTest(classes = FlowEngineApplication.class)
@Slf4j
public class StartNotApplicationTest {
    @Test
    void doTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId("102501").list();
        Task task = list.get(0);
        log.info(task.getName());
        taskService.complete(task.getId());

    }

}
