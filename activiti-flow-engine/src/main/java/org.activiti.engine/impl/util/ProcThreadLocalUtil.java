package org.activiti.engine.impl.util;

/**
 * @desc:
 * @Author: lvjing
 * @Date: 2021/3/15 4:08 下午
 */
public class ProcThreadLocalUtil {
    public static ThreadLocal<Boolean> nodeJumpThread = new ThreadLocal<>();
    /**
     * 设置参数
     */
    public static void setNodeJump(boolean nodeJump){
        nodeJumpThread.set(nodeJump);
    }

    public static boolean getNodeJump(){
        try {
            if(null == nodeJumpThread){
                return false;
            }
            return nodeJumpThread.get();
        }catch (Exception e){
            return false;
        }
    }

    public static void clearNodeJumpThreadLocal(){
        nodeJumpThread.remove();
    }
}
