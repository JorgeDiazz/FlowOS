plugins {
  id("java-library")
  id("kotlin")
  id("jacoco")
  id("plugins.jacoco-report")
  kotlin("kapt")
}

dependencies {

  implementation(Libraries.kotlinJDK)

  implementation(Libraries.javaInject)
  implementation(Libraries.rxJava)

  implementation(Libraries.moshi)
  kapt(AnnotationProcessors.moshiCodegen)

  kapt(AnnotationProcessors.dagger)
  kapt(AnnotationProcessors.daggerAndroid)

  Libraries.suiteTest.forEach { testImplementation(it) }
}

afterEvaluate {
  val function = extra.get("generateCheckCoverageTasks") as (File, String, Coverage, List<String>, List<String>) -> Unit
  function.invoke(
    buildDir,
    "test",
    Coverage(
      instructions = 3.89,
      lines = 5.41,
      complexity = 6.85,
      methods = 5.17,
      classes = 5.26
    ),
    emptyList(),
    emptyList()
  )
}
