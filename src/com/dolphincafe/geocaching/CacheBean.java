package com.dolphincafe.geocaching;

import java.io.Serializable;

/**
 *
 *
 * @author Shigehiro Soejima
 */
public final class CacheBean implements Serializable {
    // serial version
    public static final long serialVersionUID = 1L;

    // container
    public static final int CONTAINER_LARGE = 0;
    public static final int CONTAINER_REGULAR = 1;
    public static final int CONTAINER_SMALL = 2;
    public static final int CONTAINER_MICRO = 3;
    public static final int CONTAINER_NOT_CHOSEN = 4;
    public static final int CONTAINER_OTHER = 5;
    public static final int CONTAINER_VIRTUAL = 6;

    // type
    public static final int TYPE_TRADITIONAL = 0;
    public static final int TYPE_MULTI = 1;
    public static final int TYPE_UNKNOWN = 2;
    public static final int TYPE_WEBCAM = 3;
    public static final int TYPE_VIRTUAL = 4;
    public static final int TYPE_EVENT = 5;
    public static final int TYPE_EARTH = 6;
    public static final int TYPE_LETTERBOX_HYBRID = 7;
    public static final int TYPE_CITO = 8;
    public static final int TYPE_MEGA = 9;
    public static final int TYPE_PROJECT_APE = 10;
    public static final int TYPE_LOCATIONLESS = 11;
    public static final int TYPE_PARKING = 12;
    public static final int TYPE_REF_POINT = 13;
    public static final int TYPE_LOST_AND_FOUND = 14;
    public static final int TYPE_WHERIGO = 15;
    public static final int TYPE_UNSUPPORTED = 99;

    // status
    public static final int SYM_FOUND = 0;
    public static final int SYM_FTF = 1;
    public static final int SYM_MILESTONE = 2;
    public static final int SYM_FAVORITE = 3;
    public static final int SYM_OTHER = 4;
    public static final int SYM_DNF = 8;

    // smiley
    public static final int SMILEY_NONE = -1;
    public static final int SMILEY_SMILE = 0;
    public static final int SMILEY_SMILE_BIG = 1;
    public static final int SMILEY_SMILE_COOL = 2;
    public static final int SMILEY_SMILE_BLUSH = 3;
    public static final int SMILEY_SMILE_TONGUE = 4;
    public static final int SMILEY_SMILE_EVIL = 5;
    public static final int SMILEY_SMILE_WINK = 6;
    public static final int SMILEY_SMILE_CLOWN = 7;
    public static final int SMILEY_SMILE_BLACK_EYE = 8;
    public static final int SMILEY_SMILE_EIGHT_BALL = 9;
    public static final int SMILEY_SMILE_FROWN = 10;
    public static final int SMILEY_SMILE_SHY = 11;
    public static final int SMILEY_SMILE_SHOCKED = 12;
    public static final int SMILEY_SMILE_ANGRY = 13;
    public static final int SMILEY_SMILE_DEAD = 14;
    public static final int SMILEY_SMILE_SLEEPY = 15;
    public static final int SMILEY_SMILE_KISSES = 16;
    public static final int SMILEY_SMILE_APPROVE = 17;
    public static final int SMILEY_SMILE_DISAPPROVE = 18;
    public static final int SMILEY_SMILE_QUESTION = 19;

    //
    protected String country = null;
    protected String difficulty = null;
    protected String hint = null;
    protected String log = null;
    protected String longDescription = null;
    protected String name = null;
    protected String owner = null;
    protected String placedBy = null;
    protected String shortDescription = null;
    protected String state = null;
    protected String terrain = null;
    protected String waypoint = null;
    protected boolean archived = false;
    protected boolean available = false;
    protected int container = -1;
    protected int smiley = -1;
    protected int sym = -1;
    protected int type = -1;
    protected long id = -1;
    protected long logId = 0L;

    protected double latitude = 0.0;
    protected double longitude = 0.0;

    protected long foundOn = 0l;
    protected long placedOn = 0l;

    /**
     * Creates a CacheBean.
     *
     */
    public CacheBean() {
        // no-op
    }

