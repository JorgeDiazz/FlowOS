plugins {
  id("com.android.application")
  id("de.mannodermaus.android-junit5")
  id("jacoco")
  id("plugins.jacoco-report")
  id("com.google.gms.google-services")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(Api.compileSDK)

  defaultConfig {
    applicationId = "com.flowos.app"
    minSdkVersion(Api.minSDK)
    targetSdkVersion(Api.targetSDK)
    versionCode = getNewVersionCode()
    versionName = getNewVersionName()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArguments += mapOf(
      "disableAnalytics" to "true",
      "clearPackageData" to "true"
    )
    multiDexEnabled = true
  }
  signingConfigs {
    getByName("debug") {
      val defaultPassword = "android"
      keyAlias = "android_celerik"
      keyPassword = defaultPassword
      storeFile = file("debug-keystore.jks")
      storePassword = defaultPassword
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
        "multidex-config.pro"
      )
    }
    getByName("debug") {
      isTestCoverageEnabled = true
      isPseudoLocalesEnabled = true
      signingConfig = signingConfigs.getByName("debug")
    }
  }

  flavorDimensions("version", "target")
  productFlavors {
    create("staging") {
      dimension("version")
      applicationIdSuffix = ".staging"
      versionNameSuffix = "-Staging"
      manifestPlaceholders["scheme"] = "flowos.staging"
      buildConfigField("String", "SCHEME", "\"${manifestPlaceholders["scheme"]}\"")
      buildConfigField("String", "BASE_URL", "\"https://staging.base.url.com\"")
    }
    create("production") {
      dimension("version")
      manifestPlaceholders["scheme"] = "flowos.production"
      buildConfigField("String", "SCHEME", "\"${manifestPlaceholders["scheme"]}\"")
      buildConfigField("String", "BASE_URL", "\"https://production.base.url.com\"")
    }
    create("internal") {
      dimension("target")
    }
    create("external") {
      dimension("target")
    }

    variantFilter {
      val names = flavors.map { it.name }
      if (names.contains("external") && names.contains("staging")) {
        ignore = true
      }
    }
  }

  lintOptions {
    isAbortOnError = false
  }

  buildFeatures {
    dataBinding = true
    viewBinding = true
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isReturnDefaultValues = true
    animationsDisabled = true
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
  }
}

dependencies {
  implementation(project(":auth"))
  implementation(project(":auth-domain"))
  implementation(project(":base"))
  implementation(project(":components"))
  implementation(project(":core"))
  implementation(project(":core-test"))
  implementation(project(":sensors"))
  implementation(project(":sensors-domain"))
  implementation(project(":sensors-entities"))

  implementation(Libraries.multidex)

  kapt(AnnotationProcessors.dagger)
  kapt(AnnotationProcessors.daggerAndroid)
  kapt(AnnotationProcessors.moshiCodegen)

  implementation(platform(Libraries.firebaseBoM))
  implementation(Libraries.firebaseAnalytics)

  implementation(Libraries.kotlinJDK)
  implementation(Libraries.appcompat)
  implementation(Libraries.androidXCore)
  implementation(Libraries.constraintLayout)
  implementation(Libraries.material)
  implementation(Libraries.preferences)
  coreLibraryDesugaring(Libraries.desugarJdkLibs)

  implementation(Libraries.dagger)
  implementation(Libraries.daggerAndroid)
  implementation(Libraries.daggerAndroidSupport)

  implementation(Libraries.room)

  implementation(Libraries.glide)

  implementation(Libraries.rxJava)
  implementation(Libraries.rxAndroid)

  implementation(Libraries.moshi)

  implementation(Libraries.retrofit)
  implementation(Libraries.retrofitMoshi)
  implementation(Libraries.retrofitRxJava)
  implementation(platform(Libraries.okHttpBoM))
  implementation(Libraries.okHttpInterceptor)
  implementation(Libraries.okHttp)

  implementation(Libraries.lifeCycleProcess)
  implementation(Libraries.lifeCycleCommonJava8)

  implementation(Libraries.timber)

  debugImplementation(Libraries.leakCanary)

  kaptTest(AnnotationProcessors.moshiCodegen)
  testImplementation(Libraries.mockWebServer)
  Libraries.suiteTest.forEach { testImplementation(it) }
  testRuntimeOnly(Libraries.jUnit5Engine)

  debugImplementation(Libraries.okReplay)
  releaseImplementation(Libraries.okReplayNoop)
  androidTestImplementation(Libraries.okReplayEspresso)

  androidTestImplementation(Libraries.roomTesting)
  androidTestImplementation(Libraries.jUnitExtKtx)
  androidTestImplementation(Libraries.testCoreKtx)
  androidTestImplementation(Libraries.androidXRunner)
  androidTestImplementation(Libraries.espressoCore)
  androidTestImplementation(Libraries.androidXRules)
  androidTestImplementation(Libraries.barista) {
    exclude(group = "org.jetbrains.kotlin")
  }
  androidTestUtil(Libraries.orchestrator)
}

fun getNewVersionCode(): Int {
  val versionCode = if (project.hasProperty("version_code")) {
    project.properties["version_code"].toString().toIntOrNull()
  } else {
    null
  }
  return versionCode ?: 32
}

fun getNewVersionName(): String {
  return if (project.hasProperty("version_name")) {
    project.properties["version_name"].toString()
  } else {
    "v1.0-Dirty"
  }
}

tasks.withType<Test> {
  configure<JacocoTaskExtension> {
    isIncludeNoLocationClasses = true
    excludes = listOf("jdk.internal.*")
  }
}

afterEvaluate {
  val function =
    extra.get("generateCheckCoverageTasks") as (File, String, Coverage, List<String>, List<String>) -> Unit
  function.invoke(
    buildDir,
    "testStagingInternalDebugUnitTest",
    Coverage(
      instructions = 13.41,
      lines = 14.66,
      complexity = 11.72,
      methods = 14.02,
      classes = 18.92
    ),
    listOf("**/tmp/kotlin-classes/stagingInternalDebug/**"),
    emptyList()
  )
}
