Twitter to Kafka
=========================

This project aims at palying around with Spark Streaming. It reads from Twitter Firehose via Spark Streaming
and store the data into a Kafka topic.

Lots of inspiration has been taken from the following projects:

* [https://github.com/databricks/reference-apps](https://github.com/databricks/reference-apps) 
  for collecting tweets and Utils class
* [https://github.com/cloudera/spark-kafka-writer](https://github.com/cloudera/spark-kafka-writer)
  to write the DStream to Kafka
* [https://databricks.gitbooks.io/databricks-spark-reference-applications/content/twitter_classifier/collect.html](https://databricks.gitbooks.io/databricks-spark-reference-applications/content/twitter_classifier/collect.html)
  for human description of the tweet collection

# Run it

Build the project

```
mvn clean install
```

Upload the produced jar in the DAPLAB infrastructure

```
scp target/twitter-to-kafka-1.0.0-SNAPSHOT-jar-with-dependencies.jar pubgw1.daplab.ch:
```

Get the [Twitter API credentials](http://docs.daplab.ch/twitter_account/)

Launch the job

```
spark-submit --master yarn --num-executors 4 \
  --class ch.daplab.spark.streaming.TwitterToKafka \
  twitter-to-kafka-1.0.0-SNAPSHOT-jar-with-dependencies.jar \
  public.tweets daplab-rt-11.fri.lan:6667 10 \
  --consumerKey ${YOUR_TWITTER_CONSUMER_KEY} \
  --consumerSecret ${YOUR_TWITTER_CONSUMER_SECRET} \
  --accessToken ${YOUR_TWITTER_ACCESS_TOKEN}  \
  --accessTokenSecret ${YOUR_TWITTER_ACCESS_SECRET}
```
