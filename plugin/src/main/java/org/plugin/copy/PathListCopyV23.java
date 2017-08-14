package org.plugin.copy;

import org.plugin.util.ReflectUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * 支持Android 6.0 - 7.1
 *
 * @author liuwh
 */
class PathListCopyV23 extends PathListCopyV19 {

    //List<File>
    static final String nativeLibraryPathElements = "nativeLibraryPathElements";
    static final String systemNativeLibraryDirectories = "systemNativeLibraryDirectories";

    PathListCopyV23(DexClassLoader[] dexClassLoaders, ClassLoader ctxClassLoader){
        //
        super(dexClassLoaders, ctxClassLoader);
        copyNativeLibraryPathElements(pathListArray, pathListObj);
    }

    //private final Element[] nativeLibraryPathElements;
    /**
     * 复制和合并nativeLibraryPathElements
     * @param pathListArray 动态加载的pathList
     * @param pathList 应用加载的pathList
     */
    protected void copyNativeLibraryPathElements(Object[] pathListArray, Object pathList) {
        try{
            copyField(pathListArray, nativeLibraryPathElements, pathList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //private final List<File> nativeLibraryDirectories;
    /**
     * 复制和合并nativeLibraryDirectories
     * @param pathListArray 动态加载的pathList
     * @param pathList 应用加载的pathList
     */
    @Override
    protected void copyNativeLibraryDirectories(Object[] pathListArray, Object pathList) {
        try {
            copyList(pathListArray, nativeLibraryDirectories, pathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private final List<File> systemNativeLibraryDirectories;
    /**
     * 复制和合并systemNativeLibraryDirectories
     * @param pathListArray 动态加载的pathList
     * @param pathList 应用加载的pathList
     */
    protected void copySystemNativeLibraryDirectories(Object[] pathListArray, Object pathList) {
        try {
            copyList(pathListArray, systemNativeLibraryDirectories, pathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将动态加载的数据与系统中的合并，并重新设置系统中给fieldName的属性值
     * @param pathListArray 动态加载的pathList
     * @param fieldName 属性名称
     * @param pathList 应用加载的pathList
     * @throws Exception
     */
    void copyList(Object[] pathListArray, String fieldName, Object pathList) throws Exception {
        //用于记录属性值的数组
        Field field = ReflectUtil.getField(pathList, fieldName);
        List<File> fieldValues = (List<File>) field.get(pathList);

        if(fieldValues == null){
            fieldValues = new ArrayList<>();
        }
        //
        List<File> temp = null;
        boolean hasData = false;
        for(int i= pathListArray.length -1; i > -1; i--){
            try {
                temp = (List<File>) ReflectUtil.getFieldValue(pathListArray[i], fieldName);
                //
                if(temp != null && !temp.isEmpty()){
                    fieldValues.addAll(0, temp);
                    hasData = true;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if(fieldValues == null){
            //
            return;
        }

        if(hasData){
            field.set(pathList, fieldValues);
        }

    }

}
