package com.flow.center;

import com.alibaba.fastjson.JSONObject;
import com.flow.center.dubbo.ICommandRSV;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = FlowEngineApplication.class)
@Slf4j
class FlowEngineApplicationTests {
    @Reference
    private ICommandRSV commandRSV;

    /**
     * 初始化表结构
     */
    @Test
    void contextLoads() {
        // 引擎配置
        ProcessEngineConfiguration pec=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        // 创建流程引擎对象
        pec.buildProcessEngine();

    }


    /**
     * 部署流程
     */
    @Test
    void deployProcess(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        repositoryService.createDeployment().addClasspathResource("processes/test.bpmn20.xml").deploy();
        log.info("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
    }

    /**
     * 创建流程实例
     */
    @Test
    void createProcess(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("invoiceProcess");

        // Verify that we started a new process instance
        log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }

    @Test
    void doTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().processInstanceId("10001").list();
        Task task = list.get(0);
        log.info(task.getName());
        taskService.complete(task.getId());

    }

    @Test
    void createProcessByMsg(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("newInvoiceMessage");

        // Verify that we started a new process instance
        log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }


    @Test
    void createProcessByVar(){
        Map<String, Object> variables = new HashMap<>();
        variables.put("myVar", "listening!");
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("executionListenersProcess", variables);

        Object varSetByListener = runtimeService.getVariable(processInstance.getId(), "var");

        // Result is a concatenation of fixed injected field and injected expression
        log.info(String.valueOf(varSetByListener));
    }


    @Test
    void testCreateProcess(){
        System.out.println(JSONObject.toJSONString(commandRSV.startProcess("a1001:1:2504")));
    }

}
