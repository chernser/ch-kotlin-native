plugins {
//    kotlin("jvm") version "1.9.22"
    kotlin("multiplatform") version "2.3.0"
}

group = "com.github.chernser"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

}


val ktor_version = "3.3.0"




kotlin {
//    jvmToolchain(21)
    jvm()
    macosX64("native") { // on macOS
        // linuxX64("native") // on Linux
        // mingwX64("native") // on Windows
        binaries {
            executable()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktor_version")
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-server-cio:$ktor_version")
                implementation("io.ktor:ktor-client-cio:$ktor_version")
                implementation("io.ktor:ktor-client-logging:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        commonTest {
            dependencies {
                // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-test
                implementation("org.jetbrains.kotlin:kotlin-test:2.3.0")

            }
        }
    }
}
