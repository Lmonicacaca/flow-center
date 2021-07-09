package com.flow.center;

import com.flow.center.dubbo.IActivitiRSV;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = FlowEngineApplication.class)
public class IActivitiRSVTest {

    @Resource
    private IActivitiRSV activitiRSV;


    @Test
    void testFindFirstUserTask(){
        /**
         * 根据部署id查询流程的第一个用户节点
         */
        activitiRSV.getFirstUserTaskNodeId("a5001:4:282504");
    }
}
