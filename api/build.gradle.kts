plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
}

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencies {
    implementation(project(":data"))
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("com.h2database:h2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
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
