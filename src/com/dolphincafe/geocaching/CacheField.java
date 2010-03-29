package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton ;
import javax.swing.JPanel;

/**
 *
 * @author Shigehiro Soejima
 */
class CacheField extends JPanel {
    private final DataField FIELD = new DataField();
    private final JButton BUTTON = new JButton(Icons.FRAME_LOGO) {
            /** */
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = FIELD.getPreferredSize();
                dim.width = dim.height;

                return dim;
            }
        };

    //
    private String waypoint = null;

    /**
     * Creates a CacheField.
     *
     */
    CacheField() {
        setLayout(new BorderLayout());

        BUTTON.setToolTipText(I18N.get("CacheField.button.text.0"));
        BUTTON.addActionListener(new ActionListener() {
                /** */
                public void actionPerformed(ActionEvent evt) {
                    viewCache();
                }
            });

        add(FIELD);
        add(BUTTON, BorderLayout.EAST);
    }

    //
    //
    //

    /**
     * Sets the cache text.
     *
     * @param cache
     */
    void setCacheName(String cache) {
        FIELD.setText(cache);
        FIELD.setCaretPosition(0);
    }

    /**
     * Returns the cache text.
     *
     */
    String getCacheName() {
        return FIELD.getText();
    }

    /**
     *
     * @param waypoint
     */
    void setWaypoint(String waypoint) {
        this.waypoint = waypoint;
    }

    /**
     *
     */
    String getWaypoint() {
        return this.waypoint;
    }

    /**
     * Decides whether this field is editable or not.
     *
     * @param editable
     */
    void setEditable(boolean editable) {
        FIELD.setEditable(editable);
    }

    /**
     * Tests whether this field is editable or not.
     *
     */
    boolean isEditable() {
        return FIELD.isEditable();
    }

    //
    //
    //

    /**
     *
     */
    private void viewCache() {
        String wpt = getWaypoint();
        if (wpt == null) {
            return;
        }
        //http://www.geocaching.com/seek/cache_details.aspx?wp=GC112BT&log=y&decrypt=
        String url = "http://www.geocaching.com/seek/cache_details.aspx?wp=" + wpt;
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
