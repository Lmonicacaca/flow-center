package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

/**
 * 用户任务 删除监听
 */
public abstract class AbstractDeleteTaskListener implements TaskListener {
  @Override
  public void notify(DelegateTask delegateTask) {

  }
}
