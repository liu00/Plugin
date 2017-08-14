package org.plugin.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * Created by liu1359041 on 2017/8/4.
 */

public class FileUtil {
    static private final String TAG = "FileUtil";

    public static boolean createDir(String path){
        return new File(path).mkdirs();
    }

    public static boolean copyApk(Context ctx, String path){
        File dir = new File(ctx.getCacheDir(), "plugin");

        if(!dir.exists()){
            dir.mkdir();
        }
        return copyApk(ctx, new File(dir, path));
    }

    public static boolean isSdcardAvailable(){
        return Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState());
    }

    public static boolean copyApk(Context ctx, File file){
        if(!isSdcardAvailable()){
            Log.i(TAG, "can not access sdcard!");
            return false;
        }

        File apkFile = new File(Environment.getExternalStorageDirectory(), file.getName());

        if(!apkFile.exists()){
            Log.i(TAG, "file " + file.getName() + " not exist!");
            return false;
        }

        return copyFile(apkFile, file);
    }

    public static boolean isExist(Context ctx, String name){
        //File cacheDir = new File(ctx.getCacheDir(), "plugin");
        File dir = new File(ctx.getCacheDir(), "plugin");

        if(!dir.exists()){
            dir.mkdir();
        }
        File pluginFile = new File(dir, name);

        if(pluginFile.exists()){
            Log.i(TAG, "file " + pluginFile.getAbsolutePath() + " exist!");
            return true;
        }

        return copyApk(ctx, pluginFile);
    }

    public static boolean copyFile(File targetFile, File destFile){
        if(targetFile == null || destFile == null)
            return false;

        if(!targetFile.exists()){
            Log.i(TAG, "file " + targetFile.getAbsolutePath() + " not exist!");
            return false;
        }

        FileOutputStream fos = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(targetFile);
            fos = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int len = 0;
            while((len = fis.read(buf)) > 0){
                fos.write(buf, 0, len);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean copyApk(Context ctx){

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is  = ctx.getAssets().open("test.apk");
            File dir = new File(ctx.getCacheDir(), "apks");
            if(!dir.exists()){
                dir.mkdirs();
            }

            fos = new FileOutputStream(new File(dir, "test.apk"));
            byte[] buf = new byte[1024];
            int len = 0;
            while((len = is.read(buf)) > 0){
                fos.write(buf, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static boolean installApk(Context ctx){
        try{
            if(FileUtil.isExist(ctx, "test_opt.apk")){
                //
                String dexPath = ctx.getCacheDir() + "/plugin/test_opt.apk";
                String optimizedDirectory = ctx.getCacheDir() + "/apk";
                FileUtil.createDir(optimizedDirectory);

                //一 获取加载的dex信息
                DexClassLoader dexClassLoader = new DexClassLoader(dexPath, optimizedDirectory, null, ctx.getClassLoader());

                Class<?> clazz = dexClassLoader.loadClass("com.plugin.test.PluginActivity");
                Log.i("loadClass", " class = " + clazz);

                Log.i("loadClass", " dexClassLoader = " + dexClassLoader);
                Log.i("loadClass", " getClassLoader() = " + ctx.getClassLoader());

//                //1 获取dexPathList属性
//                Field dexPathListField = HookUtil.getField(dexClassLoader, "pathList");
//                //  获取dexPathList属性值
//                Object dexPathList = HookUtil.getFieldValue(dexClassLoader, dexPathListField);
//                //2 获取dexElements属性
//                Field dexElementsField = HookUtil.getField(dexPathList, "dexElements");
//                //  获取dexElements属性值
//                Object[] dexElements = (Object[]) HookUtil.getFieldValue(dexPathList, dexElementsField);
//
//                //二 获取系统的dex信息
//                //
//                Field apkDexPathListField = HookUtil.getField(ctx.getClassLoader(), "pathList");
//                Object apkDexPathList = HookUtil.getFieldValue(ctx.getClassLoader(), apkDexPathListField);
//                //
//                Field apkDexElementsField = HookUtil.getField(apkDexPathList, "dexElements");
//                Object[] apkDexElements = (Object[]) HookUtil.getFieldValue(apkDexPathList, apkDexElementsField);
//
//                //创建新的Element[]
//                Object[] newDexElements = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(), dexElements.length + apkDexElements.length);
//                //复制动态加载的dex数组
//                System.arraycopy(dexElements, 0, newDexElements, 0, dexElements.length);
//                //复制apk中的dex数组
//                System.arraycopy(apkDexElements, 0, newDexElements, dexElements.length, apkDexElements.length);
//
//                HookUtil.setFieldValue(apkDexPathList, apkDexElementsField, newDexElements);

                copyFieldInfo(dexClassLoader, ctx.getClassLoader(), new String[]{"pathList", "dexElements"});



//                //处理IOException[] dexElementsSuppressedExceptions

                clazz = ctx.getClassLoader().loadClass("com.plugin.test.PluginActivity");
                Log.i("loadClass", " class = " + clazz);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取Class中指定名称的属性Field
     * @param clazz Class对象
     * @param fieldName 属性名称
     * @return 属性Field对象
     * @throws NoSuchFieldException 如果未查找到对应属性，则抛出
     */
    private Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        Field field = null;
        String classStr = clazz.toString();
        //从子类向父类循环查找Field
        while (clazz != null) {
            try {
                Log.i("Test", "parent class = " + clazz);
                field = clazz.getDeclaredField(fieldName);
                if (field != null) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    return field;
                }
            } catch (Exception e) {
                //忽略错误信息
                //e.printStackTrace();
            }
            //获取父类字节码，从父类查找
            clazz = clazz.getSuperclass();
        }

        //如果未查找到，则抛出异常
        if (field == null) {
            throw new NoSuchFieldException("field: " + fieldName + " not in " + classStr);
        }

        return field;
    }

    /**
     * 动态加载dex，并将dex合并到应用加载器中
     * @param ctx Context对象
     */
    private void copyDexElements(Context ctx){

        File file = new File(ctx.getCacheDir(), "apksDir/test.dex");
        if(!file.exists()){
            Log.i("Test", "Dex file not exists, file: " + file.getAbsolutePath());
            return;
        }
        //含有dex的jar或apk文件的路径
        String dexPath = file.getAbsolutePath();
        File dir = new File(ctx.getCacheDir(), "optDir");
        if(!dir.exists()){
            dir.mkdir();
        }
        //优化后的dex文件存放的目录的路径
        String optimizedDirectory = dir.getAbsolutePath();
        dir = null;

        try {
            //应用的加载器
            ClassLoader pathClassLoader = ctx.getClassLoader();
            //动态加载器，加载的dex文件
            DexClassLoader dexClassLoader = new DexClassLoader(dexPath, optimizedDirectory, null, pathClassLoader);

            //1, 获取DexClassLoader的pathList
            Field dexPathListField = getField(dexClassLoader.getClass(), "pathList");
            Object dexPathList = dexPathListField.get(dexClassLoader);
            if(dexPathList == null){
                Log.i("Test", "Fail to get pathList from DexClassLoader, dexClassLoader: " + dexClassLoader);
                return;
            }

            //2, 获取pathList的dexElements
            Field dexDexElementsField = getField(dexPathList.getClass(), "dexElements");
            Object[] dexDexElements = (Object[]) dexDexElementsField.get(dexPathList);
            if(dexDexElements == null){
                Log.i("Test", "Fail to get dexElements from pathList in DexClassLoader, dexClassLoader: " + dexClassLoader);
                return;
            }

            if(dexDexElements.length == 0){
                Log.i("Test", "The size of dexElements from pathList in DexClassLoader is 0, dexClassLoader: " + dexClassLoader);
                return;
            }

            //3, 获取应用加载器PathClassLoader的pathList
            Field pathPathListField = getField(pathClassLoader.getClass(), "pathList");
            Object pathPathList = pathPathListField.get(pathClassLoader);
            if(pathPathList == null){
                Log.i("Test", "Fail to get pathList from application ClassLoader, classLoader: " + pathClassLoader);
                return;
            }

            //4, 获取应用加载器的pathList的dexElements
            Field pathDexElementsField = getField(pathPathList.getClass(), "dexElements");
            Object[] pathDexElements = (Object[]) pathDexElementsField.get(pathPathList);
            if(pathDexElements == null){
                Log.i("Test", "Fail to get dexElements from pathList in application ClassLoader, classLoader: " + pathClassLoader);
                return;
            }

            //5, 创建新数组，并复制Element到新数组
            //创建长度为dexDexElements.length + pathDexElements.length的Element[]
            Object[] newDexElements = (Object[]) Array.newInstance(pathDexElements.getClass().getComponentType(), dexDexElements.length + pathDexElements.length);
            //将动态加载的dexElements复制到newDexElements，范围：[0，dexDexElements.length-1]
            System.arraycopy(dexDexElements, 0, newDexElements, 0, dexDexElements.length);
            //将应用的dexElements复制到newDexElements，范围：[dexDexElements.length, n]
            System.arraycopy(pathDexElements, 0, newDexElements, dexDexElements.length, pathDexElements.length);

            //6，将newDexElements设置给应用加载器的pathList的dexElements
            pathDexElementsField.set(pathClassLoader, newDexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean copyFieldInfo(Object srcObj, Object destObj, String[] fieldNames){
        //
        boolean flag = false;
        try {
            Object src = ReflectUtil.getFieldValue(srcObj, fieldNames[0]);
            Object dest = ReflectUtil.getFieldValue(destObj, fieldNames[0]);
            flag = copyArray(src, dest, fieldNames[1]);

            flag = copyArray(src, dest, "dexElementsSuppressedExceptions");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    static boolean copyArray(Object obj0, Object obj1, String fieldName){
        boolean flag = false;
        try {
            Object[] array0 = (Object[]) ReflectUtil.getFieldValue(obj0, fieldName);

            if(array0 == null || array0.length == 0){
                Log.i(TAG, "this is nothing in " + obj0);
                return true;
            }

            Field field1 = ReflectUtil.getField(obj1, fieldName);
            Object[] array1 = (Object[]) field1.get(obj1);//ReflectUtil.getFieldValue(obj1, field1);

            if(array1 == null || array1.length == 0){
                field1.set(obj1, array0);
                return true;
            }

            //创建新的Element[]
            Object[] newDexElements = (Object[]) Array.newInstance(array0.getClass().getComponentType(), array0.length + array1.length);
            //复制动态加载的dex数组
            System.arraycopy(array0, 0, newDexElements, 0, array0.length);
            //复制apk中的dex数组
            System.arraycopy(array1, 0, newDexElements, array0.length, array1.length);

            field1.set(obj1, newDexElements);

            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
