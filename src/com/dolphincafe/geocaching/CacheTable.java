package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import javax.xml.stream.XMLStreamException;

/**
 *
 *
 */
class CacheTable extends JTable implements MouseListener {
    // column identifiers
    private static final String[] COLUMN_IDENTIFIERS =
        new String[]{I18N.get("CacheTable.col.identifer.0"),
                     I18N.get("CacheTable.col.identifer.1"),
                     I18N.get("CacheTable.col.identifer.2"),
                     I18N.get("CacheTable.col.identifer.3"),
                     I18N.get("CacheTable.col.identifer.4"),
                     I18N.get("CacheTable.col.identifer.5"),
                     I18N.get("CacheTable.col.identifer.6"),
                     I18N.get("CacheTable.col.identifer.7"),
                     I18N.get("CacheTable.col.identifer.8"),
                     I18N.get("CacheTable.col.identifer.9"),
                     I18N.get("CacheTable.col.identifer.10"),
                     I18N.get("CacheTable.col.identifer.11"),
                     I18N.get("CacheTable.col.identifer.12"),
                     I18N.get("CacheTable.col.identifer.13"),
                     I18N.get("CacheTable.col.identifer.14"),
                     I18N.get("CacheTable.col.identifer.15"),
                     "STATUS", "SMILEY"}; // STATUS & SMILEY are hidden columns

    private final CacheTableModel MODEL = new CacheTableModel();
    private final TableRowSorter<CacheTableModel> ROW_SORTER =
        new TableRowSorter<CacheTableModel>() {
            {
                setModel(MODEL);
            }

            /** */
            @Override
            public boolean useToString(int col) { // TODO: remove magic numbers
                return col != 6 && col != 7;
            }

            /** */
            @Override
            public Comparator getComparator(int col) {
                if (col == 6 || col == 7) {
                    return new Comparator<Double>() {
                        /** */
                        @Override
                        public int compare(Double o1, Double o2) {
                            return o1.compareTo(o2);
                        }

                        /** */
                        @Override
                        public boolean equals(Object obj) {
                            return false; // never used
                        }

                        /** */
                        @Override
                        public int hashCode() {
                            return 0; // never used
                        }
                    };
                }

                if (col == 10) {
                    return new Comparator<String>() { // tmp
                        private final List<String> ORDER_LIST =
                        new ArrayList<String>();
                        {
                            ORDER_LIST.add("M"); // micro
                            ORDER_LIST.add("S"); // small
                            ORDER_LIST.add("R"); // regular
                            ORDER_LIST.add("L"); // large
                            ORDER_LIST.add("O"); // other
                            ORDER_LIST.add("N"); // not chosen
                            ORDER_LIST.add("V"); // virtual
                        }

                        /** */
                        @Override
                        public int compare(String o1, String o2) {
                            int index1 = ORDER_LIST.indexOf("" + o1.charAt(0));
                            int index2 = ORDER_LIST.indexOf("" + o2.charAt(0));

                            return index1 - index2;
                        }

                        /** */
                        @Override
                        public boolean equals(Object obj) {
                            return false; // never used
                        }

                        /** */
                        @Override
                        public int hashCode() {
                            return 0; // never used
                        }
                    };
                }

                return super.getComparator(col);
            }
        };
    //
    private final CacheTableCellRenderer RENDERER =
        new CacheTableCellRenderer();

    private JPanel filterPanel = null;

