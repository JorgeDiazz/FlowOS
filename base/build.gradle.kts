plugins {
  id("java-library")
  id("kotlin")
  id("jacoco")
  id("plugins.jacoco-report")
  kotlin("kapt")
}

dependencies {
  implementation(project(":sensors-entities"))

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
      instructions = 3.04,
      lines = 2.78,
      complexity = 5.56,
      methods = 3.51,
      classes = 5.26
    ),
    emptyList(),
    emptyList()
  )
}
