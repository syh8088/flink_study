package flink.records;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SpaceCongestionEventDeserializationSchema implements DeserializationSchema<SpaceCongestionEvent> {
  private static final long serialVersionUID = 1L;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public SpaceCongestionEvent deserialize(byte[] message) throws IOException {
    return objectMapper.readValue(message, SpaceCongestionEvent.class);
  }

  @Override
  public boolean isEndOfStream(SpaceCongestionEvent nextElement) {
    return false;
  }

  @Override
  public TypeInformation<SpaceCongestionEvent> getProducedType() {
    return TypeInformation.of(SpaceCongestionEvent.class);
  }
}
