plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
}

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {

    implementation(project(":func"))
    implementation(project(":data"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")

}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        layered()
    }
    bootRun {
        jvmArgs = listOf("-Dspring.profiles.active=dev")
    }

}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}