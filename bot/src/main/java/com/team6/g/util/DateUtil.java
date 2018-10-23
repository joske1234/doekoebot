package com.team6.g.util;

import com.team6.g.model.UserActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    public static boolean isBeforeNoon(Date date) {
        return date.before(getNoonInstance());
    }

    public static boolean isAfterNoon(Date date) {
        return date.after(getNoonInstance());
    }
    
    private static Date getNoonInstance() {
        Calendar noon = Calendar.getInstance();
        noon.set(Calendar.HOUR_OF_DAY, 12);

        return noon.getTime();
    }

    public static String getWorkedTime(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return String.format("%d hour(s), %d minute(s) and %d second(s)", elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    public static String calculateOverTime(List<UserActivity> userActivities) {
        long totalOverTime = 0L;

        for (UserActivity userActivity : userActivities) {
            long diffInMillies = (userActivity.getDateOut().getTime() - userActivity.getDateIn().getTime()) - TimeUnit.MINUTES.toMillis(userActivity.getUser().getWorkPeriodMinutes());

            totalOverTime += diffInMillies; 
        }

        return formatTimeToString(totalOverTime);
    }

    private static String formatTimeToString(long millis) {
        long secs = millis / 1000;
        return String.format("%02d hour(s), %02d minute(s), %02d second(s)", secs / 3600, (secs % 3600) / 60, secs % 60);
    }
}
