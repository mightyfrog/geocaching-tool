package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * RENAME ME
 *
 * @author Shigehiro Soejima
 */
class CacheInfoPanel2 extends JPanel implements ChangeListener {
    // http://www.geocaching.com/seek/cache_details.aspx?wp=GCKX6N

    //
    private final JTabbedPane TABBED_PANE = new JTabbedPane();

    // 1st tab component
    private final JPanel INFO_PANEL = new JPanel();
    private final CacheField NAME_FIELD = new CacheField();
    private final UserField OWNER_FIELD = new UserField();
    private final DateTimeField FOUND_ON_FIELD = new DateTimeField();
    private final DataField TYPE_FIELD = new DataField();
    private final DataField CONTAINER_FIELD = new DataField();
    private final RateField DIFFICULTY_FIELD = new RateField();
    private final RateField TERRAIN_FIELD = new RateField();
    private final DataField COUNTRY_FIELD = new DataField();
    private final DataField STATE_FIELD = new DataField();
    private final DataField PLACED_ON_FIELD = new DataField();
    private final DataField PLACED_BY_FIELD = new DataField();
    private final CoordinateField COORD_FIELD = new CoordinateField();
    private final DistanceBearingField DIST_BEAR_FIELD =
        new DistanceBearingField();
    private final HintField HINT_FIELD = new HintField();
    private final NotePanel NOTE_PANEL = new NotePanel();
    private final LogPanel LOG_PANEL = new LogPanel();

    // 2nd tab component - description
    private final DescriptionPane DESC_PANE = new DescriptionPane();

    // 3rd tab component - attachments
    private final AttachmentTable ATTACHMENT_TABLE = new AttachmentTable();

    //
    private CacheBean bean = null;

    /**
     * Creates a CacheInfoPanel2. // RENAME ME
     *
     */
    CacheInfoPanel2() {
        setLayout(new BorderLayout());

        INFO_PANEL.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 3, 0, 3);
        gbc.weightx = 1.0;

