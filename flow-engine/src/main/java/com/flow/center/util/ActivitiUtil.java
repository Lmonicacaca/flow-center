package com.flow.center.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti5.engine.impl.javax.el.ExpressionFactory;
import org.activiti5.engine.impl.javax.el.ValueExpression;
import org.activiti5.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti5.engine.impl.juel.SimpleContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author coco
 * @date 2020-06-10 17:32
 **/
@Component
@Slf4j
public class ActivitiUtil implements ApplicationContextAware {

    private static RepositoryService repositoryService;
    private static RuntimeService runtimeService;
    private static TaskService taskService;
    private static HistoryService historyService;
    private static ProcessEngineConfigurationImpl processEngineConfiguration;

    public static ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        return processEngineConfiguration;
    }

    public static FlowNode findFlowNodeById(String appId, Long courtId, String id) {
        Process process = getProcess(appId, courtId);
        if (process != null) {
            return (FlowNode) process.getFlowElement(id);
        }
        return null;
    }

    public static void setTaskVariable(String taskId, String variableName, Object value) {
        taskService.setVariable(taskId, variableName, value);
    }

    public static Object getTaskVariable(String taskId, String variableName) {
        return taskService.getVariable(taskId, variableName);
    }

    public static <T> T getTaskVariable(String taskId, String variableName, Class<T> clz) {
        return taskService.getVariable(taskId, variableName, clz);
    }


    public static <T> T getVariable(String executeId, String variableName, Class<T> clz) {
        return runtimeService.getVariable(executeId, variableName, clz);
    }

    public static Object getVariable(String executeId, String variableName) {
        return runtimeService.getVariable(executeId, variableName);
    }


    public static void setVariableLocal(String taskId, String variableName, Object value) {
        taskService.setVariableLocal(taskId, variableName, value);
    }

    public static void addComment(String taskId, String proccessInstanceId, String reason) {
        taskService.addComment(taskId, proccessInstanceId, reason);
    }

    /**
     * 完成任务
     *
     * @param taskId
     * @return
     */
    @Deprecated
    public static void complete(String taskId, Map<String, Object> map) {
        //完成任务
        taskService.complete(taskId, map);
    }

    /**
     * 完成任务
     *
     * @param taskId
     * @return
     */
    @Deprecated
    public static void complete(String taskId) {
        //完成任务
        taskService.complete(taskId);
    }


    /**
     * 获取开始节点，圆圈圈那个
     *
     * @param processDefinitionId
     * @return
     */
    public static FlowNode findFirstActivity(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process mainProcess = bpmnModel.getMainProcess();
        return (FlowNode) mainProcess.getInitialFlowElement();
    }


    /**
     * 获取开始节点，圆圈圈那个
     *
     * @param flowId
     * @param tenantId
     * @return
     */
    public static FlowElement findFirstActivity(String flowId, Long tenantId) {
        Process process = getProcess(flowId, tenantId);
        return process.getInitialFlowElement();
    }

    /**
     * 必须是用户节点usertask
     *
     * @param processDefinitionId
     * @param activity
     * @return
     */
    public static List<String> findNextActivityId(String processDefinitionId, String activity) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process mainProcess = bpmnModel.getMainProcess();
        FlowElement flowElement = mainProcess.getFlowElement(activity);
        if (Objects.nonNull(flowElement)) {
            if (flowElement instanceof UserTask) {
                FlowNode flowElement1 = (FlowNode) flowElement;
                List<SequenceFlow> outgoingFlows = flowElement1.getOutgoingFlows();
                if (!CollectionUtils.isEmpty(outgoingFlows)) {
                    return outgoingFlows.stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
                }
            }

        }
        return Lists.newArrayList();
    }

    /**
     * 找到发起人节点
     *
     * @param processDefinitionId
     * @return
     */
    public static FlowNode findFirstUserTask(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process mainProcess = bpmnModel.getMainProcess();

        return findFirstUserTaskNode(mainProcess);
    }


    /**
     * 找到当前任务节点的上一个节点信息
     *
     * @param processDefinitionId
     * @param
     * @return
     */
    public static FlowNode findIncomingUserTask(String processDefinitionId, String taskId, String activityId) {

        FlowNode targetActivity = null;

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //查找上一个user task节点
        /*List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId())
                .finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();*/

        while (true) {
            Process mainProcess = bpmnModel.getMainProcess();
            //根据活动节点获取当前的组件信
            FlowNode previousActivityNode = (FlowNode) mainProcess.getFlowElement(activityId);

            if (previousActivityNode.getIncomingFlows().size() != 1) {
                throw new IllegalStateException(
                        "previous activity incoming transitions cannot more than 1, now is : "
                                + previousActivityNode.getIncomingFlows().size());
            }

            //获取该流程组件的之前的组件信息
            //sequenceFlowListIncoming数量可能大于1,可以自己做判断,此处只取第一个
            SequenceFlow sequenceFlowListIncoming = previousActivityNode.getIncomingFlows()
                    .get(0);
            //获取的下个节点不一定是userTask的任务节点，所以要判断是否是任务节点
            targetActivity = (FlowNode) sequenceFlowListIncoming.getTargetFlowElement();

            if (!(targetActivity instanceof UserTask)) {
                log.info("previous incoming activity is not userTask, just skip");
                //上一节点不是任务userTask的任务节点,所以要获取再上一个节点的信息,直到获取到userTask任务节点信息
                String flowElementId = targetActivity.getId();
                activityId = flowElementId;
                continue;
            } else {
                break;
            }
        }

        return targetActivity;
    }


    /**
     * 获取上一环节节点信息
     * @param taskDefinitionKey
     * @param processDefinitionId
     * @return
     */
    public static FlowNode getPreOneIncomeNode(String taskDefinitionKey, String processDefinitionId) {
        final List<FlowNode> preNodes = new ArrayList<>();
        getIncomeNodesRecur(taskDefinitionKey, processDefinitionId, preNodes, false);
        /*preNodes.forEach(node -> {
            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                    .processDefinitionId(processDefId).activityId(node.getId()).finished().list();
            if (CollectionUtil.isEmpty(historicActivityInstances)) {
                preNodes.remove(node);
            }
        });*/
        if (CollectionUtil.isEmpty(preNodes)) {
            return null;
        }
        return preNodes.get(0);
    }

    public static void getIncomeNodesRecur(String taskDefinitionKey, String processDefId, List<FlowNode> incomeNodes, boolean isAll) {
        Process process = getProcess(processDefId);
        FlowElement currentFlowElement = process.getFlowElement(taskDefinitionKey);
        List<SequenceFlow> incomingFlows = null;
        if (currentFlowElement instanceof UserTask) {
            incomingFlows = ((UserTask) currentFlowElement).getIncomingFlows();
        } else if (currentFlowElement instanceof Gateway) {
            incomingFlows = ((Gateway) currentFlowElement).getIncomingFlows();
        } else if (currentFlowElement instanceof StartEvent) {
            incomingFlows = ((StartEvent) currentFlowElement).getIncomingFlows();
        }
        if (incomingFlows != null && incomingFlows.size() > 0) {
            incomingFlows.forEach(incomingFlow -> {
                String expression = incomingFlow.getConditionExpression();
                // 出线的上一节点
                String sourceFlowElementID = incomingFlow.getSourceRef();
                // 查询上一节点的信息
                FlowNode preFlowElement = (FlowNode) process.getFlowElement(sourceFlowElementID);

                //用户任务
                if (preFlowElement instanceof UserTask) {
                    incomeNodes.add(preFlowElement);
                    if (isAll) {
                        getIncomeNodesRecur(preFlowElement.getId(), processDefId, incomeNodes, true);
                    }
                }
                //排他网关
                else if (preFlowElement instanceof ExclusiveGateway) {
                    getIncomeNodesRecur(preFlowElement.getId(), processDefId, incomeNodes, isAll);
                }
                //并行网关
                else if (preFlowElement instanceof ParallelGateway) {
                    getIncomeNodesRecur(preFlowElement.getId(), processDefId, incomeNodes, isAll);
                }
            });
        }
    }

    public static Process getProcess(String processDefId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefId);
        return bpmnModel.getProcesses().get(0);
    }




    /**
     * 获取发起人节点
     *
     * @param appId
     * @param courtId
     * @return
     */
    public static FlowElement findFlowFirstUserTask(String appId, Long courtId) {
        Process process = getProcess(appId, courtId);
        return findFirstUserTaskNode(process);
    }

    private static FlowNode findFirstUserTaskNode(Process mainProcess) {
        if (Objects.isNull(mainProcess)) {
            return null;
        }
        FlowNode startActivity = (FlowNode) mainProcess.getInitialFlowElement();
        if (startActivity.getOutgoingFlows().size() != 1) {
            throw new IllegalStateException(
                    "start activity outgoing transitions cannot more than 1, now is : "
                            + startActivity.getOutgoingFlows().size());
        }

        SequenceFlow sequenceFlow = startActivity.getOutgoingFlows()
                .get(0);
        FlowNode targetActivity = (FlowNode) sequenceFlow.getTargetFlowElement();

        if (!(targetActivity instanceof UserTask)) {
            log.info("first activity is not userTask, just skip");

            return null;
        }

        return targetActivity;
    }


    public static FlowNode findEndActivity(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process mainProcess = bpmnModel.getMainProcess();
        List<EndEvent> endEvents = mainProcess.findFlowElementsOfType(EndEvent.class);
        if (endEvents == null || endEvents.isEmpty()) {
            throw new IllegalStateException(
                    "end activity size is 0");
        }
        return (FlowNode) endEvents.get(0);
    }


    public static Map<String, FlowElement> getFlowElementMap(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        return bpmnModel.getMainProcess().getFlowElementMap();
    }

    public static FlowElement getFlowElement(String processDefinitionId, String activityId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process mainProcess = bpmnModel.getMainProcess();
        return mainProcess.getFlowElement(activityId);
    }

    /**
     * 获取流程中用户节点的节点名称list
     *
     * @param appId
     * @param courtId
     * @return
     */
    public static Map<String, String> getUserTaskNodeNameMap(String appId, Long courtId) {
        Process process = ActivitiUtil.getProcess(appId, courtId);
        Map<String, String> nodeMap = new HashMap<>();
        Map<String, FlowElement> flowElementMap = process.getFlowElementMap();
        if (CollectionUtils.isEmpty(flowElementMap)) {
            return nodeMap;
        }
        for (String key : flowElementMap.keySet()) {
            FlowElement flowElement = flowElementMap.get(key);
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                nodeMap.put(key, userTask.getName());
            }
        }
        return nodeMap;
    }
    public static Map<String, String> getUserTaskNodeNameMap(String procDefId) {
        Process process = ActivitiUtil.getProcess(procDefId);
        Map<String, String> nodeMap = new HashMap<>();
        Map<String, FlowElement> flowElementMap = process.getFlowElementMap();
        if (CollectionUtils.isEmpty(flowElementMap)) {
            return nodeMap;
        }
        for (String key : flowElementMap.keySet()) {
            FlowElement flowElement = flowElementMap.get(key);
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                nodeMap.put(key, userTask.getName());
            }
        }
        return nodeMap;
    }

    /**
     * 获取下一个userTask
     *
     * @param sid
     */
    public static FlowElement getNextUserTask(String sid, Map<String, FlowElement> flowElementMap, Map<String, Object> formData) {
        return getNextUserTask(sid, flowElementMap, formData, 0);
    }

    public static FlowElement getNextUserTask(String sid, Map<String, FlowElement> flowElementMap, Map<String, Object> formData, int flag) {
        //获取下一个userTask
        FlowElement flowElement = flowElementMap.get(sid);

        if (flowElement instanceof EndEvent) {
            return null;
        }
        List<String> outgoingFlows = null;
        if (flowElement instanceof StartEvent) {
            outgoingFlows = ((StartEvent) flowElement).getOutgoingFlows().stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
        } else if (flowElement instanceof UserTask) {
            if (flag == 0) {
                //往下循环找userTask
                outgoingFlows = ((UserTask) flowElement).getOutgoingFlows().stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
            } else {
                //返回
                return flowElement;
            }
        } else if (flowElement instanceof SequenceFlow) {
            outgoingFlows = Lists.newArrayList(((SequenceFlow) flowElement).getTargetRef());
        } else if (flowElement instanceof ExclusiveGateway) {
            List<SequenceFlow> gateWayOutgoingFlows = ((ExclusiveGateway) flowElement).getOutgoingFlows();
            //获取到某一个节点出口
            if (!CollectionUtils.isEmpty(gateWayOutgoingFlows)) {
                boolean expressFlag = false;
                //获取表达式为true的出线
                for (SequenceFlow sequenceFlow : gateWayOutgoingFlows) {
                    String condition = sequenceFlow.getConditionExpression();
                    if (checkFormDataByRuleEl(condition, formData)) {
                        outgoingFlows = Lists.newArrayList(sequenceFlow.getId());
                        expressFlag = true;
                        break;
                    }
                }
                //找不到表达式为true的出现，随机取一条出线
                if (!expressFlag) {
                    List<SequenceFlow> collect = gateWayOutgoingFlows.stream()
                            .filter(sequenceFlow -> Objects.isNull(sequenceFlow.getConditionExpression())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(collect)) {
                        outgoingFlows = Lists.newArrayList(collect.get(0).getId());
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(outgoingFlows)) {
            return null;
        }
        //默认节点出线都只为一条，所以这里随机取某一条出线
        for (String outgoingId : outgoingFlows) {
            return getNextUserTask(outgoingId, flowElementMap, formData, 1);
        }
        return null;
    }


    /**
     * 获取当前节点的入线userTask
     * @param sid 当前节点
     * @param appId 应用id
     * @param courtId 机构id
     * @return
     */
    public static List<String> getInCommingUserTask(String sid, String appId, Long courtId) {
        Process process = getProcess(appId, courtId);
        return getInUserTask(sid, process);
    }
    /**
     * 获取当前节点的入线userTask
     * @param sid
     * @param procDefId 流程定义id
     * @return
     */
    public static List<String> getInCommingUserTask(String sid, String procDefId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);
        Process process = bpmnModel.getMainProcess();
        return getInUserTask(sid, process);
    }

    public static List<String> getInUserTask(String sid, Process process) {
        if (Objects.isNull(process)) {
            return null;
        }
        List<String> result = Lists.newArrayList();
        getInCommingUserFlows(sid, process.getFlowElementMap(), result);
        return result;
    }

    static void getInCommingUserFlows(String sid, Map<String, FlowElement> flowElementMap, List<String> result) {
        if (Objects.isNull(flowElementMap)) {
            return;
        }
        FlowElement flowElement = flowElementMap.get(sid);
        List<String> incomingFlows = Lists.newArrayList();
        if (flowElement instanceof StartEvent) {
            incomingFlows = ((StartEvent) flowElement).getIncomingFlows().stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
        } else if (flowElement instanceof UserTask) {
            incomingFlows = ((UserTask) flowElement).getIncomingFlows().stream().map(SequenceFlow::getTargetRef).collect(Collectors.toList());
        } else if (flowElement instanceof SequenceFlow) {
            incomingFlows = Lists.newArrayList(((SequenceFlow) flowElement).getSourceRef());
        } else if (flowElement instanceof ExclusiveGateway) {
            //网关接口的入线不管
            return;
        }

        if (CollectionUtils.isEmpty(incomingFlows)) {
            return;
        }
        for (String incommingUserTask : incomingFlows) {
            FlowElement element = flowElementMap.get(incommingUserTask);
            if (element instanceof UserTask) {
                result.add(incommingUserTask);
            } else {
                getInCommingUserFlows(incommingUserTask, flowElementMap, result);
            }
        }
    }

    public static Boolean checkFormDataByRuleEl(String el, Map<String, Object> formData) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Object k : formData.keySet()) {
            if (formData.get(k) != null) {
                //
                context.setVariable(k.toString(), factory.createValueExpression(formData.get(k), formData.get(k).getClass()));
            }
        }
        ValueExpression e = factory.createValueExpression(context, el, Boolean.class);
        return (Boolean) e.getValue(context);
    }

    public static Process getProcess(String appId, Long courtId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("a" + appId + "-" + courtId).latestVersion().singleResult();
        if (processDefinition != null) {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            return bpmnModel.getMainProcess();
        }
        return null;
    }


    public static Boolean isFinished(String processInstanceId) {
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).active().singleResult();
            if (processInstance != null) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public static HistoryService historyService() {
        return processEngineConfiguration.getHistoryService();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        repositoryService = applicationContext.getBean(RepositoryService.class);
        runtimeService = applicationContext.getBean(RuntimeService.class);
        taskService = applicationContext.getBean(TaskService.class);
        processEngineConfiguration = applicationContext.getBean(ProcessEngineConfigurationImpl.class);
    }
}
