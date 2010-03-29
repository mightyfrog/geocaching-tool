package com.dolphincafe.geocaching;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 *
 */
class DistanceBearingField extends JPanel implements ActionListener {
    static final String[] DISTANCE_MODE =
        new String[]{I18N.get("DistanceBearingField.distance.mode.0"),
                     I18N.get("DistanceBearingField.distance.mode.1")};

    private double distance = 0;
    private double bearing = 0;

    private final DataField DISTANCE_FIELD = new DataField();
    private final DataField BEARING_FIELD = new DataField();

    private final JComboBox DIST_COMBO_BOX = new JComboBox(DISTANCE_MODE);

    /**
     *
     */
    public DistanceBearingField() {
        DISTANCE_FIELD.setHorizontalAlignment(JLabel.RIGHT);
        BEARING_FIELD.setHorizontalAlignment(JLabel.RIGHT);
        DIST_COMBO_BOX.addActionListener(this);
        DIST_COMBO_BOX.setSelectedIndex(Pref.getDistanceMode());

        String title = I18N.get("DistanceBearingField.border.title.0");
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 3, 0, 3);
        gbc.weightx = 0.0;
        add(new JLabel(I18N.get("DistanceBearingField.label.0")), gbc);
        gbc.weightx = 1.0;
        add(DISTANCE_FIELD, gbc);
        gbc.weightx = 0.0;
        add(DIST_COMBO_BOX, gbc);
        gbc.weightx = 0.0;
        add(new JLabel(I18N.get("DistanceBearingField.label.1")), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        add(BEARING_FIELD, gbc);
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

    /** */
    @Override
    public void actionPerformed(ActionEvent evt) {
        update();
    }

    //
    //
    //

    /**
     *
     * @param latitude
     * @param longitude
     */
    void setCoordinates(double latitude, double longitude) {
        double homeLat = Pref.getLatitude();
        double homeLon = Pref.getLongitude();
        double distance = GeoCoordConverter.calculateDistance(latitude,
                                                              longitude,
                                                              homeLat,
                                                              homeLon);
        double bearing =
            GeoCoordConverter.calculateBearingInCompassDegree(latitude,
                                                              longitude,
                                                              homeLat,
                                                              homeLon);

        setDistance(distance);
        setBearing(bearing);
        update();
    }

    //
    //
    //

    /**
     *
     * @param distance
     */
    private void setDistance(double distance) {
        this.distance = distance;

        update();
    }

    /**
     *
     * @param bearing
     */
    private void setBearing(double bearing) {
        this.bearing = bearing;

        update();
    }

    /**
     *
     */
    private void update() { // separate me
        BEARING_FIELD.setText(String.format("%.2f", this.bearing));
        
        String str = null;
        if (DIST_COMBO_BOX.getSelectedIndex() == 0) {
            str = String.format("%.3f", 1.6 * this.distance);
        } else {
            str = String.format("%.3f", this.distance);
        }
        DISTANCE_FIELD.setText(str);
    }
}
