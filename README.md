# Plugin
一个Apk插件工程，目前只支持Android 5.0-7.1

# 原理

动态加载Apk资源
通过AssetManager的addAssetPath()方法将apk中的资源包添加到应用的资源管理器中，这样就可以使用getResources()去获取添加的资源。

动态加载Apk中的dex
通过DexClassLoader加载apk，使用反射获取dexElements，并将其与应用加载器中的dexElements合并，将合并之后的新Element[]设置给应用加载器。
