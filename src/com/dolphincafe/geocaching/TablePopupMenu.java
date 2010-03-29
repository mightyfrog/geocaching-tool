package com.dolphincafe.geocaching;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 */
class TablePopupMenu extends JPopupMenu implements ActionListener,
                                                   PopupMenuListener {
    //
    private static final String LOG_URL =
        "http://www.geocaching.com/seek/log.aspx?wp=";

    //
    private static final String FOUND_AC = "found";
    private static final String FTF_AC = "ftf";
    private static final String MILESTONE_AC = "milestone";
    private static final String FAVORITE_AC = "favorite";
    private static final String OTHER_AC = "other";
    private static final String SMILEY_AC = "smiley";
    private static final String DELETE_AC = "delete";

    //
    private JMenuItem logMI = null;

    //
    private JMenu labelMenu = null;
    private JMenuItem foundMI = null;
    private JMenuItem ftfMI = null;
    private JMenuItem milestoneMI = null;
    private JMenuItem favoriteMI = null;
    private JMenuItem otherMI = null;

    //
    private JMenuItem smileyMI = null;
    private JMenuItem findFTFMI = null;
    private JMenuItem deleteMI = null;

    /**
     *
     */
    public TablePopupMenu() {
        this.labelMenu = new JMenu(I18N.get("TablePopupMenu.menu.0"));
        add(this.labelMenu);

        this.foundMI = new JMenuItem(I18N.get("TablePopupMenu.menu.1"));
        //this.ftfMI = new JMenuItem("<html><body style=\"color: blue\">" +
        //                           I18N.get("TablePopupMenu.menu.2") +
        //                           "</body></html>");
        //this.milestoneMI = new JMenuItem("<html><body style=\"color: FF00FF\">" + // magenta
        //                                 I18N.get("TablePopupMenu.menu.4") +
        //                                 "</body></html>");
        //this.favoriteMI = new JMenuItem("<html><body style=\"color: FFA500\">" + // orange
        //                                I18N.get("TablePopupMenu.menu.5") +
        //                                 "</body></html>");
        //this.otherMI = new JMenuItem("<html><body style=\"color: teal\">" +
        //                             I18N.get("TablePopupMenu.menu.6") +
        //                             "</body></html>");

        this.ftfMI = new JMenuItem(I18N.get("TablePopupMenu.menu.2"),
                                   Icons.BLUE_SQUARE);
        this.milestoneMI = new JMenuItem(I18N.get("TablePopupMenu.menu.4"),
                                         Icons.MAGENTA_SQUARE);
        this.favoriteMI = new JMenuItem(I18N.get("TablePopupMenu.menu.5"),
                                        Icons.ORANGE_SQUARE);
        this.otherMI = new JMenuItem(I18N.get("TablePopupMenu.menu.6"),
                                     Icons.TEAL_SQUARE);

        this.labelMenu.add(this.foundMI);
        this.labelMenu.addSeparator();
        this.labelMenu.add(this.ftfMI);
        this.labelMenu.add(this.milestoneMI);
        this.labelMenu.add(this.favoriteMI);
        this.labelMenu.add(this.otherMI);

        this.smileyMI = new JMenuItem(I18N.get("TablePopupMenu.menu.7"));
        add(this.smileyMI);

        this.findFTFMI = new JMenuItem(I18N.get("TablePopupMenu.menu.9")) {
                {
                    addActionListener(new ActionListener() {
                            /** */
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                Map<String, CacheBean> map =
                                    GPXParser.singleton().getCacheBeanMap();
                                CacheTable t = (CacheTable) getInvoker();
                                t.filter(); // reset filter reulst
                                int col = t.getColumnCount() - 2;
                                int count = 0;
                                for (Map.Entry entry : map.entrySet()) {
                                    CacheBean bean = (CacheBean) entry.getValue();
                                    if (bean.getSym() == CacheBean.SYM_FTF) {
                                        count++;
                                    } else if (bean.getLog() != null &&
                                               bean.getLog().indexOf("FTF") != -1) {
                                        count++;
                                        for (int i = 0; i < t.getRowCount(); i++) {
                                            if (entry.getKey().equals(t.getValueAt(i, 1))) {
                                                t.setValueAt(CacheBean.SYM_FTF, i, col);
                                                break;
                                            }
                                        }
                                    }
                                }
                                String message = I18N.get("TablePopupMenu.dialog.0", count);
                                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                                              message);
                            }
                        });
                }
            };
        add(this.findFTFMI);

        addSeparator();
        this.logMI = new JMenuItem(I18N.get("TablePopupMenu.menu.10"));
        this.logMI.addActionListener(new ActionListener() {
                /** */
                public void actionPerformed(ActionEvent evt) {
                    CacheTable table = (CacheTable) getInvoker();
                    try {
                        java.net.URL url = new java.net.URL(LOG_URL + table.getWaypoint());
                        Desktop.getDesktop().browse(url.toURI());
                    } catch (java.net.MalformedURLException e) {
                    } catch (java.net.URISyntaxException e) {
                    } catch (java.io.IOException e) {
                    }
                }
            });
        add(this.logMI);

        addSeparator();
        this.deleteMI = new JMenuItem(I18N.get("TablePopupMenu.menu.8"));
        add(this.deleteMI);

        this.foundMI.setActionCommand(FOUND_AC);
        this.ftfMI.setActionCommand(FTF_AC);
        this.milestoneMI.setActionCommand(MILESTONE_AC);
        this.favoriteMI.setActionCommand(FAVORITE_AC);
        this.otherMI.setActionCommand(OTHER_AC);
        this.smileyMI.setActionCommand(SMILEY_AC);
        this.deleteMI.setActionCommand(DELETE_AC);

        this.foundMI.addActionListener(this);
        this.ftfMI.addActionListener(this);
        this.milestoneMI.addActionListener(this);
        this.favoriteMI.addActionListener(this);
        this.otherMI.addActionListener(this);
        this.smileyMI.addActionListener(this);
        this.deleteMI.addActionListener(this);

        addPopupMenuListener(this);
    }

    /** */
    @Override
    public void setEnabled(boolean enabled) {
        this.labelMenu.setEnabled(enabled);
        this.deleteMI.setEnabled(enabled);
        this.smileyMI.setEnabled(enabled);
    }

    /** */
    @Override
    public void actionPerformed(ActionEvent evt) {
        String ac = evt.getActionCommand();
        CacheTable t = (CacheTable) getInvoker();
        int row = t.getSelectedRow();
        int col = t.getColumnCount() - 2;
        if (ac.equals(FOUND_AC)) {
            t.setValueAt(CacheBean.SYM_FOUND, row, col);
        } else if (ac.equals(MILESTONE_AC)) {
            t.setValueAt(CacheBean.SYM_MILESTONE, row, col);
        } else if (ac.equals(FAVORITE_AC)) {
            t.setValueAt(CacheBean.SYM_FAVORITE, row, col);
        } else if (ac.equals(OTHER_AC)) {
            t.setValueAt(CacheBean.SYM_OTHER, row, col);
        } else if (ac.equals(FTF_AC)) {
            t.setValueAt(CacheBean.SYM_FTF, row, col);
        } else if (ac.equals(DELETE_AC)) {
            t.delete(row);
        } else if (ac.equals(SMILEY_AC)) {
            t.showSmileyChooserDialog();
        } else {
            assert false : "Unknown action: " + ac;
        }
    }

    /** */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getInvoker().repaint(); // TODO: fix fix fix
    }

    /** */
    @Override
    public void popupMenuCanceled(PopupMenuEvent evt) {
    }

    /** */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
        CacheTable table = (CacheTable) getInvoker();
        String waypoint = table.getWaypoint();
        CacheBean bean = GPXParser.singleton().getBean(waypoint);
        this.labelMenu.setEnabled(bean.getSym() != CacheBean.SYM_DNF);
    }

    /** */
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
    }
}
