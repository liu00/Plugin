package org.plugin.copy;

import dalvik.system.DexClassLoader;

/**
 * 支持Android 4.4 - 5.1
 *
 * @author liuwh
 */
class PathListCopyV19 extends PathListCopyV14{

    static final String dexElementsSuppressedExceptions = "dexElementsSuppressedExceptions";

    PathListCopyV19(DexClassLoader[] dexClassLoaders, ClassLoader ctxClassLoader){
        //
        super(dexClassLoaders, ctxClassLoader);
        copyDexElementsSuppressedExceptions(pathListArray, pathListObj);
    }

    //private final IOException[] dexElementsSuppressedExceptions;
    /**
     * 复制和合并dexElementsSuppressedExceptions
     * @param pathListArray 动态加载的pathList
     * @param pathList 应用加载的pathList
     */
    protected void copyDexElementsSuppressedExceptions(Object[] pathListArray, Object pathList){
        try {
            copyField(pathListArray, dexElementsSuppressedExceptions, pathList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
