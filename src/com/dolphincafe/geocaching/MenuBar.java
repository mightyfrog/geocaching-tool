package com.dolphincafe.geocaching;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

/**
 *
 */
class MenuBar extends JMenuBar {
    private final JMenu FILE_MENU = new JMenu() {
            {
                setText(I18N.get("MenuBar.menu.0"));
                setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.0"));
            }

            /** */
            @Override
            public void setPopupMenuVisible(boolean b) {
                createFileMenu();
                super.setPopupMenuVisible(b);
            }
        };
    private final JMenu TOOLS_MENU = new JMenu() {
            {
                setText(I18N.get("MenuBar.menu.1"));
                setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.1"));
            }

            /** */
            @Override
            public void setPopupMenuVisible(boolean b) {
                createToolsMenu();
                super.setPopupMenuVisible(b);
            }
        };
    private final JMenu HELP_MENU = new JMenu() {
            {
                setText(I18N.get("MenuBar.menu.2"));
                setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.2"));
            }

            /** */
            @Override
            public void setPopupMenuVisible(boolean b) {
                createHelpMenu();
                super.setPopupMenuVisible(b);
            }
        };

    // file menu items
    private JMenuItem importFromMI = null;
    private JMenuItem exitMI = null;

    // toosl menu items
    private JMenuItem findLoggerMI = null;
    private JMenuItem geocoderMI = null;
    private JMenuItem hexEncoderMI = null;
    private JMenuItem hintCoderMI = null;
    private JMenuItem preferencesMI = null;

    // help menu items
    private JMenuItem aboutMI = null;
    private JMenu systemMenu = null;
    private JMenu lafMenu = null;
    private JMenuItem fontMI = null;
    private JMenuItem clearImageCacheMI = null;
    private JMenuItem heapSizeMI = null;
    private JMenuItem infoMI = null;

    //
    private GeocoderPanel geocoderPanel = null;
    private HexCoderPanel entityRefPanel = null;

    //
    private JFileChooser fileChooser = null;
    private FontChooserPanel fontChooserPanel = null;

    /**
     * Creates a MenuBar.
     *
     */
    public MenuBar() {
        add(FILE_MENU);
        add(TOOLS_MENU);
        add(HELP_MENU);
    }

    /**
     *
     */
    public Map<String, CacheBean> getCacheMap() {
        return GPXParser.singleton().getCacheBeanMap();
    }

    //
    //
    //

    /**
     *
     */
    private GCOrganizer getOwner() {
        return (GCOrganizer) getTopLevelAncestor();
    }

