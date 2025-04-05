package flink.functions;

import flink.records.SpaceCongestionEvent;
import org.apache.flink.api.common.functions.MapFunction;

import java.time.LocalTime;

public class BackpressureMap implements MapFunction<SpaceCongestionEvent, SpaceCongestionEvent> {
  private boolean causeBackpressure() {
    return ((LocalTime.now().getMinute() % 2) == 0);
  }

  @Override
  public SpaceCongestionEvent map(SpaceCongestionEvent event) throws Exception {
    if (causeBackpressure()) {
      Thread.sleep(100);
    }

    return event;
  }
}
