package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

/**
 * 用户任务完成 监听
 */
public abstract class AbstractFinishTaskListener implements TaskListener {
  @Override
  public void notify(DelegateTask delegateTask) {

  }
}
