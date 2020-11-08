import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val exposedVersion: String by project
val arrowVersion: String by project
val kotlinxSerializationVersion: String by project
val logbackVersion: String by project

plugins {
    application
    java
    idea
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

group = "dev.kongsvik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

application {
    mainClassName = "dev.kongsvik.ktor_oidc_server.MainKt"
}

dependencies {
    fun ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"
    fun exposed(module: String) = "org.jetbrains.exposed:exposed-$module:$exposedVersion"
    fun arrow(module: String) = "io.arrow-kt:arrow-$module:$arrowVersion"

    implementation(kotlin("stdlib"))
    implementation(ktor("server-core"))
    implementation(ktor("serialization"))
    implementation(ktor("server-netty"))
    implementation(ktor("auth-jwt"))
    implementation(arrow("core"))
    implementation(arrow("fx"))
    implementation(arrow("syntax"))
    implementation(arrow("mtl"))
    kapt(arrow("meta"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation(exposed("core"))
    implementation(exposed("dao"))
    implementation(exposed("jdbc"))
    implementation(exposed("java-time"))
    implementation("com.h2database:h2:1.4.200")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}