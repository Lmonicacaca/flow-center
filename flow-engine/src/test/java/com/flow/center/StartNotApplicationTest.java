package com.flow.center;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StartNotApplicationTest {
    @Test
    void doTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId("32501").list();
        Task task = list.get(0);
        log.info(task.getName());
        Map<String,Object> params= new HashMap<>();
        params.put("day",4);
        taskService.complete(task.getId(),params);

    }

}