    /**
     *
     * @param archived
     */
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    /**
     *
     * @param available
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     *
     * @param container
     */
    public void setContainer(int container) {
        this.container = container;
    }

    /**
     *
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @param difficulty
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     *
     * @param foundOn
     */
    public void setFoundOn(long foundOn) {
        this.foundOn = foundOn;
    }

    /**
     *
     * @param hint
     */
    public void setHint(String hint) {
        this.hint = hint;
    }

    /**
     *
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @param log
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     *
     * @param logId
     */
    public void setLogId(long logId) {
        this.logId = logId;
    }

    /**
     *
     * @param longDescription
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     *
     * @param placedBy
     */
    public void setPlacedBy(String placedBy) {
        this.placedBy = placedBy;
    }

    /**
     *
     * @param shortDescription
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     *
     * @param smiley
     */
    public void setSmiley(int smiley) {
        this.smiley = smiley;
    }

    /**
     *
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @param sym
     */
    public void setSym(int sym) {
        this.sym = sym;
    }

    /**
     *
     * @param terrain
     */
    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    /**
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     *
     * @param waypoint
     */
    public void setWaypoint(String waypoint) {
        this.waypoint = waypoint;
    }

    /**
     *
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @param placedOn
     */
    public void setPlacedOn(long placedOn) {
        this.placedOn = placedOn;
    }

    //
    //
    //

    /**
     *
     */
    public boolean getArchived() {
        return this.archived;
    }

    /**
     *
     */
    public boolean getAvailable() {
        return this.available;
    }

    /**
     *
     */
    public int getContainer() {
        return this.container;
    }

    /**
     *
     */
    public String getCountry() {
        return this.country;
    }

    /**
     *
     */
    public String getDifficulty() {
        return this.difficulty;
    }

    /**
     *
     */
    public long getFoundOn() {
        return this.foundOn;
    }

    /**
     *
     */
    public String getHint() {
        return this.hint;
    }

    /**
     *
     */
    public long getId() {
        return this.id;
    }

    /**
     *
     */
    public String getLog() {
        return this.log;
    }

    /**
     *
     * @param logId
     */
    public long getLogId() {
        return this.logId;
    }

    /**
     *
     */
    public String getLongDescription() {
        return this.longDescription;
    }

    /**
     *
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     *
     */
    public String getPlacedBy() {
        return this.placedBy;
    }

    /**
     *
     */
    public String getShortDescription() {
        return this.shortDescription;
    }

    /**
     *
     */
    public int getSmiley() {
        return this.smiley;
    }

    /**
     *
     */
    public String getState() {
        return this.state;
    }

    /**
     *
     */
    public int getSym() {
        return this.sym;
    }

    /**
     *
     */
    public String getTerrain() {
        return this.terrain;
    }

    /**
     *
     */
    public int getType() {
        return this.type;
    }

    /**
     *
     */
    public String getWaypoint() {
        return this.waypoint;
    }

    /**
     *
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     *
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     *
     */
    public long getPlacedOn() {
        return this.placedOn;
    }

    /**
     * Tests whether this object and the specified object are equal. Returns true only
     * if both have the same waypoint.
     *
     * @param obj
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != CacheBean.class) {
            return false;
        }

        return ((CacheBean) obj).getWaypoint().equals(getWaypoint());
    }

    /** */
    @Override
    public int hashCode() {
        return getWaypoint().hashCode(); // waypoint guarantees no dupelicate
    }

    /** */
    @Override
    public String toString() {
        String str = super.toString() + " [waypoint=" + this.waypoint +
            ", type=" + this.type + ", latitude=" + this.latitude +
            ", longitude=" + this.longitude + ", sym=" + this.sym +
            ", foundOn=" + this.foundOn + ", placedOn=" + this.placedOn +
            ", difficulty=" + this.difficulty + ", terrain=" + this.terrain +
            ", container=" + this.container + ", country=" + this.country +
            ", state=" + this.state + ", owner=" + this.owner +
            ", placedBy=" + this.placedBy + ", smiley=" + this.smiley +
            ", name=" + this.name + ", hint=" + this.hint +
            ", shortDescription=" + this.shortDescription +
            ", longDescription=" + this.longDescription;

        return str;
    }
}
