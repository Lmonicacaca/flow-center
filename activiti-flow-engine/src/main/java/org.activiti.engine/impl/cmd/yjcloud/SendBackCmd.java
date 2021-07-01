package org.activiti.engine.impl.cmd.yjcloud;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;

/**
 * @author coco
 * @date 2020-07-01 15:56
 **/
public class SendBackCmd extends NeedsActiveTaskCmd<Void> {

    public SendBackCmd(String taskId) {
        super(taskId);
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity task) {
        return null;
    }
}
