package flink.records;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class SpaceCongestionEventStatisticsSerializationSchema implements SerializationSchema<SpaceCongestionEventStatistics> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public byte[] serialize(SpaceCongestionEventStatistics event) {
    try {
      //if topic is null, default topic will be used
      return objectMapper.writeValueAsBytes(event);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Could not serialize record: " + event, e);
    }
  }
}
