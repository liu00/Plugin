package org.plugin.demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.plugin.demo.R;
import org.plugin.proxy.AMSProxyHandler;
import org.plugin.proxy.HCallback;
import org.plugin.proxy.PMProxyHandler;
import org.plugin.util.ReflectUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "Plugin";

    EditText et = null;
    TextView tv = null;
    ImageView iv = null;
    FrameLayout fl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View v = findViewById(R.id.btn);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //printInfo();
                //copy();
                makeResources("test_opt.apk");
                newResources();
                hookLoadedApks();
            }
        });

        v = findViewById(R.id.btn_start);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPluginApk();
            }
        });

        et = (EditText) findViewById(R.id.et_name);
        tv = (TextView) findViewById(R.id.tv_str);
        iv = (ImageView) findViewById(R.id.iv);
        fl = (FrameLayout) findViewById(R.id.fl);
    }

//    void copy(){
//        String name = et.getText().toString();
//        if(FileUtil.isExist(getApplication(), name)){
//            makeResources(name);
//        }else{
//            Log.i(TAG, "fail to copy file " + name);
//        }
//
//    }


    private void makeResources(String name){
        try {
//            AssetManager assetManager = getAssets();
            Log.i(TAG, " getClassLoader = " + getClassLoader());
            Log.i(TAG, " resources = " + getResources());
            Log.i(TAG, " actvity assertManager = " + getAssets() +
                    ", application ssertManager = " + getApplication().getAssets());
//            //AssetManager assetManager = AssetManager.class.newInstance();
//            Method method = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
//            if(!method.isAccessible()){
//                //
//                method.setAccessible(true);
//            }
//            String pluginPath = getCacheDir().getPath() + "/plugin/" + name;
//            int cook = (int) method.invoke(assetManager, pluginPath);
//            if(cook == 0){
//                Log.e(TAG, "fail to add plugin apk(" + pluginPath + ") to asset path !");
//                return;
//            }
//
//            //
//            Log.i(TAG, "cook = " + cook);

            ((PluginApp)getApplication()).installRes(getAssets());
            int strId = getResources().getIdentifier("p_str", "string", "com.plugin.test");//com.plugin.test
            Log.i(TAG, "p_str id = " + strId);
            tv.setText(getString(strId));

            int iconId = getResources().getIdentifier("p_icon_play", "drawable", "com.plugin.test");
            Log.i(TAG, "p_icon_play id = " + iconId);
            iv.setImageResource(iconId);

            int layoutId = getResources().getIdentifier("plugin_item", "layout", "com.plugin.test");
            if(fl.getChildCount() == 0){
                View v = View.inflate(fl.getContext(), layoutId, null);
                fl.addView(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Resources resources = null;

    void newResources(){
        try {

            if( resources == null){
                AssetManager assetManager = getAssets().getClass().newInstance();
                Method method = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
                if(!method.isAccessible()){
                    method.setAccessible(true);
                }
                String pluginPath = getCacheDir().getPath() + "/apks/test.apk";
                int cook = (int) method.invoke(assetManager, pluginPath);
                Log.i(TAG, "assetManager cook = " + cook);
                resources = new Resources(assetManager, getResources().getDisplayMetrics(), getResources().getConfiguration());
            }

            int strId = resources.getIdentifier("p_str", "string", "com.plugin.test");//com.plugin.test
            Log.i(TAG, "p_str id = " + strId);
            tv.setText(resources.getString(strId));

            int iconId = resources.getIdentifier("p_icon_play", "drawable", "com.plugin.test");
            Log.i(TAG, "p_icon_play id = " + iconId);
//            iv.setImageResource(iconId);
            iv.setImageDrawable(resources.getDrawable(iconId));

            int layoutId = resources.getIdentifier("plugin_item", "layout", "com.plugin.test");
            Log.i(TAG, "layoutId id = " + layoutId);
            if(fl.getChildCount() == 0){
//                View v = View.inflate(fl.getContext(), layoutId, null);
//                fl.addView(v);

                View v = LayoutInflater.from(this).inflate(resources.getLayout(layoutId), fl);
                fl.addView(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hookLoadedApks(){
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            //从ActivityThread的静态方法currentActivityThread()
            Object activityThread = ReflectUtil.callStaticMethod(clazz, "currentActivityThread");

            ArrayMap<String, WeakReference> mPackages = (ArrayMap<String, WeakReference>) ReflectUtil.getFieldValue(activityThread, "mPackages");
            if(mPackages == null){
                Log.i(TAG, "mPackage is null.");
                return;
            }
            if(Build.VERSION.SDK_INT > 18){
                Class aClass = Class.forName("android.app.LoadedApk");
                Field mAppDir = ReflectUtil.getField(aClass, "mAppDir");
                Field mResDir = ReflectUtil.getField(aClass, "mResDir");

                int size = mPackages.size();
                for(int i=0; i<size; i++){
                    String key = mPackages.keyAt(i);
                    Log.i(TAG, " key = " + key);
                    Object obj = mPackages.get(key).get();
                    Log.i(TAG, " mAppDir = " + mAppDir.get(obj));
                    Log.i(TAG, " mResDir = " + mResDir.get(obj));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPluginApk(){
//        String dexPath = getCacheDir() + "/plugin/test_opt.apk";
//        String optimizedDirectory = getCacheDir() + "/apk";
//        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, optimizedDirectory, null, getClassLoader());
        try {
//            Class<?> clazz = dexClassLoader.loadClass("com.plugin.test.PluginActivity");
//            Log.i(TAG, " class = " + clazz);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(getPackageName(), "com.plugin.test.PluginActivity"));
            intent.putExtra("proxy", "org.plugin.demo.ProxyActivity");

            String str = et.getText().toString().trim();
            if("1".equalsIgnoreCase(str)){
                intent.putExtra("type", "1");
            }else{
                intent.putExtra("type", str);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), "fail to start plugin Activity!", Toast.LENGTH_SHORT).show();
        }
    }

//    private void printInfo() {
//        ClassLoader cl = getClassLoader();
//        Log.i(tag, "getClassLoader(): " + cl);
//
//        while(cl.getParent() != null){
//            cl = cl.getParent();
//            Log.i(tag, "parent classLoader: " + cl);
//        }
//
//        cl = ClassLoader.getSystemClassLoader();
//
//        Log.i(tag, "System classLoader: " + cl);
//
//        while(cl.getParent() != null){
//            cl = cl.getParent();
//            Log.i(tag, "parent classLoader: " + cl);
//        }
//
//        Log.i(tag, "Activity classLoader: " + Activity.class.getClassLoader());
//
//        Log.i(tag, "Integer classLoader: " + Integer.class.getClassLoader());
//
//        Log.i(tag, "CardView classLoader: " + Fragment.class.getClassLoader());
//
//        Log.i(tag, "HookUtil classLoader: " + HookUtil.class.getClassLoader());
//
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}





//Process: org.plugin, PID: 23275
//        java.lang.RuntimeException: Unable to instantiate activity ComponentInfo{org.plugin/com.plugin.test.PluginActivity}: java.lang.ClassNotFoundException: Didn't find class "com.plugin.test.PluginActivity" on path: DexPathList[[zip file "/data/app/org.plugin-1/base.apk"],nativeLibraryDirectories=[/vendor/lib64, /system/lib64]]
//        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2472)
//        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2647)
//        at android.app.ActivityThread.access$800(ActivityThread.java:193)
//        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1485)
//        at android.os.Handler.dispatchMessage(Handler.java:111)
//        at android.os.Looper.loop(Looper.java:194)
//        at android.app.ActivityThread.main(ActivityThread.java:5759)
//        at java.lang.reflect.Method.invoke(Native Method)
//        at java.lang.reflect.Method.invoke(Method.java:372)
//        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1042)
//        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:837)
//        Caused by: java.lang.ClassNotFoundException: Didn't find class "com.plugin.test.PluginActivity" on path: DexPathList[[zip file "/data/app/org.plugin-1/base.apk"],nativeLibraryDirectories=[/vendor/lib64, /system/lib64]]
//        at dalvik.system.BaseDexClassLoader.findClass(BaseDexClassLoader.java:56)
//        at java.lang.ClassLoader.loadClass(ClassLoader.java:511)
//        at java.lang.ClassLoader.loadClass(ClassLoader.java:469)
//        at android.app.Instrumentation.newActivity(Instrumentation.java:1071)
//        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2449)
//        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2647) 
//        at android.app.ActivityThread.access$800(ActivityThread.java:193) 
//        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1485) 
//        at android.os.Handler.dispatchMessage(Handler.java:111) 
//        at android.os.Looper.loop(Looper.java:194) 
//        at android.app.ActivityThread.main(ActivityThread.java:5759) 
//        at java.lang.reflect.Method.invoke(Native Method) 
//        at java.lang.reflect.Method.invoke(Method.java:372) 
//        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1042) 
//        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:837) 
//        Suppressed: java.lang.ClassNotFoundException: Didn't find class "com.plugin.test.PluginActivity" on path: DexPathList


//
//getClassLoader(): dalvik.system.PathClassLoader[
// DexPathList[[zip file "/data/app/org.plugin-1/base.apk"],
// nativeLibraryDirectories=[/vendor/lib64, /system/lib64]]]
//parent classLoader: java.lang.BootClassLoader@3cdf9f5b

//System classLoader: dalvik.system.PathClassLoader[
// DexPathList[[directory "."],nativeLibraryDirectories=[/vendor/lib64, /system/lib64]]]
//parent classLoader: java.lang.BootClassLoader@3cdf9f5b

//Activity classLoader: java.lang.BootClassLoader@3cdf9f5b
//Integer classLoader: java.lang.BootClassLoader@3cdf9f5b

//CardView classLoader: dalvik.system.PathClassLoader[
// DexPathList[[zip file "/data/app/org.plugin-1/base.apk"],
// nativeLibraryDirectories=[/vendor/lib64, /system/lib64]]]
//HookUtil classLoader: dalvik.system.PathClassLoader[
// DexPathList[[zip file "/data/app/org.plugin-1/base.apk"],
// nativeLibraryDirectories=[/vendor/lib64, /system/lib64]]]




//树形打印命令
//find . -print | sed -e 's;[^/]*/;|____;g;s;____|; |;g'

// 1
//../../../devTools/aapt/aapt package -f -m --apk-module 0x8f -J gen -S res -M AndroidManifest.xml -I ../../../devTools/android/android-sdk-macosx/platforms/android-23/android.jar -F build/out/resources.ap_



// 2.0 切换JDK环境

//export JAVA_7_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home
//export JAVA_HOME=$JAVA_7_HOME

// 2.1 编译代码
//javac -encoding GBK -bootclasspath ../../../devTools/android/android-sdk-macosx/platforms/android-23/android.jar -d ./build/out ./gen/com/plugin/test/R.java ./src/com/plugin/test/*.java



// 3 转换class为dex
//../../../devTools/android/android-sdk-macosx/build-tools/23.0.1/dx --dex --output=build/out/classes.dex build/out/



// 4 合并dex和资源包
//java -cp ../../../devTools/android/android-sdk-macosx/tools/lib/sdklib.jar com.android.sdklib.build.ApkBuilderMain build/out/test.apk -u -v -z build/out/resources.ap_ -f build/out/classes.dex


// 5 签名
//jarsigner -verbose -keystore test.keystore -storepass 123456 -keypass 123456 -signedjar build/out/test_s.apk build/out/test.apk test

// 6 优化
//../../../devTools/android/android-sdk-macosx/build-tools/23.0.1/zipalign -f 4 build/out/test_s.apk build/out/test_opt.apk

