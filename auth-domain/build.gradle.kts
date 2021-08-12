plugins {
  id("java-library")
  id("kotlin")
  id("jacoco")
  id("plugins.jacoco-report")
  kotlin("kapt")
}

dependencies {
  implementation(Libraries.kotlinJDK)

  implementation(project(":auth-entities"))
  implementation(project(":base"))

  implementation(Libraries.javaInject)
  implementation(Libraries.rxJava)

  implementation(Libraries.retrofit)
  implementation(Libraries.retrofitMoshi)
  implementation(Libraries.retrofitRxJava)

  Libraries.suiteTest.forEach { testImplementation(it) }
}

afterEvaluate {
  val function =
    extra.get("generateCheckCoverageTasks") as (File, String, Coverage, List<String>, List<String>) -> Unit
  function.invoke(
    buildDir,
    "test",
    Coverage(
      instructions = 76.19,
      lines = 88.31,
      complexity = 76.92,
      methods = 76.92,
      classes = 80.0
    ),
    emptyList(),
    emptyList()
  )
}
