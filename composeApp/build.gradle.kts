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
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }


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

        ios.deploymentTarget = "16.6"

        framework {
            baseName = "ComposeApp"
            isStatic = true

            linkerOpts += listOf("-framework", "AVFoundation")
            linkerOpts += listOf("-framework", "AudioToolbox")
            linkerOpts += listOf("-framework", "CoreAudio")

            linkerOpts += listOf("-framework", "MediaPlayer")
            linkerOpts += listOf("-framework", "CoreMedia")
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

        pod("FirebaseDatabase") {
            version = "~> 11.13"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("GoogleSignIn") {
            version = "~> 8.0.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        // Revenue Cat
        pod("PurchasesHybridCommon") {
            version = libs.versions.purchases.common.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

//        pod("GTMSessionFetcher") {
//            version = "~> 3.3"
//            extraOpts += listOf("-compiler-option", "-fmodules")
//        }
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
            implementation(libs.android.firebase.database.ktx)
            implementation(libs.play.services.auth)
            implementation(libs.sqldelight.android)
            implementation(libs.coroutines.play.services)

            // Google sign-in
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.google.id)

            // Stream-Chat
            implementation(libs.stream.chat.compose)
            implementation(libs.stream.chat.offline)

            // Splash Screen
            implementation(libs.splash.screen)
//            implementation(libs.ktor.logging.jvm)

            // Exoplayer
            implementation(libs.media3.exoplayer)
            implementation(libs.media3.ui)
            implementation(libs.media3.common)

            // Accompanist
            implementation(libs.accompanist.permissions)

            implementation(libs.ktor.client.android)
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
            implementation(libs.ktor.logging)
            implementation(libs.content.negotiation)
            implementation(libs.kotlinx.json)

            //Kermit  for logging
            implementation(libs.kermit)

            // Multiplatform Settings
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.noargs)

            // Kotlinx Date-Time
            implementation(libs.kotlinx.datetime)

            // Korge
//            implementation(libs.korge.core)

            // Coil SVG
            implementation(libs.coil.svg)

            // KmpAuth
            implementation(libs.kmpAuth.google)
            implementation(libs.kmpAuth.firebase)
            implementation(libs.kmpAuth.uihelper)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Haze
            implementation(libs.haze)

            // RevenueCat Purchases
            implementation(libs.purchases.core)
            implementation(libs.purchases.ui)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
            implementation(libs.ktor.client.darwin)
//            implementation(libs.ktor.logging.ios)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // Needed for Revenue Cat KMP SDK
        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }
}

android {
    namespace = "com.judahben149.tala_app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.judahben149.tala_app"
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

    signingConfigs {
        create("release") {
            storeFile = file(secretsProperties["store_path"]?.toString() ?: "")
            storePassword = secretsProperties["store_password"]?.toString() ?: ""
            keyAlias = secretsProperties["key_alias"]?.toString() ?: ""
            keyPassword = secretsProperties["key_password"]?.toString() ?: ""
        }

        getByName("debug") {
            storeFile = file(secretsProperties["store_path"]?.toString() ?: "")
            storePassword = secretsProperties["store_password"]?.toString() ?: ""
            keyAlias = secretsProperties["key_alias"]?.toString() ?: ""
            keyPassword = secretsProperties["key_password"]?.toString() ?: ""
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")

//            applicationIdSuffix = ".dev"
//            versionNameSuffix = "-DEBUG"
        }

        getByName("release") {
            isMinifyEnabled = false
             signingConfig = signingConfigs.getByName("release")

            lint {
                checkReleaseBuilds = false
            }
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
        buildConfigField(STRING, "GEMINI_API_KEY", secretsProperties["GEMINI_API_KEY"]?.toString() ?: "")
        buildConfigField(STRING, "ELEVEN_LABS_API_KEY", secretsProperties["ELEVEN_LABS_API_KEY"]?.toString() ?: "")

        buildConfigField(STRING, "FIREBASE_WEB_CLIENT", secretsProperties["FIREBASE_WEB_CLIENT_DEV"]?.toString() ?: "")
//        buildConfigField(STRING, "FIREBASE_WEB_CLIENT", secretsProperties["FIREBASE_WEB_CLIENT_PROD"]?.toString() ?: "")

        buildConfigField(STRING, "REVENUE_CAT_PLAY_STORE_API_KEY", secretsProperties["REVENUE_CAT_PLAY_STORE_API_KEY"]?.toString() ?: "")
        buildConfigField(STRING, "REVENUE_CAT_APP_STORE_API_KEY", secretsProperties["REVENUE_CAT_APP_STORE_API_KEY"]?.toString() ?: "")
    }
}