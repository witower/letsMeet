package dev.witoldwerdon.letsMeetMvn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Calendar {

    @SerializedName("working_hours")
    private HoursRange workingHours;

    @SerializedName("planned_meeting")
    private List<HoursRange> plannedMeetings = new ArrayList<>();

    public Calendar() {}

    public Calendar(HoursRange workingHours) {
        this.workingHours = workingHours;
    }

    public HoursRange getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(HoursRange workingHours) {
        this.workingHours = workingHours;
    }

    public List<HoursRange> getPlannedMeetings() {
        return plannedMeetings;
    }

    public void setPlannedMeetings(List<HoursRange> plannedMeetings) {
        this.plannedMeetings = plannedMeetings;
    }

    public List<HoursRange> getNotAvailableSchedule() {
        List<HoursRange> results = new ArrayList<>(plannedMeetings);
        results.add(0,new HoursRange(LocalTime.MIN,workingHours.getStart()));
        results.add(new HoursRange(workingHours.getEnd(), LocalTime.MAX));
        return results;
    }

    public List<HoursRange> getAvailableSchedule() {
        List<HoursRange> results = new ArrayList<>();
        List<HoursRange> notAvailableSchedule = getNotAvailableSchedule();

        for (int i = 1, size = notAvailableSchedule.size(); i < size; i++) {
            results.add(new HoursRange(
                    notAvailableSchedule.get(i-1).getEnd(),
                    notAvailableSchedule.get(i).getStart()));
        }

        return results;
    }

    public List<HoursRange> getAvailableSchedule(Duration meetingDuration) {
        return getAvailableSchedule().stream().
                filter(available -> available.getDuration().compareTo(meetingDuration) >= 0).
                collect(Collectors.toList());
    }

    public static Calendar fromJson (String cal) {
        Gson g = new GsonBuilder().registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, jsonDeserializationContext) ->
                LocalTime.parse(json.getAsJsonPrimitive().getAsString())).create();

        Calendar newCalendar = g.fromJson(cal, Calendar.class);

        newCalendar.setPlannedMeetings(
                newCalendar.getPlannedMeetings().stream().
                        filter(m -> m.getDuration().toMinutes() > 0).
                        filter(m -> m.getDuration().compareTo(
                                newCalendar.getWorkingHours().getDuration()) <= 0).
                        collect(Collectors.toList()));

        return newCalendar;
    }
}
