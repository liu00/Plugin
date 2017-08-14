package org.plugin.proxy;

import android.content.ComponentName;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理PackageManager
 *
 * @author liuwh
 */

public class PMProxyHandler implements InvocationHandler {
    private static final String TAG = "PMProxyHandler";

    private static final String PROXY_ACTIVITY = "org.plugin.ProxyActivity";

    private Object target;

    public PMProxyHandler(Object obj){
        target = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.i(TAG, "method=" + method.getName());
            if(TextUtils.equals("getActivityInfo", method.getName())){
                for(int i=0; i< args.length; i++){
                    Log.i("TAG", "args= " + args[i]);
                    if(args[i] instanceof ComponentName){
                        ComponentName componentName = (ComponentName) args[i];
                        if(!componentName.getClassName().startsWith("org.plugin")){
                            //
                            args[i] = new ComponentName(componentName.getPackageName(), PROXY_ACTIVITY);
                            Log.i(TAG, "change activity " + componentName.getClassName() + " to " + PROXY_ACTIVITY);
                            break;
                        }
                    }
                }
            }

            return method.invoke(target, args);
    }
}
