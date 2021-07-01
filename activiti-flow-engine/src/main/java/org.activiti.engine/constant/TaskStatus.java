package org.activiti.engine.constant;

/**
 * @author coco
 * @date 2020-08-10 16:03
 **/
public interface TaskStatus {

    /**
     * 正在审批
     */
    String APPROVALING = "approvaling";

    /**
     * 拒绝
     */
    String REFUSE = "refuse";

    /**
     * 同意
     */
    String AGREE = "agree";

    /**
     * 撤回
     */
    String WITHDRAW = "withdraw";


    /**
     * 或签审批完成
     */
    String OR_SIGN_FINISHED = "orSignFinished";




}
