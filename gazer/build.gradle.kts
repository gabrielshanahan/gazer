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

val ktor_version = "1.3.2"
dependencies {

    implementation(project(":func"))
    implementation(project(":data"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

    implementation("io.github.gabrielshanahan", "moroccode", "1.0.0")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

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
        jvmArgs = listOf("-Dspring.profiles.active=dev", "-Dkotlinx.coroutines.debug")
    }

}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}
