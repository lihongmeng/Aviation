# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-ignorewarnings

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference


#androidx配置
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**


-keepclasseswithmembernames class * {
native <methods>;
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
 static final android.os.Parcelable$Creator *;
}

-keep public class * implements java.io.Serializable {
    public *;
}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod

-dontwarn android.support.**
-dontwarn com.alibaba.fastjson.**
-keep class **.model.**{*;}


-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements android.os.Parcelable {
 public <fields>;
 private <fields>;
}


# eventbus
-keepclassmembers class ** {
    public void onEvent*(**);
}

# bugly
-keep public class com.tencent.bugly.**{*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-dontwarn  org.eclipse.jdt.annotation.**
# sdk版本小于18时需要以下配置, 建议使用18或以上版本的sdk编译
-dontwarn  android.location.Location
-dontwarn  android.net.wifi.WifiManager

-dontnote ct.**

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod


# x5
-dontwarn MTT.**
-dontwarn com.tencent.**
-keep class com.tencent.**{*;}
-keep class MTT.**{*;}


# google-exoplayer
-keep class com.google.android.exoplayer.** {*;}


#retrofit
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
#okio
-dontwarn okio.**
#okhttp
-dontwarn okhttp3.**
#rxjava
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

-dontwarn android.support.annotation.Keep
-keepattributes *Annotation*
-keep @android.support.annotation.Keep class **{
    @android.support.annotation.Keep <fields>;
    @android.support.annotation.Keep <methods>;

}

## pptv
#-keep public class com.pplive.videoplayer.SDKConfig {
#    public static <methods>;
#}
#
#-keep public class com.pplive.videoplayer.PPTVVideoView {
#    public <methods>;
#}
#
#-keepclassmembers class com.pplive.videoplayer.PPTVVideoView {
#    public static <fields>;
#}
#
#
#-keep class com.pplive.** {
#    <fields>;
#    <methods>;
#}
#
#
#-keep class android.slkmedia.mediaplayer.** {
#    <fields>;
#    <methods>;
#}
#
#-keep interface android.slkmedia.mediaplayer.** {
#    <fields>;
#    <methods>;
#}
#

#------新加混淆------

# 小米
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.push.** { *; }
#华为
-dontwarn com.huawei.**
-keep public class * extends android.app.Activity
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

-keep interface com.huawei.hms.analytics.type.HAEventType{*;}
-keep interface com.huawei.hms.analytics.type.HAParamType{*;}

#oppo
-dontwarn com.coloros.mcsdk.**
-keep class com.coloros.mcsdk.** { *; }
-keep public class * extends android.app.Service
-keep class com.heytap.msp.** { *;}

#vivo
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }
-keep class com.jxntv.push.vivo.VivoPushReceiver
-keep class **.PushMessageReceiverImpl{*;}

#------pptv 相关------
# pptv  oneplayer
-keep class com.suning.oneplayer.utils.** {*;}
-keep class com.suning.oneplayer.**
-keep class com.suning.oneplayer.commonutils.snstatistics.imp.SNStatsIPlayerImp.* {*;}
-keep class com.pplive.sdk.** {*;}
-keep class com.suning.oneplayer.commonutils.snstatistics.** {<methods>;}
-keep class android.slkmedia.**{*;}
-keep class com.admaster.sdk.**{*;}
-keep class com.miaozhen.mzmonitor.**{*;}
-keep class com.suning.oneplayer.admonitor.AdMonitorWrapperImpl{public *;}
-keep class android.mediastation.**{*;}
-keep class org.dom4j.**{*;}

# feedback
-keep class com.pplive.feedback.** {*;}
-keep public class * extends com.suning.oneplayer.utils.playerkernel.sdk.PlayerKernelWrapper{
    public <init>();
    public <init>(android.content.Context);
}
-keep public class * extends com.suning.oneplayer.utils.unionsdk.sdk.UnionSdkWrapper {
    public <init>();
}
-keep public class * extends com.suning.oneplayer.utils.sastatistic.sdk.SaStatisticWrapper {
    public <init>();
}
# for stream sdk
-keep class com.pplive.streamingsdk.** {*;}
-keep interface com.pplive.streamingsdk.** {*;}
#------pptv 相关 结束------

# superplayer
-keep class com.tencent.** { *; }

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}


#短视频混淆配置
-keep class com.aliyun.**{*;}
-keep class com.duanqu.**{*;}
-keep class com.qu.**{*;}
-keep class com.alibaba.**{*;}
-keep class component.alivc.**{*;}
-keep class com.alivc.**{*;}
