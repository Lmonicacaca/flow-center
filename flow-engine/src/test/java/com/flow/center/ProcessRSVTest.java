package com.flow.center;

import com.alibaba.fastjson.JSONObject;
import com.flow.center.dubbo.IProcessRSV;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = FlowEngineApplication.class)
@Slf4j
public class ProcessRSVTest {
    @Reference
    private IProcessRSV processRSV;

    @Test
    void testCreateProcess(){
        System.out.println(JSONObject.toJSONString(processRSV.startProcess("a1002:1:30004")));
    }
}
