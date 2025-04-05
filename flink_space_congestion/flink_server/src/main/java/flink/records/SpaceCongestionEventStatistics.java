package flink.records;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;

public class SpaceCongestionEventStatistics {

  //using java.util.Date for better readability
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss:SSS")
  private Date windowStart;
  //using java.util.Date for better readability
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss:SSS")
  private Date windowEnd;
  private String spaceId;
  private double spaceCongestion;

  public SpaceCongestionEventStatistics() {
  }

  public SpaceCongestionEventStatistics(
    final Date windowStart,
    final Date windowEnd,
    final String spaceId,
    final double spaceCongestion
  ) {
    this.windowStart = windowStart;
    this.windowEnd = windowEnd;
    this.spaceId = spaceId;
    this.spaceCongestion = spaceCongestion;
  }

  public Date getWindowStart() {
    return windowStart;
  }

  public void setWindowStart(Date windowStart) {
    this.windowStart = windowStart;
  }

  public Date getWindowEnd() {
    return windowEnd;
  }

  public void setWindowEnd(Date windowEnd) {
    this.windowEnd = windowEnd;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public double getSpaceCongestion() {
    return spaceCongestion;
  }

  public void setSpaceCongestion(double spaceCongestion) {
    this.spaceCongestion = spaceCongestion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SpaceCongestionEventStatistics that = (SpaceCongestionEventStatistics) o;
    return Double.compare(spaceCongestion, that.spaceCongestion) == 0 && Objects.equals(windowStart, that.windowStart) && Objects.equals(windowEnd, that.windowEnd) && Objects.equals(spaceId, that.spaceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(windowStart, windowEnd, spaceId, spaceCongestion);
  }

  @Override
  public String toString() {
    return "SpaceCongestionEventStatistics{" +
            "windowStart=" + windowStart +
            ", windowEnd=" + windowEnd +
            ", spaceId='" + spaceId + '\'' +
            ", spaceCongestion=" + spaceCongestion +
            '}';
  }
}
