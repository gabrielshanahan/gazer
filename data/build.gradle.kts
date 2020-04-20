import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
}

//TODO: Does all this need to be here?
val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

dependencyManagement {
    imports {
        mavenBom(BOM_COORDINATES)
    }
}

dependencies {
    runtimeOnly("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")

    implementation("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
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
