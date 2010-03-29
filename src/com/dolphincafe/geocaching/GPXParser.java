package com.dolphincafe.geocaching;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 */
class GPXParser {
    static final String gpx = "http://www.topografix.com/GPX/1/0";
    static final QName TIME_QN = new QName(gpx, "time"); // gpx creation time
    static final QName WPT_QN = new QName(gpx, "wpt");
    static final QName ID_QN = new QName(null, "id");
    static final QName ARCHIVED_QN = new QName(null, "archived");
    static final QName AVAILABLE_QN = new QName(null, "available");
    static final QName HTML_QN = new QName(null, "html");
    static final QName LAT_QN = new QName(null, "lat");
    static final QName LON_QN = new QName(null, "lon");

    private Map<String, CacheBean> map = new HashMap<String, CacheBean>();

    private static GPXParser _singleton = null;

    private String time = null;

    /**
     * State name and its abbreviation map, like California and CA.
     *
     */
    public static final Map<String, String> STATE_ABRV_MAP =
        new HashMap<String, String>();

    /**
     *
     */
    private GPXParser() {
        STATE_ABRV_MAP.put("Alabama", "AL");
        STATE_ABRV_MAP.put("Alaska", "AK");
        STATE_ABRV_MAP.put("Arizona", "AZ");
        STATE_ABRV_MAP.put("Arkansas", "AR");
        STATE_ABRV_MAP.put("California", "CA");
        STATE_ABRV_MAP.put("Colorado", "CO");
        STATE_ABRV_MAP.put("Connecticut", "CT");
        STATE_ABRV_MAP.put("Delaware", "DE");
        STATE_ABRV_MAP.put("District of Columbia", "DC");
        STATE_ABRV_MAP.put("Florida", "FL");
        STATE_ABRV_MAP.put("Georgia", "GA");
        STATE_ABRV_MAP.put("Hawaii", "HI");
        STATE_ABRV_MAP.put("Idaho", "ID");
        STATE_ABRV_MAP.put("Illinois", "IL");
        STATE_ABRV_MAP.put("Indiana", "IN");
        STATE_ABRV_MAP.put("Iowa", "IA");
        STATE_ABRV_MAP.put("Kansas", "KS");
        STATE_ABRV_MAP.put("Kentucky", "KY");
        STATE_ABRV_MAP.put("Louisiana", "LA");
        STATE_ABRV_MAP.put("Maine", "ME");
        STATE_ABRV_MAP.put("Maryland", "MD");
        STATE_ABRV_MAP.put("Massachusetts", "MA");
        STATE_ABRV_MAP.put("Michigan", "MI");
        STATE_ABRV_MAP.put("Minnesota", "MN");
        STATE_ABRV_MAP.put("Mississippi", "MS");
        STATE_ABRV_MAP.put("Missouri", "MO");
        STATE_ABRV_MAP.put("Montana", "MT");
        STATE_ABRV_MAP.put("Nebraska", "NE");
        STATE_ABRV_MAP.put("Nevada", "NV");
        STATE_ABRV_MAP.put("New Hampshire", "NH");
        STATE_ABRV_MAP.put("New Jersey", "NJ");
        STATE_ABRV_MAP.put("New Mexico", "NM");
        STATE_ABRV_MAP.put("New York", "NY");
        STATE_ABRV_MAP.put("North Carolina", "NC");
        STATE_ABRV_MAP.put("North Dakota", "ND");
        STATE_ABRV_MAP.put("Ohio", "OH");
        STATE_ABRV_MAP.put("Oklahoma", "OK");
        STATE_ABRV_MAP.put("Oregon", "OR");
        STATE_ABRV_MAP.put("Pennsylvania", "PA");
        STATE_ABRV_MAP.put("Rhode Island", "RI");
        STATE_ABRV_MAP.put("South Carolina", "SC");
        STATE_ABRV_MAP.put("South Dakota", "SD");
        STATE_ABRV_MAP.put("Tennessee", "TN");
        STATE_ABRV_MAP.put("Texas", "TX");
        STATE_ABRV_MAP.put("Utah", "UT");
        STATE_ABRV_MAP.put("Vermont", "VT");
        STATE_ABRV_MAP.put("Virginia", "VA");
        STATE_ABRV_MAP.put("Washington", "WA");
        STATE_ABRV_MAP.put("West Virginia", "WV");
        STATE_ABRV_MAP.put("Wisconsin", "WI");
        STATE_ABRV_MAP.put("Wyoming", "WY");
    }

