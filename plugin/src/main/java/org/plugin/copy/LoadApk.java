package org.plugin.copy;

import android.util.Log;

import dalvik.system.DexClassLoader;

/**
 *
 * 动态加载Apk，复制和合并数据
 *
 * @author liuwh
 */

public final class LoadApk {
    private static final String TAG = "LoadApk";

    /**
     * 执行加载apk和复制dex等信息
     *
     * @param files 文件路径
     * @param optDir dex优化存放目录
     * @param appClassLoader 父加载器
     */
    public void doLoadAndCopy(String[] files, String optDir, ClassLoader appClassLoader) {

        DexClassLoader[] dexClassLoaders = null;
        //加载apk
        if (files != null && files.length > 0) {
            dexClassLoaders = new DexClassLoader[files.length];
            for (int i = 0; i < files.length; i++) {
                dexClassLoaders[i] = new DexClassLoader(files[i], optDir, null, appClassLoader);
            }
        }


        if (dexClassLoaders == null) {
            throw new RuntimeException("Nothing to be loaded!!");
        }

        PathListCopy pathListCopy = new PathListCopy();
        try {
            pathListCopy.copy(dexClassLoaders, appClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Fail to load apk or copy data");
        }
    }
}
