import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.hospital.systems"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Main
    implementation(compose.desktop.currentOs)

    // Koin
    implementation("io.insert-koin:koin-core:3.6.0-wasm-alpha2")

    // Postgres
    implementation("org.postgresql:postgresql:42.7.3")

    // PreCompose
    api("moe.tlaster:precompose:1.6.0-rc05")
    api("moe.tlaster:precompose-molecule:1.6.0-rc05")
    api("moe.tlaster:precompose-viewmodel:1.6.0-rc05")
    api("moe.tlaster:precompose-koin:1.6.0-rc05")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "HospitalSystems"
            packageVersion = "1.0.0"
        }
    }
}
