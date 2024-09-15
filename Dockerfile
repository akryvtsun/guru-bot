FROM openjdk:17-jdk-slim

WORKDIR /use/local/guru-bot

COPY ./build/libs/guru-bot-1.0-SNAPSHOT.jar /use/local/guru-bot/guru-bot.jar

CMD ["java", "-jar", "guru-bot.jar"]
