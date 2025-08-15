plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.build.logic.library.kmp)
    alias(libs.plugins.build.logic.library.android)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.shared)
                api(projects.sharedUi)

                api(compose.foundation)
                api(compose.material3)
            }
        }
    }
}

android {
    namespace = "dev.sdkforge.pdf.app"
}
