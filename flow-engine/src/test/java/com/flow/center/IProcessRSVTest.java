package com.flow.center;

import com.flow.center.dubbo.IProcessRSV;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = FlowEngineApplication.class)
public class IProcessRSVTest {
    @Resource
    private IProcessRSV processRSV;

    @Test
    void testStartProcess(){
        //发起流程
        processRSV.startProcess("a5001:4:282504");

    }
}
