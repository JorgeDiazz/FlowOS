plugins {
  id("java-library")
  id("kotlin")
  kotlin("kapt")
}

dependencies {
  implementation(project(":base"))
  implementation(project(":sensors-entities"))

  implementation(Libraries.kotlinJDK)

  implementation(Libraries.javaInject)
  implementation(Libraries.rxJava)

  implementation(Libraries.dagger)
  implementation(Libraries.daggerAndroid)

  implementation(Libraries.moshi)
  kapt(AnnotationProcessors.moshiCodegen)

  Libraries.suiteTest.forEach { testImplementation(it) }
}
