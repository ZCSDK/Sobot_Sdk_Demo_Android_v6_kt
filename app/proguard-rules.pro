#############################################
#
# 对于一些基本指令的添加
#
#############################################
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

-ignorewarnings

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

-keepattributes Exceptions

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*


#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View


#adapter也不能混淆
-keep public class * extends android.widget.BaseAdapter { *; }


# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# 保留继承的
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}


# 移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用
# 记得proguard-android.txt中一定不要加-dontoptimize才起作用
# 另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
#-assumenosideeffects class android.util.Log {
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}

#############################################
#
# 项目中特殊处理部分
#
#############################################

#-----------处理反射类---------------



#-----------处理js交互---------------



#-----------处理实体类---------------
# 在开发的时候我们可以将所有的实体类放在一个包内，这样我们写一次混淆就行了。
#-keep public class com.ljd.example.entity.** {
#    public void set*(***);
#    public *** get*();
#    public *** is*();
#}
-keep public class com.sobot.custom.model.**  {
    public void set*(***);
    public *** get*();
    public *** is*();
}

#-----------处理第三方依赖库---------


# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-dontwarn butterknife.**
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


# Glide
-keep class com.bumptech.glide.Glide { *; }
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Gson
-keep class com.google.gson.stream.** { *; }
# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
# 将下面替换成自己的实体类
-keep class com.sobot.custom.model.** { *; }
-dontwarn com.google.gson.**

#sobot SDK
-keep class com.sobot.chat.** {*;}
-dontwarn com.sobot.chat.**

# OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

## ----------------------------------
##      Glide相关
## ----------------------------------
-keep class com.bumptech.glide.Glide { *; }
-keep class com.bumptech.glide.request.RequestOptions {*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**

## ----------------------------------
##      Picasso相关
## ----------------------------------
-keep class com.squareup.picasso.Picasso { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.**
