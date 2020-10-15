package com.gujun.nativeappcontainsflutter.app;

import android.content.Context;
import android.os.Build;

import com.gujun.nativeappcontainsflutter.utils.PageRouter;
import com.gujun.nativeappcontainsflutter.utils.TextPlatformViewFactory;
import com.idlefish.flutterboost.FlutterBoost;
import com.idlefish.flutterboost.Platform;
import com.idlefish.flutterboost.Utils;
import com.idlefish.flutterboost.interfaces.INativeRouter;

import java.util.Map;

import io.flutter.app.FlutterApplication;
import io.flutter.embedding.android.FlutterView;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;

/**
 * author : gujun
 * date   : 2020/10/13 9:57
 * desc   : 初始化flutter_boost
 */
public class MyApplication extends FlutterApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initFlutterBoost();
    }

    private void initFlutterBoost() {
        Platform platform = new FlutterBoost
                .ConfigBuilder(this, router)
                .isDebug(true)
                .dartEntrypoint("mainDev") //dart入口，默认为main函数，这里可以根据native的环境自动选择Flutter的入口函数来统一Native和Flutter的执行环境，（比如debugMode == true ? "mainDev" : "mainProd"，Flutter的main.dart里也要有这两个对应的入口函数）
                .whenEngineStart(FlutterBoost.ConfigBuilder.ANY_ACTIVITY_CREATED)
                .renderMode(FlutterView.RenderMode.texture)
                .lifecycleListener(boostLifecycleListener)
                .build();
        // 此处会触发flutter的dart入口函数方法，
        // 此处还进行了FlutterEngine的创建
        // 此处还进行了GeneratedPluginRegistrant的registerWith调用，所有第三方的flutter插件(这些插件多数其实也是通过channel调用原生来解决的)，这是创建flutter_boost自带的channel,如果自定义的可以在onEngineCreated回调中添加
        FlutterBoost.instance().init(platform);

    }

    private INativeRouter router = new INativeRouter() {
        @Override
        public void openContainer(Context context, String url, Map<String, Object> urlParams, int requestCode, Map<String, Object> exts) {
            //只要是在Flutter页面中调用FlutterBoost.singleton.open()就会触发此回调,flutter页面打开原生页面或者是flutter页面
            String assembleUrl = Utils.assembleUrl(url, urlParams);
            //PageRouter为根据url进行页面跳转的工具类
            PageRouter.openPageByUrl(context, assembleUrl, urlParams);
        }

    };

    // 生命周期监听
    private FlutterBoost.BoostLifecycleListener boostLifecycleListener = new FlutterBoost.BoostLifecycleListener() {

        @Override
        public void beforeCreateEngine() {

        }

        @Override
        public void onEngineCreated() {
            // Application创建后就会执行此回调
            // 引擎创建后的操作，比如自定义MethodChannel，PlatformView等

            // 注册MethodChannel，监听flutter侧的getPlatformVersion调用,获取native平台的数据用MethodChannel
            MethodChannel methodChannel = new MethodChannel(FlutterBoost.instance().engineProvider().getDartExecutor(), "flutter_native_channel");
            methodChannel.setMethodCallHandler((call, result) -> {

                if (call.method.equals("getPlatformVersion")) {
                    result.success(Build.VERSION.RELEASE);
                } else {
                    result.notImplemented();
                }

            });

            // 注册PlatformView viewTypeId要和flutter中的viewType对应，使用native平台的view用如下方式PlatformView,TextPlatformViewFactory为自定义的工具类
            FlutterBoost
                    .instance()
                    .engineProvider()
                    .getPlatformViewsController()
                    .getRegistry()
                    .registerViewFactory("plugins.test/view", new TextPlatformViewFactory(StandardMessageCodec.INSTANCE));


        }

        @Override
        public void onPluginsRegistered() {

        }

        @Override
        public void onEngineDestroy() {

        }

    };
}
