import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "leonoid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val seleniumVersion = "4.9.1"
val tgbotapiVersion = "8.0.0"
val arrowVersion = "1.2.0-RC"

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion")
    implementation("dev.inmo:tgbotapi:$tgbotapiVersion")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}
