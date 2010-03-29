
package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 *
 *
 * @author Shigehiro Soejima
 */
public class GCOrganizer extends JFrame {
    //
    private final MenuBar MENU_BAR;
    private final TabbedPane TABBED_PANE;
    private final StatusBar STATUS_BAR;

    //
    private CacheBean bean = null;

    /**
     * Creates a GCOrganizer.
     *
     */
    public GCOrganizer() {
        JOptionPane.setRootFrame(this);

        MENU_BAR = new MenuBar();
        TABBED_PANE = new TabbedPane();
        STATUS_BAR = new StatusBar();

        setTitle("\"mightyfrog\" Geocaching Tool");
        setJMenuBar(MENU_BAR);
        add(TABBED_PANE);
        add(STATUS_BAR, BorderLayout.PAGE_END);

        setIconImage(Icons.FRAME_LOGO.getImage());
        int width = 1024;
        int height = 768;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if (width > dim.width || height > dim.height) {
            setExtendedState(MAXIMIZED_BOTH);
        } else {
            setSize(width, height);
        }
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);

        // auto-save on exit, option me
        addWindowListener(new WindowAdapter() {
                /** */
                @Override
                public void windowClosing(WindowEvent evt) {
                    exit();
                }
            });

        final Component glassPane = getGlassPane();
        glassPane.setVisible(true);
        glassPane.addMouseListener(new MouseAdapter() {
                /** */
                @Override
                public void mousePressed(MouseEvent evt) {
                    evt.consume();
                    if (!isUserNameSet()) {
                        promptForUserName();
                    }
                }
            });
        
