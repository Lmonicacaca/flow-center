package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 *流程结束节点执行监听
 */
public abstract class AbstractCreateEndExecutionListener implements ExecutionListener {
  @Override
  public void notify(DelegateExecution delegateExecution) {

  }
}
