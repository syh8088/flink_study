package flink.records;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;

public class SpaceCongestionEvent {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss:SSS")
  private Date timestamp;
  private String spaceId;
  private int peopleCount;

  public SpaceCongestionEvent() {
  }

  public SpaceCongestionEvent(final Date timestamp, final String spaceId, final int peopleCount) {
    this.timestamp = timestamp;
    this.spaceId = spaceId;
    this.peopleCount = peopleCount;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(final String spaceId) {
    this.spaceId = spaceId;
  }

  public int getPeopleCount() {
    return peopleCount;
  }

  public void setPeopleCount(int peopleCount) {
    this.peopleCount = peopleCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SpaceCongestionEvent that = (SpaceCongestionEvent) o;
    return peopleCount == that.peopleCount && Objects.equals(timestamp, that.timestamp) && Objects.equals(spaceId, that.spaceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, spaceId, peopleCount);
  }

  @Override
  public String toString() {
    return "SpaceCongestionEvent{" +
            "timestamp=" + timestamp +
            ", spaceId='" + spaceId + '\'' +
            ", peopleCount=" + peopleCount +
            '}';
  }
}
