package com.dolphincafe.geocaching;

/**
 * Geographic coordinates converter.
 *
 * DM: Degree Minute
 * DD: Decimal Degree
 * DMS: Degree, Minute, Second
 *
 * @author Shigehiro Soejima
 */
class GeoCoordConverter {
    /**
     *
     */
    private enum LatLong {LATITUDE, LONGITUDE};

    /**
     * The earth's equatorial radius in km.
     *
     */
    public static final double ER_KM = 6378.135;

    /**
     * The earth's equatorial radius in statute miles.
     *
     */
    public static final double ER_SM = 3963.189;

    /**
     * The earth's equatorial radius in nautical miles.
     *
     */
    public static final double ER_NM = 3443.917;

    /**
     * The earth's polar radius in km.
     *
     */
    public static final double PR_KM = 6356.750;

    /**
     * The earth's polar radius in statute miles.
     *
     */
    public static final double PR_SM = 3949.901;

    /**
     * The earth's polar radius in nautical miles.
     *
     */
    public static final double PR_NM = 3432.370;


    /**
     * The earth's meridional radius in km.
     *
     */
    public static final double MR_KM = 6370.999;

    /**
     * The earth's meridional radius in statute miles.
     *
     */
    public static final double MR_SM = 3956.548;

    /**
     * The earth's meridional radius in nautical miles.
     *
     */
    public static final double MR_NM = 3438.146;

    /**
     *
     */
    public static final String DEG = "\u00B0";

    /**
     *
     */
    public static final String MIN = "'";

    /**
     *
     */
    public static final String SEC = "\"";

    /**
     *
     *
     */
    public static final GeoCoordConverter LATITUDE = new GeoCoordConverter(LatLong.LATITUDE);

    /**
     *
     *
     */
    public static final GeoCoordConverter LONGITUDE = new GeoCoordConverter(LatLong.LONGITUDE);

    //
    private LatLong type = null;

    /**
     *
     *
     * @param type
     */
    private GeoCoordConverter(LatLong type) {
        this.type = type;
    }

    //
    // DM to DD, DMS
    //

    /**
     * Converts degree minute to decimal degree.
     *
     * @param degmin
     */
    public double toDecimalDegree(int[] degmin) {
        double[] dms = new double[3];
        dms[0] *= Math.signum(dms[0]);
        dms[0] = (double) degmin[0];
        dms[1] = (double) degmin[1];
        dms[2] = (double) degmin[2] / 1000 * 60;

        return toDecimalDegree(dms);
    }

    /**
     * Converts degree minute to degree minute second.
     *
     * @param degmin
     */
    public double[] toDegreeMinuteSecond(int[] degmin) {
        double[] dms = new double[3];
        dms[0] = (double) degmin[0];
        dms[1] = (double) degmin[1];
        dms[2] = (double) degmin[2] / 1000 * 60;

        return dms;
    }

    /**
     * Converts degree minute to degree minute second string.
     *
     * @param degmin
     */
    public String toDegreeMinuteSecondString(int[] degmin) {
        double[] dms = toDegreeMinuteSecond(degmin);

        return "" + (int) dms[0] + DEG + (int) dms[1] + MIN + dms[2] + SEC;
    }

    //
    // DMS to DD, DM
    //

    /**
     * Converts degree minute second to decimal degree.
     *
     * @param dms double array containing degree, minute, and second
     */
    public double toDecimalDegree(double[] dms) {
        double signum = Math.signum(dms[0]);
        dms[0] *= signum;
        return signum * (dms[0] + dms[1] / 60 + dms[2] / 3600);
    }

    /**
     * Converts degree minute second to degree minute.
     *
     * @param dms degree, minute, and second
     */
    public int[] toDegreeMinute(double[] dms) {
        int[] degmin = new int[3];
        degmin[0] = (int) Math.round(dms[0]);
        degmin[1] = (int) Math.round(dms[1]);
        degmin[2] = (int) Math.round(dms[2] / 60 * 1000);

        return degmin;
    }

    /**
     * Converts degree minute second to degree minute string.
     *
     * @param decdeg decimal degree
     * @return degree minute in string
     */
    public String toDegreeMinuteString(int[] dms) {
        return "" + dms[0] + DEG + dms[1] + "." + dms[2];
    }

    //
    // DD to DM, DMS
    //

    /**
     * Converts decimal degree to degree minute.
     *
     * @param decdeg decimal degree
     */
    public double[] toDegreeMinute(double decdeg) {
        double[] degmin = new double[3];
        double[] dms = toDegreeMinuteSecond(decdeg);
        degmin[0] = dms[0];
        degmin[1] = dms[1];
        degmin[2] = (dms[2] / 60) * 1000;

        return degmin;
    }

