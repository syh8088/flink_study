package flink.functions;

import flink.records.ClickEvent;
import org.apache.flink.api.common.functions.AggregateFunction;

public class CountingAggregator implements AggregateFunction<ClickEvent, Long, Long> {
  @Override
  public Long createAccumulator() {
    return 0L;
  }

  @Override
  public Long add(final ClickEvent value, final Long accumulator) {
    return accumulator + 1;
  }

  @Override
  public Long getResult(final Long accumulator) {
    return accumulator;
  }

  @Override
  public Long merge(final Long a, final Long b) {
    return a + b;
  }
}
