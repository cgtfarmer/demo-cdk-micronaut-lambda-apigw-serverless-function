import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.tasks.Jar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Usage: gradle build -PoutDir=/tmp
val outDir: String? by project
outDir?.let {
  layout.buildDirectory.set(file(it))
}

// Usage: gradle test -Ptags="unit,integration,application,system"
val tags: String? by project

plugins {
  java
  id("com.gradleup.shadow") version "8.3.6"
  id("com.diffplug.spotless") version "7.0.3"
  id("io.micronaut.library") version "4.5.3"
}

group = "com.cgtfarmer"
version = "0.0.1"

tasks.named<Jar>("jar") {
  archiveBaseName.set("app")
  archiveVersion.set("0.0.1")
  enabled = false
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

repositories {
  mavenCentral()
}

dependencies {
  compileOnly("org.projectlombok:lombok:1.18.34")
  annotationProcessor("org.projectlombok:lombok:1.18.34")

  implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

  implementation("org.apache.commons:commons-lang3:3.14.0")
  implementation("software.amazon.awssdk:s3:2.31.24")

  implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
  implementation("com.amazonaws:aws-lambda-java-events:3.11.6")

  implementation(platform("software.amazon.awssdk:bom:2.25.65"))
  implementation("software.amazon.awssdk:dynamodb-enhanced")

  // implementation("com.google.dagger:dagger:2.56.1")
  // annotationProcessor("com.google.dagger:dagger-compiler:2.56.1")

  annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
  implementation("io.micronaut.aws:micronaut-aws-apigateway")
  implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")
  implementation("io.micronaut.aws:micronaut-function-aws")
  implementation("io.micronaut.crac:micronaut-crac")
  implementation("io.micronaut.serde:micronaut-serde-jackson")
  runtimeOnly("ch.qos.logback:logback-classic")

  runtimeOnly("org.yaml:snakeyaml")
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

tasks.named<ShadowJar>("shadowJar") {
  exclude("module-info.class")
  exclude("META-INF/*")
  exclude("META-INF/versions/**")
}

tasks.named("assemble") {
  dependsOn("shadowJar")
}
