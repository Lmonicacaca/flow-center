package org.activiti.engine.delegate.yjcloud;

import org.activiti.engine.delegate.BaseTaskListener;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author coco
 * 默认的创建节点任务事件监听
 * @date 2020-03-31 16:50
 **/
public abstract class AbstractCreateTaskListener implements TaskListener {


    @Override
    public void notify(DelegateTask delegateTask) {
        //去重策略
        String duplicateRemove = delegateTask.getVariable(BaseTaskListener.DUPLICATEREMOVE, String.class);

    }
}
