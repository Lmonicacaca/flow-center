package com.flow.center.dubbo;

import com.yjcloud.idol.common.response.BaseResponse;

import java.util.List;
import java.util.Map;

/**
 * @author coco
 * @date 2020-08-06 10:06
 **/
public interface ICommandRSV {

    /**
     * 撤回请求
     *
     * @param taskId
     * @param reason
     * @return
     */
    BaseResponse withdrawCommand(String taskId, String userId, String reason);

    /**
     * 重新发起
     *
     * @param taskId
     * @param userId
     * @return
     * @author zhangl
     * @since pma-1.0
     */
    BaseResponse restartCommand(String taskId, String userId);

    /**
     * 拒绝
     *
     * @param taskId
     * @param reason
     * @return
     */
    BaseResponse refuseCommand(String taskId, String reason);

    /**
     * 同意
     *
     * @param taskId
     * @param reason
     * @return
     */
    BaseResponse agreeCommand(String taskId, String reason);


    /**
     * 同意
     *
     * @param taskId
     * @param reason
     * @return
     */
    BaseResponse agreeCommand(String taskId, String reason, Map<String, Object> param);


    /**
     * 完成当前任务 临时添加一个指定人的任务
     *
     * @param taskId
     * @param userId
     * @param activityName
     * @return
     */
    BaseResponse copyTaskCommand(String taskId, String userId, String activityName);

    /**
     * 在当前会签节点，加签
     * TODO 目前非会签节点只支持单个加签
     * @param taskId
     * @param assigneeList
     * @return
     */
    BaseResponse addActivityCommand(String taskId, List<String> assigneeList);

    /**
     * 在当前会签节点，减签
     *
     * @param taskId
     * @param assigneeList
     * @return
     */
    BaseResponse removeActivityCommand(String taskId, List<String> assigneeList);

    /**
     * 完成当前任务，跳转到任意节点
     *
     * @param taskId     当前任务id
     * @param activityId 需要跳转的节点id sid
     * @return
     */
    BaseResponse jumpAnyActivityCommand(String taskId, String activityId);


    /**
     * 完成当前任务，跳转到任意节点
     *
     * @param taskId    当前任务id
     * @param jumpCount 往前跳几个任务，上一个节点的话，jumpCount=1
     *                  不可跳转到申请人节点（目前没有这个需求）
     * @return
     */
    BaseResponse jump2BeforeUserTaskCommand(String taskId, Long jumpCount);




    /**
     * 完成当前任务，跳转到任意节点
     *
     * @param taskId    当前任务id
     * @param jumpCount 往前跳几个任务，上一个节点的话，jumpCount=1
     *                  不可跳转到申请人节点（目前没有这个需求）
     * @return
     */
    BaseResponse jump2BeforeUserTaskCommand(String taskId, String reason, Long jumpCount);


    BaseResponse changeAssginee(String taskId, String newUserId);

    /**
     * 快速结束
     * 完成当前节点，
     * 并且将当前节点的审批人切换为当前登录人
     * 完成当前流程
     * @param taskId
     * @param userId 当前登录人，会将该节点审批人变成userid
     * @param reason
     * @return
     */
    BaseResponse quickEndCommand(String taskId, String userId, String reason);

}
