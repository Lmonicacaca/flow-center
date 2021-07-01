package org.activiti.engine.impl.cmd.yjcloud;

/**
 * Activiti 会签任务中变量标志
 * @author coco
 * @date 2020-07-03 10:58
 **/
public interface CountersigningVariables {
    /**
     *  默认审核人
     */
    String ASSIGNEE_USER = "assignee";

    /**
     *  审核人集合
     */
    String ASSIGNEE_LIST = "assigneeList";

    /**
     *  会签任务总数
     */
    String NUMBER_OF_INSTANCES = "nrOfInstances";

    /**
     *  正在执行的会签总数
     */
    String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";

    /**
     *  已完成的会签任务总数
     */
    String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";

    /**
     *  会签任务表示
     *  collectionElementIndexVariable
     */
    String LOOP_COUNTER = "loopCounter";
}
