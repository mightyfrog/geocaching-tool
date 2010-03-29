package com.dolphincafe.geocaching;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 *
 * @author Shigehiro Soejima
 */
class PreferencePanel extends JPanel {
    //private DataField userNameField = null;
    private UserField userNameField = null;
    private CoordinateEditor coordEditor = null;
    private JComboBox distanceComboBox = null;
    private JComboBox coordComboBox = null;
    private LanguageChooser langChooser = null;
    private MapURLComboBox mapURLComboBox = null;
    private JCheckBox useOtherMapCheckBox = null;
    private DataField otherMapField = null;

    /**
     * Creates a PreferencePanel.
     *
     */
    public PreferencePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        this.userNameField = new UserField();
        this.userNameField.setEditable(true);
        this.coordEditor = new CoordinateEditor();
        this.langChooser = new LanguageChooser();
        this.distanceComboBox =
            new JComboBox(new String[]{I18N.get("PreferencePanel.checkbox.text.0"),
                                       I18N.get("PreferencePanel.checkbox.text.1")});
        this.coordComboBox =
            new JComboBox(new String[]{I18N.get("PreferencePanel.checkbox.text.2"),
                                       I18N.get("PreferencePanel.checkbox.text.3"),
                                       I18N.get("PreferencePanel.checkbox.text.4")});
        this.mapURLComboBox = new MapURLComboBox();
        this.useOtherMapCheckBox =
            new JCheckBox(I18N.get("PreferencePanel.checkbox.text.5"));
        this.useOtherMapCheckBox.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JCheckBox cb = (JCheckBox) evt.getSource();
                    PreferencePanel.this.mapURLComboBox.setEnabled(!cb.isSelected());
                    PreferencePanel.this.otherMapField.setEditable(cb.isSelected());
                    PreferencePanel.this.otherMapField.requestFocusInWindow();
                }
            });
        this.otherMapField = new DataField() {
                /** */
                @Override
                public Dimension getPreferredSize() {
                    Dimension dim = super.getPreferredSize();
                    dim.width = 300;
                    return dim;
                }
            };

        gbc.weightx = 0.0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        add(new JLabel(I18N.get("PreferencePanel.label.4")), gbc);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(this.userNameField, gbc);
        gbc.weightx = 0.0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        add(new JLabel(I18N.get("PreferencePanel.label.2")), gbc);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(this.langChooser, gbc);
        add(this.coordEditor, gbc);
        add(createFormatPanel(), gbc);
        add(createMapPanel(), gbc);
        add(new JLabel(I18N.get("PreferencePanel.label.5")), gbc);

        setAll();
    }

    //
    //
    //

    /**
     *
     */
    void store() {
        Pref.setUserName(this.userNameField.getUser());
        Pref.setLatitude(this.coordEditor.getLatitude());
        Pref.setLongitude(this.coordEditor.getLongitude());
        Pref.setDistanceMode(this.distanceComboBox.getSelectedIndex());
        Pref.setCoordinatesMode(this.coordComboBox.getSelectedIndex());
        Pref.setLanguage(this.langChooser.getSelectedItem());
        if (this.useOtherMapCheckBox.isSelected()) {
            Pref.setMapURL(this.otherMapField.getText());
        } else {
            Pref.setMapURL(this.mapURLComboBox.getMapURL());
        }
        Pref.store();
    }

    //
    //
    //

    /**
     *
     */
    private void setAll() {
        this.userNameField.setUser(Pref.getUserName());

        double lat = Pref.getLatitude();
        double lon = Pref.getLongitude();
        this.coordEditor.setCoordinates(lat, lon);

        int index = Pref.getCoordinatesMode();
        this.coordComboBox.setSelectedIndex(index);

        index = Pref.getDistanceMode();
        this.distanceComboBox.setSelectedIndex(index);

        this.langChooser.setSelectedItem(Pref.getLanguage());

        String mapUrl = Pref.getMapURL();
        boolean useOtherMap = true;
        for (int i = 0; i < this.mapURLComboBox.getItemCount(); i++) {
            if (mapUrl.equals(this.mapURLComboBox.getMapURLAt(i))) {
                this.mapURLComboBox.setSelectedIndex(i);
                useOtherMap = false;
                break;
            }
        }
        if (useOtherMap) {
            this.mapURLComboBox.setEnabled(false);
            this.otherMapField.setText(mapUrl);
            this.otherMapField.setEditable(true);
            this.useOtherMapCheckBox.setSelected(true);
        }
    }

    /**
     *
     */
    private JPanel createFormatPanel() {
        JPanel panel = new JPanel();
        String title = I18N.get("PreferencePanel.border.title.0");
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.weightx = 0.0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        panel.add(new JLabel(I18N.get("PreferencePanel.label.0")), gbc);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(this.coordComboBox, gbc);

        gbc.weightx = 0.0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        panel.add(new JLabel(I18N.get("PreferencePanel.label.1")), gbc);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(this.distanceComboBox, gbc);

        return panel;
    }

    /**
     *
     */
    private JPanel createMapPanel() {
        JPanel panel = new JPanel();
        String title = I18N.get("PreferencePanel.border.title.1");
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.weightx = 0.0;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        panel.add(new JLabel(I18N.get("PreferencePanel.label.3")), gbc);
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(this.mapURLComboBox, gbc);
        panel.add(this.useOtherMapCheckBox, gbc);
        panel.add(this.otherMapField, gbc);

        return panel;
    }
}
