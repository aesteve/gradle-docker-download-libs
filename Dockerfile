FROM adoptopenjdk/openjdk13:jre-13_33-debian

# Copy dependencies: this will be cached as long as dependencies jars don't change
ADD libs/* /opt/libs/

# Add our code, this will change every build, but is a very thin layer
ADD build/libs/gradle-docker-download-libs-1.0-SNAPSHOT.jar /opt/application.jar

# Define the entrypoint of our application, not using a fatjar, but -cp instead
ENTRYPOINT java -cp "/opt/application.jar:/opt/libs/*" com.github.aesteve.gradle.docker.Main
