FROM gradle:9.4-jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine
EXPOSE 40000
COPY --from=build /home/gradle/src/build/libs/backend.jar backend.jar
ENTRYPOINT ["java", "-jar", "/backend.jar"]