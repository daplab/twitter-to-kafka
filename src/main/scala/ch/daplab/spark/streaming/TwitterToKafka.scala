package ch.daplab.spark.streaming

import java.util.Properties

import com.google.gson.Gson
import kafka.producer.KeyedMessage
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.cloudera.spark.streaming.kafka.KafkaWriter._

/**
 * Collect at least the specified number of tweets into json text files.
 */
object TwitterToKafka {

  private val gson = new Gson()

  def main(args: Array[String]) {
    // Process program arguments and set properties
    if (args.length < 3) {
      System.err.println("Usage: " + this.getClass.getSimpleName +
        "<topicName> <kafkaBrokers> <intervalInSeconds>")
      System.exit(1)
    }
    val Array(topicName: String, kafkaBrokers: String,  Utils.IntParam(intervalSecs)) =
      Utils.parseCommandLineWithTwitterCredentials(args)

    println("Initializing Streaming Spark Context...")
    val conf = new SparkConf().setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(intervalSecs))

    val tweetStream = TwitterUtils.createStream(ssc, Utils.getAuth)
      .map(gson.toJson(_))

    val producerConf = new Properties()
    producerConf.put("serializer.class", "kafka.serializer.DefaultEncoder")
    producerConf.put("key.serializer.class", "kafka.serializer.StringEncoder")
    producerConf.put("metadata.broker.list", kafkaBrokers)
    producerConf.put("request.required.acks", "-1")
    tweetStream.writeToKafka(producerConf,
      (x: String) => new KeyedMessage[String,Array[Byte]](topicName, null, x.getBytes))

    ssc.start()
    ssc.awaitTermination()
  }
}
