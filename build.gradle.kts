import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "com.aerospike"
version = "1.0.0"

repositories {
    mavenCentral()
}

application {
    mainClassName = "com.aerospike.redispike.MainKt"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

// Common dependency versions.
extra["nettyVersion"] = "4.1.60.Final"
extra["logbackVersion"] = "1.2.3"
extra["jacksonVersion"] = "2.12.2"

dependencies {
    implementation("com.aerospike:aerospike-client:5.0.5")
    implementation("io.netty:netty-all:${project.extra["nettyVersion"]}")
    implementation("io.netty:netty-codec-redis:${project.extra["nettyVersion"]}")
    implementation("com.google.inject:guice:5.0.1")
    implementation("io.github.microutils:kotlin-logging:2.0.6")
    implementation("ch.qos.logback:logback-classic:${project.extra["logbackVersion"]}")
    implementation("ch.qos.logback:logback-core:${project.extra["logbackVersion"]}")
    implementation("info.picocli:picocli:4.6.1")
    implementation("commons-io:commons-io:2.8.0")
    implementation("com.fasterxml.jackson.core:jackson-core:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${project.extra["jacksonVersion"]}")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
