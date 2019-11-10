FROM adoptopenjdk/openjdk13:jre-13_33-debian

# Copy dependencies: this will be cached as long as dependencies jars don't change
ADD build/libs/libs/* /run/libs/

# Add our code, this will change every build, but is a very thin layer
ADD build/libs/gradle-docker-download-libs-1.0-SNAPSHOT.jar /run/application.jar

# Define the entrypoint of our application, not using a fatjar, a jar (with manifest) instead
ENTRYPOINT ["java", "-jar", "/run/application.jar"]
