package flink;

import flink.records.SpaceCongestionEvent;
import flink.records.SpaceCongestionEventStatisticsSerializationSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EventMain {

    public static final int EVENTS_PER_WINDOW = 1000;
    private static final List<String> SPACE_ID_LIST = Arrays.asList("AAA", "BBB", "CCC", "DDD", "EEE", "FFF");


    public static void main(String[] args) throws Exception {

        final ParameterTool params = ParameterTool.fromArgs(args);

        String topic = params.get("topic", "input");

        Properties kafkaProps = createKafkaProperties(params);

        KafkaProducer<byte[], byte[]> producer = new KafkaProducer<>(kafkaProps);

        SpaceCongestionIterator spaceCongestionIterator = new SpaceCongestionIterator();

        while (true) {

            ProducerRecord<byte[], byte[]> record = new SpaceCongestionEventSerializationSchema(topic).serialize(
                    spaceCongestionIterator.next(),
                    null
            );

            producer.send(record);

            //Thread.sleep(DELAY);

            Thread.sleep((long) ((Math.random() * (10 - 1)) + 1));
        }
    }

    private static Properties createKafkaProperties(final ParameterTool params) {
        String brokers = params.get("bootstrap.servers", "localhost:9092");
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
        return kafkaProps;
    }

    static class SpaceCongestionIterator {

        private final Map<String, Long> nextTimestampPerKey;
        private int nextPageIndex;

        SpaceCongestionIterator() {
            nextTimestampPerKey = new HashMap<>();
            nextPageIndex = 0;
        }

        SpaceCongestionEvent next() {
            String spaceId = this.nextPage();
            int peopleCount = this.nextPeopleCount();
            return new SpaceCongestionEvent(new Date(), spaceId, peopleCount);
        }

        private int nextPeopleCount() {
            return this.getRandomNumberUsingThreadLocalRandom(0, 50);
        }

        private int getRandomNumberUsingThreadLocalRandom(int min, int max) {
            return ThreadLocalRandom.current().nextInt(min, max);
        }

        private Date nextTimestamp(String page) {
            long nextTimestamp = nextTimestampPerKey.getOrDefault(page, 0L);
            nextTimestampPerKey.put(page, nextTimestamp + Main.WINDOW_SIZE.toMilliseconds() / EVENTS_PER_WINDOW);
            return new Date(nextTimestamp);
        }

        private String nextPage() {

            String nextPage = SPACE_ID_LIST.get(nextPageIndex);
            if (nextPageIndex == SPACE_ID_LIST.size() - 1) {
                nextPageIndex = 0;
            }
            else {
                nextPageIndex++;
            }

            return nextPage;
        }
    }
}
