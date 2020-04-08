import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.71"

	kotlin("jvm") version kotlinVersion apply false
	kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.allopen") version kotlinVersion apply false
    kotlin("plugin.noarg") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false

	id("org.springframework.boot") version "2.3.0.M4" apply false
	id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false

	`java-library`
	jacoco

	id("org.jmailen.kotlinter") version "2.3.2" apply false
	id("io.gitlab.arturbosch.detekt") version "1.7.4" apply false
	id("org.jetbrains.dokka") version "0.10.1" apply false
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
