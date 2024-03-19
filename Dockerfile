FROM openjdk:8
WORKDIR /app

COPY ./src .
RUN javac *.java
CMD ["java", "-classpath", "./ojdbc7.jar:./", "MainApplication"]