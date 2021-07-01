package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

/**
 * 任务设置代理人的监听事件
 */
public abstract class AbstractAssignTaskListener implements TaskListener {
  @Override
  public void notify(DelegateTask delegateTask) {

  }

}
