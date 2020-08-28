# Kafka

## Chapter 2 Kafka Theory

### Topics
- Topics are similar to tables on SQL dbs, can have as many as you want
- Topic holds partitions
- Can create as many partitions as you want
- Each partitions are ordered
- New messages added to a partition as offset, guaranteed
- Cannot edit backwards, always offset to next
- Accross partitions, cannot determine which one was added first, unless compared
- i.e. Partition 1, offset 6 may or may not be added after Partition 2 offset 5

### Broker

- Kafka cluster is comprised of multiple servers
- Broker = server
- Each broker is identified by an integer, cannot be string
- Each broker contains certain topic partitions
- Kafka is distributed, so each broker contains certain topic partitions, not all
- If you connect to one broker (called bootstrap), you are connected to all 
- Starters: pick 3 brokers, Uber like companies can have 100 brokers 

#### Brokers and Topics 

- When you create a Topic with 3 partitions, those 3 partitions are distributed amongst the brokers; no guarantee which partition will be on which broker
- When you crate a Topic with 2 partitions and 3 brokers, 1 will have no partition
- When you create a topic with 4 partitions on 3 brokers, 1 broker will have 2 partitions


### Replicas

- High Availability in Kafka
- Topics should have replicas, usually between 2 and 3
- With that if a broker is down, another server will pick up the tab
- So Topic 1 - Partition 0 will be in 2 to 3 servers (depending on how many replicas you picked)
- Who's the replica leader? At any time one broker can be the leader, and it will be the active for read/write, the passive replica will be in-sync replica
- When the leader goes down, passive replica will pick up the tab, and when the active leader goes live again, it will continue to be the leader, others will be ISR (in sync replica)

### Producer

- How do you get data into Kafka? Producers
- Producers write data to topics, which is made of partitions 
- Producers are smart enought to know which broker and partition to write to
- Producers will automatically recover, if a producer goes down
- Round robin style to write the data
- Acknowledgement for the data write, (confirm the receipt please), when acks=0, no confirmation, acks=1 only leader confirm, acks=all leader and sync replicas to confirm, so data loss, limited data loss and no data loss cases 

#### Message Keys

- Any data type, if key=null, data is sent round robin
- if key is sent, then all messages for that key will go the the same partition
- If you need message ordering for a specific field, send the key, such as car_id for Uber
- So if you send car_id_123, it will always go to , say Partition 1, thanks to key hashing

### Consumers

- Consumers read data from a topic
- They know which broker to read from
- In case of broker failure, they know how to recover and continue to read
- Data is read in order, **within each partitions**

#### Consumer groups
- Consumers read data in consumer groups
- Each consumer within a group reads from exclusive partitions
- If you have more consumers than partitions, some consumers will be inactive

#### Consumer offsets
- Kafka stores the offsets at which a consumer group has been reading, so if a broker goes down, it knows what was the last offset position
- Consumers choose when to commit offset, when do you want to commit consumer?
- That's called delivery semantics in tech terms
- You have 3 options, at most once, exactly once, at least once
- At most once: offsets are committed as soon as the message is received
- At least once: (usually use this one), offsets are committed afther the msg is processed, but say the server goes down, and backs up again, consumer will re-read the message, thus be careful, you should have an idempotent solution, i.e. processing the same info again should not impact your systems
- Exactly once: Can be achieved for Kafka => Kafka workflows using Kafka Streams API, for Kafka => External Systems workflows, us e and idempotent consumer

### Kafka Broker Discovery

- Every Kafka broker is also called a "bootstrap server"
- If you connect one broker, you're connected to all
- Each broker knows about all brokers, topics and partitions (metadata)
- Upon connection, the broker sends the metadata to the Kafka client with all the brokers, topcis and partitions, so client can consume from wherever


### Zookeeper
- Zookeper keeps a list of brokers, thus manages brokers
- Selects the leader for the partitions
- Sends notifications to Kafka in case of changes
- Need odd Zookepers, 1,3,5,7
- Zookeeper has a leaader that handles writes, others handle reads

### Kafka Guarantees
- Messages are appened to topic-partitions in the order they are sent
- As long as the # of partitions remain constant for a topic, the same key will always go to the same parition

Source Systems will send info to Kafka Producer, the producer will connect to Cluster and consumers of kafka cluster will publish to Target Systems


## Chapter 3 Kafka CLI

For me kafka is installed in ~/Sandboxes/kafka

try /bin/kafka-topics.sh

if you get an error, kafka is working

add kafka to the path
`export PATH=$PATH:/home/oy/Sandboxes/kafka/bin`
then
`source ~/.bashrc`
this will put kafka on the path

1. Create data directory for zookeeper
go to ~/Sandboxes/kafka and add a data directory to hold zookeeper data
```
cd ~/Sandboxes/kafka
mkdir data
cd data
mkdir zookepeer
```
so the folder structure is ~/Sandboxes/kafka/data/zookeeper

2. Open zookeper.properties file from the config folder

```
cd ~/Sandboxes/kafka
sudo nano config/zookeper.properties
```

edit the dataDir there
```
dataDir=home/oy/Sandboxes/kafka/data/zookeeper
```

3. start zookeper
```
cd ~/Sandboxes/kafka
bin/zookeper-server-start.sh config/zookeper.properties
```
Binds port to 2181
[2020-08-28 09:50:21,912] INFO binding to port 0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)


4. Check if version 2.0 is in zookeeper
```
cd ~/Sandboxes/kafka/data/zookeper
ll
```

5. Make a kafka directory under data
```
cd ~/Sandboxes/kafka/data
sudo mkdir kafka
```

6. Edit kafka server properties
```
cd ~/Sandboxes/kafka
sudo nano config/server.properties
```

7. now start kafka on a new terminal
```
sudo ./bin/kafka-server-start.sh config/server.properties 
```

#### Kafka Topics CLI

- Create a topic, register with zookeper at localhost binded port, topic name is first_topic, create 3 partitions and replicate among 1 broker, (for demo, for prod replicate at least 3 brokers, so you should have 3 machines to start with)
```kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1```

- Check if topic is created


```kafka-topics.sh --zookeeper 127.0.0.1:2181 --list```

- See Topic Details

```kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe```
output:

```
kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe
Topic: first_topic	PartitionCount: 3	ReplicationFactor: 1	Configs: 
	Topic: first_topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
	Topic: first_topic	Partition: 1	Leader: 0	Replicas: 0	Isr: 0
	Topic: first_topic	Partition: 2	Leader: 0	Replicas: 0	Isr: 0

```

- Delete topic

```
kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic second_topic --create --partitions 3 --replication-factor 1

kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic second_topic --delete

kafka-topics.sh --zookeeper 127.0.0.1:2181 --list
```

#### Kafka Console Producer

```kafka-console-producer.sh```

```kafka-console-producer.sh --topic first_topic --broker-list 127.0.0.1:9092```

```kafka-console-producer.sh --topic first_topic --broker-list 127.0.0.1:9092 --producer-property acks=all```

Producer sending a non-existing topic creates the new topic, with a warning, but it creates w/ default options 1 replica 1 partition

If you want to update default options, set server.properties file for num.partitions=3, this will create 3 partitions


To see the topics posted, start bootstrap server

``` kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic```

As you type messages on the producer, the consumer will show the results.

End of the Kafka Basics

