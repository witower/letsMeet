package dev.witoldwerdon.letsMeetMvn;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Algorithm {
    public static void main(String[] args) {

        String calendar1InputStr = "{working_hours: {start: \"09:00\",end: \"20:00\"},planned_meeting: [{start: \"09:00\",end: \"10:30\"},{start: \"12:00\",end: \"13:00\" },{start: \"16:00\",end: \"18:30\"}] }";

        String calendar2InputStr = "{working_hours: {start: \"10:00\",end: \"18:30\"},planned_meeting: [{start: \"10:00\",end: \"11:30\"},{start: \"12:30\",end: \"14:30\"},{start: \"14:30\",end: \"15:00\"},{start: \"16:00\",end: \"17:00\"}]}";

        String meetingDurationInputStr = "[00:30]";

        String outputStr = letsMeet(
                calendar1InputStr,
                calendar2InputStr,
                meetingDurationInputStr);

        System.out.println(outputStr);
    }

    private static String letsMeet (String cal1, String cal2, String durationInput) {

        String durationStr = durationInput.substring(1,durationInput.length()-1);

        Calendar calendar1 = Calendar.fromJson(cal1);
        Calendar calendar2 = Calendar.fromJson(cal2);
        Duration meetingDuration = Duration.between(LocalTime.MIN, LocalTime.parse(durationStr));

        List<HoursRange> available1 = calendar1.getAvailableSchedule(meetingDuration);
        List<HoursRange> available2 = calendar2.getAvailableSchedule(Duration.ofMinutes(30));
        List<HoursRange> results = new ArrayList<>();

        int j = 0;

        for (int i = 0, size = available1.size(); i < size; i++) {

            LocalTime start1 = available1.get(i).getStart();
            LocalTime end1 = available1.get(i).getEnd();

            for (size = available2.size(); j < size; j++) {
                LocalTime start2 = available2.get(j).getStart();
                LocalTime end2 = available2.get(j).getEnd();

                if (end2.isBefore(start1.plus(meetingDuration))) continue;
                if (start2.isAfter(end1.minus(meetingDuration))) break;

                results.add(new HoursRange(
                        getComparedTime(start1, start2, false),
                        getComparedTime(end1, end2, true)));

                if (end1.isAfter(end2.plus(meetingDuration))) continue;

                if (end2.isAfter(end1.plus(meetingDuration))) break;
                else {
                    j++;
                    break;
                }
            }
        }
        return Arrays.deepToString(results.toArray());
    }
    private static LocalTime getComparedTime(LocalTime t1, LocalTime t2, boolean earlier){
        LocalTime r;
        switch (t1.compareTo(t2)*(earlier? 1 : -1)) {
            case -1:
                r = t1;
                break;
            case 0:
            case 1:
                r = t2;
                break;
            default:
                r = LocalTime.MIN;
                break;
        }
        return r;
    }
}