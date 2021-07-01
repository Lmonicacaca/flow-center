package com.flow.center;

import com.alibaba.fastjson.JSONObject;
import com.flow.center.dubbo.ITaskRSV;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = FlowEngineApplication.class)
@Slf4j
public class TaskRSVTest {
    @Resource
    private ITaskRSV taskRSV;

    @Test
    void teskAssigneeQuery(){
        log.info(JSONObject.toJSONString(taskRSV.queryByAssignee("1","32501").getResult()));
    }
}
