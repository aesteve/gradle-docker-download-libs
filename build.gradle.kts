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

    task<Copy>("downloadDependencies") {
        from(configurations.runtimeClasspath)
        into("libs")
    }

    clean {
        doFirst { delete("libs") }
    }

    jar {
        dependsOn("downloadDependencies")
    }

}