    /**
     *
     */
    private void createFileMenu() {
        if (this.importFromMI != null) {
            return;
        }

        this.importFromMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("MenuBar.menu.3"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    JFileChooser fc = getFileChooser();
                    int option = fc.showOpenDialog(getOwner());
                    if (option == JFileChooser.APPROVE_OPTION) {
                        getOwner().addCaches(fc.getSelectedFile());
                    }
                }
            });
        this.exitMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("MenuBar.menu.9"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    getOwner().exit();
                }
            });
        this.exitMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.3"));

        FILE_MENU.add(this.importFromMI);
        FILE_MENU.addSeparator();
        FILE_MENU.add(this.exitMI);
    }

    /**
     *
     */
    private void createToolsMenu() {
        if (this.geocoderMI != null) {
            return;
        }
        this.findLoggerMI = new JMenuItem(I18N.get("MenuBar.menu.16"));
        this.findLoggerMI.addActionListener(new ActionListener() {
                /** */
                public void actionPerformed(ActionEvent evt) {
                    FindLoggerFrame.singleton().setVisible(true);
                }
            });
        this.findLoggerMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.12"));

        this.geocoderMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("MenuBar.menu.5"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (MenuBar.this.geocoderPanel == null) {
                        MenuBar.this.geocoderPanel = new GeocoderPanel();
                    }
                    String title = I18N.get("MenuBar.dialog.title.0");
                    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
                    JOptionPane.showOptionDialog(getOwner(),
                                                 MenuBar.this.geocoderPanel,
                                                 title,
                                                 JOptionPane.DEFAULT_OPTION,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 null, null, null);
                    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
                }
            });
        this.geocoderMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.4"));
        this.hexEncoderMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("MenuBar.menu.6"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (MenuBar.this.entityRefPanel == null) {
                        MenuBar.this.entityRefPanel = new HexCoderPanel();
                    }
                    String title = I18N.get("MenuBar.dialog.title.1");
                    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
                    JOptionPane.showOptionDialog(getOwner(),
                                                 MenuBar.this.entityRefPanel,
                                                 title,
                                                 JOptionPane.DEFAULT_OPTION,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 null, null, null);
                    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
                }
            });
        this.hexEncoderMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.5"));
        this.preferencesMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("MenuBar.menu.7"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    PreferencePanel panel = new PreferencePanel();
                    String title = I18N.get("MenuBar.dialog.title.3",
                                            Pref.getUserName(), Pref.getUserId());
                    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
                    int option =
                        JOptionPane.showOptionDialog(getOwner(), panel,
                                                     title,
                                                     JOptionPane.OK_CANCEL_OPTION,
                                                     JOptionPane.PLAIN_MESSAGE,
                                                     null, null, null);
                    UIManager.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
                    if (option == JOptionPane.OK_OPTION) {
                        panel.store();
                        getOwner().repaint();
                    }
                    panel = null;
                }
            });

        this.hintCoderMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("MenuBar.menu.10"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    HintCoderPanel panel = new HintCoderPanel();
                    String title = I18N.get("MenuBar.dialog.title.4");
                    JOptionPane.showOptionDialog(getOwner(), panel,
                                                 title,
                                                 JOptionPane.DEFAULT_OPTION,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 null, null, null);
                }
            });
        this.hintCoderMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.6"));

        this.preferencesMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.11"));

        TOOLS_MENU.add(this.findLoggerMI);
        TOOLS_MENU.addSeparator();
        TOOLS_MENU.add(this.geocoderMI);
        TOOLS_MENU.add(this.hexEncoderMI);
        TOOLS_MENU.add(this.hintCoderMI);
        TOOLS_MENU.addSeparator();
        TOOLS_MENU.add(this.preferencesMI);
    }

    /**
     *
     */
    private void createHelpMenu() {
        if (this.aboutMI != null) {
            return;
        }

        this.aboutMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("MenuBar.menu.8"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    String title = I18N.get("MenuBar.dialog.title.2");
                    JOptionPane.showOptionDialog(getOwner(),
                                                 getAboutPanel(),
                                                 title,
                                                 JOptionPane.DEFAULT_OPTION,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 Icons.ABOUT, null, null);
                }
            });
        this.aboutMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.7"));
        this.systemMenu = new JMenu(I18N.get("MenuBar.menu.4"));
        this.systemMenu.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.8"));
        final UIManager.LookAndFeelInfo[] info =
            UIManager.getInstalledLookAndFeels();
        ActionListener lafChangeListener = new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    String lafName = evt.getActionCommand();
                    // UIManager.getLookAndFeel().getName() !=
                    // UIManager.LookAndFeelInfo.getName(). must be a JDK bug
                    //if (lafName.equals(UIManager.getLookAndFeel().getName())) {
                    //    return;
                    //}
                    UIManager.LookAndFeelInfo[] info =
                        UIManager.getInstalledLookAndFeels();
                    for (UIManager.LookAndFeelInfo i : info) {
                        if (lafName.equals(i.getName())) {
                            changeLaf(i);
                            break;
                        }
                    }
                }
            };

        this.lafMenu = new JMenu(I18N.get("MenuBar.menu.11"));
        this.lafMenu.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.9"));
        for (UIManager.LookAndFeelInfo i : info) {
            JMenuItem mi = new JMenuItem(i.getName());
            mi.addActionListener(lafChangeListener);
            this.lafMenu.add(mi);
        }

        this.fontMI = new JMenuItem(I18N.get("MenuBar.menu.12"));
        this.fontMI.setMnemonic(I18N.getMnemonic("MenuBar.mnemonic.10"));
        this.fontMI.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    EventQueue.invokeLater(new Runnable() {
                            /** */
                            @Override
                            public void run() {
                                updateFont();
                            }
                        });
                }
            });

        this.clearImageCacheMI = new JMenuItem(I18N.get("MenuBar.menu.13"));
        this.clearImageCacheMI.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    clearImageCache();
                }
            });

        this.heapSizeMI = new JMenuItem(I18N.get("MenuBar.menu.15"));
        this.heapSizeMI.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    String message = I18N.get("MenuBar.dialog.1");
                    int heapSize = Pref.getMaxHeapSize();
                    String str =
                        JOptionPane.showInputDialog(getOwner(), message,
                                                    "" + heapSize);
                    if (str != null) {
                        try {
                            heapSize = Integer.parseInt(str);
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                        if (heapSize < 64) {
                            heapSize = 64;
                        }
                        Pref.setMaxHeapSize(heapSize);
                        Pref.store();
                    }
                }
            });

        this.infoMI = new JMenuItem(I18N.get("MenuBar.menu.14"));
        this.infoMI.addActionListener(new ActionListener() {
                /** */
                public void actionPerformed(ActionEvent evt) {
                    String title = I18N.get("MenuBar.dialog.title.5");
                    JOptionPane.showOptionDialog(getOwner(),
                                                 getSystemInfoPanel(),
                                                 title,
                                                 JOptionPane.DEFAULT_OPTION,
                                                 JOptionPane.PLAIN_MESSAGE,
                                                 null, null, null);
                }
            });

        this.systemMenu.add(this.lafMenu);
        this.systemMenu.add(this.fontMI);
        this.systemMenu.addSeparator();
        this.systemMenu.add(this.clearImageCacheMI);
        this.systemMenu.addSeparator();
        this.systemMenu.add(this.heapSizeMI);
        this.systemMenu.addSeparator();
        this.systemMenu.add(this.infoMI);

        HELP_MENU.add(this.systemMenu);
        HELP_MENU.addSeparator();
        HELP_MENU.add(this.aboutMI);
    }

    /**
     *
     * @param info
     */
    private void changeLaf(UIManager.LookAndFeelInfo info) {
        try {
            UIManager.setLookAndFeel(info.getClassName());
            Pref.setLookAndFeelClassName(info.getClassName());
            Pref.store();
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }
        SwingUtilities.updateComponentTreeUI(getOwner());
        SwingUtilities.updateComponentTreeUI(FindLoggerFrame.singleton());
        JOptionPane.showMessageDialog(getOwner(), I18N.get("MenuBar.dialog.5"));
    }

    /**
     *
     */
    private void updateFont() {
        if (this.fontChooserPanel == null) {
            this.fontChooserPanel = new FontChooserPanel(getOwner());
        }
        int fontCount = this.fontChooserPanel.getFontCount();
        String title = I18N.get("MenuBar.dialog.title.6", fontCount);
        int option = JOptionPane.showOptionDialog(getOwner(),
                                                  this.fontChooserPanel,
                                                  title,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  null, null, null);
        if (option == JOptionPane.OK_OPTION) {
            Font font = this.fontChooserPanel.getSelectedFont();
            getOwner().changeFont(font);
            Pref.setFont(font);
            Pref.store();
        }
    }

    /**
     *
     */
    private void clearImageCache() {
        String message = I18N.get("MenuBar.dialog.0");
        String title = UIManager.getString("OptionPane.titleText",
                                           Pref.getLocale());
        int option = JOptionPane.showConfirmDialog(getOwner(),
                                                   message,
                                                   title,
                                                   JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        File dir = new File("data/imgcache"); // don't delete this directory
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        for (File f : files) {
            if (!f.delete()) {
                f.deleteOnExit();
            }
        }
        File serFile = new File("data/imgcache/imgcache.ser");
        if (!serFile.delete()) {
            serFile.deleteOnExit();
        }
    }

    /**
     * Creates and returns a file chooser.
     *
     */
    private JFileChooser getFileChooser() {
        if (this.fileChooser == null) {
            this.fileChooser = new JFileChooser() {
                    {
                        setFileFilter(new FileFilter() {
                                /** */
                                @Override
                                public boolean accept(File file) {
                                    if (file.isDirectory()) {
                                        return true;
                                    }
                                    if (file.getName().toLowerCase().
                                        endsWith(".gpx")) {
                                        return true;
                                    }

                                    return false;
                                }

                                /** */
                                @Override
                                public String getDescription() {
                                    return "*.gpx";
                                }
                            });
                    }

                    /** */
                    @Override
                    public void addNotify() {
                        super.addNotify();

                        updateUI();
                    }
                };
        }

        return this.fileChooser;
    }

    /**
     *
     */
    private JPanel getSystemInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.BorderLayout());

        TextArea ta = new TextArea();
        ta.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(ta);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        Properties prop = System.getProperties();
        Enumeration<?> enm = prop.propertyNames();
        List<String> list = new ArrayList<String>();
        for (; enm.hasMoreElements(); ) {
            String key = (String) enm.nextElement();
            list.add(key + ": " + prop.getProperty(key) + "\n");
        }
        Collections.sort(list);
        for (String str : list) {
            ta.append(str);
        }
        ta.setCaretPosition(0);

        panel.add(scrollPane);

        return panel;
    }

    //
    private ImageIcon statImageIcon = Icons.SIGN;

    //
    private static final String IMG_URL =
        "http://img.geocaching.com/stats/img.aspx?txt=Let's+go+geocaching&uid=d7d5f7de-b7a0-4fbb-ace0-4181cebcd8e5&bg=1";

    /**
     *
     */
    private JPanel getAboutPanel() {
        getOwner().toggleCursor(true);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(3, 3, 3, 3);

        URL url = null;
        try {
            url = new URL(IMG_URL);
            this.statImageIcon = new ImageIcon(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // won't happen
        }

        JLabel urlLabel = null;
        urlLabel = new JLabel(this.statImageIcon);
        urlLabel.setBorder(null);
        urlLabel.addMouseListener(new MouseAdapter() {
                /** */
                @Override
                public void mousePressed(MouseEvent evt) {
                    try {
                        String url =
                            "http://www.geocaching.com/profile?u=mightyfrog";
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (URISyntaxException e) {
                        // shouldn't happen
                    } catch (IOException e) {
                    } finally {
                        setCursor(null);
                    }
                }

                /** */
                @Override
                public void mouseEntered(MouseEvent evt) {
                    ((JLabel) evt.getSource()).
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                /** */
                @Override
                public void mouseExited(MouseEvent evt) {
                    ((JLabel) evt.getSource()).setCursor(null);
                }
            });

        String labelText1 = I18N.get("MenuBar.dialog.4", "@TIMESTAMP@");
        panel.add(new JLabel(labelText1), gbc);
        String labelText2 = I18N.get("MenuBar.dialog.2", Pref.getGPXCreationTime());
        panel.add(new JLabel(labelText2), gbc);
        panel.add(urlLabel, gbc);
        getOwner().toggleCursor(false);

        return panel;
    }
}
