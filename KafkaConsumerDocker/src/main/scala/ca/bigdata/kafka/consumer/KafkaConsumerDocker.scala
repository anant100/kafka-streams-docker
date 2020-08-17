package ca.bigdata.kafka.consumer

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Calendar, Properties}

import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.{Decoder, HCursor, Json, parser}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object KafkaConsumerDocker extends App {

  // JSON Deserializer
  def fromJson(json: String) = {
    implicit val jsonSplitter = new Decoder[List[Json]] {
      override def apply(c: HCursor): Result[List[Json]] = {
        c.downField("tickers").as[List[Json]]
      }
    }
    parser.decode[List[Json]](json) match {
      case Right(value) => {
        value.map(value => {
          parser.decode[Ticker](value.toString) match {
            case Right(stock) => stock
            case Left(ex) => println(s"Ooops something happened ${ex}")
          }
        })
      }
      case Left(ex) => println(s"Something Wrong with decoding List[Json] :-> ${ex}")
    }
  }

  // Returns average price of the tickers
  def average(input: List[Ticker]): Double = input.map(_.price).sum / input.size

  // Timestamp
  def getCurrentDateTimeStamp: Timestamp ={
    val today = Calendar.getInstance.getTime
    val timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val now = timeFormat.format(today)
    val timestamp = java.sql.Timestamp.valueOf(now)
    timestamp
  }
  // Ticker Case Class
  case class Ticker(name : String, price : Int)

  /** Configuration of Kafka Consumer */
  val consumerProperty: Properties = new Properties()
  consumerProperty.put("bootstrap.servers", "localhost:9092")
  consumerProperty.put("group.id", "ticker-consumer-group-1")
  consumerProperty.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  consumerProperty.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  consumerProperty.put("auto.offset.reset", "earliest")

  val consumer = new KafkaConsumer[String, String](consumerProperty)
  consumer.subscribe(List("stocks").asJava) // Subscribe to list of topics for configured group id

  var sourceList = new ListBuffer[Ticker]()
  val nano: Long = 1000000000
  var start_time = System.nanoTime()

  println("....Processing the live ticker price....")
  while (true) {
    val polledRecords: ConsumerRecords[String, String] = consumer.poll(1000)

    polledRecords.asScala.foreach(records => {
      val record = records.value()
      val stockOutput = fromJson(record).asInstanceOf[List[Ticker]]

      stockOutput.map(stock => {
        val timeOut = ((System.nanoTime() - start_time) / nano)
        if (timeOut < 30) {
          sourceList += stock
        }
        else {
          println("------ Updated  Price --------")
          val tickerList: List[Ticker] = sourceList.toList
          tickerList.groupBy(_.name).mapValues(average(_)).map {
            case (name, avgPrice) => Ticker(name, avgPrice.toInt)
          }.foreach(x => println(x + s" - $getCurrentDateTimeStamp"))
          start_time = System.nanoTime()
          sourceList = ListBuffer.empty
        }
      })
    })
    consumer.commitSync() // Commit offsets
  }
}