package org.plugin.task;

import org.plugin.util.HookUtil;

/**
 * 执行hook操作的任务
 *
 * @author liuwh
 */

public class HookTask implements Runnable {
    @Override
    public void run() {
        System.out.println(" ------ start hook ------ ");
        HookUtil.hook();
        System.out.println(" ------ end hook ------ ");
    }
}
