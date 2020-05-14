# HOW TO BUILD THIS IMAGE
## Get a fresh version for the final image
FROM openjdk:15-jdk-alpine
MAINTAINER Andrade Sampaio <asampaio3006@gmail.com>

RUN apk add --update bash

# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

ADD build/libs/*.jar /app/app.jar

CMD java -jar /app/app.jar $APP_OPTIONS