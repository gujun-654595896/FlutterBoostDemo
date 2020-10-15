package com.gujun.nativeappcontainsflutter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gujun.nativeappcontainsflutter.FlutterFragmentPageActivity;
import com.gujun.nativeappcontainsflutter.NativePageActivity;
import com.idlefish.flutterboost.containers.BoostFlutterActivity;

import java.util.HashMap;
import java.util.Map;

public class PageRouter {
    // 这里可以配置管理Native和Flutter的映射，通过Boost提供的open方法在Flutter打开Native和Flutter页面并传参，或者通过openPageByUrl方法在Native打开Native和Flutter页面并传参。
    // 一定要确保Flutter端registerPageBuilders里注册的路由的key和这里能够一一映射，否则会报page != null的红屏错误

    //flutter页面映射（native/flutter打开flutter页面），key:调用打开页面的url,这个是native端定义的;value:flutter注册的页面的key
    public final static Map<String, String> pageName = new HashMap<String, String>() {{
        put("first", "first");
        put("second", "second");
        put("tab", "tab");
        put("sample://flutterPage", "flutterPage");
    }};

    public static final String NATIVE_PAGE_URL = "sample://nativePage";
    public static final String FLUTTER_PAGE_URL = "sample://flutterPage";
    public static final String FLUTTER_FRAGMENT_PAGE_URL = "sample://flutterFragmentPage";

    public static boolean openPageByUrl(Context context, String url, Map params) {
        return openPageByUrl(context, url, params, 0);
    }

    public static boolean openPageByUrl(Context context, String url, Map params, int requestCode) {

        String path = url.split("\\?")[0];

        Log.i("openPageByUrl",path);

        try {
            if (pageName.containsKey(path)) {
                //打开以BoostFlutterActivity页面为容器的flutter页面
                Intent intent = BoostFlutterActivity.withNewEngine().url(pageName.get(path)).params(params)
                        .backgroundMode(BoostFlutterActivity.BackgroundMode.opaque).build(context);
                if(context instanceof Activity){
                    Activity activity=(Activity)context;
                    activity.startActivityForResult(intent,requestCode);
                }else{
                    context.startActivity(intent);
                }
                return true;
            } else if (url.startsWith(FLUTTER_FRAGMENT_PAGE_URL)) {
                //原生页面
                context.startActivity(new Intent(context, FlutterFragmentPageActivity.class));
                return true;
            } else if (url.startsWith(NATIVE_PAGE_URL)) {
                //原生页面
                context.startActivity(new Intent(context, NativePageActivity.class));
                return true;
            }

            return false;

        } catch (Throwable t) {
            return false;
        }
    }
}