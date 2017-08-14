package org.plugin.task;

import android.content.Context;

import org.plugin.copy.LoadApk;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 执行动态加载apk和合并相关信息的任务
 *
 * @author liuwh
 */

public class LoadApkTask implements Runnable {

    private static final String APK_SUFFIX = ".apk";

    private String apkPath;
    private String optDir;
    private ClassLoader appClassLoader;
    private Context app;

    /**
     *
     * @param apkPath apk文件或目录路径。（这些文件最好存放再私有目录下）
     * @param optDir 优化的dex存放的目录
     * @param appClassLoader 应用加载器
     */
    public LoadApkTask(String apkPath, String optDir, ClassLoader appClassLoader, Context appContext){
        //
        this.apkPath = apkPath;
        this.optDir = optDir;
        this.appClassLoader = appClassLoader;
        this.app = appContext;
    }

    @Override
    public void run() {
        //
        File file = new File(apkPath);
        File[] files = null;
        if(file.isDirectory()){
            //过滤非apk文件
            files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(APK_SUFFIX);
                }
            });
        } else if(file.getName().endsWith(APK_SUFFIX)){
            files = new File[]{file};
        }

        // TODO 验证这些apk文件的签名
        String[] fileNames = new String[files.length];
        for(int i=0; i<files.length; i++){
            //TODO 验证签名，只有合法的apk才能加载
            fileNames[i] = files[i].getAbsolutePath();
        }

        File optFile = new File(optDir);
        if(!optFile.exists()){
            optFile.mkdirs();
        }
        optFile = null;
        file = null;
        files = null;

        new LoadApk().doLoadAndCopy(fileNames, optDir, appClassLoader);
    }
}
