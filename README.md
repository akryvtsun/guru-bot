# guru-bot
Telegram bot for sending predefined learning materials with some periodicity

How to build
```
./gradlew build
```

How to create Docker image
```
docker build -t guru-bot .
```

How to run Docker container
```
docker run -d --name guru-bot-container \
  -e BOT_USERNAME="<bot username>" \
  -e BOT_TOKEN="bot token" \
  guru-bot     
```