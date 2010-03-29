package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 *
 *
 */
class CacheInfoPanel extends TabPanel {
    private final CacheTable TABLE = new CacheTable();
    private final CacheInfoPanel2 INFO_PANEL = new CacheInfoPanel2();
    private final JSplitPane SPLIT_PANE = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    private String waypoint = null;

    /**
     *
     *
     */
    CacheInfoPanel() {
        setLayout(new BorderLayout());

        SPLIT_PANE.setTopComponent(createTablePanel());
        SPLIT_PANE.setBottomComponent(INFO_PANEL);
        SPLIT_PANE.setOneTouchExpandable(true);
        SPLIT_PANE.setDividerLocation(300);

        TABLE.addPropertyChangeListener(new PropertyChangeListener() {
                /** */
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("rowCount")) {
                        int rowCount = (Integer) evt.getNewValue();
                        INFO_PANEL.setEnabled(rowCount != 0);
                    }
                }
            });

        add(SPLIT_PANE);
    }

    /** */
    @Override
    public void update(String waypoint) {
        if (waypoint == null) {
            this.waypoint = null;
            return;
        }
        if (waypoint.equals(this.waypoint)) {
            return;
        }
        this.waypoint = waypoint;
        INFO_PANEL.setWaypoint(this.waypoint);
    }

    /** */
    @Override
    public String getTitle() {
        return I18N.get("CacheInfoPanel.tab.title.0");
    }

    /** */
    @Override
    public ImageIcon getIcon() {
        return null;
    }

    //
    //
    //

    /**
     *
     */
    void setAutoResizeColumns(boolean b) {
        TABLE.setAutoResizeColumns(b);
    }

    /**
     *
     */
    String[] getFilteredWaypoints() {
        int rowCount = TABLE.getRowCount();
        int col = TABLE.convertColumnIndexToView(1);
        String[] waypoints = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            waypoints[i] = (String) TABLE.getValueAt(i, col);
        }

        return waypoints;
    }

    /**
     *
     * @param file
     */
    void addCaches(File file) {
        TABLE.addGPXFiles(new File[]{file});
    }

    /**
     *
     */
    void requestTableFocus() {
        TABLE.requestFocusInWindow();
    }

    //
    //
    //

    /**
     *
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(TABLE);
        sp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                /** */
                @Override
                public void adjustmentValueChanged(AdjustmentEvent evt) {
                    if (!evt.getValueIsAdjusting()) {
                        TABLE.packColumns();
                    }
                }
            });
        panel.add(sp);
        panel.add(TABLE.getFilterBar(), BorderLayout.SOUTH);
        
        return panel;
    }
}
