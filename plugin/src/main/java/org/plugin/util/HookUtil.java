package org.plugin.util;

import android.os.Handler;
import android.util.Log;

import org.plugin.proxy.AMSProxyHandler;
import org.plugin.proxy.PMProxyHandler;
import org.plugin.proxy.HCallback;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Hook工具类
 *
 * @author liuwh
 */

public final class HookUtil {

    private static final String TAG = "HookUtil";

    private final String AMN = "android.app.ActivityManagerNative";
    private final String IAM = "android.app.IActivityManager";
    private final String AT = "android.app.ActivityThread";
    private final String IPM = "android.content.pm.IPackageManager";

    private HookUtil(){}

    /**
     * hook ActivityManagerProxy和
     */
    public static void hook(){
        HookUtil hookUtil = new HookUtil();
        hookUtil.hookAMSProxy();
        hookUtil.hookCallbackAndIPM();
    }

    /**
     * 代理ActivityManagerProxy对象
     */
    private void hookAMSProxy() {
        try {
            Log.i(TAG, "--------start hook ActivityManagerProxy class-------");
            //获取静态属性gDefault的值，即即Singleton<IActivityManager>对象
            Object gDefault = ReflectUtil.getStaticFieldValue(Class.forName(AMN), "gDefault");

            //获取Singleton<IActivityManager>的mInstance属性，这个属性值是ActivityManagerProxy对象
            Field mInstanceField = ReflectUtil.getField(gDefault.getClass(), "mInstance");

            //代理ActivityManagerProxy对象中继承IActivityManager的方法
            mInstanceField.set(gDefault,
                    Proxy.newProxyInstance(
                            gDefault.getClass().getClassLoader(),
                            new Class[]{Class.forName(IAM)},
                            new AMSProxyHandler(mInstanceField.get(gDefault))
                    ));
            Log.i(TAG, "--------complete hook ActivityManagerProxy class-------");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给ActivityThread的mH设置Callback和代理sPackageManager
     */
    private void hookCallbackAndIPM(){
        try {
            Class<?> clazz = Class.forName(AT);
            //从ActivityThread的静态方法currentActivityThread()
            Object activityThread = ReflectUtil.callStaticMethod(clazz, "currentActivityThread");

            //------- 获取msg信息，处理msg信息 --------
            //设置mH（Handler对象）的mCallback值，用于提前获取msg信息
            Handler mH = (Handler) ReflectUtil.getFieldValue(activityThread, "mH");
            ReflectUtil.setFieldValue(mH, "mCallback", new HCallback());

            //------- 代理IPackageManager --------
            //通过ActivityThread的静态方法getPackageManager()获取IPackageManager
            Object sPackageManager = ReflectUtil.callStaticMethod(clazz, "getPackageManager");
            //获取activityThread的静态属性sPackageManager
            Field field = ReflectUtil.getField(clazz, "sPackageManager");
            field.set(sPackageManager,
                    Proxy.newProxyInstance(
                            clazz.getClassLoader(),
                            new Class[]{Class.forName(IPM)},
                            new PMProxyHandler(sPackageManager)));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
