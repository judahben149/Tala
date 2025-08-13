import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.buildkonfig.plugin)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.sqlDelight)
//    alias(libs.plugins.spmForKmp)
}

// Load secrets.properties
val secretsProperties = Properties()
val secretsPropertiesFile = rootProject.file("secrets.properties")
if (secretsPropertiesFile.exists()) {
    secretsProperties.load(secretsPropertiesFile.inputStream())
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }


    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    cocoapods {
        summary = "Tala"
        homepage = "https://github.com/judahben149/Tala"
        version = "1.0"

        podfile = project.file("../iosApp/Podfile")

        ios.deploymentTarget = "16.0"

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        pod("sqlite3")

        pod("FirebaseCore") {
            version = "~> 11.13"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseAuth") {
            version = "~> 11.13"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.decompose)
            implementation(libs.room.runtime.android)
            implementation(project.dependencies.platform(libs.android.firebase.bom))
            implementation(libs.android.firebase.auth)
            implementation(libs.android.firebase.analytics)
            implementation(libs.android.firebase.auth)
            implementation(libs.play.services.auth)
            implementation(libs.sqldelight.android)

            // Stream-Chat
            implementation(libs.stream.chat.compose)
            implementation(libs.stream.chat.offline)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Koin
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Decompose
            implementation(libs.decompose)
            implementation(libs.decompose.compose)
            implementation(libs.serialization.json)

            // Sql Delight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            // kstore
//            implementation(libs.kstore)
//            implementation(libs.kstore.file)
//            implementation(libs.kstore.storage)

            // Ktorfit
            implementation(libs.ktorfit)
            implementation(libs.content.negotiation)
            implementation(libs.kotlinx.json)

            //Kermit  for logging
            implementation(libs.kermit)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.judahben149.tala"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.judahben149.tala"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-DEBUG"
        }

        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    add("kspCommonMainMetadata", libs.ktorfit.compiler)
    add("kspAndroid", libs.ktorfit.compiler)
    add("kspIosSimulatorArm64", libs.ktorfit.compiler)
    add("kspIosX64", libs.ktorfit.compiler)
    add("kspIosArm64", libs.ktorfit.compiler)
}

sqldelight {
    databases {
        create("TalaDatabase") {
            packageName.set("com.judahben149.tala")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/com/judahben149/tala/schemas"))
            migrationOutputDirectory.set(file("src/commonMain/sqldelight/com/judahben149/tala/migrations"))
            verifyMigrations.set(true)
        }
    }

    linkSqlite.set(true)
}

buildkonfig {
    packageName = "com.judahben149.tala"

    defaultConfigs {
        buildConfigField(STRING, "STREAM_API_KEY", secretsProperties["STREAM_API_KEY"]?.toString() ?: "")
        buildConfigField(STRING, "STREAM_CLIENT_SECRET", secretsProperties["STREAM_CLIENT_SECRET"]?.toString() ?: "")
    }
}