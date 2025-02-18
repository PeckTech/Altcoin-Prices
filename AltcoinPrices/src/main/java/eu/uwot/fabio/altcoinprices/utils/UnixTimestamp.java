package eu.uwot.fabio.altcoinprices.utils;

import java.util.Calendar;

public class UnixTimestamp {

    public long getUnixTimestamp (int minute, int hour, int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTimeInMillis() / 1000;
    }

    public long getYesterdayUnixTimestamp () {
        Calendar calendar = Calendar.getInstance();

        // Yesterday at midnight
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar.getTimeInMillis() / 1000;
    }

}
