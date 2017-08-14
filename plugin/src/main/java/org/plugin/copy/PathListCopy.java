package org.plugin.copy;

import android.os.Build;

import dalvik.system.DexClassLoader;

/**
 *
 * 复制和合并数据
 *
 * @author liuwh
 */

final class PathListCopy {

    PathListCopy(){}

    /**
     * 复制和合并动态加载的apk信息到应用加载器
     *
     * @param dexClassLoaders DexClassLoader数组
     * @param ctxClassLoader 应用加载器
     * @return
     */
    boolean copy(DexClassLoader[] dexClassLoaders, ClassLoader ctxClassLoader){

        if(dexClassLoaders == null || dexClassLoaders.length == 0){
            throw new IllegalArgumentException("Error argument, dexClassLoaders = " + dexClassLoaders);
        }

        if(ctxClassLoader == null){
            throw new IllegalArgumentException("Error argument, ctxClassLoader = " + ctxClassLoader);
        }

        if(Build.VERSION.SDK_INT > 22){
            //复制Android 6.0及以上版本动态加载的pathList属性
            new PathListCopyV23(dexClassLoaders, ctxClassLoader);
        } else if(Build.VERSION.SDK_INT > 18){
            //复制Android 4.4 - 5.1版本动态加载的pathList属性
            new PathListCopyV19(dexClassLoaders, ctxClassLoader);
        } else if(Build.VERSION.SDK_INT > 14){
            //复制Android 4.0 - 4.3版本动态加载的pathList属性
            new PathListCopyV14(dexClassLoaders, ctxClassLoader);
        } else{
            //不支持Android 4.0以下的版本
            throw new RuntimeException("Not support Android that before ICE_CREAM_SANDWICH(4.0)!!!");
        }
        return false;
    }
}