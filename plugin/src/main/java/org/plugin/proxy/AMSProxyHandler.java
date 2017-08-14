package org.plugin.proxy;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理ActivityManagerProxy
 *
 * @author liuwh
 */

public class AMSProxyHandler implements InvocationHandler {
    private static final String TAG = "AMSProxyHandler";

    private Object target;

    public AMSProxyHandler(Object obj){
        target = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (TextUtils.equals("startActivity", method.getName())) {
            Log.i(TAG, "----- invoke method:" + method.getName());
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    //
                    Object obj = args[i];
                    Log.i(TAG, "args: " + obj);
                    if (obj instanceof Intent) {
                        Intent intent = (Intent) obj;
                        String proxyActivity = intent.getStringExtra("proxy");
                        if(proxyActivity == null){
                            break;
                        }
                        ComponentName cn = intent.getComponent();
                        intent.putExtra("realCN", cn);
                        intent.setComponent(new ComponentName(cn.getPackageName(), proxyActivity));
                        Log.i(TAG, "change " + cn.getClassName() + " to proxy : " + proxyActivity);
                    }
                }

            }
        }
        return method.invoke(target, args);
    }
}
