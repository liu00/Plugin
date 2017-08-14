package org.plugin.util;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 *
 * @author liuwh
 */

public class ReflectUtil {

    private static final String TAG = "ReflectUtil";

    /**
     * 根据方法名称获取对象的Method对象
     *
     * @param obj 对象
     * @param methodName 方法名称
     * @param argsClass 参数字节码数组
     * @return Method对象
     * @throws NoSuchMethodException
     */
    public static Method getMehtod(Object obj, String methodName, Class... argsClass) throws NoSuchMethodException {
        return getMethod(obj.getClass(), methodName, argsClass);
    }

    /**
     *
     * @param clazz
     * @param methodName
     * @param argsClass
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getMethod(Class clazz, String methodName, Class... argsClass) throws NoSuchMethodException {
        Method method = null;
        String classStr = clazz.toString();
        //从子类向父类循环查找Method
        while(clazz != null){
            try {
                //Log.i(TAG, "parent class = " + clazz);
                method = clazz.getDeclaredMethod(methodName, argsClass);
                if(method != null){
                    if(!method.isAccessible()){
                        method.setAccessible(true);
                    }
                    return method;
                }
            } catch (NoSuchMethodException e) {
                //忽略
                //e.printStackTrace();
            }
            //获取父类字节码，从父类查找
            clazz = clazz.getSuperclass();
        }

        //如果未查找到，则抛出异常
        if (method == null) {
            throw new NoSuchMethodException("method: " + methodName + " not in " + classStr);
        }
        return method;
    }

    /**
     * 执行对象指定名称的方法，并返回结果
     *
     * @param obj 对象
     * @param methodName 方法名称
     * @param argsValue 参数数组
     * @return 执行结果
     *
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object callMethod(Object obj, String methodName, Object ... argsValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(argsValue == null || argsValue.length == 0){
            return getMethod(obj.getClass(), methodName).invoke(obj);
        }

        Class<?>[] argsClass = new Class[argsValue.length];
        for(int i=0; i<argsValue.length; i++){
            argsClass[i] = argsValue[i].getClass();
        }
        return getMehtod(obj.getClass(), methodName, argsClass).invoke(obj, argsValue);
    }

    /**
     *
     * @param clazz
     * @param methodName
     * @param argsValue
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object callStaticMethod(Class clazz, String methodName, Object ... argsValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(argsValue == null || argsValue.length == 0){
            return getMethod(clazz, methodName).invoke(null);
        }

        Class<?>[] argsClass = new Class[argsValue.length];
        for(int i=0; i<argsValue.length; i++){
            argsClass[i] = argsValue[i].getClass();
        }
        return getMehtod(clazz, methodName, argsClass).invoke(null, argsValue);
    }

    /**
     * 获取指定名称的属性Field对象
     *
     * @param obj 对象
     * @param fieldName 属性名称
     * @return Field对象
     * @throws Exception
     */
    public static Field getField(Object obj, String fieldName) throws Exception {
        return getField(obj.getClass(), fieldName);
    }

    /**
     * 获取指定名称的属性Field对象
     *
     * @param clazz 字节码对象
     * @param fieldName 属性名称
     * @return Field对象
     * @throws NoSuchFieldException
     */
    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        Field field = null;
        String classStr = clazz.toString();
        //从子类向父类循环查找Field
        while (clazz != null) {
            try {
                //Log.i(TAG, "parent class = " + clazz);
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
     * 获取对象的指定名称属性的值
     *
     * @param obj 对象
     * @param fieldName 属性名称
     * @return 属性的值
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getField(obj.getClass(), fieldName).get(obj);
    }

    /**
     *
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static Object getStaticFieldValue(Class clazz, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return getField(clazz, fieldName).get(null);
    }

    /**
     * 给对象指定名称的属性赋值
     * @param obj 对象
     * @param fieldName 属性名称
     * @param value 值
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        getField(obj.getClass(), fieldName).set(obj, value);
    }
}
