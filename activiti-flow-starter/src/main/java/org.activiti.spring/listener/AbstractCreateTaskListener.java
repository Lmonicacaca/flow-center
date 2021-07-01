package org.activiti.spring.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

/**
 * 创建用户任务
 */
public abstract class AbstractCreateTaskListener implements TaskListener {
  @Override
  public void notify(DelegateTask delegateTask) {

  }

  /**
   * 通知
   */
  protected abstract void notice();


  /**
   * 分配审批人
   */
  protected abstract void assginUser();

}