    /**
     *
     */
    public static GPXParser singleton() {
        if (_singleton == null) { // no sync needed
            _singleton = new GPXParser();
        }

        return _singleton;
    }

    //
    //
    //

    /**
     * Returns the total number of caches.
     *
     */
    int getCacheCount() {
        return this.map.size();
    }

    /**
     *
     * @param state
     */
    static String getStateAbbreviation(String state) {
        return STATE_ABRV_MAP.get(state);
    }

    /**
     *
     */
    boolean serializeCacheData() {
        File file = new File("data");
        if (!file.exists() && !file.mkdir()) {
            return false;
        }
        ObjectOutputStream oos = null;
        try {
            OutputStream out = new FileOutputStream("data/cache.zip");
            ZipOutputStream zos = new ZipOutputStream(out);
            zos.putNextEntry(new ZipEntry("cache.ser"));
            oos = new ObjectOutputStream(new BufferedOutputStream(zos));
            oos.writeObject(this.map);
        } catch (IOException e) {
            return false;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }

        return true;
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    Map<String, CacheBean> deserializeCacheData() {
        File file = new File("data/cache.zip");
        if (!file.exists()) {
            return this.map;
        }
        ObjectInputStream dec = null;
        try {
            InputStream in = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(in);
            zis.getNextEntry();
            dec = new ObjectInputStream(new BufferedInputStream(zis));
            this.map = (Map<String, CacheBean>) dec.readObject();
        } catch (ClassNotFoundException e) {
            // shouldn't happen
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dec != null) {
                try {
                    dec.close();
                } catch (IOException e) {
                }
            }
        }

        //InputStream in = null;
        //java.beans.XMLDecoder dec = null;
        //try {
        //    in = new FileInputStream(file);
        //    ZipInputStream zis = new ZipInputStream(in);
        //    zis.getNextEntry();
        //    dec = new java.beans.XMLDecoder(new BufferedInputStream(zis));
        //    this.map = (Map<String, CacheBean>) dec.readObject();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //} finally {
        //    if (dec != null) {
        //        dec.close();
        //    }
        //    if (in != null) {
        //        try {
        //            in.close();
        //        } catch (IOException e) {
        //        }
        //    }
        //}

        return this.map;
    }

    /**
     *
     */
    Map<String, CacheBean> getCacheBeanMap() {
        return this.map;
    }

    /**
     *
     * @param waypoint
     */
    CacheBean getBean(String waypoint) {
        return this.map.get(waypoint);
    }

    /**
     *
     * @param bean
     */
    void updateBean(CacheBean bean) {
        this.map.put(bean.getWaypoint(), bean);
    }

    /**
     *
     * @param bean
     */
    void removeBean(CacheBean bean) {
        this.map.remove(bean.getWaypoint());
    }

    /**
     *
     * @param waypoint
     */
    void removeByWaypoint(String waypoint) {
        this.map.remove(waypoint);
    }

    /**
     *
     * @param waypoint
     */
    CacheBean getCacheBean(String waypoint) {
        return this.map.get(waypoint);
    }

    /**
     *
     */
    String getCreationTime() {
        return this.time;
    }