        if (!isUserNameSet()) {
            promptForUserName();
        } else {
            firePropertyChange("userName", false, true);
        }
    }

    /**
     *
     */
    public static void main(String[] args) {
        //JFrame.setDefaultLookAndFeelDecorated(true); // JDK Bug 5050922

        try {
            makeBackup();
            Pref.load();
            Locale locale = Pref.getLocale();
            if (locale == Locale.ROOT) {
                JComponent.setDefaultLocale(Locale.ENGLISH);
            } else {
                JComponent.setDefaultLocale(locale);
            }

            // JDK bug workaround
            // w/o calling this method, setLookAndFeel fails w/ GTK
            String lafClassName = Pref.getLookAndFeelClassName();
            if (lafClassName.equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
                UIManager.getInstalledLookAndFeels();
            }

            UIManager.setLookAndFeel(lafClassName);
            initFont(Pref.getFont());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        EventQueue.invokeLater(new Runnable() {
                /** */
                @Override
                public void run() {
                    new GCOrganizer();
                }
            });
    }

    //
    //
    //

    /**
     *
     */
    void fireCacheDataUpdate() {
        firePropertyChange("cacheDataUpdate", null, this.bean);
    }

    /**
     *
     * @param row
     */
    void fireRowSelectionVisible() {
        firePropertyChange("rowSelection", false, true);
    }

    /**
     *
     * @param file cache data file
     */
    void addCaches(File file) {
        TABBED_PANE.addCaches(file);
    }

    /**
     *
     * @param waypoint
     */
    void update(String waypoint) {
        TABBED_PANE.update(waypoint);
        this.bean = GPXParser.singleton().getCacheBean(waypoint);
        setTitle("(" + waypoint + ") " + this.bean.getName() + " by " +
                 this.bean.getPlacedBy());
        if (this.bean.getArchived()) {
            setStatus(I18N.get("GCOrganizer.StatusBar.message.2",
                               Pref.getGPXCreationTime()), 1);
        } else if (!this.bean.getAvailable()) {
            setStatus(I18N.get("GCOrganizer.StatusBar.message.3",
                               Pref.getGPXCreationTime()), 1);
        } else {
            setStatus(null, 1);
        }
    }

    /**
     *
     * @param viewSelected the selected row
     * @param viewTotal the total number of rows (could be filtered)
     */
    void updateCacheCount(int viewSelected, int viewTotal) {
        int total = GPXParser.singleton().getCacheCount();
        if (viewSelected == -1) {
            setStatus("--/" + viewTotal + "/" + total, 0);
        } else {
            setStatus("" + viewSelected + "/" +
                      viewTotal + "/" + total, 0);
        }

        updateCacheTypeCount();
    }

    /**
     *
     */
    void updateCacheTypeCount() {
        // TODO: rewrite me
        Map<String, CacheBean> map = GPXParser.singleton().getCacheBeanMap();
        if (map.size() == 0) {
            STATUS_BAR.updateCenterPanel(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            return;
        }
        CacheBean bean = null;
        int[] c = new int[12];
        for (int i = 0; i < c.length; i++) {
            Iterator itr = map.values().iterator();
            while (itr.hasNext()) {
                bean = (CacheBean) itr.next();
                if (bean.getType() == i) {
                    c[i]++;
                }
            }
        }
        STATUS_BAR.updateCenterPanel(c[0], c[1], c[2], c[3], c[4], c[5],
                                     c[6], c[7], c[8], c[9], c[10], c[11]);
    }

    /**
     *
     * @param text
     */
    void setStatus(String text, int position) { // TODO: define positions
        switch (position) {
        case 0:
            STATUS_BAR.setLeftText(text);
            break;
        case 1:
            STATUS_BAR.setCenterText(text);
            break;
        }
        // right is exclusively for mem info
    }

    /**
     *
     */
    CacheBean getCacheBean() {
        return this.bean;
    }

    /**
     *
     */
    String[] getFilteredWaypoints() {
        return TABBED_PANE.getFilteredWaypoints();
    }

    /**
     *
     * @param waiting
     */
    void toggleCursor(boolean waiting) {
        if (!isUserNameSet()) {
            return;
        }
        Component gp = getGlassPane();
        if (waiting) {
            gp.addMouseListener(new MouseAdapter() {
                    /** */
                    @Override
                    public void mousePressed(MouseEvent evt) {
                        evt.consume();
                    }
                });
            gp.setVisible(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            MouseListener[] ml = gp.getMouseListeners();
            for (MouseListener l : ml) {
                gp.removeMouseListener(l);
            }
            gp.setVisible(false);
            setCursor(null);
        }
    }

    /**
     *
     */
    void exit() {
        if (bean != null) {
            String message = I18N.get("GCOrganizer.message.0");
            String title = UIManager.getString("OptionPane.titleText",
                                               Pref.getLocale());
            int option = // title is there because setLocale(...) is not honored
                JOptionPane.showConfirmDialog(GCOrganizer.this, message, title,
                                              JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                toggleCursor(true);
                if (GPXParser.singleton().serializeCacheData()) {
                    String gpxCreationTime =
                        GPXParser.singleton().getCreationTime();
                    if (gpxCreationTime != null) {
                        Pref.setGPXCreationTime(gpxCreationTime);
                    }
                    Pref.store();
                    System.exit(0);
                } else {
                    // TODO: show error dialog here
                }
            } else if (option == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    /**
     *
     * @param font
     */
    static void initFont(Font font) {
        FontUIResource fontUIResource = new FontUIResource(font);
        UIDefaults uid = UIManager.getLookAndFeelDefaults();
        Set<Object> keySet = new HashSet<Object>(uid.keySet());
        for (Object obj : keySet) {
            if (uid.getFont(obj) != null) {
                UIManager.put(obj, fontUIResource);
            }
        }
    }

    /**
     *
     * @param font
     */
    void changeFont(Font font) {
        initFont(font);

        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(FindLoggerFrame.singleton());
        ((JPanel) getContentPane()).revalidate();
    }

    /**
     *
     */
    boolean isUserNameSet() {
        String userName = Pref.getUserName();
        return userName != null && !userName.equals("");
    }

    //
    //
    //

    /**
     *
     */
    private void promptForUserName() {
        String message = I18N.get("GCOrganizer.message.1");
        String name = JOptionPane.showInputDialog(this, message);
        if (name != null && !name.trim().equals("")) {
            Component glassPane = getGlassPane();
            MouseListener[] ml =
                glassPane.getMouseListeners();
            for (MouseListener l : ml) {
                glassPane.removeMouseListener(l);
            }
            glassPane.setVisible(false);
            firePropertyChange("userName", false, true);
            Pref.setUserName(name);
            Pref.store();
        }
    }

    /**
     *
     */
    private static void makeBackup() {
        File file = new File("data/cache.zip");
        File backup = new File("data/backup.zip");
        if (file.exists()) {
            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            int n = 0;
            byte[] b = new byte[4096];
            try {
                in = new BufferedInputStream(new FileInputStream(file));
                out = new BufferedOutputStream(new FileOutputStream(backup));
                while ((n = in.read(b)) != -1) {
                    out.write(b, 0, n);
                }
            } catch (IOException e) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    //
    //
    //

    /**
     *
     */
    private class StatusBar extends JPanel implements ActionListener {
        private final JLabel RIGHT = new JLabel() {
                {
                    setIcon(Icons.MEMORY);
                    ToolTipManager.sharedInstance().registerComponent(this);
                    setToolTipText(I18N.get("GCOrganizer.StatusBar.message.0"));
                    addMouseListener(new MouseAdapter() {
                            /** */
                            @Override
                            public void mousePressed(MouseEvent evt) {
                                showPopup(evt);
                            }

                            /** */
                            @Override
                            public void mouseReleased(MouseEvent evt) {
                                showPopup(evt);
                            }
                        });
                }
            };
        private final JLabel CENTER_LABEL = new JLabel();
        private final JPanel CENTER = new JPanel();
        private final JLabel LEFT = new JLabel();

        /**
         *
         */
        public StatusBar() {
            setLayout(new BorderLayout());
            LEFT.setBorder(BorderFactory.createEtchedBorder()); // cache counts
            CENTER.setBorder(BorderFactory.createEtchedBorder()); // messages
            RIGHT.setBorder(BorderFactory.createEtchedBorder()); // mem info

            LEFT.setText("--/--/--");
            RIGHT.addMouseListener(new MouseAdapter() {
                    /** */
                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        if (evt.getClickCount() == 2) {
                            fireRowSelectionVisible();
                        }
                    }
                });

            CENTER.setPreferredSize(LEFT.getPreferredSize());

            add(LEFT, BorderLayout.WEST);
            add(CENTER);
            add(RIGHT, BorderLayout.EAST);

            updateLeftLabel();

            // updates memory status
            //new Timer(10 * 1000, this).start();
            new Timer(1000, this).start();
        }

        /**
         *
         * @param text
         */
        public void setLeftText(String text) {
            LEFT.setText(text);
        }

        /**
         *
         * @param text
         */
        public void setCenterText(String text) {
            CENTER_LABEL.setText(text);
            CENTER_LABEL.setToolTipText(text);
        }

        /**
         *
         */
        public void updateCenterPanel(int c0, int c1, int c2, int c3, int c4,
                                      int c5, int c6, int c7, int c8, int c9,
                                      int c10, int c11) {
            CENTER.removeAll();
            CENTER.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridwidth = 13;
            gbc.insets = new Insets(0, 3, 0, 0);
            gbc.fill = GridBagConstraints.NONE;            
            gbc.weightx = 0.0;
            JLabel l0 = new JLabel("" + c0, Icons.TRADITIONAL_CACHE, JLabel.LEFT);
            l0.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_TRADITIONAL));
            CENTER.add(l0, gbc);
            JLabel l1 = new JLabel("" + c1, Icons.MULTI_CACHE, JLabel.LEFT);
            l1.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_MULTI));
            CENTER.add(l1, gbc);
            JLabel l2 = new JLabel("" + c2, Icons.UNKNOWN_CACHE, JLabel.LEFT);
            l2.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_UNKNOWN));
            CENTER.add(l2, gbc);
            JLabel l3 = new JLabel("" + c3, Icons.WEBCAM_CACHE, JLabel.LEFT);
            l3.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_WEBCAM));
            CENTER.add(l3, gbc);
            JLabel l4 = new JLabel("" + c4, Icons.VIRTUAL_CACHE, JLabel.LEFT);
            l4.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_VIRTUAL));
            CENTER.add(l4, gbc);
            JLabel l5 = new JLabel("" + c5, Icons.EVENT_CACHE, JLabel.LEFT);
            l5.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_EVENT));
            CENTER.add(l5, gbc);
            JLabel l6 = new JLabel("" + c6, Icons.EARTH_CACHE, JLabel.LEFT);
            l6.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_EARTH));
            CENTER.add(l6, gbc);
            JLabel l7 = new JLabel("" + c7, Icons.LETTERBOX_CACHE, JLabel.LEFT);
            l7.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_LETTERBOX_HYBRID));
            CENTER.add(l7, gbc);
            JLabel l8 = new JLabel("" + c8, Icons.CITO_CACHE, JLabel.LEFT);
            l8.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_CITO));
            CENTER.add(l8, gbc);
            JLabel l9 = new JLabel("" + c9, Icons.MEGA_CACHE, JLabel.LEFT);
            l9.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_MEGA));
            CENTER.add(l9, gbc);
            JLabel l10 = new JLabel("" + c10, Icons.PROJECT_APE_CACHE, JLabel.LEFT);
            l10.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_PROJECT_APE));
            CENTER.add(l10, gbc);
            JLabel l11 = new JLabel("" + c11, Icons.LOCLESS_CACHE, JLabel.LEFT);
            l11.setToolTipText(I18N.getCacheTypeName(CacheBean.TYPE_LOCATIONLESS));
            CENTER.add(l11, gbc);
            gbc.insets = new Insets(0, 6, 0, 0);
            gbc.weightx = 1.0;
            CENTER.add(CENTER_LABEL, gbc);
        }

        /** */
        @Override
        public void actionPerformed(ActionEvent evt) {
            updateLeftLabel();
        }

        //
        //
        //

        /**
         *
         * @param evt
         */
        private void showPopup(MouseEvent evt) {
            if (!evt.isPopupTrigger()) {
                return;
            }
            JPopupMenu menu = new JPopupMenu();
            JMenuItem mi = new JMenuItem(I18N.get("GCOrganizer.StatusBar.menu.0"));
            menu.add(mi);
            mi.addActionListener(new ActionListener() {
                    /** */
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        System.gc();
                        updateLeftLabel();
                    }
                });
            menu.show(RIGHT, evt.getX(), evt.getY());
        }

        /**
         *
         */
        private void updateLeftLabel() {
            long freeMem = Runtime.getRuntime().freeMemory();
            long maxMem = Runtime.getRuntime().maxMemory();
            long totalMem = Runtime.getRuntime().totalMemory();

            // current, committed, total
            String um =
                String.format("%.2f", (totalMem - freeMem) / 1000000.0F) + "MB";
            String mm = String.format("%.2f", maxMem / 1000000.0F) + "MB";
            String tm = String.format("%.2f", totalMem / 1000000.0F) + "MB";
            RIGHT.setText(um + "/" + tm + "/" + mm);
        }
    }
}
