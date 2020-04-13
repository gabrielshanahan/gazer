import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.71"

	kotlin("jvm") version kotlinVersion

	`java-library`
	jacoco

	id("org.jmailen.kotlinter") version "2.3.2"
	id("io.gitlab.arturbosch.detekt") version "1.7.4"
	id("org.jetbrains.dokka") version "0.10.1"

    // Spring specific plugins
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.allopen") version kotlinVersion apply false
    kotlin("plugin.noarg") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false

    id("org.springframework.boot") version "2.3.0.M4" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false

}

allprojects {

    apply {
        plugin("java-library")
    }

    group = "io.github.gabrielshanahan"
    version = "0.0.1-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_1_8

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        jcenter()
    }
}

subprojects {
    apply {

        plugin("org.jetbrains.kotlin.jvm")
        plugin("jacoco")
        plugin("org.jmailen.kotlinter")
        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jetbrains.dokka")

        plugin("io.spring.dependency-management")

    }

    dependencies {
        implementation(platform(kotlin("bom")))
        implementation(kotlin("reflect"))
        implementation(kotlin("stdlib-jdk8"))

        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.7.4")
    }

    tasks {
        test {
            useJUnitPlatform()
            // Always run ktlint, detekt and jacoco when running tests
            finalizedBy(jacocoTestReport, lintKotlin, detekt)

            testLogging {
                // Make sure output from
                // standard out or error is shown
                // in Gradle output.
                showStandardStreams = true
            }
        }

        dokka {
            outputFormat = "html"
            configuration {
                includeNonPublic = true
                reportUndocumented = true
            }
        }

        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "1.8"
            }
        }
    }

    detekt {
        baseline = file("config/detekt/baseline.xml")
        parallel = true // Builds the AST in parallel. Rules are always executed in parallel.
    }
}
