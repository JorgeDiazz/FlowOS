plugins {
  id("com.android.library")
  id("de.mannodermaus.android-junit5")
  id("jacoco")
  id("plugins.jacoco-report")
  kotlin("android")
  kotlin("kapt")
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
  implementation(project(":base"))
  implementation(project(":components"))
  implementation(project(":core"))

  implementation(Libraries.constraintLayout)
  implementation(Libraries.material)
  implementation(Libraries.fragmentKtx)
  implementation(Libraries.lifeCycleCommonJava8)

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
      instructions = 47.08,
      lines = 49.22,
      complexity = 30.43,
      methods = 33.33,
      classes = 57.14
    ),
    emptyList(),
    emptyList()
  )
}
