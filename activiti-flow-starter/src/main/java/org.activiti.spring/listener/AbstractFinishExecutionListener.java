package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Service;

/**
 * 流程完成执行监听
 */
public abstract class AbstractFinishExecutionListener implements ExecutionListener {
  @Override
  public void notify(DelegateExecution delegateExecution) {

  }
}
