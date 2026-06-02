# ProGuard rules for OpenFind AI
-keep class openfind.ai.OpenFindApp { *; }
-keep class openfind.ai.MainActivity { *; }
-keep class openfind.ai.domain.model.** { *; }
-keep class openfind.ai.data.local.entity.** { *; }
-keep class openfind.ai.data.native.OpenfindNative { *; }

# General Android rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**
