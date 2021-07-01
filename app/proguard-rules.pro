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
# 需要 LineNumberTable 属性，以消除方法内经过优化的位置之间的歧义
# 如需获取在虚拟机或设备上的堆栈轨迹中输出的行号，则必须使用 SourceFile 属性
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 在gson转化的bean文件
-keep public class com.yy.mediaplayer.net.bean.** { *; }

# Java 原生接口 (JNI) 类
-keep public class com.yy.mediaplayer.model.IMediaPlayer

# RxJava
# RxJava 本身不需要任何 ProGuard/R8 设置
# 自版本 1.0.3 以来，反应流依赖性已在其 JAR 中嵌入 Java 9 类文件，这些文件可能会导致与普通 ProGuard 一起发出警告
-dontwarn java.util.concurrent.Flow*

# Retrofit
# 如果您使用 R8，则自动包含收缩和混淆规则
# 您可能还需要针对 OkHttp和Okio的规则，这些规则是该库的依赖性

# OkHttp3
# 如果您在使用 R8 作为默认编译器的 Android 项目中使用 OkHttp 作为依赖，则不必执行任何操作。特定规则已捆绑到 JAR 中，可自动由 R8 解释

# okio
# 特定规则已捆绑到 JAR 中，可自动由 R8 解释
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

