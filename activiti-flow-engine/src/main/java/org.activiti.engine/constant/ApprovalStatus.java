package org.activiti.engine.constant;

/**
 * @author coco
 * @date 2020-08-10 15:10
 **/
public interface ApprovalStatus {

    /**
     * 正在审批
     */
    String APPROVALING = "approvaling";

    /**
     * 拒绝
     */
    String REFUSE = "refuse";

    /**
     * 拒绝
     */
    String BACK = "back";

    /**
     * 撤回
     */
    String WITHDRAW = "withdraw";

    /**
     * 审批完成
     */
    String FINISHED = "finished";

}
