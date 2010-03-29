package com.dolphincafe.geocaching;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 */
class FormatUtil {
    //
    static final SimpleDateFormat SDF =
        new SimpleDateFormat("yyyy/MM/dd HH:mm");

    /**
     *
     * @param time
     */
    static String formatDate(long time) {
        DateFormat df =
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return df.format(new Date(time));
    }

    /**
     *
     * @param time
     */
    static String formatDateTime(long time) {
        String date = null;
        synchronized (SDF) {
            date = SDF.format(new Date(time));
        }
        return date;
    }

    /**
     *
     *
     */
    static double formatDistance(double distance) {
        return Double.parseDouble(String.format("%.3f", distance));
    }

    /**
     *
     *
     */
    static double formatBearing(double bearing) {
        return Double.parseDouble(String.format("%.2f", bearing));
    }
}
