val ktor_version: String = "3.4.1"
val kotlin_version: String = "2.3.20"
val logback_version: String = "1.5.32"

plugins {
    application
    kotlin("jvm") version "2.3.20"
    id("io.ktor.plugin") version "3.4.1"
}

application {
    mainClass.set("sudoku.ApplicationKt")
}

repositories {
    mavenCentral()
}

ktor {
    fatJar {
        archiveFileName.set("backend.jar")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}