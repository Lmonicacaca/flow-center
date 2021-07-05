package com.flow.center;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;

@Slf4j
class FlowEngineApplicationTests {

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
        repositoryService.createDeployment().addClasspathResource("test/parallel.bpmn20.xml").deploy();
        log.info("Number of process definitions: " + repositoryService.createProcessDefinitionQuery().count());
    }

    /**
     * 创建流程实例
     */
    @Test
    void createProcess(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("a2002:1:107504");

        // Verify that we started a new process instance
        log.info("Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }





}
