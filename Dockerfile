FROM gradle:7.0.2-jdk8 AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:8-jre-slim

EXPOSE 6379

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*all.jar /app/skyhook.jar
COPY config/skyhook.yml /app/skyhook.yml

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/app/skyhook.jar"]
CMD ["-f", "/app/skyhook.yml"]
