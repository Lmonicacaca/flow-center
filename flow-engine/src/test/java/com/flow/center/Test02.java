package com.flow.center;

import com.flow.center.listener.Notice;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = FlowEngineApplication.class)
@Slf4j
public class Test02 {
    /**
     * 创建流程实例
     */
    @Test
    void createProcess(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById("expressProcess:1:2507");

        // Verify that we started a new process instance
        log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }

    @Test
    void doTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId("15001").list();
        Task task = list.get(0);
        log.info(task.getName());
        taskService.complete(task.getId());

    }

}
