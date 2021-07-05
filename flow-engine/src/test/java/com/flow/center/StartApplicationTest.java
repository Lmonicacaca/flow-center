package com.flow.center;

import com.flow.center.dubbo.IProcessRSV;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(classes = FlowEngineApplication.class)
@Slf4j
public class StartApplicationTest {
    @Resource
    private IProcessRSV processRSV;
    @Resource
    private TaskService taskService;

    @Test
    void doTask(){
        List<Task> list = taskService.createTaskQuery().processInstanceId("125001").list();
        Task task = list.get(0);
        log.info(task.getName());
        taskService.complete(task.getId());

    }



    @Test
    void withdraw(){
        List<Task> list = taskService.createTaskQuery().processInstanceId("125001").list();
        Task task = list.get(0);
        processRSV.withdraw(task.getId(),"","");
    }

}
