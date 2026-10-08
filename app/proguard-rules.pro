# EdgeChildAI ProGuard Rules
# isMinifyEnabled is currently false, so these rules are not applied during debug/release builds.
# They are included here so the build configuration reference resolves correctly.

# Keep TensorFlow Lite classes
-keep class org.tensorflow.** { *; }
-dontwarn org.tensorflow.**

# Keep Gemini client
-keep class com.google.ai.client.** { *; }
-dontwarn com.google.ai.client.**

# Keep Room entities and DAOs
-keep class com.edgechild.ai.AuditLog* { *; }

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**
