package org.activiti.engine.delegate.event.listener;

import org.activiti.engine.constant.TaskStatus;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.bpmn.helper.BaseDelegateEventListener;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * @author coco
 * @date 2020-08-28 17:59
 **/
public class TaskCompletedEventListener extends BaseDelegateEventListener {

    private HistoryManager historyManager;

    public TaskCompletedEventListener(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void onEvent(ActivitiEvent event) {
        if (isValidEvent(event)) {
            if (event instanceof ActivitiEntityEventImpl) {
                ActivitiEntityEventImpl activitiEvent = (ActivitiEntityEventImpl) event;
                TaskEntity entity = (TaskEntity) activitiEvent.getEntity();
                historyManager.recordTaskStatusChange(entity.getId(), TaskStatus.AGREE);
                //commandContext.getHistoryManager()

            }
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
