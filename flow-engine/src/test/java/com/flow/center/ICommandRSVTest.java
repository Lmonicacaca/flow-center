package com.flow.center;

import com.flow.center.dubbo.ICommandRSV;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = FlowEngineApplication.class)
public class ICommandRSVTest {

    @Resource
    private ICommandRSV commandRSV;

    @Test
    void testWithdraw(){
        //撤回
        commandRSV.withdrawCommand("292505","","");
    }
    @Test
    void testRestart(){
        //重新发起
        commandRSV.restartCommand("292505","tom");
    }

    @Test
    void testAgree(){
        //同意
        commandRSV.agreeCommand("312505","同意");
    }


    @Test
    void testRefuse(){
        //拒绝
        commandRSV.refuseCommand("300003","不行");
    }

    @Test
    void testCopyTaskCommand(){
        //串行加签
        commandRSV.copyTaskCommand("302503","tom","临时节点");
    }


    @Test
    void testAddActivityCommand(){
        //会签加签，此命令只进行了加签，节点的通过需要单独再调接口
        commandRSV.addActivityCommand("315013", Lists.newArrayList("tom"));
    }


    @Test
    void testChangeAssginee(){
        //修改任务的审批人
        commandRSV.changeAssginee("315013","tom");
    }


    @Test
    void testQuickEndCommand(){
        commandRSV.quickEndCommand("315013","tome","");
    }
}
