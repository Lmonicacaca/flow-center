package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Service;

/**
 * 用户任务创建 执行监听
 */
public abstract class AbstractCreateTaskExecutionListener implements ExecutionListener {
  @Override
  public void notify(DelegateExecution delegateExecution) {

  }
}
