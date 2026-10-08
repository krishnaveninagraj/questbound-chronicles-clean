plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // Hilt – version aligned with app/build.gradle.kts (2.51.1)
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    // KSP plugin removed: this project uses kapt, not KSP
}