    /**
     *
     * @param file
     */
    Map<String, CacheBean> parse(InputStream in) throws IOException,
                                                        XMLStreamException {
        XMLInputFactory factory = null;
        XMLEventReader reader = null;
        Map<String, CacheBean> tmpMap = new HashMap<String, CacheBean>();
        try {
            factory = XMLInputFactory.newInstance();
            reader = factory.createXMLEventReader(in);
            reader = factory.createFilteredReader (reader, new GPXEventFilter());

            while (reader.hasNext()) {
                XMLEvent evt = reader.nextEvent();
                StartElement el = evt.asStartElement();
                if (el.getName().equals(TIME_QN)) {
                    String time = reader.getElementText().replace("T", " ");
                    int index = time.indexOf("Z");
                    if (index != -1) {
                        time = time.substring(0, index);
                    }
                    this.time = time;
                    break;
                }
            }

            long userId = Pref.getUserId();
            while (reader.hasNext()) {
                XMLEvent evt = reader.nextEvent();
                StartElement el = evt.asStartElement();
                if (el.getName().equals(WPT_QN)) { // <wpt>
                    String tmp = el.getAttributeByName(LAT_QN).getValue();
                    double lat = Double.parseDouble(tmp);
                    tmp = el.getAttributeByName(LON_QN).getValue();
                    double lon = Double.parseDouble(tmp);

                    reader.nextEvent();  // <time>
                    String placedOn = reader.getElementText().replace("T", " ");
                    int index = placedOn.indexOf("Z");
                    if (index != -1) {
                        placedOn = placedOn.substring(0, index);
                    }

                    reader.nextEvent(); // <name>
                    String waypoint = reader.getElementText();
                    if (!waypoint.startsWith("GC")) {
                        continue;
                    }

                    CacheBean bean = this.map.get(waypoint);
                    boolean newCache = false;
                    if (bean == null) {
                        newCache = true;
                        bean = new CacheBean();
                        bean.setWaypoint(waypoint);
                    }
                    bean.setLatitude(lat);
                    bean.setPlacedOn(Timestamp.valueOf(placedOn).getTime());
                    bean.setLongitude(lon);

                    reader.nextEvent(); // <desc>
                    reader.nextEvent(); // <url>
                    reader.nextEvent(); // <urlname>

                    reader.nextEvent(); // <sym>
                    if (newCache) {
                        setSym(bean, reader.getElementText());
                    }

                    reader.nextEvent(); // <type>
                    String type = reader.getElementText();
                    setType(bean, type);

                    evt = reader.nextEvent(); // <groundspeak:cache>
                    el = evt.asStartElement();
                    String id = el.getAttributeByName(ID_QN).getValue();
                    bean.setId(Long.parseLong(id));
                    String available = el.getAttributeByName(AVAILABLE_QN).getValue();
                    if (available.equals("True")) {
                        bean.setAvailable(true);
                    } else {
                        bean.setAvailable(false);
                    }
                    String archived = el.getAttributeByName(ARCHIVED_QN).getValue();
                    if (archived.equals("True")) {
                        bean.setArchived(true);
                    } else {
                        bean.setArchived(false);
                    }
                    reader.nextEvent(); // <groundspeak:name>
                    bean.setName(reader.getElementText());
                    reader.nextEvent(); // <groundspeak:placed_by>
                    bean.setPlacedBy(reader.getElementText());
                    reader.nextEvent(); // <groundspeak:owner>
                    bean.setOwner(reader.getElementText());
                    reader.nextEvent(); // <groundspeak:type>
                    reader.nextEvent(); // <groundspeak:container>
                    setContainer(bean, reader.getElementText());
                    reader.nextEvent(); // <groundspeak:difficulty>
                    bean.setDifficulty(reader.getElementText());
                    reader.nextEvent(); // <groundspeak:terrain>
                    bean.setTerrain(reader.getElementText());
                    reader.nextEvent(); // <groundspeak:country>
                    bean.setCountry(reader.getElementText());
                    reader.nextEvent(); // <groundspeak:state>
                    bean.setState(reader.getElementText());
                    evt = reader.nextEvent(); // <groundspeak:short_description>
                    el = evt.asStartElement();
                    tmp = el.getAttributeByName(HTML_QN).getValue();
                    String desc = reader.getElementText().trim();
                    //if (desc.length() != 0 && new Boolean(tmp).booleanValue()) {
                    if (desc.length() != 0 && Boolean.valueOf(tmp)) {
                        desc = "$html" + desc;
                    }
                    bean.setShortDescription(desc);
                    evt = reader.nextEvent(); // <groundspeak:long_description>
                    el = evt.asStartElement();
                    tmp = el.getAttributeByName(HTML_QN).getValue();
                    desc = reader.getElementText().trim();
                    //if (desc.length() != 0 && new Boolean(tmp).booleanValue()) {
                    if (desc.length() != 0 && Boolean.valueOf(tmp)) {
                        desc = "$html" + desc;
                    }
                    bean.setLongDescription(desc);
                    reader.nextEvent(); // <groundspeak:encoded_hints>
                    bean.setHint(reader.getElementText());

                    evt = reader.nextEvent(); // <groundspeak:logs>
                    boolean valid = false;
                    while (reader.hasNext()) {
                        evt = reader.nextEvent(); // <groundspeak:log>
                        el = evt.asStartElement();
                        if (!el.getName().getLocalPart().equals("log")) {
                            // encountered <groundspeak:travelbugs>
                            break;
                        }
                        long logId = Long.parseLong(el.getAttributeByName(ID_QN).getValue());
                        reader.nextEvent(); // <groundspeak:date>
                        String foundOn = reader.getElementText().replace("T", " "); // TODO: revamp me
                        foundOn = foundOn.substring(0, foundOn.length() - 1); // remove UTC time zone designater
                        if (newCache) {
                            try {
                                bean.setFoundOn(Timestamp.valueOf(foundOn).getTime());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        reader.nextEvent(); // <groundspeak:type>
                        String logType = reader.getElementText();
                        if (!logType.equals("Found it") &&
                            !logType.equals("Webcam Photo Taken") &&
                            !logType.equals("Attended")) {
                            reader.nextEvent(); // <groundspeak:finder>
                            reader.nextEvent(); // <groundspeak:text>
                            continue;
                        }
                        evt = reader.nextEvent(); // <groundspeak:finder>
                        if (userId == -1) {
                            el = evt.asStartElement();
                            userId = Long.parseLong(el.getAttributeByName(ID_QN).getValue());
                        }
                        String finder = reader.getElementText();
                        reader.nextEvent(); // <groundspeak:text>
                        if (finder.equals(Pref.getUserName())) {
                            Pref.setUserId(userId);
                            Pref.store();
                            bean.setLogId(logId);
                            bean.setLog(reader.getElementText());
                            valid = true;
                            break;
                        }
                    }

                    // travel bug info follows, ignore

                    if (valid) {
                        tmpMap.put(waypoint, bean);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                }
            }
        }

        this.map.putAll(tmpMap);

        return tmpMap;
    }

    //
    //
    //

    /**
     *
     * @param bean
     * @param container
     */
    private void setContainer(CacheBean bean, String container) {
        if (container.equals("Regular")) {
            bean.setContainer(CacheBean.CONTAINER_REGULAR);
        } else if (container.equals("Small")) {
            bean.setContainer(CacheBean.CONTAINER_SMALL);
        } else if (container.equals("Micro")) {
            bean.setContainer(CacheBean.CONTAINER_MICRO);
        } else if (container.equals("Not chosen")) {
            bean.setContainer(CacheBean.CONTAINER_NOT_CHOSEN);
        } else if (container.equals("Other")) {
            bean.setContainer(CacheBean.CONTAINER_OTHER);
        } else if (container.equals("Large")) {
            bean.setContainer(CacheBean.CONTAINER_LARGE);
        } else if (container.equals("Virtual")) {
            bean.setContainer(CacheBean.CONTAINER_VIRTUAL);
        } else {
            assert false : "Unknown type";
        }
    }

    /**
     *
     * @param bean
     * @param type
     */
    private void setType(CacheBean bean, String type) {
        if (type.equals("Geocache|Traditional Cache")) {
            bean.setType(CacheBean.TYPE_TRADITIONAL);
        } else if (type.equals("Geocache|Multi-cache")) {
            bean.setType(CacheBean.TYPE_MULTI);
        } else if (type.equals("Geocache|Unknown Cache")) {
            bean.setType(CacheBean.TYPE_UNKNOWN);
        } else if (type.equals("Geocache|Virtual Cache")) {
            bean.setType(CacheBean.TYPE_VIRTUAL);
        } else if (type.equals("Geocache|Earthcache")) {
            bean.setType(CacheBean.TYPE_EARTH);
        } else if (type.equals("Geocache|Webcam Cache")) {
            bean.setType(CacheBean.TYPE_WEBCAM);
        } else if (type.equals("Geocache|Letterbox Hybrid")) {
            bean.setType(CacheBean.TYPE_LETTERBOX_HYBRID);
        } else if (type.equals("Geocache|Event Cache")) {
            bean.setType(CacheBean.TYPE_EVENT);
        } else if (type.equals("Geocache|Mega-Event Cache")) {
            bean.setType(CacheBean.TYPE_MEGA);
        } else if (type.equals("Geocache|Project APE Cache")) {
            bean.setType(CacheBean.TYPE_PROJECT_APE);
        } else if (type.equals("Geocache|Cache In Trash Out Event")) {
            bean.setType(CacheBean.TYPE_CITO);
        } else if (type.equals("Geocache|Locationless (Reverse) Cache")) {
            bean.setType(CacheBean.TYPE_LOCATIONLESS);
        } else if (type.equals("Waypoint|Parking Area")) {
            bean.setType(CacheBean.TYPE_PARKING);
        } else if (type.equals("Waypoint|Reference Point")) {
            bean.setType(CacheBean.TYPE_REF_POINT);
        } else {
            assert false : "Unknown type";
        }
    }

    /**
     *
     * @param bean
     * @param sym
     */
    private void setSym(CacheBean bean, String sym) {
        if (sym.equals("Geocache Found")) {
            bean.setSym(CacheBean.SYM_FOUND);
        } else {
            bean.setSym(CacheBean.SYM_OTHER);
        }
    }

    /**
     *
     */
    private static class GPXEventFilter implements EventFilter {
        /** */
        @Override
        public boolean accept(XMLEvent evt) {
            return evt.isStartElement();
        }
    }
}
