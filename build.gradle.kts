plugins {
    java
}

group = "com.github.aesteve"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vertx:vertx-core:4.0.0-milestone3")
}

tasks {
    val runtimeCp = configurations.runtimeClasspath.get()
    val libsDestFolder = buildDir.resolve("libs/libs")
    task<Copy>("downloadDependencies") {
        from(runtimeCp)
        into(libsDestFolder)
    }

    clean {
        doFirst { delete(libsDestFolder) }
    }

    getByName<Jar>("jar") {
        dependsOn("downloadDependencies")
        manifest {
            attributes["Main-Class"] = "com.github.aesteve.gradle.docker.Main"
            attributes["Class-Path"] = runtimeCp.files.joinToString(separator = " ", transform = { "libs/${it.name}"})
        }
    }

}
