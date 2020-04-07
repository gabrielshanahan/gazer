import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.71"

	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion

	id("org.springframework.boot") version "2.3.0.M4"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"

	`java-library`
	jacoco

	id("org.jmailen.kotlinter") version "2.3.2"
	id("io.gitlab.arturbosch.detekt") version "1.7.4"
	id("org.jetbrains.dokka") version "0.10.1"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

group = "io.github.gabrielshanahan"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

repositories {
	mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    jcenter()
}

dependencies {
	implementation(platform(kotlin("bom")))
	implementation(kotlin("reflect"))
	implementation(kotlin("stdlib-jdk8"))

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("mysql:mysql-connector-java")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

    developmentOnly("org.springframework.boot:spring-boot-devtools")
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

    bootJar {
        layered()
    }
}

detekt {
    baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
    parallel = true // Builds the AST in parallel. Rules are always executed in parallel.
}

