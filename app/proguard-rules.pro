# Add project specific ProGuard rules here.
-keep class com.remapper.app.data.** { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
