FROM eclipse-temurin:17 AS build
WORKDIR /work
COPY . .
RUN ./gradlew jar

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /work/build/libs/migrations.jar /app/migrations.jar

ENTRYPOINT java -jar migrations.jar
