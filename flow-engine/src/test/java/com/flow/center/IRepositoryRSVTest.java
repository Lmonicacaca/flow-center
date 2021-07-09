package com.flow.center;

import com.alibaba.fastjson.JSONObject;
import com.flow.center.dubbo.IRepositoryRSV;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = FlowEngineApplication.class)
@Slf4j
public class IRepositoryRSVTest {

    @Resource
    private IRepositoryRSV repositoryRSV;

    @Test
    void testAllSequequeFlowsId(){
        log.info(JSONObject.toJSONString(repositoryRSV.allSequequeFlowsId("2002",null)));
    }

    @Test
    void testGetModelVersion(){
        log.info(JSONObject.toJSONString(repositoryRSV.getModelVersion("5001",null)));
    }

    @Test
    void testCheckProcessDefinition(){
        log.info(JSONObject.toJSONString(repositoryRSV.checkProcessDefinition("3001",null)));
    }
}

