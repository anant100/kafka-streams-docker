package ca.bigdata.kafka.producer

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.util.Random

object KafkaProducerDocker extends  App {

  val sampleData: List[(String, Int)] = List(("AMZN", 1902), ("MSFT", 107), ("AAPL", 215))

  // Convert stock data to JSON
  def toJson(input: List[(String, Int)]): String  = "{\"tickers\": [" +
    input.map(stock => "{\"name\": \"" + stock._1 + "\", \"price\": " + stock._2 + "}").mkString(",") +
    "]}"

  /** Kafka Producer Properties */
  val producerProp = new Properties()
  producerProp.put("bootstrap.servers", "localhost:9092")
  producerProp.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProp.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](producerProp)

  val random = new Random
  var count = 0
  try{
    while(true) {
      val randomList = sampleData.take(1 + random.nextInt(sampleData.length)) // Create random list of Tickers msg
      val stockList: List[(String, Int)] = randomList.map(x => {
        val num = random.nextInt(2)
        if (num == 0) {
          (x._1, (x._2) + (((x._2)*random.nextInt(10))/100))
        }
        else {
          (x._1, (x._2) - (((x._2)*random.nextInt(10))/100))
        }
      })
      //Generate JSON tickers message with 1-3 (stocks, price)
      val newEntry = toJson(stockList)

      //Produce tickers as JSON to Kafka Topic
      val producedMessage = new ProducerRecord[String, String]("stocks", newEntry)
      producer.send(producedMessage)

      count += 1
      if (count >= 10) {
        Thread.sleep(1000)
        count = 0
      }
    }
  }
  finally {
    producer.flush()
  }
}