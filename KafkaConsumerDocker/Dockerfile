FROM confluentinc/cp-kafka:5.3.0

COPY out/artifacts/KafkaConsumerDocker_jar/KafkaConsumerDocker.jar /consumer.jar

CMD ["java", "-jar", "/consumer.jar"]