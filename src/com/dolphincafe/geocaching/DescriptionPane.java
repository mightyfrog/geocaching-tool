package com.dolphincafe.geocaching;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 *
 * @author Shigehiro Soejima
 */
class DescriptionPane extends TextPane {
    // groundspeak smiley icons
    private final Pattern ICON_PATTERN =
        Pattern.compile("^http://www.geocaching.com/images/icons/.*.gif$");

    private final Pattern IMG_PATTERN = // TODO: redefine me
        Pattern.compile("(?i)http:[^\"]*(jpg|gif|png|bmp|tiff)(?!<\" )");

    private final Map<String, String> IMG_MAP = createCacheMap();
    private final Set<String> IMG_SET = new HashSet<String>(0);

    private File imgCacheDir = null;
    private File cacheDataFile = null;


    /**
     * Creates a DescriptionPane.
     *
     */
    public DescriptionPane() {
        super();

        addHyperlinkListener(new HyperlinkListener() {
                /** */
                @Override
                public void hyperlinkUpdate(HyperlinkEvent evt) {
                    HyperlinkEvent.EventType type = evt.getEventType();
                    GCOrganizer gco = (GCOrganizer) getTopLevelAncestor();
                    if (type == HyperlinkEvent.EventType.ENTERED) {
                        gco.setStatus(String.valueOf(evt.getURL()), 1);
                    } else if (type == HyperlinkEvent.EventType.EXITED) {
                        gco.setStatus(null, 1);
                    }
                }
            });

        this.imgCacheDir = new File("data/imgcache");
        if (!this.imgCacheDir.exists()) {
            if (this.imgCacheDir.mkdirs()) {
                // TODO: show error dialog here
            }
        }
    }

    /**
     *
     * @param text
     */
    @Override
    public void setText(String text) {
        try {
            Matcher m = ICON_PATTERN.matcher(text);
            String url = null;
            while (m.find()) {
                url = m.group();
                text = text.replace(url, getIconResourceURL(url));
            }

            m = IMG_PATTERN.matcher(text);
            while (m.find()) {
                final String imgUrl = m.group();
                String cache = getImageCache(imgUrl);
                if (cache == null) {
                    new Thread(new Runnable() {
                            /** */
                            @Override
                            public void run() {
                                cacheImage(imgUrl);
                            }
                        }).start();
                } else {
                    text = text.replace(imgUrl, cache);
                }
            }
        } catch (PatternSyntaxException e) {
            // shouldn't happen
            e.printStackTrace();
        }

        super.setText(text);
    }

    //
    //
    //

    /**
     *
     * @param url
     */
    void cacheImage(final String url) {
        if (IMG_SET.contains(url)) { // avoids duplicate
            return;
        }
        IMG_SET.add(url);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        String cachePath = url;
        String ext = url.substring(url.lastIndexOf("."),
                                   url.length());
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("img", ext, this.imgCacheDir);
            cachePath = "" + tmpFile.toURI().toURL();
            in = new BufferedInputStream(new URL(url).openStream());
            out = new BufferedOutputStream(new FileOutputStream(tmpFile));
            int n = 0;
            byte[] b = new byte[4096];
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            out.close();
        } catch (MalformedURLException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
            
        if (FileUtil.isImageFile(tmpFile)) {
            IMG_MAP.put(url, cachePath);
            IMG_SET.remove(url);
            serializeCacheData();
        } else {
            if (!tmpFile.delete()) {
                tmpFile.deleteOnExit();
            }
        }
    }

    /**
     *
     */
    void serializeCacheData() {
        if (this.cacheDataFile == null) {
            this.cacheDataFile = new File("data/imgcache/imgcache.ser");
        }
        if (!this.cacheDataFile.getParentFile().exists()) {
            return;
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(this.cacheDataFile));
            out.writeObject(IMG_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

    //
    //
    //

    /**
     *
     * @param imgUrl
     */
    private String getImageCache(String imgUrl) {
        String cacheUrl = IMG_MAP.get(imgUrl);
        try {
            if (cacheUrl == null ||
                !new File(new URL(cacheUrl).getFile()).exists()) {
                IMG_MAP.remove(imgUrl);
                return null;
            }
        } catch (MalformedURLException e) {
            IMG_MAP.remove(imgUrl);
            return null;
        }

        return cacheUrl;
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> createCacheMap() {
        Map<String, String> map = new HashMap<String, String>();
        File file = new File("data/imgcache/imgcache.ser");
        if (file.exists()) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream(file));
                map = (Map<String, String>) in.readObject();
            } catch (ClassNotFoundException e) {
            } catch (IOException e) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        return java.util.Collections.synchronizedMap(map);
    }

    /**
     *
     * @param url the original url
     */
    private String getIconResourceURL(String url) {
        String name = url.substring(url.lastIndexOf("/") + 1, url.length());
        return "" + DescriptionPane.class.getResource("icons/" + name);
    }
}
