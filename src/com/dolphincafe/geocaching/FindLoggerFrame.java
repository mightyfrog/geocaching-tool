package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 *
 */
class FindLoggerFrame extends JFrame {
    //
    private static final String LOG_URL =
        "http://www.geocaching.com/seek/log.aspx?wp=";

    //
    private final GPXHandler GPX_HANDLER;

    //
    private JButton button = new JButton(I18N.get("FindLoggerFrame.button.0"));

    //
    private final TableModel MODEL = new TableModel();
    private JTable table = new JTable(MODEL) {
            /** */
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                super.valueChanged(evt);
                int rows = getSelectedRows().length;
                FindLoggerFrame.this.button.
                    setEnabled(rows != 0 && rows <= 5);
            }

            /** */
            @Override
            public void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);

                if (getRowCount() == 0) {
                    String str = I18N.get("FindLoggerFrame.dialog.message.0");
                    int w = g.getFontMetrics().stringWidth(str);
                    int x = (getVisibleRect().width - w) / 2;
                    int y = getVisibleRect().height / 2;
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                    g2.setColor(SystemColor.textInactiveText);
                    g2.drawString(str, x, y);
                }
            }
        };
    private final TableRowSorter<TableModel> ROW_SORTER =
        new TableRowSorter<TableModel>(MODEL);

    //
    static FindLoggerFrame _singleton = null;

    /**
     *
     */
    private FindLoggerFrame() {
        // DO NOT SET OWNER
        setTitle(I18N.get("FindLoggerFrame.dialog.title.0"));
        setSize(350, 200);
        //setResizable(false);
        setIconImage(Icons.FRAME_LOGO.getImage());

        this.table.setFillsViewportHeight(true);
        this.table.setRowSorter(ROW_SORTER);
        this.table.getTableHeader().setReorderingAllowed(false);

        // TODO: fix ESC listener
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.getActionMap().put("escape", new AbstractAction() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    setVisible(false);
                }
            });
        contentPane.getInputMap().
            put(KeyStroke.getKeyStroke("pressed ESCAPE"), "escape");
        this.table.getActionMap().put("escape", new AbstractAction() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    FindLoggerFrame.this.setVisible(false);
                }
            });
        this.table.getInputMap().
            put(KeyStroke.getKeyStroke("pressed ESCAPE"), "escape");
        this.button.getActionMap().put("escape", new AbstractAction() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    FindLoggerFrame.this.setVisible(false);
                }
            });
        this.button.getInputMap().
            put(KeyStroke.getKeyStroke("pressed ESCAPE"), "escape");

        GPX_HANDLER = new GPXHandler();
        this.table.setTransferHandler(new TransferHandler() {
                //
                private List<?> list = null;

                /** */
                @Override
                public boolean canImport(TransferHandler.TransferSupport support) {
                    support.setShowDropLocation(false);

                    Transferable t = support.getTransferable();
                    try {
                        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            this.list =
                                (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        } else {
                            String uriList =
                                (String) t.getTransferData(FileUtil.uriListFlavor);
                            this.list = FileUtil.textURIListToFileList(uriList);
                        }
                        for (Object obj : this.list) {
                            File file = (File) obj;
                            String name = file.getName().toLowerCase();
                            if (file.isFile() && (name.endsWith(".gpx"))) {
                                return true;
                            }
                        }
                    } catch (InvalidDnDOperationException e) {
                        // ignore
                        //e.printStackTrace();
                    } catch (UnsupportedFlavorException e) {
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }

                    return true;
                }

                /** */
                @Override
                public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                    return true;
                }

                /** */
                @Override
                @SuppressWarnings("unchecked")
                public boolean importData(JComponent comp, Transferable t) {
                    if (!canImport(comp, t.getTransferDataFlavors())) {
                        return false;
                    }

                    List<File> fileList = new ArrayList<File>();
                    for (Object obj : this.list) {
                        fileList.add((File) obj);
                    }
                    parse(fileList);

                    return true;
                }
            });

        this.button.setEnabled(false);
        this.button.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    openLogs();
                }
            });

        add(new JScrollPane(this.table));
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    /** */
    static FindLoggerFrame singleton() {
        if (_singleton == null) {
            _singleton = new FindLoggerFrame();
        }

        return _singleton;
    }

    /** */
    @Override
    public void setVisible(boolean visible) {
        if (visible && isVisible()) {
            toFront();
            return;
        }
        super.setVisible(visible);
    }

    //
    //
    //

    /**
     *
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(this.button);

        return panel;
    }

    /**
     *
     * @param fileList
     */
    @SuppressWarnings("unchecked")
    private void parse(List<File> fileList) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            return;
        } catch (SAXException e) {
            return;
        }

        for (File file : fileList) {
            try {
                boolean b = file.getName().toLowerCase().endsWith(".loc");
                GPX_HANDLER.setLocMode(b);
                parser.parse(file, GPX_HANDLER);
            } catch (SAXException e) {
            } catch (IOException e) {
            }
        }

        EventQueue.invokeLater(new Runnable() {
                /** */
                @Override
                public void run() {
                    packColumn(0);
                    toFront();
                }
            });
    }

    /**
     *
     */
    private void openLogs() {
        int[] rows = this.table.getSelectedRows();
        for (int i = rows.length - 1; i >= 0; i--) {
            String wpt = (String) this.table.getValueAt(rows[i], 0);
            MODEL.removeRow(this.table.convertRowIndexToModel(rows[i]));
            openLog(wpt);
        }
        this.button.setEnabled(false);
    }

    /**
     *
     * @param waypoint
     */
    private void openLog(String waypoint) {
        try {
            Desktop.getDesktop().browse(new URI(LOG_URL + waypoint));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // shadows UnsupportedEncodingException
            e.printStackTrace();
        }
    }

    /**
     * Packs the specified column.
     *
     * @param index column index
     * @return column width
     */
    private void packColumn(int index) {
        DefaultTableColumnModel colModel =
            (DefaultTableColumnModel) this.table.getColumnModel();
        TableColumn col = colModel.getColumn(index);
        int width = 0;
        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = this.table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(this.table,
                                                                col.getHeaderValue(),
                                                                false, false, 0, 0);
        int offset = 10;
        width = comp.getPreferredSize().width + offset;
        for (int i = 0; i < this.table.getRowCount(); i++) {
            renderer = this.table.getCellRenderer(i, index);
            comp = renderer.getTableCellRendererComponent(this.table,
                                                          this.table.getValueAt(i, index),
                                                          false, false, i, index);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        col.setMaxWidth(width + offset);
        col.setPreferredWidth(width + offset);
    }

    //
    //
    //

    /**
     *
     */
    private class GPXHandler extends DefaultHandler {
        String name = null; // wpt
        boolean flag = false; // read name if this flag is on
        boolean locMode = false; // true for .loc

        /** */
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) {
            if (getLocMode()) {
                if (qName.equals("name")) {
                    this.name = attributes.getValue("id");
                }
            } else {
                if (qName.equals("name")) {
                    this.flag = true;
                } else if (qName.equals("urlname")) {
                    this.flag = true;
                }
            }
        }

        /** */
        @Override
        public void characters(char[] ch, int offset, int length) {
            if (getLocMode()) {
                if (this.name != null) {
                    String urlname = new String(ch, offset, length);
                    MODEL.addRow(new String[]{this.name, urlname});
                    this.name = null;
                    this.flag = false;
                }
            } else {
                if (this.flag && this.name == null) {
                    this.name = new String(ch, offset, length);
                    if (!this.name.startsWith("GC")) {
                        this.name = null;
                    }
                    this.flag = false;
                } else if (this.flag && this.name != null) {
                    String urlname = new String(ch, offset, length);
                    MODEL.addRow(new String[]{this.name, urlname});
                    this.name = null;
                    this.flag = false;
                }
            }
        }

        /**
         *
         */
        void setLocMode(boolean locMode) {
            this.locMode = locMode;
        }

        /**
         *
         */
        boolean getLocMode() {
            return this.locMode;
        }
    }

    /**
     *
     */
    private class TableModel extends DefaultTableModel {
        private final String[] COLUMN_IDENTIFIERS =
            new String[]{I18N.get("FindLoggerFrame.table.col.identifier.0"),
                         I18N.get("FindLoggerFrame.table.col.identifier.1")};
        /**
         * Creates a AttachmentTableModel.
         *
         */
        public TableModel() {
            setColumnIdentifiers(COLUMN_IDENTIFIERS);
        }

        /** */
        @Override
        public void addRow(Object[] rowData) {
            for (int i = 0; i < FindLoggerFrame.this.table.getRowCount(); i++) {
                if (rowData[0].equals(getValueAt(i, 0))) {
                    return;
                }
            }
            super.addRow(rowData);
        }

        /** */
        @Override
        public int getColumnCount() {
            return COLUMN_IDENTIFIERS.length;
        }

        /** */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
