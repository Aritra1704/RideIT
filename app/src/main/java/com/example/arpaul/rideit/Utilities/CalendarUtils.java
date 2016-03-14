package com.example.arpaul.rideit.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ARPaul on 13-03-2016.
 */
public class CalendarUtils {
    private static final String DATE_FORMAT_WITH_COMMA = "dd MMM, yyyy\nhh:mm:ss aa";
    private static final String DATE_TIME_FORMAT = "dd-MM-yyyy'T'HH:mm:ss";

    public static String getCommaFormattedDateTime(String dateTime) {
        String reqDate = "";

        Calendar calendar = Calendar.getInstance();

        if (dateTime.contains("T")){
            String date = dateTime.split("T")[0];
            String str[] = date.split("-");

            calendar.set(Calendar.DAY_OF_MONTH,StringUtils.getInt(str[0]));
            calendar.set(Calendar.MONTH,StringUtils.getInt(str[1]) - 1);
            calendar.set(Calendar.YEAR,StringUtils.getInt(str[2]));

            String time = dateTime.split("T")[1];
            String strTime[] = time.split(":");
            calendar.set(Calendar.HOUR_OF_DAY,StringUtils.getInt(strTime[0]));
            calendar.set(Calendar.MINUTE,StringUtils.getInt(strTime[1]));
            calendar.set(Calendar.SECOND,StringUtils.getInt(strTime[2]));
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_WITH_COMMA);
        reqDate = simpleDateFormat.format(calendar.getTime());

        return reqDate;
    }

    public static String getCurrentDateTime() {
        String reqDate = "";

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        reqDate = simpleDateFormat.format(calendar.getTime());

        return reqDate;
    }
}