        INFO_PANEL.add(createTopPanel(), gbc);
        INFO_PANEL.add(createMiddlePanel(), gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        INFO_PANEL.add(createBottomPanel(), gbc);

        TABBED_PANE.addTab(I18N.get("CacheInfoPanel2.tab.title.0"), INFO_PANEL);
        TABBED_PANE.addTab(I18N.get("CacheInfoPanel2.tab.title.1"),
                           new JScrollPane(DESC_PANE));
        TABBED_PANE.addTab(I18N.get("CacheInfoPanel2.tab.title.2"),
                           new JScrollPane(ATTACHMENT_TABLE));

        TABBED_PANE.setMnemonicAt(0, I18N.getMnemonic("CacheInfoPanel2.tab.mnemonic.0"));
        TABBED_PANE.setMnemonicAt(1, I18N.getMnemonic("CacheInfoPanel2.tab.mnemonic.1"));
        TABBED_PANE.setMnemonicAt(2, I18N.getMnemonic("CacheInfoPanel2.tab.mnemonic.2"));

        TABBED_PANE.addChangeListener(this);
        add(TABBED_PANE);

        setTransferHandler(new TransferHandler() {
                // TODO: rewrite w/ AttachmentTable trans handler
                /** */
                @Override
                public boolean canImport(JComponent comp,
                                         DataFlavor[] transferFlavors) {
                    if (CacheInfoPanel2.this.bean == null) {
                        return false;
                    }
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    if (!TABBED_PANE.contains(p)) {
                        SwingUtilities.convertPointFromScreen(p, TABBED_PANE);
                        int index = TABBED_PANE.indexAtLocation(p.x, p.y);
                        if (index != -1) {
                            TABBED_PANE.setSelectedIndex(index);
                        }
                        if (index != 2) {
                            return false;
                        }
                    }
                    for (DataFlavor fl : transferFlavors) {
                        if (fl.isFlavorJavaFileListType()) {
                            return true;
                        }
                    }

                    return false;
                }

                /** */
                @Override
                public boolean importData(JComponent comp, Transferable t) {
                    if (!canImport(comp, t.getTransferDataFlavors())) {
                        return false;
                    }
                    try {
                        List<?> list =
                            (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        ATTACHMENT_TABLE.addFiles(list);
                    } catch (UnsupportedFlavorException e) {
                        return false;
                    } catch (IOException e) {
                        return false;
                    }

                    return true;
                }
            });
    }

    /** */
    @Override
    public void stateChanged(ChangeEvent evt) {
        updateSelectedTabContent();
    }

    /** */
    @Override
    public void setEnabled(boolean enabled) {
        TABBED_PANE.setEnabled(enabled);
        Component[] comps = TABBED_PANE.getComponents();
        for (Component c : comps) {
            setAllEnabled(enabled, c);
        }
    }

    //
    //
    //

    /**
     * Sets the cache name.
     *
     * @param name
     */
    void setCacheName(String name) {
        NAME_FIELD.setCacheName(name);
    }

    /**
     * Sets the cache owner.
     *
     * @param owner
     */
    void setOwner(String owner) {
        OWNER_FIELD.setUser(owner);
    }

    /**
     * Sets the 'found' time.
     *
     * @param time
     */
    void setFoundOn(long time) {
        FOUND_ON_FIELD.setTime(time);
    }

    /**
     * Sets the cache type.
     *
     * @param type
     */
    void setType(int type) {
        TYPE_FIELD.setText(I18N.getCacheTypeName(type));
    }

    /**
     * Sets the cache container type.
     *
     * @param container
     */
    void setContainer(int container) {
        CONTAINER_FIELD.setText(I18N.getContainer(container));
    }

    /**
     * Sets the difficulty.
     * and 5.
     *
     * @param difficulty
     */
    void setDifficulty(String difficulty) {
        DIFFICULTY_FIELD.setText(difficulty);
    }

    /**
     * Sets the terrain level.
     * and 5.
     *
     * @param terrain
     */
    void setTerrain(String terrain) {
        TERRAIN_FIELD.setText(terrain);
    }

    /**
     * Sets the hint string. Not aware of encoding status.
     *
     * @param hint
     */
    void setHint(String hint) {
        HINT_FIELD.setHint(hint);
    }

    /**
     * Sets the country name.
     *
     * @param country
     */
    void setCountry( String country) {
        COUNTRY_FIELD.setText(country);
    }

    /**
     * Sets the state name.
     *
     * @param state
     */
    void setState(String state) {
        STATE_FIELD.setText(state);
    }

    /**
     * Sets the date the cache was placed.
     *
     * @param placedOn
     */
    void setPlacedOn(long placedOn) {
        PLACED_ON_FIELD.setText("" + FormatUtil.formatDate(placedOn));
    }

    /**
     * Sets the cacher name who placed the cache.
     *
     * @param placedBy
     */
    void setPlacedBy(String placedBy) {
        PLACED_BY_FIELD.setText(placedBy);
    }

    /**
     *
     * @param waypoint
     */
    void setWaypoint(String waypoint) {
        GPXParser parser = GPXParser.singleton();
        this.bean = parser.getCacheBean(waypoint);

        updateSelectedTabContent();
    }

    //
    //
    //

    /**
     * Recursively enables/disables <code>comp</code> and its children.
     *
     * @param enabled
     * @param comp
     */
    private void setAllEnabled(boolean enabled, Component comp) {
        if (!(comp instanceof JComponent)) {
            return;
        }
        Component[] comps = ((JComponent) comp).getComponents();
        for (Component c : comps) {
            setAllEnabled(enabled, c);
            c.setEnabled(enabled);
        }
    }

    /**
     *
     */
    private void updateSelectedTabContent() {
        if (this.bean == null) {
            return;
        }

        switch (TABBED_PANE.getSelectedIndex()) {
        case 0:
            updateMainTab();
            break;
        case 1:
            updateDescriptionTab();
            break;
        case 2:
            updateAttachmentTab();
            break;
        }
    }

    /**
     *
     */
    private void updateMainTab() {
        double latitude = this.bean.getLatitude();
        double longitude = this.bean.getLongitude();
        setCoordinates(latitude, longitude);
        NAME_FIELD.setWaypoint(this.bean.getWaypoint());
        setCacheName(this.bean.getName());
        setOwner(this.bean.getOwner());
        setFoundOn(this.bean.getFoundOn());
        setType(this.bean.getType());
        setContainer(this.bean.getContainer());
        setDifficulty(this.bean.getDifficulty());
        setTerrain(this.bean.getTerrain());
        setCountry(this.bean.getCountry());
        setState(this.bean.getState());
        setPlacedOn(this.bean.getPlacedOn());
        setPlacedBy(this.bean.getPlacedBy());
        setHint(this.bean.getHint());
        LOG_PANEL.setLog(this.bean.getLog());
        NOTE_PANEL.reload(this.bean.getWaypoint());
    }

    /**
     *
     */
    private void updateDescriptionTab() {
        String shortDesc = this.bean.getShortDescription();
        if (shortDesc.startsWith("$html")) {
            shortDesc = shortDesc.substring(5, shortDesc.length());
        } else {
            shortDesc = shortDesc.replace("\n", "<br>");
        }

        if (shortDesc.length() != 0) {
            shortDesc += "<br><br>";
        }
        String longDesc = this.bean.getLongDescription();
        if (longDesc.startsWith("$html")) {
            longDesc = longDesc.substring(5, longDesc.length());
        } else {
            longDesc = longDesc.replace("\n", "<br>");
        }

        //DESC_PANE.setText("<div>" + shortDesc + longDesc + "</div>");
        DESC_PANE.setText(shortDesc + longDesc);
    }

    /**
     *
     */
    private void updateAttachmentTab() {
        ATTACHMENT_TABLE.setWaypoint(this.bean.getWaypoint());
    }

    /**
     *
     * @param latitude
     * @param longitude
     */
    private void setCoordinates(double latitude, double longitude) {
        COORD_FIELD.setCoordinates(latitude, longitude);
        DIST_BEAR_FIELD.setCoordinates(latitude, longitude);
    }

    /**
     *
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(6, 3, 3, 3));
        panel.setLayout(new GridLayout(0, 3, 6, 6));
        JLabel[] labels =
            new JLabel[]{new JLabel(I18N.get("CacheInfoPanel2.label.0") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.1") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.2") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.3") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.4") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.5") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.6") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.7") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.8") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.9") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.10") + " "),
                         new JLabel(I18N.get("CacheInfoPanel2.label.11") + " ")};
        JComponent[] comps = new JComponent[] {NAME_FIELD,
                                               FOUND_ON_FIELD,
                                               TYPE_FIELD,
                                               OWNER_FIELD,
                                               PLACED_BY_FIELD,
                                               PLACED_ON_FIELD,
                                               CONTAINER_FIELD,
                                               DIFFICULTY_FIELD,
                                               TERRAIN_FIELD,
                                               COUNTRY_FIELD,
                                               STATE_FIELD,
                                               HINT_FIELD};
        // TODO: recalculate on laf change
        Dimension labelMaxDim = getMaximumSize(labels);
        Dimension fieldMaxDim = getMaximumSize(comps);
        labelMaxDim.height = fieldMaxDim.height;

        final GridBagLayout GBL = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < labels.length; i++) {
            labels[i].setPreferredSize(labelMaxDim);
            labels[i].setMinimumSize(labelMaxDim);

            comps[i].setPreferredSize(fieldMaxDim);
            comps[i].setMinimumSize(fieldMaxDim);

            JComponent c = new JComponent(){{setLayout(GBL);}};
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            gbc.gridwidth = GridBagConstraints.RELATIVE;
            c.add(labels[i], gbc);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            c.add(comps[i], gbc);

            panel.add(c);
        }

        return panel;
    }

    /**
     *
     */
    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.add(COORD_FIELD);
        panel.add(DIST_BEAR_FIELD);

        return panel;
    }

    /**
     *
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.add(LOG_PANEL);
        panel.add(NOTE_PANEL);

        return panel;
    }

    /**
     *
     * @param c
     */
    private Dimension getMaximumSize(JComponent[] c) {
        Dimension dim = null;
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < c.length; i++) {
            c[i].setPreferredSize(null);
            dim = c[i].getPreferredSize();
            maxWidth = (dim.width > maxWidth ? dim.width : maxWidth);
            maxHeight = (dim.height > maxHeight ? dim.height : maxHeight);
        }
        dim.width = maxWidth;
        dim.height = maxHeight;

        return dim;
    }
}
