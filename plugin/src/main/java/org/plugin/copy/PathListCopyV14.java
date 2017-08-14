package org.plugin.copy;

import org.plugin.util.ReflectUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 *
 * 支持Android 4.0 - 4.3
 *
 * @author liuwh
 */
class PathListCopyV14 {
    static final String pathList = "pathList";
    static final String dexElements = "dexElements";
    static final String nativeLibraryDirectories = "nativeLibraryDirectories";

    protected Object pathListObj;
    protected Object[] pathListArray;

    PathListCopyV14(DexClassLoader[] dexClassLoaders, ClassLoader ctxClassLoader){
        //
        try {
            pathListObj = ReflectUtil.getFieldValue(ctxClassLoader, pathList);
            //
            pathListArray = new Object[dexClassLoaders.length];
            for(int i=0; i<pathListArray.length; i++){
                pathListArray[i] = ReflectUtil.getFieldValue(dexClassLoaders[i], pathList);
            }
            //复制动态加载的dexElements到应用加载器
            copyDexElements(pathListArray, pathListObj);

            //复制动态加载的nativeLibraryDirectories到应用加载器
            copyNativeLibraryDirectories(pathListArray, pathListObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private final Element[] dexElements;
    /**
     * 复制和合并dexElements
     * @param pathListArray 动态加载的pathList
     * @param pathList 应用加载的pathList
     */
    protected void copyDexElements(Object[] pathListArray, Object pathList) throws Exception {
        copyField(pathListArray, dexElements, pathList);
    }

    //private final File[] nativeLibraryDirectories;
    /**
     * 复制和合并nativeLibraryDirectories
     *
     * @param pathListArray 动态加载的pathList
     * @param pathList 应用加载的pathList
     */
    protected void copyNativeLibraryDirectories(Object[] pathListArray, Object pathList) throws Exception {
        copyField(pathListArray, nativeLibraryDirectories, pathList);
    }

    /**
     * 将动态加载的数据与系统中的合并，并重新设置系统中给fieldName的属性值
     *
     * @param pathListArray pathList数组
     * @param fieldName 属性名称
     * @param pathList 应用加载器的pathList
     */
    void copyField(Object[] pathListArray, String fieldName, Object pathList) throws Exception {
        //用于记录属性值的数组
        Object[] fieldValuesArray = new Object[pathListArray.length];
        //遍历获取名称为fieldName的属性值，并存入fieldValuesArray
        //属性值数据的总长度
        int len = 0;
        Object[] temp = null;
        for(int i=0; i<fieldValuesArray.length; i++){
            try {
                temp = (Object[]) ReflectUtil.getFieldValue(pathListArray[i], fieldName);
                //记录数据长度
                if(temp != null){
                    len = len + temp.length;
                }
                fieldValuesArray[i] = temp;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //执行复制操作
        copyArray(fieldValuesArray, len, pathList, fieldName);

    }

    /**
     *  将arrays的数据合并到对象obj中名称为fieldName的属性值，然后将合并数据赋值给该属性
     *
     * @param arrays 数组数据
     * @param len 数据总长度
     * @param obj 被操作的对象
     * @param fieldName 需要重新赋值的属性的名称
     * @throws Exception
     */
    void copyArray(Object[] arrays, int len, Object obj, String fieldName) throws Exception {
        if(len == 0){
            //
            return;
        }
        //属性
        Field field = ReflectUtil.getField(obj, fieldName);
        //属性值
        Object[] objFieldValue = (Object[]) field.get(obj);
        //生产新的的数组
        Object[] newFiledValue = (Object[]) Array.newInstance(objFieldValue.getClass().getComponentType(), len + objFieldValue.length);

        //将arrays中的数据写入到newFiledValue
        Object[] array = null;
        int copyLen = 0;
        for(Object oArray : arrays){
            if(oArray != null){
                array = (Object[]) oArray;
                if(array.length > 0){
                    //将该项数据写入到newFiledValue
                    System.arraycopy(array, 0, newFiledValue, copyLen, array.length);
                    //已复制数据的长度
                    copyLen = copyLen + array.length;
                }
            }
        }
        //将obj中名称为fieldName的属性写入到newFiledValue
        System.arraycopy(objFieldValue, 0, newFiledValue, len, objFieldValue.length);
        //将newFiledValue设置给名称为fieldName的属性
        field.set(obj, newFiledValue);
    }
}
