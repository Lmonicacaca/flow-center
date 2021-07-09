package com.flow.center.dubbo.impl;

import com.flow.center.dubbo.IRepositoryRSV;
import com.flow.center.util.ActivitiUtil;
import com.flow.center.util.FlowUtil;
import com.google.common.collect.Lists;
import com.yjcloud.idol.common.response.BaseResponse;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author coco
 * @date 2020-08-20 18:31
 **/
@Service
public class RepositoryRSVImpl implements IRepositoryRSV {

    @Resource
    private RepositoryService repositoryService;

    @Override
    public List<String> allSequequeFlowsId(String appId, Long courtId) {
        String appKey = FlowUtil.buildAppKey(appId, String.valueOf(courtId));
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(appKey).latestVersion().singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process mainProcess = bpmnModel.getMainProcess();
        List<SequenceFlow> sequenceFlows = mainProcess.findFlowElementsOfType(SequenceFlow.class);
        if (sequenceFlows != null) {
            return sequenceFlows.stream().map(SequenceFlow::getId).collect(Collectors.toList());
        } else {
            return Lists.newArrayList();
        }
    }

    @Override
    public Long getModelVersion(String appId, Long courtId) {
        String s = String.valueOf(courtId);
        String appKey = FlowUtil.buildAppKey(appId, s);
        if (StringUtils.isNotBlank(appKey) && StringUtils.isNotBlank(s)) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(appKey).latestVersion().singleResult();
            return Long.parseLong(String.valueOf(processDefinition.getVersion()));

        } else {
            return null;
        }
    }

    @Override
    public BaseResponse<Boolean> checkProcessDefinition(String appId, Long courtId) {

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("a" + appId).latestVersion().list();
        if (CollectionUtils.isEmpty(processDefinitions)) {
            return BaseResponse.buildBaseResponse().success(true).result(false);
        }
        return BaseResponse.buildBaseResponse().success(true).result(true);
    }

    @Override
    public BaseResponse<Map<String, String>> getUserTaskNodeNameMap(String appId, Long courtId) {
        if (StringUtils.isBlank(appId) || null == courtId) {
            return BaseResponse.buildErrorResponse().code(4000).message("param is null");
        }
        Map<String, String> userTaskNodeNameMap = ActivitiUtil.getUserTaskNodeNameMap(appId, courtId);
        return BaseResponse.buildSuccessResponse().result(userTaskNodeNameMap);
    }
}
