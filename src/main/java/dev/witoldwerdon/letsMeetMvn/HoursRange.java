package dev.witoldwerdon.letsMeetMvn;

import java.time.Duration;
import java.time.LocalTime;

public class HoursRange {
    private LocalTime start;
    private LocalTime end;

    public HoursRange(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "[\"" + start + "\", \"" + end + "\"]";
    }

    public Duration getDuration() {
        return Duration.between(start, end);
    }
}
