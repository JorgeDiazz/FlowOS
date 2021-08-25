plugins {
  id("com.android.library")
  id("de.mannodermaus.android-junit5")
  id("jacoco")
  id("plugins.jacoco-report")
  kotlin("android")
  kotlin("kapt")
  kotlin("plugin.parcelize")
  id("kotlin-android")
}

android {
  compileSdkVersion(Api.compileSDK)
  defaultConfig {
    minSdkVersion(Api.minSDK)
    targetSdkVersion(Api.targetSDK)
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }
  lintOptions {
    isAbortOnError = false
  }

  buildFeatures {
    dataBinding = true
    viewBinding = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

dependencies {
  implementation(project(":auth-domain"))
  implementation(project(":auth-entities"))
  implementation(project(":base"))
  implementation(project(":components"))
  implementation(project(":core"))
  implementation(project(":core-test"))
  implementation(project(":sensors"))
  implementation(project(":sensors-entities"))

  implementation(Libraries.multidex)

  implementation(Libraries.kotlinJDK)
  implementation(Libraries.appcompat)
  implementation(Libraries.activityKtx)
  implementation(Libraries.fragmentKtx)
  implementation(Libraries.androidXCore)
  implementation(Libraries.lifeCycleCommonJava8)
  implementation(Libraries.constraintLayout)
  implementation(Libraries.material)

  implementation(Libraries.dagger)
  implementation(Libraries.daggerAndroid)
  implementation(Libraries.daggerAndroidSupport)

  kapt(AnnotationProcessors.dagger)
  kapt(AnnotationProcessors.daggerAndroid)

  implementation(Libraries.rxJava)
  implementation(Libraries.rxAndroid)

  Libraries.suiteTest.forEach { testImplementation(it) }

  androidTestImplementation(Libraries.jUnitExtKtx)
  androidTestImplementation(Libraries.espressoCore)
}

afterEvaluate {
  val function =
    extra.get("generateCheckCoverageTasks") as (File, String, Coverage, List<String>, List<String>) -> Unit
  function.invoke(
    buildDir,
    "testDebugUnitTest",
    Coverage(
      instructions = 53.07,
      lines = 53.26,
      complexity = 31.01,
      methods = 31.86,
      classes = 51.72
    ),
    emptyList(),
    emptyList()
  )
}
