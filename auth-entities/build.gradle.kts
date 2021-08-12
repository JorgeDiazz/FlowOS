plugins {
  id("java-library")
  id("kotlin")
  kotlin("kapt")
}

dependencies {
  implementation(Libraries.kotlinJDK)

  implementation(project(":base"))

  implementation(Libraries.javaInject)
  implementation(Libraries.rxJava)

  implementation(Libraries.dagger)
  implementation(Libraries.daggerAndroid)

  implementation(Libraries.moshi)
  kapt(AnnotationProcessors.moshiCodegen)

  Libraries.suiteTest.forEach { testImplementation(it) }
}
