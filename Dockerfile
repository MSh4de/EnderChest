FROM openjdk:11
MAINTAINER _RealAlpha_
WORKDIR /usr/src/enderchest
COPY . .
EXPOSE 25565
ENTRYPOINT ["java", "-jar", "enderchest-0.0.1.jar"]