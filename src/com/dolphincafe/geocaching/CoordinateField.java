package com.dolphincafe.geocaching;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 *
 */
class CoordinateField extends JPanel {
    //
    static final int DEGREE_MINUTE = 0;
    static final int DECIMAL_DEGREE = 1;
    static final int DMS = 2;

    //
    private final String[] COORD_SYSTEM_TYPES =
        new String[]{I18N.get("CoordinateField.coord.system.type.0"),
                     I18N.get("CoordinateField.coord.system.type.1"),
                     I18N.get("CoordinateField.coord.system.type.2")};

    private final DataField LATITUDE_FIELD = new DataField();
    private final DataField LONGITUDE_FIELD = new DataField();
    private final JComboBox COORD_SYSTEM_COMBOBOX =
        new JComboBox(COORD_SYSTEM_TYPES);

    //
    private final Action OPEN_MAP_AC = new AbstractAction() {
            {
                putValue(SMALL_ICON, Icons.BROWSER);
                putValue(SHORT_DESCRIPTION,
                         I18N.get("CoordinateField.button.tooltip.0"));
            }

            /** */
            @Override
            public void actionPerformed(ActionEvent evt) {
                double lat = getLatitude();
                double lon = getLongitude();
                if (lat * lon == 0.0) {
                    return;
                }
                String url = Pref.getMapURL();
                url = url.replace("$lat", "" + lat);
                url = url.replace("$lon", "" + lon);

                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    //
    private final Action COPY_AC = new AbstractAction() {
            {
                putValue(SMALL_ICON, Icons.COPY);
                putValue(SHORT_DESCRIPTION,
                         I18N.get("CoordinateField.button.tooltip.1"));
            }

            /** */
            @Override
            public void actionPerformed(ActionEvent evt) {
                String coords = LATITUDE_FIELD.getText() + " " +
                    LONGITUDE_FIELD.getText();
                StringSelection ss = new StringSelection(coords);
                Toolkit tk = Toolkit.getDefaultToolkit();
                tk.getSystemClipboard().setContents(ss, ss);
            }
        };

    //
    private JButton MAP_BUTTON = new JButton(OPEN_MAP_AC) {
            /** */
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                dim.width = dim.height;
                return dim;
            }

            /** */
            @Override
            public void setPreferredSize(Dimension dim) {
                // no-op
            }
        };
    private JButton COPY_BUTTON = new JButton(COPY_AC) {
            /** */
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                dim.width = dim.height;
                return dim;
            }

            /** */
            @Override
            public void setPreferredSize(Dimension dim) {
                // no-op
            }
        };

    private double latitude = 0.0;
    private double longitude = 0.0;

    /**
     *
     */
    CoordinateField() {
        setBorderTitle(I18N.get("CoordinateField.border.title.0"));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 3, 0, 3);
        gbc.weightx = 0.0;
        add(new JLabel(I18N.get("CoordinateField.label.0")), gbc);
        gbc.weightx = 1.0;
        add(LATITUDE_FIELD, gbc);
        gbc.weightx = 0.0;
        add(new JLabel(I18N.get("CoordinateField.label.1")), gbc);
        gbc.weightx = 1.0;
        add(LONGITUDE_FIELD, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        add(COORD_SYSTEM_COMBOBOX, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 3, 0, 0);
        add(COPY_BUTTON, gbc);
        gbc.insets = new Insets(0, 1, 0, 3);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(MAP_BUTTON, gbc);

        COORD_SYSTEM_COMBOBOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    update();
                }
            });
    }

    /**
     *
     * @param title
     */
    void setBorderTitle(String title) {
        setBorder(BorderFactory.createTitledBorder(title));
    }

    /**
     *
     * @param latitude
     * @param longitude
     */
    void setCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        update();
    }

    /**
     *
     */
    double getLatitude() {
        return this.latitude;
    }

    /**
     *
     */
    double getLongitude() {
        return this.longitude;
    }

    /** */
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        Component[] comps = getComponents();
        for (Component c : comps) {
            dim.width += c.getPreferredSize().width;
        }

        return dim;
    }

    //
    //
    //

    /**
     *
     */
    private void update() {
        int type = COORD_SYSTEM_COMBOBOX.getSelectedIndex();
        switch (type) {
        case 0:
            LATITUDE_FIELD.setText(GeoCoordConverter.LATITUDE.
                                   toDegreeMinuteString(getLatitude()));
            LONGITUDE_FIELD.setText(GeoCoordConverter.LONGITUDE.
                                    toDegreeMinuteString(getLongitude()));
            break;
        case 1:
            LATITUDE_FIELD.setText("" + getLatitude());
            LONGITUDE_FIELD.setText("" + getLongitude());
            break;
        case 2:
            LATITUDE_FIELD.setText(GeoCoordConverter.LATITUDE.
                                   toDegreeMinuteSecondString(getLatitude()));
            LONGITUDE_FIELD.setText(GeoCoordConverter.LONGITUDE.
                                    toDegreeMinuteSecondString(getLongitude()));
            break;
        }
    }
}
