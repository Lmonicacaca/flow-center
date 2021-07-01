package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Service;

/**
 * 用户任务完成 执行监听
 */
public abstract class AbstractFinishTaskExecutionListener implements ExecutionListener {
  @Override
  public void notify(DelegateExecution delegateExecution) {

  }
}
