package com.dolphincafe.geocaching;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 *
 * @author Shigehiro Soejima
 */
abstract class TabPanel extends JPanel {
    /**
     *
     *
     * @param waypoint
     */
    public abstract void update(String waypoint);

    /**
     *
     */
    public abstract ImageIcon getIcon();

    /**
     *
     */
    public abstract String getTitle();

    /**
     *
     */
    protected GCOrganizer getRootFrame() {
        return (GCOrganizer) getTopLevelAncestor();
    }
}
