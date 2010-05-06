package com.dolphincafe.geocaching;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.jar.JarFile;

/**
 *
 * @author Shigehiro Soejima
 */
class I18N {
    public static final String BASE = "i18n";

    // current bundle
    private static ResourceBundle _rb =
        ResourceBundle.getBundle(BASE, Pref.getLocale());

    /**
     *
     */
    private I18N() {
        // no-op
    }

    /**
     *
     * @param key
     */
    static String get(String key) {
        String str = "<MISSING RESOURCE>";
        try {
            str = _rb.getString(key);
        } catch (MissingResourceException e) {
            System.out.println("missing key: " + key);
            // ignore
        }

        return str;
    }

    /**
     *
     *
     * @param key
     * @param inserts
     */
    static String get(String key, Object... inserts) {
        String str = get(key);
        
        for (int i = 0; i < inserts.length; i++) {
            str = str.replaceAll("%" + i, String.valueOf(inserts[i]));
        }

        return str;
    }

    /**
     *
     */
    static char getMnemonic(String key) {
        String s = get(key);
        char c = '\uFFFF'; // never matches anything
        if (s.length() == 1) {
            c = s.charAt(0);
        }
        if (c == '\uFFFF') {
            System.err.println("missing mnemonic: " + key);
        }

        return c;
    }

    /**
     * Returns available locales.
     *
     */
    static Locale[] getAvailableLocales() {
        List<String> list = new ArrayList<String>();
        URL url =
            I18N.class.getProtectionDomain().getCodeSource().getLocation();
        File file = null;
        try {
            file = new File(url.toURI());
            JarFile jarFile = new JarFile(file);
            Enumeration enm = jarFile.entries();
            while (enm.hasMoreElements()) {
                String path = "" + enm.nextElement();
                if (path.startsWith(BASE)) {
                    list.add(path.substring(BASE.length(), path.length()));
                }
            }
        } catch (URISyntaxException e) {
            // won't happen
        } catch (IOException e) {
            // no-op
        }
        if (file == null) {
            // TODO: show error dialog here
            return new Locale[]{};
        }
        File parentDir = file.getParentFile();
        File[] files = parentDir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                continue;
            }
            String name = f.getName();
            if (name.startsWith(BASE) && name.endsWith(".properties")) {
                // TODO: this condition could pick up the wrong properties
                list.add(name.substring(BASE.length(), name.length()));
            }
        }
        File workingDir = new File(System.getProperty("user.dir"));
        if (!workingDir.toString().equals(parentDir.toString())) {
            files = workingDir.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    continue;
                }
                String name = f.getName();
                if (name.startsWith(BASE) && name.endsWith(".properties")) {
                    // TODO: this condition could pick up the wrong properties
                    list.add(name.substring(BASE.length(), name.length()));
                }
            }
        }
        Locale[] locales = new Locale[list.size()];
        for (int i = 0; i < locales.length; i++) {
            String str = list.get(i);
            if (str.charAt(0) == '.') {
                locales[i] = Locale.ROOT;
            } else {
                String localeStr = str.substring(1, str.indexOf("."));
                locales[i] = toLocale(localeStr);
            }
        }

        return locales;
    }

    /**
     *
     * @param l locale string, like en_US
     */
    static Locale toLocale(String l) {
        if (l == null) {
            return Locale.getDefault();
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

        return locale;
    }

    /**
     *
     * @param type
     */
    static String getCacheTypeName(int type) {
        String str = null;
        switch (type) {
        case CacheBean.TYPE_TRADITIONAL:
            str = "Traditional Cache";
            break;
        case CacheBean.TYPE_MULTI:
            str = "Multi-Cache";
            break;
        case CacheBean.TYPE_UNKNOWN:
            str = "Mystery or Puzzle Cache";
            break;
        case CacheBean.TYPE_WEBCAM:
            str = "Webcam Cache";
            break;
        case CacheBean.TYPE_VIRTUAL:
            str = "Virtual Cache";
            break;
        case CacheBean.TYPE_EVENT:
            str = "Event Cache";
            break;
        case CacheBean.TYPE_EARTH:
            str = "Earthcache";
            break;
        case CacheBean.TYPE_LETTERBOX_HYBRID:
            str = "Letterbox Hybrid";
            break;
        case CacheBean.TYPE_CITO:
            str = "Cache In Trash Out Event";
            break;
        case CacheBean.TYPE_LOCATIONLESS:
            str = "Locationless (Reverse) Cache";
            break;
        case CacheBean.TYPE_PROJECT_APE:
            str = "Project APE Cache";
            break;
        case CacheBean.TYPE_MEGA:
            str = "Mega-Event Cache";
            break;
        case CacheBean.TYPE_PARKING:
            str = "Parking Area";
            break;
        case CacheBean.TYPE_REF_POINT:
            str = "Reference Point";
            break;
        case CacheBean.TYPE_WHERIGO:
            str = "Wherigo Cache";
            break;
        case CacheBean.TYPE_LOST_AND_FOUND:
            str = "Lost And Found";
            break;
        default:
            str = "Not Supported Yet";
        }

        return str;
    }

    /**
     *
     * @param container
     */
    static String getContainer(int container) {
        String str = null;
        switch (container) {
        case CacheBean.CONTAINER_SMALL:
            str = "Small";
            break;
        case CacheBean.CONTAINER_MICRO:
            str = "Micro";
            break;
        case CacheBean.CONTAINER_REGULAR:
            str = "Regular";
            break;
        case CacheBean.CONTAINER_NOT_CHOSEN:
            str = "Not chosen";
            break;
        case CacheBean.CONTAINER_OTHER:
            str = "Other";
            break;
        case CacheBean.CONTAINER_VIRTUAL:
            str = "Virtual";
            break;
        case CacheBean.CONTAINER_LARGE:
            str = "Large";
            break;
        }

        return str;
    }
}
