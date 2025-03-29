plugins {
    id("java-library")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register<Zip>("buildZip") {
    into("lib") {
        destinationDirectory.set(layout.buildDirectory.dir("../../resources")) // Set output directory
        from(tasks.jar)
        from(configurations.runtimeClasspath)
    }
}

tasks.named("build") {
    dependsOn("buildZip")
}