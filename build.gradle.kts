import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("nebula.ospackage") version "9.1.1"
}

group = "com.aerospike"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.aerospike.skyhook.MainKt")
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClass,
                "Implementation-Version" to archiveVersion
            )
        )
    }
}

// Common dependency versions.
extra["nettyVersion"] = "4.1.96.Final"
extra["logbackVersion"] = "1.3.11" // latest for JDK 8
extra["jacksonVersion"] = "2.15.2"
extra["junitVersion"] = "5.10.0"

dependencies {
    implementation("com.aerospike:aerospike-client:7.1.0")
    implementation("io.netty:netty-all:${project.extra["nettyVersion"]}")
    implementation("io.netty:netty-codec-redis:${project.extra["nettyVersion"]}")
    implementation("com.google.inject:guice:5.1.0")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("ch.qos.logback:logback-classic:${project.extra["logbackVersion"]}")
    implementation("ch.qos.logback:logback-core:${project.extra["logbackVersion"]}")
    implementation("info.picocli:picocli:4.7.4")
    implementation("commons-io:commons-io:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-core:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${project.extra["jacksonVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${project.extra["jacksonVersion"]}")
    implementation("com.google.guava:guava:32.1.2-jre")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.extra["junitVersion"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.extra["junitVersion"]}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val distZip: Zip by tasks
distZip.duplicatesStrategy = DuplicatesStrategy.EXCLUDE

fun setUpPackaging(
    packagingTask: com.netflix.gradle.plugins.packaging.SystemPackagingTask,
    installFolder: String = "install",
    debFolder: String = "deb"
) {
    packagingTask.dependsOn("distZip")

    // Mark configuration file as configuration file.
    val targetConfigFile =
        "/etc/${packagingTask.project.name}/${packagingTask.project.name}.yml"
    packagingTask.configurationFile(targetConfigFile)

    val sourceConfigFile =
        File("${projectDir}/pkg/$installFolder/$targetConfigFile")

    if (packagingTask is com.netflix.gradle.plugins.rpm.Rpm && sourceConfigFile.isFile) {
        packagingTask.from(sourceConfigFile) {
            // For rpm this is the way to mark configuration files.
            com.netflix.gradle.plugins.packaging.CopySpecEnhancement
                .setFileType(
                    this,
                    org.redline_rpm.payload.Directive(
                        org.redline_rpm.payload.Directive.RPMFILE_CONFIG or org.redline_rpm.payload.Directive
                            .RPMFILE_NOREPLACE
                    )
                )
            into(
                sourceConfigFile.parentFile.path.replace(
                    ".*pkg/$installFolder".toRegex(), ""
                )
            )
        }
    }

    // Package config and other dependent files.
    val installDir = File(project.projectDir, "pkg/$installFolder")
    if (installDir.isDirectory) {
        for (file in installDir.listFiles()!!) {
            packagingTask.from(file) {
                into(
                    file.path.replace(".*pkg/$installFolder".toRegex(), "")
                )
                packagingTask.addParentDirs = false
                fileMode = 0x755
                if (packagingTask is com.netflix.gradle.plugins.rpm.Rpm) {
                    // We have config it separately above.
                    exclude("**/" + sourceConfigFile.name)
                }
            }
        }
    }

    // Copy installation scripts.
    val installScriptsDir = File(project.projectDir, "pkg/$debFolder")
    if (installScriptsDir.isDirectory) {
        packagingTask.postInstall(
            packagingTask.project.file(
                "${projectDir}/pkg/$debFolder/postInstall.sh"
            )
        )
        packagingTask.preUninstall(
            packagingTask.project.file
                ("${projectDir}/pkg/$debFolder/preUninstall.sh")
        )
        packagingTask.postUninstall(
            packagingTask.project.file(
                "${projectDir}/pkg/$debFolder/postUninstall.sh"
            )
        )
    }

    // Copy the installer.
    packagingTask.from(
        project.zipTree(
            distZip.archiveFile.get().asFile.absolutePath
        )
    ) {
        into("/opt/${packagingTask.project.name}/")
        packagingTask.addParentDirs = false
        eachFile {
            path =
                path.replaceFirst(
                    "/${packagingTask.project.name}-${packagingTask.project.version}/",
                    "/"
                )
        }
    }
}

/**
 * Create the Debian package.
 */
task("deb", com.netflix.gradle.plugins.deb.Deb::class) {
    // Currently we cannot specify a dependency on any package providing
    // java 8+ (for e.g java 11). Exact dependency on Java 8 is the only
    // thing that works. Skip adding a packaging dependency until this
    // works reliably.
    // requires("java8-runtime").or("java8-sdk")
    setUpPackaging(this)
}

/**
 * Create the RPM package.
 */
task("rpm", com.netflix.gradle.plugins.rpm.Rpm::class) {
    os = org.redline_rpm.header.Os.LINUX
    release = "1"
    user = "root"
    packageGroup = "root"

    // Currently we cannot specify a dependency on any package providing
    // java 8+ (for e.g java 11). Exact dependency on Java 8 is the only
    // thing that works. Skip adding a packaging dependency until this
    // works reliably.
    // requires("java", "1.8", Flags.GREATER or Flags.EQUAL).or("java",
    //    "11", Flags.GREATER or Flags.EQUAL)
    setUpPackaging(this)
}
