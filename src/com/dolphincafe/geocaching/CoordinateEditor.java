package com.dolphincafe.geocaching;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner ;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 *
 * @author Shigehiro Soejima
 */
class CoordinateEditor extends JPanel {
    private JSpinner nsSpinner = null;
    private JSpinner ewSpinner = null;

    private JSpinner latDegSpinner = null;
    private JSpinner latMinSpinner = null;
    private JSpinner latDecSpinner = null;

    private JSpinner lonDegSpinner = null;
    private JSpinner lonMinSpinner = null;
    private JSpinner lonDecSpinner = null;

    /**
     * Creates a CoordinateEditor.
     *
     */
    public CoordinateEditor() {
        this.nsSpinner = new JSpinner() { // REWRITE ME
                {
                    setModel(new SpinnerListModel(new String[]{"S", "N"}));
                }

                /** */
                @Override
                public Object getNextValue() {
                    if ("N".equals(getValue())) {
                        return "S";
                    } else {
                        return "N";
                    }
                }

                /** */
                @Override
                public Object getPreviousValue() {
                    if ("N".equals(getValue())) {
                        return "S";
                    } else {
                        return "N";
                    }
                }
            };

        this.ewSpinner = new JSpinner() { // REWRITE ME
                {
                    setModel(new SpinnerListModel(new String[]{"E", "W"}));
                }

                /** */
                @Override
                public Object getNextValue() {
                    if ("E".equals(getValue())) {
                        return "W";
                    } else {
                        return "E";
                    }
                }

                /** */
                @Override
                public Object getPreviousValue() {
                    if ("E".equals(getValue())) {
                        return "W";
                    } else {
                        return "E";
                    }
                }
            };

        this.latDegSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 90, 1));
        this.latMinSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 60, 1));
        this.latDecSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));

        this.lonDegSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 180, 1));
        this.lonMinSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 60, 1));
        this.lonDecSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));

        this.nsSpinner.setValue("N");
        this.ewSpinner.setValue("W");

        String title = I18N.get("CoordinateEditor.border.title.0");
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST ;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(3, 3, 3, 3);

        add(new JLabel(I18N.get("CoordinateEditor.label.0")), gbc);
        add(this.nsSpinner, gbc);
        add(this.latDegSpinner, gbc);
        gbc.insets = new Insets(2, 0, 3, 0);
        add(new JLabel("\u00b0"), gbc);
        gbc.insets = new Insets(3, 3, 3, 3);
        add(this.latMinSpinner, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(3, 0, 0, 0);
        add(new JLabel("."), gbc);
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(this.latDecSpinner, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(3, 3, 3, 3);
        add(new JLabel(I18N.get("CoordinateEditor.label.1")), gbc);
        add(this.ewSpinner, gbc);

        add(this.lonDegSpinner, gbc);
        gbc.insets = new Insets(2, 0, 3, 0);
        add(new JLabel("\u00b0"), gbc);
        gbc.insets = new Insets(3, 3, 3, 3);
        add(this.lonMinSpinner, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(3, 0, 0, 0);
        add(new JLabel("."), gbc);
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(this.lonDecSpinner, gbc);
    }

    /**
     *
     * @param latitude
     * @param longitude
     */
    public void setCoordinates(double latitude, double longitude) {
        double[] latArray = GeoCoordConverter.LATITUDE.toDegreeMinute(latitude);
        double[] lonArray = GeoCoordConverter.LONGITUDE.toDegreeMinute(longitude);

        int latSignum = (int) Math.signum(latArray[0]);
        int lonSignum = (int) Math.signum(lonArray[0]);
        latArray[0] = latSignum * latArray[0];
        lonArray[0] = lonSignum * lonArray[0];

        if (latSignum == -1) {
            this.nsSpinner.setValue("S");
        } else {
            this.nsSpinner.setValue("N");
        }

        if (lonSignum == -1) {
            this.ewSpinner.setValue("W");
        } else {
            this.ewSpinner.setValue("E");
        }

        this.latDegSpinner.setValue((int) latArray[0]);
        this.latMinSpinner.setValue((int) latArray[1]);
        this.latDecSpinner.setValue((int) latArray[2]);

        this.lonDegSpinner.setValue((int) lonArray[0]);
        this.lonMinSpinner.setValue((int) lonArray[1]);
        this.lonDecSpinner.setValue((int) lonArray[2]);
    }

    /**
     *
     */
    public double getLatitude() {
        int degree = (Integer) latDegSpinner.getValue();
        if (this.nsSpinner.getValue() == "S") {
            degree *= -1;
        }
        int minute = (Integer) latMinSpinner.getValue();
        int remainder = (Integer) latDecSpinner.getValue();

        int[] d = new int[]{degree, minute, remainder};

        return GeoCoordConverter.LATITUDE.toDecimalDegree(d);
    }

    /**
     *
     */
    public double getLongitude() {
        int degree = (Integer) lonDegSpinner.getValue();
        if (this.ewSpinner.getValue() == "W") {
            degree *= -1;
        }
        int minute = (Integer) lonMinSpinner.getValue();
        int remainder = (Integer) lonDecSpinner.getValue();

        int[] d = new int[]{degree, minute, remainder};

        return GeoCoordConverter.LONGITUDE.toDecimalDegree(d);
    }
}
