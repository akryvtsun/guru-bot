# guru-bot
Telegram bot for sending predefined learning materials with some periodicity

How to build
```
./gradlew build
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