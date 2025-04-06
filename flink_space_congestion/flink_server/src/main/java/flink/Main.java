package flink;

import flink.functions.SpaceCongestionEventStatisticsCollector;
import flink.functions.SpaceCongestionAverageAggregator;
import flink.records.SpaceCongestionEvent;
import flink.records.SpaceCongestionEventDeserializationSchema;
import flink.records.SpaceCongestionEventStatistics;
import flink.records.SpaceCongestionEventStatisticsSerializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Main {

    public static final String CHECKPOINTING_OPTION = "checkpointing";
    public static final String EVENT_TIME_OPTION = "event-time";
    public static final String OPERATOR_CHAINING_OPTION = "chaining";
    public static final Time WINDOW_SIZE = Time.of(15, TimeUnit.SECONDS);

    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        configureEnvironment(params, env);

        String inputTopic = params.get("input-topic", "input");
        String outputTopic = params.get("output-topic", "output");
        String brokers = params.get("bootstrap.servers", "localhost:9094");
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "space-congestion-event-count");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        KafkaSource<SpaceCongestionEvent> source = KafkaSource.<SpaceCongestionEvent>builder()
                .setProperties(kafkaProps)
                .setTopics(inputTopic)
                .setValueOnlyDeserializer(new SpaceCongestionEventDeserializationSchema())
                .setStartingOffsets(OffsetsInitializer.earliest())
                .build();

        WatermarkStrategy<SpaceCongestionEvent> watermarkStrategy = WatermarkStrategy
                .<SpaceCongestionEvent>forBoundedOutOfOrderness(Duration.ofMillis(200))
                .withIdleness(Duration.ofSeconds(5))
                .withTimestampAssigner((spaceCongestionEvent, l) -> spaceCongestionEvent.getTimestamp().getTime());

        DataStream<SpaceCongestionEvent> clicks = env.fromSource(source, watermarkStrategy, "SpaceCongestionEvent Source");

        WindowAssigner<Object, TimeWindow> assigner = params.has(EVENT_TIME_OPTION) ?
                TumblingEventTimeWindows.of(WINDOW_SIZE) :
                TumblingProcessingTimeWindows.of(WINDOW_SIZE);

        DataStream<SpaceCongestionEventStatistics> statistics = clicks
                .keyBy(SpaceCongestionEvent::getSpaceId)
                .window(assigner)
                .aggregate(
                        new SpaceCongestionAverageAggregator(),
                        new SpaceCongestionEventStatisticsCollector()
                )
                .name("SpaceCongestionEvent");

        statistics.addSink(
                        JdbcSink.sink(
                                "INSERT INTO space_congestion_event_report (window_start, window_end, space_id, space_congestion) VALUES (?, ?, ?, ?)",
                                (statement, stat) -> {
                                    statement.setString(1, sdf.format(stat.getWindowStart()));
                                    statement.setString(2, sdf.format(stat.getWindowEnd()));
                                    statement.setString(3, stat.getSpaceId());
                                    statement.setDouble(4, stat.getSpaceCongestion());
                                },
                                JdbcExecutionOptions.builder()
                                        .withBatchSize(1000)
                                        .withBatchIntervalMs(15000)
                                        .withMaxRetries(5)
                                        .build(),
                                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                        .withUrl("jdbc:mysql://localhost:3306/space_congestion")
                                        .withDriverName("com.mysql.jdbc.Driver")
                                        .withUsername("root")
                                        .withPassword("1234")
                                        .build()
                        ))
                .name("SpaceCongestionEventStatistics MySQL Sink");

        env.execute("SpaceCongestion Event");
    }

    private static void configureEnvironment(
            final ParameterTool params,
            final StreamExecutionEnvironment env
    ) {

        boolean checkpointingEnabled = params.has(CHECKPOINTING_OPTION);
        boolean enableChaining = params.has(OPERATOR_CHAINING_OPTION);

        if (checkpointingEnabled) {
            env.enableCheckpointing(1000);
        }

        if (!enableChaining) {
            //disabling Operator chaining to make it easier to follow the Job in the WebUI
            env.disableOperatorChaining();
        }
    }
}