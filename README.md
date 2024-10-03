# guru-bot
Telegram bot for sending predefined learning materials with some periodicity

## Build [![Actions Status](https://github.com/akryvtsun/guru-bot/workflows/CI/badge.svg)](https://github.com/akryvtsun/guru-bot/actions)

How to build
```
./gradlew build
```
or build without tests
```
./gradlew build -x test
```

How to create Docker image
```
docker build --platform linux/amd64 -t guru-bot .
```

How to run Docker container (don't do this on Apple Silicon)
```
docker run -d --name guru-bot-container \
  -e BOT_USERNAME="<bot username>" \
  -e BOT_TOKEN="bot token" \
  guru-bot     
```

Make label
```
docker tag guru-bot:latest akryvtsun/guru-bot:latest
```

Docker Hub push
```
docker push akryvtsun/guru-bot:latest
```

## Useful Links
- [Building Dynamic Telegram Bots with Kotlin](https://medium.com/@razavioo/building-dynamic-telegram-bots-with-kotlin-26b841966fbb)
- [Dockerfile generation](https://chatgpt.com/c/66e46e18-5298-8007-821f-025a677df112)
- [‘No Main Manifest Attribute’ Error in Java](https://ioflood.com/blog/no-main-manifest-attribute/)
- [java.lang.NoClassDefFoundError: org/telegram/telegrambots/meta/exceptions/TelegramApiException](https://stackoverflow.com/questions/65976406/java-lang-noclassdeffounderror-org-telegram-telegrambots-meta-exceptions-telegr)
- [How to pack into jar file all dependencies in build.gradle.kts?](https://chatgpt.com/c/66e6dbe8-2ffc-8007-b19b-096b3bf793a8)
- [A Step-by-Step guide to Build and Push Your Own Docker Images to DockerHub](https://medium.com/@komalminhas.96/a-step-by-step-guide-to-build-and-push-your-own-docker-images-to-dockerhub-709963d4a8bc)
- [Troubleshooting and Fixing ‘exec /usr/java/openjdk-17/bin/java: exec format error’ in Azure DevOps CI/CD Pipeline on Mac](https://medium.com/@bectorhimanshu/troubleshooting-and-fixing-exec-usr-java-openjdk-17-bin-java-exec-format-error-in-azure-devops-63e2ea1b7525)