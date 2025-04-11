plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.skill"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.github.danbeldev:alice-ktx:1.0.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}