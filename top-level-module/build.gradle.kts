plugins {
	kotlin("jvm") version "1.7.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

object Version {
	const val springBoot = 3.0.1
}

dependencies {
	implementation(project(":sub-module"))
	implementation("org.springframework.boot:spring-boot-starter:3.0.1")
}