    private final JTextField FILTER_FIELD = new JTextField() {
            //
            private final String GHOST_LABEL =
                I18N.get("CacheTable.combobox.label.1");

            {
                addFocusListener(new FocusListener() {
                        /** */
                        @Override
                        public void focusGained(FocusEvent evt) {
                            repaint();
                        }

                        /** */
                        @Override
                        public void focusLost(FocusEvent evt) {
                            repaint();
                        }
                    });
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!hasFocus() && getText().length() == 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                    g2.setColor(SystemColor.textInactiveText);
                    g2.setFont(UIManager.getFont("TextField.font"));
                    int fh = g2.getFontMetrics().getHeight(); // font height
                    int h = getPreferredSize().height;
                    int baseline =
                        fh + (h - fh) / 2 - g2.getFontMetrics().getDescent();
                    int x = 0;
                    try {
                        g2.drawString(GHOST_LABEL, modelToView(0).x, baseline);
                    } catch (javax.swing.text.BadLocationException e) {
                    }
                }
            }
        };

    private final JCheckBox CASE_CHECK_BOX =
        new JCheckBox(I18N.get("CacheTable.checkbox.text.0"));

    private final IconButton LOG_SEARCH_BUTTON =
        new IconButton(Icons.LOG_SEARCH, I18N.get("CacheTable.button.tooltip.1")) {
            {
                addActionListener(new ActionListener() {
                        /** */
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            searchLog();
                        }
                    });
            }
        };

    private final IconButton CLEAR_BUTTON =
        new IconButton(Icons.CLEAR, I18N.get("CacheTable.button.tooltip.2")) {
            {
                addActionListener(new ActionListener() {
                        /** */
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            filter();
                            FILTER_FIELD.setText(null);
                        }
                    });
            }
        };

    private final IconButton FILTER_CONFIG_BUTTON =
        new IconButton(Icons.FILTER_CONFIG, I18N.get("CacheTable.button.tooltip.0")) {
            {
                addActionListener(new ActionListener() {
                        /** */
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            showFilterConfigDialog();
                        }
                    });
            }
        };

    private final JComboBox TYPE_COMBO_BOX = new JComboBox() {
            //
            private final ImageIcon ICON =
                Icons.getDisabledIcon(Icons.TRADITIONAL_CACHE);

            {
                setToolTipText(I18N.get("CacheTable.combobox.tooltip.2"));
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSelectedIndex() == 0) {
                    int y = (getHeight() - ICON.getIconHeight()) / 2;
                    Border b =
                        ((JTextComponent) getEditor().getEditorComponent()).getBorder();
                    if (b != null) {
                        Insets insets = b.getBorderInsets(this);
                        g.drawImage(ICON.getImage(), insets.left, y, this);
                    } else {
                        g.drawImage(ICON.getImage(), 4, y, this);
                    }
                }
            }
        };

    private final JComboBox SMILEY_COMBO_BOX = new JComboBox() {
            //
            private final ImageIcon ICON =
                Icons.getDisabledIcon(Icons.SMILEY_SMILE);

            {
                setToolTipText(I18N.get("CacheTable.combobox.tooltip.0"));
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSelectedIndex() == 0) {
                    int y = (getHeight() - ICON.getIconHeight()) / 2;
                    Border b =
                        ((JTextComponent) getEditor().getEditorComponent()).getBorder();
                    if (b != null) {
                        Insets insets = b.getBorderInsets(this);
                        g.drawImage(ICON.getImage(), insets.left, y, this);
                    } else {
                        g.drawImage(ICON.getImage(), 4, y, this);
                    }
                }
            }
        };

    private final JComboBox DIFFICULTY_COMBO_BOX = new JComboBox() {
            //
            private final String GHOST_LABEL =
                I18N.get("CacheTable.combobox.label.7");

            {
                setToolTipText(I18N.get("CacheTable.combobox.tooltip.3"));
                addItem(""); // the first index item
                addItem("1");
                addItem("1.5");
                addItem("2");
                addItem("2.5");
                addItem("3");
                addItem("3.5");
                addItem("4");
                addItem("4.5");
                addItem("5");
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSelectedIndex() == 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                    g2.setColor(SystemColor.textInactiveText);
                    int fh = g2.getFontMetrics().getHeight(); // font height
                    int h = getPreferredSize().height;
                    int baseline =
                        fh + (h - fh) / 2 - g2.getFontMetrics().getDescent();
                    Border b =
                        ((JTextComponent) getEditor().getEditorComponent()).getBorder();
                    if (b != null) {
                        Insets insets = b.getBorderInsets(this);
                        g2.drawString(GHOST_LABEL, insets.left, baseline);
                    } else {
                        g2.drawString(GHOST_LABEL, 4, baseline);
                    }
                }
            }
        };

    private final JComboBox TERRAIN_COMBO_BOX = new JComboBox() {
            //
            private final String GHOST_LABEL =
                I18N.get("CacheTable.combobox.label.8");

            {
                setToolTipText(I18N.get("CacheTable.combobox.tooltip.4"));
                addItem(""); // the first index item
                addItem("1");
                addItem("1.5");
                addItem("2");
                addItem("2.5");
                addItem("3");
                addItem("3.5");
                addItem("4");
                addItem("4.5");
                addItem("5");
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSelectedIndex() == 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                    g2.setColor(SystemColor.textInactiveText);
                    int fh = g2.getFontMetrics().getHeight(); // font height
                    int h = getPreferredSize().height;
                    int baseline =
                        fh + (h - fh) / 2 - g2.getFontMetrics().getDescent();
                    Border b =
                        ((JTextComponent) getEditor().getEditorComponent()).getBorder();
                    if (b != null) {
                        Insets insets = b.getBorderInsets(this);
                        g2.drawString(GHOST_LABEL, insets.left, baseline);
                    } else {
                        g2.drawString(GHOST_LABEL, 4, baseline);
                    }
                }
            }
        };

    private final JComboBox CONTAINER_COMBO_BOX = new JComboBox() {
            //
            private final String GHOST_LABEL =
                I18N.get("CacheTable.combobox.label.9");

            {
                setToolTipText(I18N.get("CacheTable.combobox.tooltip.5"));
                addItem(""); // the first index item
                addItem("Micro");
                addItem("Small");
                addItem("Regular");
                addItem("Not chosen"); // "chosen" NOT "Chosen"
                addItem("Other");
                addItem("Virtual");
                addItem("Large");
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSelectedIndex() == 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                    g2.setColor(SystemColor.textInactiveText);
                    int fh = g2.getFontMetrics().getHeight(); // font height
                    int h = getPreferredSize().height;
                    int baseline =
                        fh + (h - fh) / 2 - g2.getFontMetrics().getDescent();
                    Border b =
                        ((JTextComponent) getEditor().getEditorComponent()).getBorder();
                    if (b != null) {
                        Insets insets = b.getBorderInsets(this);
                        g2.drawString(GHOST_LABEL, insets.left, baseline);
                    } else {
                        g2.drawString(GHOST_LABEL, 4, baseline);
                    }
                }
            }
        };

    private final JComboBox STATUS_COMBO_BOX = new JComboBox() {
            //
            private final String GHOST_LABEL =
                I18N.get("CacheTable.combobox.label.0");

            {
                setToolTipText(I18N.get("CacheTable.combobox.tooltip.1"));
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getSelectedIndex() == 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                    g2.setColor(SystemColor.textInactiveText);
                    int fh = g2.getFontMetrics().getHeight(); // font height
                    int h = getPreferredSize().height;
                    int baseline =
                        fh + (h - fh) / 2 - g2.getFontMetrics().getDescent();
                    Border b =
                        ((JTextComponent) getEditor().getEditorComponent()).getBorder();
                    if (b != null) {
                        Insets insets = b.getBorderInsets(this);
                        g2.drawString(GHOST_LABEL, insets.left, baseline);
                    } else {
                        g2.drawString(GHOST_LABEL, 4, baseline);
                    }
                }
            }
        };

    private FilterConfigPanel filterConfigPanel = new FilterConfigPanel();

    private final List<String> LOADED_WPT_LIST = new ArrayList<String>();

    private TablePopupMenu popupMenu = null;

    private boolean autoResizeColumns = true;

    private boolean isDateColumnAscending = false;

    /**
     * Creates a CacheTable.
     *
     */
    CacheTable() {
        setModel(MODEL);
        setEnabled(false);
        setRowSorter(ROW_SORTER);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        //setColumnSelectionAllowed(true);

        ToolTipManager.sharedInstance().registerComponent(this);
        getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < COLUMN_IDENTIFIERS.length; i++) {
            getColumnModel().getColumn(i).setCellRenderer(RENDERER);
        }

        CASE_CHECK_BOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (FILTER_FIELD.getText().length() != 0) {
                        filter();
                    }
                }
            });

        addAncestorListener(new AncestorListener() {
                /** */
                @Override
                public void ancestorAdded(AncestorEvent evt) {
                    fillRows();
                }

                /** */
                @Override
                public void ancestorRemoved(AncestorEvent evt) {
                    // no-op
                }

                /** */
                @Override
                public void ancestorMoved(AncestorEvent evt) {
                    // no-op
                }
            });

        addMouseListener(this);
        addKeyListener(new KeyAdapter() {
                /** */
                @Override
                public void keyPressed(KeyEvent evt) {
                    int row = getSelectedRow();
                    if (row == -1) {
                        return;
                    }
                    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                        delete(row);
                    }
                }
            });

        // hide status & smiley column
        // TODO: rewrite me
        DefaultTableColumnModel colModel =
            (DefaultTableColumnModel) getColumnModel();
        TableColumn col = colModel.getColumn(getStatusColumn()); // STATUS
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col = colModel.getColumn(getSmileyColumn()); // SMILEY
        col.setMinWidth(0);
        col.setMaxWidth(0);

        // tab to move up/down rows
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke("TAB"), im.get(KeyStroke.getKeyStroke("ENTER")));
        im.put(KeyStroke.getKeyStroke("shift TAB"), im.get(KeyStroke.getKeyStroke("shift ENTER")));
        im.put(KeyStroke.getKeyStroke("ctrl pressed A"), "focusTable");
        im.put(KeyStroke.getKeyStroke("ctrl pressed SLASH"), "focusTable");
    }

    /** */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        FILTER_FIELD.setEnabled(enabled);
    }

    /** */
    @Override
    public void addNotify() { // tmp tmp tmp
        super.addNotify();

        GCOrganizer gco = ((GCOrganizer) getTopLevelAncestor());
        gco.addPropertyChangeListener(new PropertyChangeListener() {
                /** */
                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name == "cacheDataUpdate") {
                        int index = getSelectedRow();
                        CacheBean bean = (CacheBean) evt.getNewValue();
                        Object[] rowData = createRowData(bean);
                        for (int i = 0; i < rowData.length; i++) {
                            int col = convertColumnIndexToModel(i);
                            setValueAt(rowData[i], index, col);
                            repaint(getCellRect(index, col, false));
                        }
                        ROW_SORTER.sort();
                        scrollRectToVisible(getCellRect(getSelectedRow(), 0, false));
                    } else if (name == "rowSelection") {
                        scrollRectToVisible(getCellRect(getSelectedRow(), 0, false));
                    } else if (name == "title") { // hack hack hack
                        requestFocusInWindow();
                    } else if (name == "userName") {
                        // do not add transfer handler till user name is propery set
                        setTransferHandler(new CacheTransferHander());
                        setEnabled(true);
                    }

                }
            });

        SMILEY_COMBO_BOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                   if (!SMILEY_COMBO_BOX.hasFocus()) {
                       return;
                   }
                   int index = SMILEY_COMBO_BOX.getSelectedIndex();
                   if (index == 0) {
                       filter();
                   } else {
                       filter("^" + (index - 1) + "$", getSmileyColumn());
                   }
                }
            });

        STATUS_COMBO_BOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                   if (!STATUS_COMBO_BOX.hasFocus()) {
                       return;
                   }
                   int index = STATUS_COMBO_BOX.getSelectedIndex();
                   if (index == 0) {
                       filter();
                   } else if (index == STATUS_COMBO_BOX.getItemCount() - 2) {
                       filterAvailable();
                   } else if (index == STATUS_COMBO_BOX.getItemCount() - 1) {
                       filterArchived();
                   } else {
                       filter("^" + index + "$", getStatusColumn());
                   }
                }
            });

        DIFFICULTY_COMBO_BOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                   if (!DIFFICULTY_COMBO_BOX.hasFocus()) {
                       return;
                   }
                   if (DIFFICULTY_COMBO_BOX.getSelectedIndex() == 0) {
                       filter();
                   } else {
                       String difficulty = (String) DIFFICULTY_COMBO_BOX.getSelectedItem();
                       filter("^" + difficulty + "$", 8); // TODO: remove magic #
                   }
                }
            });

        TERRAIN_COMBO_BOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                   if (!TERRAIN_COMBO_BOX.hasFocus()) {
                       return;
                   }
                   if (TERRAIN_COMBO_BOX.getSelectedIndex() == 0) {
                       filter();
                   } else {
                       String terrain = (String) TERRAIN_COMBO_BOX.getSelectedItem();
                       filter("^" + terrain + "$", 9); // TODO: remove magic #
                   }
                }
            });

        CONTAINER_COMBO_BOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                   if (!CONTAINER_COMBO_BOX.hasFocus()) {
                       return;
                   }
                   String container = (String) CONTAINER_COMBO_BOX.getSelectedItem();
                   if (CONTAINER_COMBO_BOX.getSelectedIndex() == 0) {
                       filter();
                   } else {
                       filter(container, 10); // TODO: remove magic #
                   }
                }
            });

        TYPE_COMBO_BOX.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                   if (!TYPE_COMBO_BOX.hasFocus()) {
                       return;
                   }
                   int index = TYPE_COMBO_BOX.getSelectedIndex();
                   if (index == 0) {
                       filter();
                   } else {
                       // TODO: this can be further optimized using shorter regex
                       filter("^" + I18N.getCacheTypeName(index - 1) + "$", 0);
                   }
                }
            });
    }

    /** */
    @Override
    public String getToolTipText(MouseEvent evt) {
        int row = rowAtPoint(evt.getPoint());
        if (row == -1) {
            return null;
        }
        int col = columnAtPoint(evt.getPoint());

        return String.valueOf(getValueAt(row, col));
    }

    /** */
    @Override
    public void valueChanged(ListSelectionEvent evt) {
        super.valueChanged(evt);

        GCOrganizer gco = (GCOrganizer) getTopLevelAncestor();
        if (getRowCount() != 0 && !evt.getValueIsAdjusting()) {
            String waypoint = getWaypoint();
            if (waypoint == null) {
                return;
            }
            gco.update(waypoint);
        }

        // these lines update cache counts including the selection
        int rowCount = getRowCount();
        if (getSelectedRow() == -1) {
            gco.updateCacheCount(0, rowCount);
        } else {
            gco.updateCacheCount(rowCount - getSelectedRow(), rowCount);
        }
        firePropertyChange("rowCount", -1, rowCount);
    }

    /** */
    @Override
    public void mouseEntered(MouseEvent evt) {
        // no-op
    }

    /** */
    @Override
    public void mousePressed(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            showPopup(evt);
        }
    }

    /** */
    @Override
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2 &&
            evt.getSource() instanceof JLabel) { // TODO: come up w/ better condition
            scrollRectToVisible(getCellRect(getSelectedRow(), 0, false));
        }
    }

    /** */
    @Override
    public void mouseReleased(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            showPopup(evt);
        }
    }

    /** */
    @Override
    public void mouseExited(MouseEvent evt) {
        // no-op
    }

    //
    //
    //

    /**
     *
     */
    void setAutoResizeColumns(boolean autoResizeColumns) {
        this.autoResizeColumns = autoResizeColumns;
    }

    /**
     *
     */
    boolean getAutoResizeColumns() {
        return this.autoResizeColumns;
    }

    /**
     *
     * @param row
     */
    void delete(final int row) {
        String waypoint = (String) getValueAt(row, 1);
        String message =
            I18N.get("CacheTable.message.2", waypoint, getValueAt(row, 2));
        String title = UIManager.getString("OptionPane.titleText",
                                           Pref.getLocale());
        int option =
            JOptionPane.showConfirmDialog(getTopLevelAncestor(), message,
                                          title, JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            GPXParser.singleton().removeByWaypoint(waypoint);
            LOADED_WPT_LIST.remove(waypoint);
            MODEL.removeRow(convertRowIndexToModel(row));
            if (getRowCount() != 0) {
                EventQueue.invokeLater(new Runnable() {
                        /** */
                        @Override
                        public void run() {
                            setRowSelectionInterval(row, row);
                        }
                    });
            }
        }
    }

    /**
     *
     *
     * @param files gpx files
     */
    void addGPXFiles(File[] files) {
        new FillTableTask2(files).execute();
    }

    /**
     *
     *
     * @param bean
     */
    void addCache(CacheBean bean) {
        String waypoint = bean.getWaypoint();
        if (LOADED_WPT_LIST.contains(waypoint)) {
            return;
        }
        addRow(createRowData(bean));
    }

    /**
     *
     * @param waypoint
     */
    void setSelectedWaypoint(String waypoint) {
        for (int i = 0; i < getRowCount(); i++) {
            if (waypoint.equals(getValueAt(i, 1))) {
                setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    /**
     *
     * @param bean
     */
    Object[] createRowData(CacheBean bean) {
        LOADED_WPT_LIST.add(bean.getWaypoint());
        double homeLat = Pref.getLatitude();
        double homeLon = Pref.getLongitude();
        double lat = bean.getLatitude();
        double lon = bean.getLongitude();
        double distance =
            GeoCoordConverter.calculateDistance(lat, lon, homeLat, homeLon);
        double bearing =
            GeoCoordConverter.calculateBearingInCompassDegree(lat, lon, homeLat,
                                                              homeLon);
        String latitude = GeoCoordConverter.LATITUDE.toDegreeMinuteString(lat);
        String longitude = GeoCoordConverter.LONGITUDE.toDegreeMinuteString(lon);
        String state = GPXParser.singleton().getStateAbbreviation(bean.getState());
        int sym = bean.getSym(); // status
        int smiley = bean.getSmiley();
        Object[] rowData = new Object[]{I18N.getCacheTypeName(bean.getType()),
                                        bean.getWaypoint(),
                                        bean.getName(),
                                        FormatUtil.formatDateTime(bean.getFoundOn()),
                                        latitude,
                                        longitude,
                                        FormatUtil.formatDistance(distance),
                                        FormatUtil.formatBearing(bearing),
                                        bean.getDifficulty(),
                                        bean.getTerrain(),
                                        I18N.getContainer(bean.getContainer()),
                                        bean.getOwner(),
                                        bean.getPlacedBy(),
                                        FormatUtil.formatDate(bean.getPlacedOn()),
                                        bean.getCountry(),
                                        state,
                                        sym, // = label = status
                                        smiley};
        return rowData;
    }

    /**
     *
     */
    Vector getSelectedDataVector() {
        return (Vector) MODEL.getDataVector().get(getSelectedRow());
    }

    /**
     * Returns the selected waypoint.
     *
     * @see #getWaypoint(int)
     */
    String getWaypoint() {
        int row = getSelectedRow();
        if (row == -1) {
            return null;
        }
        return getWaypoint(row);
    }

    /**
     * Returns a waypoint for the specified row.
     *
     * @see #getWaypoint()
     */
    String getWaypoint(int row) {
        return (String) getValueAt(row, convertColumnIndexToView(1));
    }

    /**
     * Filters rows.
     *
     */
    void filter() {
        String str = FILTER_FIELD.getText();
        if (!CASE_CHECK_BOX.isSelected()) {
            str = "(?i)" + str;
        }
        filter(str);
    }

    /**
     * Filters out rows that satisfies the specified regular expression.
     *
     * @param regex
     * @param indcies
     */
    void filter(String regex, int... indices) {
        GCOrganizer gco = (GCOrganizer) getTopLevelAncestor();
        String status = null;
        try { // watch for these lines, could contain a bug
            FILTER_FIELD.setForeground(SystemColor.textText);
            if (indices.length == 0) {
                indices = getFilterConfigPanel().getFilterColumns();
            }
            ROW_SORTER.setRowFilter(RowFilter.regexFilter(regex, indices));
        } catch (PatternSyntaxException e) {
            FILTER_FIELD.setForeground(Color.RED);
            status = I18N.get("CacheTable.message.3");
        }
        gco.setStatus(status, 1);

        resetFilterComboBoxes();

        if (getSelectedRow() == -1 && getRowCount() != 0) {
            setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Adds a row.
     *
     * @param rowData
     */
    void addRow(final Object[] rowData) {
        MODEL.addRow(rowData);
    }

    /**
     *
     */
    void packAndSort() {
        packAndSort(false);
    }

    /**
     * Creates and retrusn a filter bar.
     *
     */
    JPanel getFilterBar() {
        this.filterPanel = new JPanel();
        this.filterPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.weightx = 0.0;
        JLabel label = new JLabel(I18N.get("CacheTable.label.0"),
                                  Icons.FIND, JLabel.LEFT);
        label.addMouseListener(this);
        label.setDisplayedMnemonic(I18N.getMnemonic("CacheTable.label.mnemonic.0"));
        label.setLabelFor(FILTER_FIELD);
        this.filterPanel.add(label, gbc);
        gbc.weightx = 1.0;
        this.filterPanel.add(FILTER_FIELD, gbc);
        gbc.weightx = 0.0;
        this.filterPanel.add(TYPE_COMBO_BOX, gbc);
        this.filterPanel.add(SMILEY_COMBO_BOX, gbc);
        this.filterPanel.add(DIFFICULTY_COMBO_BOX, gbc);
        this.filterPanel.add(TERRAIN_COMBO_BOX, gbc);
        this.filterPanel.add(CONTAINER_COMBO_BOX, gbc);
        this.filterPanel.add(STATUS_COMBO_BOX, gbc);
        this.filterPanel.add(CASE_CHECK_BOX, gbc);
        this.filterPanel.add(CLEAR_BUTTON, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        this.filterPanel.add(LOG_SEARCH_BUTTON, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.filterPanel.add(FILTER_CONFIG_BUTTON, gbc);

        ToolTipManager.sharedInstance().registerComponent(TYPE_COMBO_BOX);
        ToolTipManager.sharedInstance().registerComponent(DIFFICULTY_COMBO_BOX);
        ToolTipManager.sharedInstance().registerComponent(TERRAIN_COMBO_BOX);
        ToolTipManager.sharedInstance().registerComponent(CONTAINER_COMBO_BOX);
        ToolTipManager.sharedInstance().registerComponent(SMILEY_COMBO_BOX);
        ToolTipManager.sharedInstance().registerComponent(STATUS_COMBO_BOX);

        TYPE_COMBO_BOX.setMaximumRowCount(10);
        TYPE_COMBO_BOX.addItem(Icons.EMPTY);
        for (int i = 0; i < 12; i++) { // TODO: remove magic #
            TYPE_COMBO_BOX.addItem(Icons.getCacheTypeIcon(i));
        }

        DIFFICULTY_COMBO_BOX.setMaximumRowCount(10);
        TERRAIN_COMBO_BOX.setMaximumRowCount(10);
        CONTAINER_COMBO_BOX.setMaximumRowCount(10);

        SMILEY_COMBO_BOX.setMaximumRowCount(10);
        SMILEY_COMBO_BOX.addItem(Icons.EMPTY);
        for (int i = 0; i < SmileyChooserPanel.SMILEY_COUNT; i++) {
            SMILEY_COMBO_BOX.addItem(Icons.getSmileyIcon(i));
        }

        STATUS_COMBO_BOX.addItem("");
        STATUS_COMBO_BOX.addItem(I18N.get("CacheTable.combobox.label.2"));
        STATUS_COMBO_BOX.addItem(I18N.get("CacheTable.combobox.label.3"));
        STATUS_COMBO_BOX.addItem(I18N.get("CacheTable.combobox.label.4"));
        STATUS_COMBO_BOX.addItem(I18N.get("CacheTable.combobox.label.5"));
        STATUS_COMBO_BOX.addItem(I18N.get("CacheTable.combobox.label.11"));
        STATUS_COMBO_BOX.addItem(I18N.get("CacheTable.combobox.label.6"));

        FILTER_FIELD.setDragEnabled(true);
        FILTER_FIELD.getDocument().addDocumentListener(new DocumentListener() {
                private static final int DELAY = 300; // 0.3 secs
                private final Action FILTER_ACTION = new AbstractAction() {
                        /** */
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            filter();
                            setLastTyped(Long.MAX_VALUE);
                        }
                    };
                private final Timer TIMER = new Timer(DELAY, FILTER_ACTION);

                private long lastTyped = Long.MAX_VALUE;

                {
                    TIMER.setRepeats(false);
                }

                /** */
                @Override
                public void changedUpdate(DocumentEvent e) {
                    // no-op
                }

                /** */
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateTable();
                }

                /** */
                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateTable();
                }

                //
                //
                //

                /**
                 *
                 * @param lastTyped
                 */
                private void setLastTyped(long lastTyped) {
                    this.lastTyped = lastTyped;
                }

                /**
                 *
                 */
                private long getLastTyped() {
                    return this.lastTyped;
                }

                /**
                 *
                 */
                private void updateTable() {
                    if (System.currentTimeMillis() - getLastTyped() > DELAY) {
                        filter();
                    } else {
                        setLastTyped(System.currentTimeMillis());
                        TIMER.stop();
                        TIMER.start();
                    }
                }
            });

        return this.filterPanel;
    }

    //
    //
    //

    /**
     *
     * @param type
     */
    int getTypeCount(int type) {
        // TODO: move/rewrite me
        String typeName = I18N.getCacheTypeName(type);
        int count = 0;
        for (int i = 0; i < getRowCount(); i++) {
            if (typeName.equals(getValueAt(i, 0))) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the number of visible rows including not fully visible ones.
     *
     */
    int getVisibleRowCount() {
        return getVisibleRows().length;
    }

    /**
     * Returns visible rows including not fully visible ones.
     *
     */
    int[] getVisibleRows() {
        Rectangle rect = getVisibleRect();
        Point p = rect.getLocation();
        int firstRow = rowAtPoint(p);
        p.y = rect.y + rect.height;
        int lastRow = rowAtPoint(p);
        if (lastRow == -1) { // off bounds
            lastRow = getRowCount();
        }
        int[] rows = new int[lastRow - firstRow];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = firstRow + i;
        }

        return rows;
    }

    /**
     *
     *
     */
    void packColumns() {
        packColumns(false);
    }

    /**
     *
     */
    void showSmileyChooserDialog() {
        SmileyChooserPanel panel = new SmileyChooserPanel();
        String title = I18N.get("SmileyChooserPanel.border.title.0");
        int option =
            JOptionPane.showOptionDialog(getTopLevelAncestor(),
                                         panel, title,
                                         JOptionPane.OK_CANCEL_OPTION,
                                         JOptionPane.PLAIN_MESSAGE,
                                         null, null, null);
        if (option == JOptionPane.OK_OPTION) {
            setValueAt(panel.getSmiley(), getSelectedRow(), getSmileyColumn());
            repaint(getCellRect(getSelectedRow(), // update smiley cell only (=2)
                                convertColumnIndexToView(2), false));
        }
    }

    /**
     * hack
     */
    static int getStatusColumn() {
        return COLUMN_IDENTIFIERS.length - 2;
    }

    /**
     * hack
     */
    static int getSmileyColumn() {
        return COLUMN_IDENTIFIERS.length - 1;
    }

    //
    //
    //

    /**
     *
     * @param force
     */
    private void packAndSort(boolean force) {
        RowSorter.SortKey key = new RowSorter.SortKey(3, SortOrder.DESCENDING);
        List<RowSorter.SortKey> keyList = new ArrayList<RowSorter.SortKey>();
        keyList.add(key);
        ROW_SORTER.setSortKeys(keyList);

        packColumns(force);
    }

    /**
     * Packs all columns.
     *
     */
    private void packColumns(boolean force) {
        if (!force && (!getAutoResizeColumns() || getRowCount() == 0)) { // nothing to pack
            return;
        }

        EventQueue.invokeLater(new Runnable() {
                /** */
                @Override
                public void run() {
                    setAutoResizeMode(AUTO_RESIZE_OFF);
                    // subtract 2 for smiley and status columns
                    // TODO: remove magic #
                    for (int i = 0; i < COLUMN_IDENTIFIERS.length - 2; i++) {
                        packColumn(i);
                    }
                }
            });
    }

    /**
     * Aligns cell/header text.
     *
     * @param label
     * @param column
     */
    private void setAlignment(JLabel label, int column) {
        column = convertColumnIndexToModel(column);
        switch (column) {
        case 0: // Type
            label.setHorizontalAlignment(JLabel.CENTER);
            break;

        case 1: // Waypoint, thru
        case 2: // Name
            label.setHorizontalAlignment(JLabel.LEFT);
            break;
        case 3: // Found On, thru
        case 4: // Latitude, thru
        case 5: // Longitude
            label.setHorizontalAlignment(JLabel.CENTER);
            break;
        case 6: // Distance, thru
        case 7: // Bear.
            label.setHorizontalAlignment(JLabel.RIGHT);
            break;
        case 8: // Difficulty, thru
        case 9: // Terrain
            label.setHorizontalAlignment(JLabel.CENTER);
            break;
        case 10: // Container, thru
        case 11: // Owner, thru
        case 12: // Placed By
            label.setHorizontalAlignment(JLabel.LEFT);
            break;
        case 13: // Placed On
            label.setHorizontalAlignment(JLabel.CENTER);
            break;
        case 14: // Country
            label.setHorizontalAlignment(JLabel.LEFT);
            break;
        case 15: // State
            label.setHorizontalAlignment(JLabel.CENTER);
            break;
        }
    }

    /**
     *
     * @param evt
     */
    private void showPopup(MouseEvent evt) {
        int row = rowAtPoint(evt.getPoint());
        if (row == -1) { // click not on any row
            return;
        }
        setRowSelectionInterval(row, row);

        if (this.popupMenu == null) {
            this.popupMenu = new TablePopupMenu() {
                    /** */
                    @Override
                    public void addNotify() {
                        super.addNotify();

                        SwingUtilities.updateComponentTreeUI(this);
                    }
                };
        }
        this.popupMenu.show(this, evt.getX(), evt.getY());
    }

    /**
     *
     */
    private void fillRows() {
        new TableFillTask().execute();
    }

    /**
     *
     */
    private void filterArchived() {
        Map<String, CacheBean> map =
            GPXParser.singleton().getCacheBeanMap();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : map.entrySet()) {
            if (((CacheBean) entry.getValue()).getArchived()) {
                sb.append(entry.getKey() + "|");
            }
        }
        String str = sb.toString();
        if (str.length() != 0) {
            str = str.substring(0, str.length() - 1);
            filter(str, convertColumnIndexToModel(1));
        } else {
            filter("^$", 0); // hack
        }
    }

    /**
     *
     */
    private void filterAvailable() {
        Map<String, CacheBean> map =
            GPXParser.singleton().getCacheBeanMap();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : map.entrySet()) {
            CacheBean bean = (CacheBean) entry.getValue();
            if (!bean.getAvailable() && !bean.getArchived()) {
                sb.append(entry.getKey() + "|");
            }
        }
        String str = sb.toString();
        if (str.length() != 0) {
            str = str.substring(0, str.length() - 1);
            filter(str, convertColumnIndexToModel(1));
        } else {
            filter("^$", 0); // hack
        }
    }

    /**
     * Packs the specified column.
     *
     * @param index column index
     */
    private void packColumn(int index) {
        DefaultTableColumnModel colModel =
            (DefaultTableColumnModel) getColumnModel();
        TableColumn col = colModel.getColumn(index);
        int width = 0;
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = getTableHeader().getDefaultRenderer();
        }
        Component comp =
            renderer.getTableCellRendererComponent(this, col.getHeaderValue(),
                                                   false, false, 0, 0);
        int offset = 10;
        width = comp.getPreferredSize().width + offset;

        int[] visibleRows = getVisibleRows();
        for (int i = 0; i < visibleRows.length; i++) {
            int row = visibleRows[i];
            renderer = getCellRenderer(row, index);
            comp = renderer.getTableCellRendererComponent(this,
                                                          getValueAt(row, index),
                                                          false, false, row, index);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        col.setPreferredWidth(width + offset);
    }

    /**
     * Shows the filter configuration dialog.
     *
     */
    private void showFilterConfigDialog() {
        int option =
            JOptionPane.showOptionDialog(getTopLevelAncestor(),
                                         getFilterConfigPanel(),
                                         I18N.get("CacheTable.dialog.title.0"),
                                         JOptionPane.OK_CANCEL_OPTION,
                                         JOptionPane.PLAIN_MESSAGE,
                                         null, null, null);
        if (option == JOptionPane.OK_OPTION) {
            filter();
        }
        FILTER_FIELD.requestFocusInWindow();
    }

    /**
     *
     */
    private FilterConfigPanel getFilterConfigPanel() {
        if (this.filterConfigPanel == null) {
            this.filterConfigPanel = new FilterConfigPanel();
        }

        return this.filterConfigPanel;
    }

    /**
     *
     * @param row
     */
    private void updateCacheBean(int row) {
        if (getRowCount() == 0) {
            return;
        }
        row = convertRowIndexToView(row);
        if (row == -1) {
            setRowSelectionInterval(0, 0);
            return;
        }
        // TODO: rewrite me
        String waypoint = (String) getValueAt(row, convertColumnIndexToView(1));
        CacheBean bean = GPXParser.singleton().getBean(waypoint);
        bean.setSym((Integer) getValueAt(row, convertColumnIndexToView(getStatusColumn())));
        bean.setSmiley((Integer) getValueAt(row, convertColumnIndexToView(getSmileyColumn())));
    }

    /**
     *
     */
    private void resetFilterComboBoxes() {
        Component[] comps = this.filterPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JComboBox) {
                if (c.hasFocus()) {
                    continue;
                }
                ((JComboBox) c).setSelectedIndex(0);
            }
        }
    }

    /**
     *
     */
    private void searchLog() {
        String message = I18N.get("CacheTable.dialog.0");
        String title = UIManager.getString("OptionPane.inputDialogTitle",
                                           Pref.getLocale());
        String str =
            JOptionPane.showInputDialog(getTopLevelAncestor(), message, title,
                                        JOptionPane.QUESTION_MESSAGE);
        if (str != null) {
            Pattern p = Pattern.compile(str);
            Matcher m = null;
            Map<String, CacheBean> map =
                GPXParser.singleton().getCacheBeanMap();
            if (map.size() == 0) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (CacheBean bean : map.values()) {
                m = p.matcher(bean.getLog());
                if (m.find()) {
                    sb.append(bean.getWaypoint() + "|");
                }
            }
            String regex = sb.toString();
            if (regex.length() != 0) {
                filter(regex.substring(0, regex.length() - 1), 1);
            } else {
                filter("^A", 1); // tmp tmp tmp use JOptionPane here
            }
        }
    }

    //
    //
    //

    /**
     *
     */
    private class CacheTableModel extends DefaultTableModel {
        /**
         * Creates a CacheTableModel.
         *
         */
        public CacheTableModel() {
            setColumnIdentifiers(COLUMN_IDENTIFIERS);
        }

        /** */
        @Override
        public int getColumnCount() {
            return COLUMN_IDENTIFIERS.length;
        }

        /** */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 6 || columnIndex == 7) {
                return Double.class;
            }

            return String.class;
        }

        /** */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /** */
        @Override
        public void fireTableChanged(TableModelEvent evt) {
            super.fireTableChanged(evt);

            if (getRowCount() == 0) { // is this necessary?
                return;
            }

            if (evt.getType() == TableModelEvent.UPDATE) {
                updateCacheBean(evt.getFirstRow());
            }
        }
    }

    //
    private final Color TEAL = new Color(0, 128, 128);

    /**
     *
     */
    private class CacheTableCellRenderer extends DefaultTableCellRenderer {
        //
        private int row = -1;

        /** */
        @Override
        @SuppressWarnings("unchecked")
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            isSelected = row == getSelectedRow();

            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
            setAlignment(this, column);

            column = convertColumnIndexToModel(column);
            if (column == 6 && Pref.getDistanceMode() == 0) { // TODO: rewrite me
                setText("" + FormatUtil.formatDistance(Double.parseDouble("" + value) * 1.6));
            }

            setIcon(null);
            String waypoint = (String) getValueAt(row, 1);
            CacheBean bean =
                    GPXParser.singleton().getCacheBeanMap().get(waypoint);
            boolean archived = bean.getArchived();
            boolean available = bean.getAvailable();
            if (column == 0) {
                setText(null);
                String type = (String) value;
                if (type.startsWith("T")) { // traditional
                    setIcon(Icons.TRADITIONAL_CACHE);
                } else if (type.startsWith("Mu")) { // multi
                    setIcon(Icons.MULTI_CACHE);
                } else if (type.startsWith("V")) { // virtual
                    setIcon(Icons.VIRTUAL_CACHE);
                } else if (type.startsWith("My")) { // mystery/puzzle
                    setIcon(Icons.UNKNOWN_CACHE);
                } else if (type.startsWith("Ea")) { // earth
                    setIcon(Icons.EARTH_CACHE);
                } else if (type.startsWith("W")) { // webcam
                    setIcon(Icons.WEBCAM_CACHE);
                } else if (type.startsWith("Ev")) { // evnt
                    setIcon(Icons.EVENT_CACHE);
                } else if (type.startsWith("C")) { // cito
                    setIcon(Icons.CITO_CACHE);
                } else if (type.startsWith("Me")) { // mega event
                    setIcon(Icons.MEGA_CACHE);
                } else if (type.startsWith("Pr")) { // project ape
                    setIcon(Icons.PROJECT_APE_CACHE);
                } else if (type.startsWith("Le")) { // letterbox hybrid
                    setIcon(Icons.LETTERBOX_CACHE);
                } else if (type.startsWith("Lo")) { // locationless (reverse)
                    setIcon(Icons.LOCLESS_CACHE);
                } else if (type.startsWith("Pa")) { // parking area
                    setIcon(Icons.PARKING);
                } else if (type.startsWith("R")) { // reference point
                    setIcon(Icons.REF_POINT);
                }
            } else if (column == 1) {
                if (archived) {
                    Map map = table.getFont().getAttributes();
                    map.put(TextAttribute.STRIKETHROUGH,
                            TextAttribute.STRIKETHROUGH_ON);
                    map.put(TextAttribute.FOREGROUND,
                            Color.RED);
                    setFont(new Font(map));
                } else if (!available) {
                    Map map = table.getFont().getAttributes();
                    map.put(TextAttribute.STRIKETHROUGH,
                            TextAttribute.STRIKETHROUGH_ON);
                    setFont(new Font(map));
                }
            } else if (column == 2) {
                int smileyIndex = convertColumnIndexToView(getSmileyColumn());
                int smiley = (Integer) table.getValueAt(row, smileyIndex);
                if (smiley != -1) {
                    setIcon(Icons.getSmileyIcon(smiley));
                }
            }

            switch (bean.getSym()) {
            case CacheBean.SYM_FOUND:
                if (isSelected) {
                    setForeground(UIManager.getColor("Table.selectionForeground"));
                } else {
                    setForeground(UIManager.getColor("Table.foreground"));
                }
                break;
            case CacheBean.SYM_FTF:
                setForeground(Color.BLUE);
                break;
            case CacheBean.SYM_MILESTONE:
                setForeground(Color.MAGENTA);
                break;
            case CacheBean.SYM_FAVORITE:
                setForeground(Color.ORANGE);
                break;
            case CacheBean.SYM_OTHER:
                if (isSelected) {
                    setForeground(UIManager.getColor("Table.selectionForeground"));
                } else {
                    setForeground(TEAL);
                }
                break;
            }

            return this;
        }
    }

    /**
     *
     */
    private static class FilterConfigPanel extends JPanel {
        private int[] filterColumns =
            new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        private JCheckBox[] filterChkBox = new JCheckBox[filterColumns.length];

        /**
         * Creates a FilterConfigPanel.
         *
         */
        public FilterConfigPanel() {
            setLayout(new BorderLayout());

            JPanel panel = new JPanel();
            String title = I18N.get("CacheTable.border.title.1");
            panel.setBorder(BorderFactory.createTitledBorder(title));
            panel.setLayout(new GridLayout(0, 3));
            this.filterChkBox[0] = new JCheckBox(I18N.get("CacheTable.checkbox.text.1"));
            panel.add(this.filterChkBox[0]);
            for (int i = 1; i < this.filterChkBox.length; i++) {
                this.filterChkBox[i] = new JCheckBox(COLUMN_IDENTIFIERS[i]);
                panel.add(this.filterChkBox[i]);
            }

            add(panel);
            add(new JLabel(I18N.get("CacheTable.label.2")), BorderLayout.SOUTH);
        }

        /**
         * Returns the indices of the checked boxes.
         *
         */
        public int[] getFilterColumns() {
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < this.filterChkBox.length; i++) {
                if (this.filterChkBox[i].isSelected()) {
                    list.add(i);
                }
            }

            int[] indices = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                indices[i] = list.get(i);
            }

            int filterSize = indices.length;
            if (indices.length == 0) {
                if (filterSize != 0 && filterSize != 16) {
                    indices = this.filterColumns;
                } else {
                    indices = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                                        10, 11, 12, 13, 14, 15};
                }
            }

            this.filterColumns = indices;

            return this.filterColumns;
        }

        /** */
        @Override
        public void addNotify() {
            super.addNotify();

            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    /**
     *
     */
    // TODO: rewrite me
    static class SmileyChooserPanel extends JPanel {
        //
        private ButtonGroup group = null;

        //
        static final int SMILEY_COUNT = 20;
        private final Map<String, ImageIcon> ICON_MAP =
            new LinkedHashMap<String, ImageIcon>(SMILEY_COUNT);
        private final Map<String, Integer> SMILEY_MAP =
            new HashMap<String, Integer>(SMILEY_COUNT);

        /**
         * Creates a FilterConfigPanel.
         *
         */
        public SmileyChooserPanel() {
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new GridLayout(0, 4));

            String[] SMILEY_NAMES = new String[]{"smile", "big smile", "cool",
                                                 "blush", "tongue", "evil",
                                                 "wink", "clown", "black eye",
                                                 "eightball", "frown", "shy",
                                                 "shocked", "angry", "dead",
                                                 "sleepy", "kisses", "approve",
                                                 "disapprove", "question"};
            // do not shuffle, ordered
            ICON_MAP.put(SMILEY_NAMES[0], Icons.SMILEY_SMILE);
            ICON_MAP.put(SMILEY_NAMES[1], Icons.SMILEY_SMILE_BIG);
            ICON_MAP.put(SMILEY_NAMES[2], Icons.SMILEY_SMILE_COOL);
            ICON_MAP.put(SMILEY_NAMES[3], Icons.SMILEY_SMILE_BLUSH);
            ICON_MAP.put(SMILEY_NAMES[4], Icons.SMILEY_SMILE_TONGUE);
            ICON_MAP.put(SMILEY_NAMES[5], Icons.SMILEY_SMILE_EVIL);
            ICON_MAP.put(SMILEY_NAMES[6], Icons.SMILEY_SMILE_WINK);
            ICON_MAP.put(SMILEY_NAMES[7], Icons.SMILEY_SMILE_CLOWN);
            ICON_MAP.put(SMILEY_NAMES[8], Icons.SMILEY_SMILE_BLACK_EYE);
            ICON_MAP.put(SMILEY_NAMES[9], Icons.SMILEY_SMILE_EIGHT_BALL);
            ICON_MAP.put(SMILEY_NAMES[10], Icons.SMILEY_SMILE_FROWN);
            ICON_MAP.put(SMILEY_NAMES[11], Icons.SMILEY_SMILE_SHY);
            ICON_MAP.put(SMILEY_NAMES[12], Icons.SMILEY_SMILE_SHOCKED);
            ICON_MAP.put(SMILEY_NAMES[13], Icons.SMILEY_SMILE_ANGRY);
            ICON_MAP.put(SMILEY_NAMES[14], Icons.SMILEY_SMILE_DEAD);
            ICON_MAP.put(SMILEY_NAMES[15], Icons.SMILEY_SMILE_SLEEPY);
            ICON_MAP.put(SMILEY_NAMES[16], Icons.SMILEY_SMILE_KISSES);
            ICON_MAP.put(SMILEY_NAMES[17], Icons.SMILEY_SMILE_APPROVE);
            ICON_MAP.put(SMILEY_NAMES[18], Icons.SMILEY_SMILE_DISAPPROVE);
            ICON_MAP.put(SMILEY_NAMES[19], Icons.SMILEY_SMILE_QUESTION);

            SMILEY_MAP.put(SMILEY_NAMES[0], 0);
            SMILEY_MAP.put(SMILEY_NAMES[1], 1);
            SMILEY_MAP.put(SMILEY_NAMES[2], 2);
            SMILEY_MAP.put(SMILEY_NAMES[3], 3);
            SMILEY_MAP.put(SMILEY_NAMES[4], 4);
            SMILEY_MAP.put(SMILEY_NAMES[5], 5);
            SMILEY_MAP.put(SMILEY_NAMES[6], 6);
            SMILEY_MAP.put(SMILEY_NAMES[7], 7);
            SMILEY_MAP.put(SMILEY_NAMES[8], 8);
            SMILEY_MAP.put(SMILEY_NAMES[9], 9);
            SMILEY_MAP.put(SMILEY_NAMES[10], 10);
            SMILEY_MAP.put(SMILEY_NAMES[11], 11);
            SMILEY_MAP.put(SMILEY_NAMES[12], 12);
            SMILEY_MAP.put(SMILEY_NAMES[13], 13);
            SMILEY_MAP.put(SMILEY_NAMES[14], 14);
            SMILEY_MAP.put(SMILEY_NAMES[15], 15);
            SMILEY_MAP.put(SMILEY_NAMES[16], 16);
            SMILEY_MAP.put(SMILEY_NAMES[17], 17);
            SMILEY_MAP.put(SMILEY_NAMES[18], 18);
            SMILEY_MAP.put(SMILEY_NAMES[19], 19);
            SMILEY_MAP.put("none", -1);

            this.group = new ButtonGroup();
            for (final String name: ICON_MAP.keySet()) {
                JRadioButton rb = new JRadioButton() {
                        {
                            setText(name);
                            setActionCommand(name);
                            setIcon(ICON_MAP.get(name));
                            Dimension dim = super.getPreferredSize();
                            dim.width += 3;
                            setPreferredSize(dim);
                        }

                        /** */
                        @Override
                        public void paintComponent(Graphics g) {
                            if (isSelected()) { // TODO: fix colors
                                setFont(getFont().deriveFont(Font.BOLD));
                                setForeground(UIManager.getColor("TitledBorder.titleColor"));
                            } else {
                                setFont(UIManager.getFont("Label.font"));
                                setForeground(UIManager.getColor("Label.foreground"));
                            }
                            super.paintComponent(g);
                        }
                    };
                add(rb);
                this.group.add(rb);
            }
            JRadioButton rb = new JRadioButton(I18N.get("CacheTable.checkbox.text.2"));
            rb.setActionCommand("none");
            rb.setSelected(true);
            this.group.add(rb);
            add(rb);

        }

        /**
         * Returns the smiley count.
         *
         */
        public int getSmileyCount() {
            return ICON_MAP.size();
        }

        /**
         * Returns the indices of the checked boxes.
         *
         */
        public int getSmiley() {
            ButtonModel model = this.group.getSelection();
            if (model == null) {
                return -1;
            }
            return SMILEY_MAP.get(model.getActionCommand());
        }
    }

    /**
     *
     */
    private class TableFillTask extends SwingWorker<Void, CacheBean> {
        /** */
        @Override
        public Void doInBackground() {
            ((GCOrganizer) getTopLevelAncestor()).toggleCursor(true);
            Map<String, CacheBean> map =
                GPXParser.singleton().deserializeCacheData();
            if (map.size() == 0) {
                return null;
            }
            for (CacheBean bean : map.values()) {
                publish(bean);
            }

            return null;
        }

        /** */
        @Override
        public void process(List<CacheBean> chunks) {
            for (CacheBean bean : chunks) {
                addCache(bean);
            }
        }

        /** */
        @Override
        public void done() {
            System.out.println("Hello");
            if (getRowCount() != 0) {
                packAndSort(true);
                setRowSelectionInterval(0, 0);
            }
            ((GCOrganizer) getTopLevelAncestor()).toggleCursor(false);
        }
    }

    /**
     *
     */
    private class FillTableTask2 extends SwingWorker<Void, CacheBean> {
        //
        private File[] files = null;

        /**
         *
         */
        public FillTableTask2(File[] files) {
            this.files = files;
        }

        /** */
        @Override
        public Void doInBackground() {
            ((GCOrganizer) getTopLevelAncestor()).toggleCursor(true);
            Map<String, CacheBean> map = new HashMap<String, CacheBean>();
            try {
                for (File file : this.files) {
                    BufferedInputStream in = null;
                    ZipFile zipFile = null;
                    String name = file.getName().toLowerCase();
                    try {
                        if (name.endsWith(".zip")) {
                            zipFile = new ZipFile(file);
                            String entryName = name.substring(0, name.indexOf("."))
                                + ".gpx";
                            ZipEntry entry = zipFile.getEntry(entryName);
                            if (entry == null) {
                                showNoCacheMessage(file.getName());
                                continue;
                            }
                            in = new BufferedInputStream(zipFile.getInputStream(entry));
                        } else {
                            in = new BufferedInputStream(new FileInputStream(file));
                        }
                        Map<String, CacheBean> tmpMap =
                            GPXParser.singleton().parse(in);
                        if (tmpMap.size() == 0) {
                            showNoCacheMessage(file.getName());
                            continue;
                        }
                        map.putAll(tmpMap);
                    } finally {
                        if (zipFile != null) {
                            zipFile.close();
                        }
                    }
                }
            } catch (IOException e) {
                return null;
            } catch (XMLStreamException e) {
                return null;
            }

            if (map.size() != 0) {
                for (CacheBean bean : map.values()) {
                    publish(bean);
                }
            }

            return null;
        }

        /** */
        @Override
        public void process(List<CacheBean> chunks) {
            for (CacheBean bean : chunks) {
                addCache(bean);
            }
        }

        /** */
        @Override
        public void done() {
            if (getRowCount() != 0) {
                packAndSort(true);
                setRowSelectionInterval(0, 0);
                valueChanged(new ListSelectionEvent(CacheTable.this,
                                                    0, 0, false));
            }
            ((GCOrganizer) getTopLevelAncestor()).toggleCursor(false);
        }

        //
        //
        //

        /**
         *
         */
        private void showNoCacheMessage(String fileName) {
            String message = I18N.get("GCOrganizer.dialog.0",
                                      fileName,
                                      Pref.getUserName());
            String title =
                UIManager.getString("OptionPane.messageDialogTitle");
            JOptionPane.showMessageDialog(getTopLevelAncestor(),
                                          message, title,
                                          JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     *
     */
    private class IconButton extends JButton {
        /**
         *
         */
        public IconButton(ImageIcon icon, String toolTipText) {
            super(icon);
            setToolTipText(toolTipText);
        }

        /** */
        @Override
        public Dimension getPreferredSize() {
            Dimension dim = super.getPreferredSize();
            dim.width = dim.height;
            return dim;
        }
    }
}
