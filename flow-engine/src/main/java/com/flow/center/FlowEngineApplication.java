package com.flow.center;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.activiti.engine","com.flow.center"})
@EnableNacosConfig
@EnableDubbo
public class FlowEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowEngineApplication.class, args);
    }

}
