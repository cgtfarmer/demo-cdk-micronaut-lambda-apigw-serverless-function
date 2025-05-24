import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

// Usage: gradle build -PoutDir=/tmp
val outDir: String? by project
outDir?.let {
  layout.buildDirectory.set(file(it))
}

// Usage: gradle test -Ptags="unit,integration,application,system"
val tags: String? by project

plugins {
  id("io.micronaut.library") version "4.5.3"
  id("com.gradleup.shadow") version "8.3.6"
  id("com.diffplug.spotless") version "7.0.3"
}

group = "com.cgtfarmer"
version = "0.0.1"

tasks.named<Jar>("jar") {
  enabled = false
}

java {
  sourceCompatibility = JavaVersion.toVersion("21")
  targetCompatibility = JavaVersion.toVersion("21")
}

repositories {
  mavenCentral()
}

dependencies {
  // General
  compileOnly("org.projectlombok:lombok:1.18.34")
  annotationProcessor("org.projectlombok:lombok:1.18.34")
  implementation("org.apache.commons:commons-lang3:3.14.0")
  runtimeOnly("org.yaml:snakeyaml")

  // Micronaut
  annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
  implementation("com.amazonaws:aws-lambda-java-events")
  implementation("io.micronaut.aws:micronaut-aws-apigateway")
  implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")
  implementation("io.micronaut.aws:micronaut-function-aws")
  implementation("io.micronaut.crac:micronaut-crac")
  implementation("io.micronaut.serde:micronaut-serde-jackson")
  runtimeOnly("ch.qos.logback:logback-classic")
}

micronaut {
  version("4.8.2")
  runtime("lambda_java")
  testRuntime("junit5")
  nativeLambda {
    lambdaRuntimeClassName = "io.micronaut.function.aws.runtime.MicronautLambdaRuntime"
  }
  processing {
    incremental(true)
    annotations("com.cgtfarmer.app.*")
  }
}

spotless {
  java {
    eclipse("4.35")
      .configFile("eclipse-formatter.xml")
    // googleJavaFormat("1.26.0")
    removeUnusedImports()
    importOrder()
    target("src/**/*.java")
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
  dependsOn(tasks.named("spotlessApply"))
}

tasks.withType<Test> {
  useJUnitPlatform {
    tags
      ?.split(",")
      ?.map(String::trim)
      ?.filter(String::isNotEmpty)
      ?.takeIf(List<String>::isNotEmpty)
      ?.let { includeTags(*it.toTypedArray()) }
  }

  testLogging {
    events(
      TestLogEvent.FAILED,
      TestLogEvent.SKIPPED,
      TestLogEvent.STANDARD_ERROR,
      TestLogEvent.STANDARD_OUT
    )
    exceptionFormat = TestExceptionFormat.FULL
    showExceptions  = true
    showCauses      = true
    showStackTraces = true

    info.events          = debug.events
    info.exceptionFormat = debug.exceptionFormat
  }

  addTestListener(object : TestListener {
    override fun beforeSuite(suite: TestDescriptor) {
      if (!suite.name.startsWith("Gradle Test")) {
        println("\nRunning ${suite.name}")
      }
    }

    override fun afterSuite(
      suite: TestDescriptor,
      result: TestResult
    ) {
      val duration =
        "%.3f sec".format((result.endTime - result.startTime) / 1000.0)

      if (suite.name.startsWith("Gradle Test Executor")) {
        val summary =
          "Result: ${result.resultType} " +
          "(${result.testCount} tests, " +
          "${result.successfulTestCount} passed, " +
          "${result.failedTestCount} failed, " +
          "${result.skippedTestCount} skipped) $duration"
        val bar = "-".repeat(summary.length + 4)
        println("\n$bar\n|  $summary  |\n$bar")
      } else if (!suite.name.startsWith("Gradle Test")) {
        println(
          "Tests run: ${result.testCount}, " +
          "Failures: ${result.failedTestCount}, Errors: 0, " +
          "Skipped: ${result.skippedTestCount} Time elapsed: $duration"
        )
      }
    }

    override fun beforeTest(desc: TestDescriptor) {}
    override fun afterTest(desc: TestDescriptor, result: TestResult) {}
  })

  // Rerun tests even when files haven't changed
  outputs.upToDateWhen { false }
}

tasks.named("assemble") {
  dependsOn("shadowJar")
}
