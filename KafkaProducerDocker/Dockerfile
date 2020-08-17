FROM confluentinc/cp-kafka:5.3.0

COPY out/artifacts/KafkaProducerDocker_jar/KafkaProducerDocker.jar /producer.jar

CMD ["java", "-jar", "/producer.jar"]