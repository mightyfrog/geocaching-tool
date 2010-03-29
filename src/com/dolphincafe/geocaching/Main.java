package com.dolphincafe.geocaching;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Shigehiro Soejima
 */
public class Main {
    /**
     *
     */
    public static void main(String[] args) {
        int heapSize = 64;
        File file = new File("data/preferences.properties");
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(in);
            String str = prop.getProperty("maxHeapSize", "64");
            try {
                heapSize = Integer.parseInt(str);
            } catch (NumberFormatException e) {
                // ignore
            }
        } catch (IOException e) {
            // ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
     
        try {
            ProcessBuilder pb = new ProcessBuilder("java",
                                                   "-Xmx" + heapSize + "m",
                                                   "-jar", "geocaching.jar");
            pb.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
