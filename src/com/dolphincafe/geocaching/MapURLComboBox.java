package com.dolphincafe.geocaching;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

/**
 *
 */
class MapURLComboBox extends JComboBox {
    public static final String MAP_TYPE_GEOCACHING = "Geocaching.com Google Map";
    public static final String MAP_TYPE_GOOGLE = "Google Maps";
    public static final String MAP_TYPE_YAHOO = "Yahoo! Maps";

    public static final String MAP_URL_GOOGLE =
        "http://maps.google.com/maps?q=$lat+$lon";
    public static final String MAP_URL_YAHOO =
        "http://maps.yahoo.com/index.php#q1=$lat%2C$lon";
    public static final String  MAP_URL_GEOCACHING =
        "http://www.geocaching.com/seek/gmnearest.aspx?lat=$lat&lon=$lon";
        
    private final Map<String, String> URL_MAP;
    private final Map<String, ImageIcon> ICON_MAP;

    /**
     *
     */
    MapURLComboBox() {
        URL_MAP = new HashMap<String, String>();
        URL_MAP.put(MAP_TYPE_GOOGLE, MAP_URL_GOOGLE);
        URL_MAP.put(MAP_TYPE_YAHOO, MAP_URL_YAHOO);
        URL_MAP.put(MAP_TYPE_GEOCACHING, MAP_URL_GEOCACHING);

        ICON_MAP = new HashMap<String, ImageIcon>();
        ICON_MAP.put(MAP_TYPE_GOOGLE, Icons.GOOGLE_MAP);
        ICON_MAP.put(MAP_TYPE_YAHOO, Icons.YAHOO_MAP);
        ICON_MAP.put(MAP_TYPE_GEOCACHING, Icons.GEOCACHING_MAP);

        addItem(MAP_TYPE_GEOCACHING);
        addItem(MAP_TYPE_GOOGLE);
        addItem(MAP_TYPE_YAHOO);

        final ListCellRenderer renderer = getRenderer();
        setRenderer(new ListCellRenderer() {
                /** */
                @Override
                public Component getListCellRendererComponent(JList list,
                                                              Object value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus) {
                    JLabel label =
                        (JLabel) renderer.getListCellRendererComponent(list,
                                                                       value,
                                                                       index,
                                                                       isSelected,
                                                                       cellHasFocus);
                    ImageIcon icon = getImageIcon(value);
                    if (MapURLComboBox.this.isEnabled()) {
                        label.setIcon(icon);
                    } else {
                        label.setIcon(Icons.getDisabledIcon(icon));
                    }

                    return label;
                }
            });
    }

    /** */
    @Override
    public void setSelectedIndex(int index) {
        super.setSelectedIndex(index);

        updateEditorIcon();
    }

    /** */
    @Override
    public void addNotify() {
        super.addNotify();

        updateEditorIcon();
    }

    //
    //
    //

    /**
     *
     * @param index
     */
    String getMapURLAt(int index) {
        return URL_MAP.get((String) getItemAt(index));
    }

    /**
     *
     */
    String getMapURL() {
        return URL_MAP.get((String) getSelectedItem());
    }

    //
    //
    //

    /**
     *
     * @param value
     */
    private ImageIcon getImageIcon(Object value) {
        return ICON_MAP.get((String) value);
    }

    /**
     *
     */
    private void updateEditorIcon() { // tmp hack
        ImageIcon icon = ICON_MAP.get(getSelectedItem());
        int width = icon.getIconWidth();
        Border b0 = BorderFactory.createEmptyBorder(2, 0, 2, 2);
        Border b1 = BorderFactory.createMatteBorder(0, width, 0, 0, icon);
        Border b2 = BorderFactory.createCompoundBorder(b0, b1);
        Border b3 = BorderFactory.createEmptyBorder(0, 0, 0, 2);
        JTextField c = (JTextField) getEditor().getEditorComponent();
        c.setBorder(BorderFactory.createCompoundBorder(b2, b3));
    }
}
