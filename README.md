# Running a Java application in docker using layering

## Step 1: Build once

### Build the project using Gradle 

```bash
gradle clean build
```
=>
```

BUILD SUCCESSFUL in 797ms
4 actionable tasks: 4 executed
```

```
ls build/libs
```


### Build the docker image

```
docker build .
```

=>

```
Step 1/4 : FROM adoptopenjdk/openjdk13:jre-13_33-debian
jre-13_33-debian: Pulling from adoptopenjdk/openjdk13
092586df9206: Already exists
eaef5db48c59: Pull complete
5f6c3cdda1cd: Pull complete
Digest: sha256:68b584b426e8fb68629dfd192f9ae454e5589810c431e34a5a4b0eecf8d14e56
Status: Downloaded newer image for adoptopenjdk/openjdk13:jre-13_33-debian
 ---> f7c2c41e5053
Step 2/4 : ADD libs/* /opt/libs/
 ---> 5fb76e2fb23c
Step 3/4 : ADD build/libs/gradle-docker-download-libs-1.0-SNAPSHOT.jar /opt/application.jar
 ---> c7afcdd8272f
Step 4/4 : ENTRYPOINT java -cp "/opt/application.jar:/opt/libs/*" com.github.aesteve.gradle.docker.Main
 ---> Running in adaaf7421d8a
Removing intermediate container adaaf7421d8a
 ---> edcacd0ffcca
Successfully built edcacd0ffcca
```

You can observe:
1. In Step 1: the JDK13 is being downloaded (`Pull complete`) and it produced the layer `f7c2c41e5053`
2. In Step 2: the dependencies are being copied into a layer `5fb76e2fb23c`
3. In Step 3: your jar is being copied into another layer `c7afcdd8272f`
4. In Step 4: the entrypoint of your image is set (simply invoking `java` with the appropriate options, notably: the correct classpath)

## Step 2: Re-Building

Now imagine we're changing our code, fixing a bug, etc.
Just change [the main class](src/main/java/com/github/aesteve/gradle/docker/Main.java) for instance, replace `"Hello from a jar file"` by `"Hello from a fixed jar file"`
Then rebuild.

### Rebuild using Gradle

```
gradle build
```

=> 

```

BUILD SUCCESSFUL in 1s
3 actionable tasks: 2 executed, 1 up-to-date
```

### Rebuild the docker image

That's where it gets interesting:

```
docker build .
```

=> 

```
Sending build context to Docker daemon  6.562MB
Step 1/4 : FROM adoptopenjdk/openjdk13:jre-13_33-debian
 ---> f7c2c41e5053
Step 2/4 : ADD libs/* /opt/libs/
 ---> Using cache
 ---> 5fb76e2fb23c
Step 3/4 : ADD build/libs/gradle-docker-download-libs-1.0-SNAPSHOT.jar /opt/application.jar
 ---> 8d6d8a97bb76
Step 4/4 : ENTRYPOINT java -cp "/opt/application.jar:/opt/libs/*" com.github.aesteve.gradle.docker.Main
 ---> Running in 685719f5fba1
Removing intermediate container 685719f5fba1
 ---> bbea02b79f29
Successfully built bbea02b79f29
```

1. Step 1: You're obviously not re-downloading JRE13, you already did, docker is using its **cache**.
2. Step 2: Here's the main interesting point. Your dependencies didn't change, when running `gradle build`, it downloaded your dependencies and placed it under the `libs` directory of your project. But since they didn't change they produce the same "hash", hence the same layer in docker. You'll notice docker is telling us this: `Using cache`
3. Step 3: Your application's jar has changed, cache cannot be used.
4. Step 4: Since a layer has changed, other layers, even if they didn't change, produce new hashes, they can't be cached.

That's why the order of your commands matter in your Dockerfile. Here, we added our dependencies before our jar because they change way less often than our code.

# Conclusion

This is an alternative strategy for creating docker images without building a fat jar. It benefits from layering, so should speed up your builds since it won't push huge layers to your docker registry every time you build your application.
Note that if build time doesn't matter to you, or you're not worried about having a "big" layer (if your fat jar is quite big) you don't have to use this technique. 