    /**
     * Converts decimal degree to degree minute string.
     *
     * @param decdeg decimal degree
     * @return degree minute in string
     */
    public String toDegreeMinuteString(double decdeg) {
        double[] mindec = toDegreeMinute(decdeg);

        String str = null;
        if (this.type == LatLong.LATITUDE) {
            if (decdeg < 0) {
                str = "S";
            } else {
                str = "N";
            }
        } else {
            if (decdeg < 0) {
                str = "W";
            } else {
                str = "E";
            }
        }

        String degree = "" + Math.abs((int) mindec[0]);
        String minute = "" + (int) mindec[1];
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        String decimal = "" + Math.round(mindec[2]);
        if (decimal.length() == 1) {
            decimal = "00" + decimal;
        } else if (decimal.length() == 2) {
            decimal = "0" + decimal;
        }

        str += degree + DEG + minute + "." + decimal;

        return str;
    }

    /**
     * Converts decimal degree to degree minute second.
     *
     * @param decdeg decimal degree
     */
    public double[] toDegreeMinuteSecond(double decdeg) {
        int sign = decdeg < 0 ? -1 : 1;
        decdeg = Math.abs(decdeg);
        double[] dms = new double[3];
        dms[0] = sign * (int) decdeg;
        dms[1] = ((decdeg - dms[0]) * 60) % 60;
        dms[2] = ((decdeg - dms[0]) * 3600) % 60;

        return dms;
    }

    /**
     * Converts decimal degree to degree minute second string.
     *
     * @param decdeg decimal degree
     * @return degree, minute, and second in string
     */
    public String toDegreeMinuteSecondString(double decdeg) {
        double[] dms = toDegreeMinuteSecond(decdeg);

        String str = null;
        if (this.type == LatLong.LATITUDE) {
            if (decdeg < 0) {
                str = "S";
            } else {
                str = "N";
            }
        } else {
            if (decdeg < 0) {
                str = "W";
            } else {
                str = "E";
            }
        }
        str += "" + Math.abs((int) dms[0]) + DEG + (int) dms[1] + MIN +
            String.format("%.3f", dms[2]) + SEC;

        return str;
    }

    /**
     * Calculates the distance between two coordinate points.
     *
     * @param lat1 a latitude in deciaml degree, use negative degree for S
     * @param lon1 a longitude in decimal degree, use negative degree for W
     * @param lat2 a latitude in deciaml degree, use negative degree for S
     * @param lon2 a longitude in decimal degree, use negative degree for W
     * @param earthRadius the earth radius
     * @see #calculateDistance(double, double, double, double)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2,
                                           double lon2, double earthRadius) {
        double a1 = Math.toRadians(lat1);
        double a2 = Math.toRadians(lat2);
        double b1 = Math.toRadians(lon1);
        double b2 = Math.toRadians(lon2);

        return Math.acos(Math.cos(a1) * Math.cos(b1) * Math.cos(a2) * Math.cos(b2) +
                         Math.cos(a1) * Math.sin(b1) * Math.cos(a2) * Math.sin(b2) +
                         Math.sin(a1) * Math.sin(a2)) * earthRadius;
    }

    /**
     * Calculates the distance between two coordinate points using meridional
     * earth radius in statute miles.
     *
     * @param lat1 a latitude in deciaml degree, use negative degree for S
     * @param lon1 a longitude in decimal degree, use negative degree for W
     * @param lat2 a latitude in deciaml degree, use negative degree for S
     * @param lon2 a longitude in decimal degree, use negative degree for W
     * @see #calculateDistance(double, double, double, double, double)
     */
    public static double calculateDistance(double lat1, double lon1,
                                           double lat2, double lon2) {
        return calculateDistance(lat1, lon1, lat2, lon2, MR_SM);
    }

    /**
     * Calculates bearing of two coordinate points.
     *
     * @return bearing in degree
     */
    public static double calculateBearing(double lat1, double lon1,
                                          double lat2, double lon2) {
        double y = Math.sin(lon2 - lon1) * Math.cos (lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) -
            Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);
        double theta = Math.atan2(y, x) % (2 * Math.PI);
        double degree = Math.toDegrees(theta);

        return degree;
    }

    /**
     *
     *
     */
    public static double calculateBearingInCompassDegree(double lat1, double lon1,
                                                         double lat2, double lon2) {
        double degree = calculateBearing(lat1, lon1, lat2, lon2);
        degree *= -1;
        if (degree > 0) {
            degree += 180;
        } else {
            degree = 180 + degree;
        }

        return 360 - degree;
    }
}
