# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# ============== common ============== #

# 忽略警告，避免打包时某些警告出现
-ignorewarnings
-dontwarn sun.**
-dontwarn javax.**
-dontwarn java.awt.**
-dontwarn org.apache.**
-android

# 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify

-keepattributes *Annotation*,Signature,InnerClasses,SourceFile,LineNumberTable

# 改变作用域提高优化效果
-allowaccessmodification

# 合并相关接口
-mergeinterfacesaggressively

# 指定压缩级别
-optimizationpasses 7

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
    public static final android.os.Parcelable$Creator *;
}

# R文件混淆会导致引用错误
-keepclassmembers class **.R$*
-keep public class **.BuildConfig{*;}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep public class * extends android.os.Bundle
-keep public class * extends android.view.View
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.Fragment

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

# Fragment
-dontnote android.support.v4.app.Fragment
-keepclassmembers class android.support.v4.app.Fragment {
    public android.view.View getView();
}

-dontnote android.support.v4.app.FragmentManager
-keepclassmembers class android.support.v4.app.FragmentManager {
    public android.support.v4.app.Fragment findFragmentById(int);
    public android.support.v4.app.Fragment findFragmentByTag(java.lang.String);
}

# 移除系统日志
-assumenosideeffects class android.util.Log{
   public static boolean isLoggable(java.lang.String,int);
   public static int v(...);
   public static int i(...);
   public static int w(...);
   public static int d(...);
   public static int e(...);
}

##############################保留Android support控件######################################

# view res/layout/design_navigation_item.xml #generated:17
-keep class android.support.design.internal.NavigationMenuItemView { <init>(...); }

# view res/layout/design_navigation_menu.xml #generated:17
-keep class android.support.design.internal.NavigationMenuView { <init>(...); }

# view res/layout/app_bar_main.xml #generated:11
-keep class android.support.design.widget.AppBarLayout { <init>(...); }

# view res/layout/app_bar_main.xml #generated:2
# view res/layout/design_bottom_sheet_dialog.xml #generated:17
-keep class android.support.design.widget.CoordinatorLayout { <init>(...); }

# view res/layout/app_bar_main.xml #generated:27
-keep class android.support.design.widget.FloatingActionButton { <init>(...); }

# view res/layout/activity_main.xml #generated:17
-keep class android.support.design.widget.NavigationView { <init>(...); }

# view res/layout/design_layout_snackbar.xml #generated:18
# view sw600dp-v13\res/layout-sw600dp-v13/design_layout_snackbar.xml #generated:18
-keep class android.support.design.widget.Snackbar$SnackbarLayout { <init>(...); }

# view res/layout/activity_main.xml #generated:2
-keep class android.support.v4.widget.DrawerLayout { <init>(...); }

# view res/layout/abc_alert_dialog_material.xml #generated:75
-keep class android.support.v4.widget.NestedScrollView { <init>(...); }

# view res/layout/abc_alert_dialog_button_bar_material.xml #generated:40
-keep class android.support.v4.widget.Space { <init>(...); }

# view res/layout/abc_action_menu_item_layout.xml #generated:17
-keep class android.support.v7.view.menu.ActionMenuItemView { <init>(...); }

# view res/layout/abc_expanded_menu_layout.xml #generated:17
-keep class android.support.v7.view.menu.ExpandedMenuView { <init>(...); }

# view res/layout/abc_list_menu_item_layout.xml #generated:17
# view res/layout/abc_popup_menu_item_layout.xml #generated:17
-keep class android.support.v7.view.menu.ListMenuItemView { <init>(...); }

# view res/layout/abc_screen_toolbar.xml #generated:27
-keep class android.support.v7.widget.ActionBarContainer { <init>(...); }

# view res/layout/abc_action_mode_bar.xml #generated:19
# view res/layout/abc_screen_toolbar.xml #generated:43
-keep class android.support.v7.widget.ActionBarContextView { <init>(...); }

# view res/layout/abc_screen_toolbar.xml #generated:17
-keep class android.support.v7.widget.ActionBarOverlayLayout { <init>(...); }

# view res/layout/abc_action_menu_layout.xml #generated:17
-keep class android.support.v7.widget.ActionMenuView { <init>(...); }

# view res/layout/abc_activity_chooser_view.xml #generated:19
-keep class android.support.v7.widget.ActivityChooserView$InnerLayout { <init>(...); }

# view res/layout/abc_alert_dialog_button_bar_material.xml #generated:18
-keep class android.support.v7.widget.ButtonBarLayout { <init>(...); }

# view res/layout/abc_screen_content_include.xml #generated:19
-keep class android.support.v7.widget.ContentFrameLayout { <init>(...); }

# view res/layout/abc_alert_dialog_material.xml #generated:48
-keep class android.support.v7.widget.DialogTitle { <init>(...); }

# view res/layout/abc_screen_simple_overlay_action_mode.xml #generated:23
-keep class android.support.v7.widget.FitWindowsFrameLayout { <init>(...); }

# view res/layout/abc_dialog_title_material.xml #generated:22
# view res/layout/abc_screen_simple.xml #generated:17
-keep class android.support.v7.widget.FitWindowsLinearLayout { <init>(...); }

# view res/layout/abc_search_view.xml #generated:78
-keep class android.support.v7.widget.SearchView$SearchAutoComplete { <init>(...); }

# view res/layout/abc_screen_toolbar.xml #generated:36
# view res/layout/app_bar_main.xml #generated:16
-keep class android.support.v7.widget.Toolbar { <init>(...); }

# view res/layout/abc_screen_simple.xml #generated:25
# view res/layout/abc_screen_simple_overlay_action_mode.xml #generated:32
-keep class android.support.v7.widget.ViewStubCompat { <init>(...); }

-keep class com.fangtang.tv.systrace.TraceTag{*;}


# LeanCloud

-dontwarn com.avos.**
-keep class com.avos.** { *;}

-keepattributes Signature
-dontwarn com.jcraft.jzlib.**
-keep class com.jcraft.jzlib.**  { *;}

# FastJson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *;}

#-dontwarn sun.misc.**
#-keep class sun.misc.** { *;}


#-dontwarn sun.security.**
#-keep class sun.security.** { *; }



# Retrofit2.
-dontnote retrofit2.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-dontwarn com.google.**
-keep class com.google.** { *;}

-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

-dontwarn android.support.**

-dontwarn org.apache.**
-keep class org.apache.** { *;}

-dontwarn org.jivesoftware.smack.**
-keep class org.jivesoftware.smack.** { *;}

-dontwarn com.loopj.**
-keep class com.loopj.** { *;}

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }

-dontwarn okio.**

-dontwarn org.xbill.**
-keep class org.xbill.** { *;}


# bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


#友盟
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

# GSON.
-keep,allowobfuscation class com.google.gson.annotations.*

-dontnote com.google.gson.annotations.Expose
-keepclassmembers class * {
    @com.google.gson.annotations.Expose <fields>;
}

-dontnote com.google.gson.annotations.SerializedName
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.blankj.** { *;}

# EventBus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

# 欢网SDK
-dontwarn com.huanad.android.volley.** 
-keep class com.huanad.android.volley.** {*;}
  -dontwarn tv.adsdk.ad.** 
-keep class tv.adsdk.ad.** {*;}
  -dontwarn com.alibaba.fastjson.**  
-keep class com.alibaba.fastjson.** { *; }
