package org.plugin.demo;

import android.app.Application;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import org.plugin.task.LoadApkTask;
import org.plugin.util.FileUtil;
import org.plugin.task.HookTask;

import java.lang.reflect.Method;

/**
 * Created by liu1359041 on 2017/8/6.
 */

public class PluginApp extends Application {
    static final String TAG = "Plugin";
    boolean hasInstallRes = false;

    @Override
    public void onCreate() {
        super.onCreate();
        long time = System.currentTimeMillis();
        installPluginApk();
        //installRes();
        Log.i(TAG, " cost time : " + (System.currentTimeMillis() - time));
    }

    private void installPluginApk() {
        FileUtil.copyApk(this);
        String dir = getCacheDir().getAbsolutePath();
        String apkDir = dir + "/apks";
        String optDir = dir + "/optDir";
        LoadApkTask loadApkTask = new LoadApkTask(apkDir, optDir, getClassLoader(), this);
        loadApkTask.run();
        new HookTask().run();
    }

    public void installRes(AssetManager assetManager){
        if(hasInstallRes)
            return;

        try{
//            assetManager = getAssets();
            Log.i(TAG, " getClassLoader = " + getClassLoader());
            Log.i(TAG, " resources = " + getResources());
            Log.i(TAG, " app assertManager = " + getAssets());
            Method method = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            if(!method.isAccessible()){
                method.setAccessible(true);
            }
            String pluginPath = getCacheDir().getAbsolutePath() + "/apks/test.apk";
            int cook = (int) method.invoke(assetManager, pluginPath);
            Log.i(TAG, "cook = " + cook);
            if(cook == 0){
                Log.e(TAG, "fail to add plugin apk(" + pluginPath + ") to asset path !");
                return;
            }else{
                hasInstallRes = true;
            }

            Method ensureStringBlocks = assetManager.getClass().getDeclaredMethod("ensureStringBlocks");
            if(!ensureStringBlocks.isAccessible()){
                ensureStringBlocks.setAccessible(true);
            }
            ensureStringBlocks.invoke(assetManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void hook(){
//        System.out.println(" ------ call hook ------ ");
//        AsyncTask.THREAD_POOL_EXECUTOR.execute(new HookTask());
//    }
}

