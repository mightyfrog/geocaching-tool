package com.dolphincafe.geocaching;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import javax.swing.UIManager;

/**
 *
 *
 */
class Pref {
    //
    private static final File PROP_FILE =
        new File("data/preferences.properties");
    private static Properties _prop;

    /**
     * Returns the user name.
     *
     */
    static String getUserName() {
        return _prop.getProperty("userName");
    }

    /**
     *
     *
     */
    static long getUserId() {
        return Long.parseLong(_prop.getProperty("userId", "-1"));
    }

    /**
     * Returns the latitude.
     *
     */
    static double getLatitude() {
        return Double.parseDouble(_prop.getProperty("latitude", "0.0"));
    }

    /**
     * Returns the longitude.
     *
     */
    static double getLongitude() {
        return Double.parseDouble(_prop.getProperty("longitude", "0.0"));
    }

    /**
     *
     */
    static int getCoordinatesMode() {
        // default = degree minute
        return Integer.parseInt(_prop.getProperty("coordinates", "0"));
    }

    /**
     *
     */
    static int getDistanceMode() {
        // default = statute system (miles)
        return Integer.parseInt(_prop.getProperty("distance", "1"));
    }

    /**
     *
     */
    static String getMapURL() {
        return _prop.getProperty("mapUrl",
                                 MapURLComboBox.MAP_URL_GEOCACHING);
    }

    /**
     *
     */
    static String getLookAndFeelClassName() {
        return _prop.getProperty("defaultLaf",
                                 UIManager.getSystemLookAndFeelClassName());
        //"javax.swing.plaf.metal.MetalLookAndFeel");
    }

    /**
     *
     */
    static String getLanguage() {
        return _prop.getProperty("language", null);
    }

    /**
     *
     */
    static Font getFont() {
        return Font.decode(_prop.getProperty("font"));
    }

    /**
     *
     */
    static Locale getLocale() {
        String l = getLanguage();
        if (l == null || l.equals("")) {
            return Locale.ROOT;
        }

        Locale locale = null;
        String[] params = l.split("_");
        switch (params.length) {
        case 1:
            locale = new Locale(params[0]);
            break;
        case 2:
            locale = new Locale(params[0], params[1]);
            break;
        case 3:
            locale = new Locale(params[0], params[1], params[2]);
            break;
        }

        Locale[] availabelLocales = I18N.getAvailableLocales();
        if (!Arrays.asList(availabelLocales).contains(locale)) {
            return Locale.ROOT;
        }

        return locale;
    }

    /**
     *
     */
    static int getMaxHeapSize() {
        String str = _prop.getProperty("maxHeapSize", "64");
        int heapSize = 64;
        try {
            heapSize = Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }

        return heapSize;
    }

    /**
     *
     */
    static String getGPXCreationTime() {
        return _prop.getProperty("gpxCreationTime", I18N.get("Pref.text.0"));
    }

    /**
     *
     */
    static boolean getAutoResizeColumns() {
        return Boolean.valueOf(_prop.getProperty("autoResizeColumns", "false"));
    }

    /**
     *
     * @param value
     */
    static void setUserName(String value) {
        _prop.setProperty("userName", value);
    }

    /**
     *
     * @param id
     */
    static void setUserId(long id) {
        _prop.setProperty("userId", "" + id);
    }

    /**
     *
     * @param value
     */
    static void setLatitude(double value) {
        _prop.setProperty("latitude", "" + value);
    }

    /**
     *
     * @param value
     */
    static void setLongitude(double value) {
        _prop.setProperty("longitude", "" + value);
    }

    /**
     *
     * @param value
     */
    static void setCoordinatesMode(int value) {
        _prop.setProperty("coordinates", "" + value);
    }

    /**
     *
     * @param value
     */
    static void setDistanceMode(int value) {
        _prop.setProperty("distance", "" + value);
    }

    /**
     *
     * @param value
     */
    static void setMapURL(String value) {
        _prop.setProperty("mapUrl", value);
    }

    /**
     *
     * @param value
     */
    static void setLanguage(String value) {
        _prop.setProperty("language", value);
    }

    /**
     *
     * @param font
     */
    static void setFont(Font font) {
        _prop.setProperty("font", font.getFamily() +
                              "-PLAIN-" + font.getSize());
    }

    /**
     *
     * @param value
     */
    static void setLookAndFeelClassName(String value) {
        _prop.setProperty("defaultLaf", value);
    }

    /**
     *
     * @param heapSize
     */
    static void setMaxHeapSize(int heapSize) {
        _prop.setProperty("maxHeapSize", "" + heapSize);
    }

    /**
     *
     * @param gpxCreationTime
     */
    static void setGPXCreationTime(String gpxCreationTime) {
        _prop.setProperty("gpxCreationTime", gpxCreationTime);
    }

    /**
     *
     * @param autoResizeColumns
     */
    static void setAutoResizeColumns(boolean autoResizeColumns) {
        _prop.setProperty("autoResizeColumns", "" + autoResizeColumns);
        store();
    }

    //
    //
    //

    /**
     *
     */
    static boolean load() {
        _prop = new Properties();
        if (PROP_FILE.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(PROP_FILE);
                _prop.load(in);
            } catch (IOException e) {
                // ignore
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            return true;
        }

        return false;
    }

    /**
     *
     */
    static void store() {
        FileOutputStream out = null;
        try {
            File parentDir = PROP_FILE.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdir()) {
                // do something here
                return;
            }
            out = new FileOutputStream(PROP_FILE);
            _prop.store(out,
                        "DO NOT MODIFY UNLESS YOU KNOW WHAT YOU ARE DOING.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        load();
    }
}
