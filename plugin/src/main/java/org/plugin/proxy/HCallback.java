package org.plugin.proxy;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * 给ActivityThread的mH设置Callback
 *
 * @author liuwh
 */

public class HCallback implements Handler.Callback {
    private static final String TAG = "HCallback";

    @Override
    public boolean handleMessage(Message msg) {
        //public static final int LAUNCH_ACTIVITY         = 100;
        //H中启动Activity的消息
        if(msg.what == 100){
            Log.i(TAG, "handler launch activity");
            //ActivityClientRecord msg.obj
            try {
                Field field = msg.obj.getClass().getDeclaredField("intent");
                field.setAccessible(true);
                Intent intent = (Intent) field.get(msg.obj);
                ComponentName realComponentName = intent.getParcelableExtra("realCN");
                if (realComponentName != null) {
                    intent.setComponent(realComponentName);
                    Log.i(TAG, "change " + intent.getStringExtra("proxy") + " to real activiy: " + realComponentName.getClassName());
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
