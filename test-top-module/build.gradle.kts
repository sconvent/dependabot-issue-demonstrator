import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {

    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
}

group = "org.test"
version = "0.0.1"

java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
}

repositories {
    mavenCentral()
}

dependencyManagement {
    dependencies {
        // note: The Spring Boot BOM is imported by default. This ensures the compatibility of libraries used by Spring.
        dependencySet("org.springdoc:1.6.2") {
            entry("springdoc-openapi-ui")
            entry("springdoc-openapi-data-rest")
            entry("springdoc-openapi-security")
            entry("springdoc-openapi-kotlin")
        }
    }
}

dependencies {
    implementation(project(":test-common-module"))
    implementation(project(":test-submodule2"))
    implementation(project(":test-submodule3"))
    implementation(project(":test-submodule1"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-config")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
