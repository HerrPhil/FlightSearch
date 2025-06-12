// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.apply {
        set("room_version", "2.7.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

tasks.register("clean", Delete::class) {
    // following line calls deprecated method, getBuildDir()
//    delete(rootProject.buildDir)
    // following line calls recommended method, getLayout().getBuildDirectory()
    delete(rootProject.layout.buildDirectory)
}
