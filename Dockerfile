FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY . .

RUN javac $(find . -name "*.java") \
 && jar cfm Main.jar Manifest.txt $(find . -name "*.class")

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/Main.jar /app/Main.jar

ENTRYPOINT ["java", "-jar", "/app/Main.jar"]
