FROM openjdk:17-jdk-slim

ARG BOT_HOME=/use/local/guru-bot

WORKDIR $BOT_HOME

COPY ./build/libs/guru-bot-1.0.0.jar $BOT_HOME/guru-bot.jar

CMD ["java", "-jar", "guru-bot.jar"]
