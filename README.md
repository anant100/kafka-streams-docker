# kafka-streams-docker

# Design Engineering Skill Improvement

Generate dummy JSON data for 3 tickers with price of ±10% with the mean price. Produce the stream of JSON msg with at least 1 to 3 tickers in Kafka Producer. Consume the JSON stream and print the average stock price every 30 seconds using Kafka Streams...

## Prerequisite

To Run pipeline on any Scala supported IDE in local system

- Java Runtime Env
- IntelliJ/Scala IDE
- Zookeeper
- Kafka-0.10.0.0 - https://archive.apache.org/dist/kafka/0.10.0.0/kafka_2.11-0.10.0.0.tgz

To Run pipeline using Docker-image
- Docker
- Docker-compose

## Run using IntelliJ/Scala IDE OR CMD
- Check/Download ZIP file attached OR Clone the project from GitHub https://github.com/anant100/kafka-streams-docker
- Make sure Zookeeper and Kafka server is running on your localhost

Different ways to get the output. You can use any of the below to run the pipeline.
#### 1. Run the Jar files in your CMD
```bash
java -jar KafkaProducerDocker.jar
```
```bash
java -jar KafkaConsumerDockesr.jar
```
#### 2. Run the program in your IDE

- First run the KafkaProducerDocker.scala
- Then run the KafkaConsumer.Docker.scala

## Run using Docker-compose
Check/download for the docker-compose.yml and use the cmd
```bash
docker-compose up
```


Please make sure to to use `docker-compose down` after running your program using docker.

....................................................................................
