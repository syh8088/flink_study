package flink.functions;

import flink.records.SpaceCongestionEventStatistics;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;

public class SpaceCongestionEventStatisticsCollector
  extends ProcessWindowFunction<Double, SpaceCongestionEventStatistics, String, TimeWindow> {

  @Override
  public void process(
    String s,
    ProcessWindowFunction<Double, SpaceCongestionEventStatistics, String, TimeWindow>.Context context,
    Iterable<Double> iterable,
    Collector<SpaceCongestionEventStatistics> collector
  ) throws Exception {

    Double averagePeopleCount = iterable.iterator().next();

    collector.collect(new SpaceCongestionEventStatistics(new Date(context.window().getStart()), new Date(context.window().getEnd()), s, averagePeopleCount));
  }
}
