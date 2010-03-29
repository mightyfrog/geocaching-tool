package com.dolphincafe.geocaching;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/**
 *
 *
 * @author Shigehiro Soejima
 */
class AttachmentTable extends JTable implements MouseListener {
    // column identifiers
    private final String[] COLUMN_IDENTIFIERS =
        new String[]{I18N.get("AttachmentTable.col.identifier.0"),
                     I18N.get("AttachmentTable.col.identifier.1")};
    //
    private String waypoint = null;
    private File file = null;

    //
    private final AttachmentTableModel MODEL = new AttachmentTableModel();
    private final TableRowSorter<AttachmentTableModel> ROW_SORTER =
        new TableRowSorter<AttachmentTableModel>(MODEL);

    private final static AttachmentTableCellRenderer RENDERER =
        new AttachmentTableCellRenderer();
    private final String DROP_FILE_MESSAGE = I18N.get("AttachmentTable.message.2");

    //
    private String fileName = null;
    private Image image = null;

    //
    private JPopupMenu popup = null;

    //
    private final JToolTip TOOLTIP = new JToolTip() {
            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getImage() != null) {
                    g.drawImage(getImage(), 0, 0, this);
                }
            }

            /** */
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = super.getPreferredSize();
                if (getImage() != null) {
                    dim.width = getImage().getWidth(this);
                    dim.height = getImage().getHeight(this);
                }

                return dim;
            }

            /** */
            @Override
            public boolean isShowing() {
                if (!super.isShowing()) {
                    dispose();
                }
                return super.isShowing();
            }
        };

    /**
     *
     */
    public AttachmentTable() {
        ImageIO.setUseCache(false);
        ToolTipManager.sharedInstance().registerComponent(this);
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        getTableHeader().setReorderingAllowed(false);

        setModel(MODEL);
        setRowSorter(ROW_SORTER);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        setFillsViewportHeight(true);
        addMouseListener(this);
        setTransferHandler(new TransferHandler() {
                //
                private List<?> list = null;

                /** */
                @Override
                public boolean canImport(TransferHandler.TransferSupport support) {
                    support.setShowDropLocation(false);
                    if (AttachmentTable.this.waypoint == null) {
                        return false;
                    }

                    Transferable t = support.getTransferable();
                    try {
                        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            this.list =
                                (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
                        } else {
                            String data =
                                (String) t.getTransferData(FileUtil.uriListFlavor);
                            this.list = FileUtil.textURIListToFileList(data);
                        }
                    } catch (InvalidDnDOperationException e) {
                        // ignore
                    } catch (UnsupportedFlavorException e) {
                        return false;
                    } catch (IOException e) {
                        return false;
                    }

                    return true;
                }

                /** */
                @Override
                public boolean importData(JComponent comp, Transferable t) {
                    List<File> fileList = new ArrayList<File>();
                    for (Object obj : this.list) {
                        fileList.add((File) obj);
                    }
                    addFiles(fileList);

                    return true;
                }
            });

        addKeyListener(new KeyAdapter() {
                /** */
                @Override
                public void keyPressed(KeyEvent evt) {
                    if (getSelectedRow() == -1) {
                        return;
                    }
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (!isEditing() && getSelectedRows().length == 1) {
                            evt.consume();
                            openSelectedFile(false);
                        }
                    } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                        deleteSelectedFiles();
                    }
                }
            });

        for (int i = 0; i < COLUMN_IDENTIFIERS.length; i++) {
            getColumnModel().getColumn(i).setCellRenderer(RENDERER);
        }
    }

    /** */
    @Override
    public void editingStopped(ChangeEvent evt) {
        super.editingStopped(evt);
        serializeNote();
    }

    /** */
    @Override
    public JToolTip createToolTip() {
        return TOOLTIP;
    }

    /** */
    @Override
    public String getToolTipText(MouseEvent evt) {
        int row = rowAtPoint(evt.getPoint());
        if (row == -1) {
            return null;
        }
        int col = columnAtPoint(evt.getPoint());
        if (convertColumnIndexToView(col) != 0) {
            return null;
        }
        col = convertColumnIndexToView(0);
        String text = ((File) getValueAt(row, col)).getName();
        if (text.equals(getFileName())) {
            return text;
        }
        setFileName(text);

        String path =
            System.getProperty("user.dir") + "/" + getValueAt(row, col);
        Image img = null;
        try {
            setImage(scaleImage(new File(path)));
        } catch (Exception e) {
            dispose();
        }

        return text;
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
        if (evt.getClickCount() == 2) {
            openSelectedFile(false);
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
     * @param waypoint
     */
    void setWaypoint(String waypoint) {
        this.waypoint = waypoint;
        reload();
    }

    /**
     *
     * @param list
     */
    void addFiles(final List<?> list) {
        final int fileCount = list.size();
        final ProgressMonitor pm =
            new ProgressMonitor(getTopLevelAncestor(),
                                I18N.get("AttachmentTable.message.0"),
                                " ", 0, fileCount);
        pm.setMillisToDecideToPopup(0); // 0 sec

        new SwingWorker<Void, Void>() {
            {
                addPropertyChangeListener(new PropertyChangeListener() {
                        /** */
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName() == "progress") {
                                if (pm.isCanceled() || isDone()) {
                                    if (pm.isCanceled()) {
                                        cancel(true);
                                    }
                                }
                            }
                        }
                    });
            }

            /** */
            @Override
            public Void doInBackground() {
                for (int i = 0; i < fileCount; i++) {
                    File file = (File) list.get(i);
                    if (file.isFile()) {
                        pm.setProgress(i);
                        pm.setNote(file.getName());
                        copy(file);
                    }
                }
                pm.setProgress(fileCount);

                return null;
            }

            /** */
            @Override
            public void done() {
                reload();
            }
        }.execute(); // start
    }

    //
    //
    //

    /**
     *
     * @param file
     */
    private Image scaleImage(File file) throws IOException,
                                               IllegalArgumentException {
        Image img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            throw e;
        } catch (IllegalArgumentException e) {// JDK Bug ID:6633448
            throw e;
        }
        // return the cell value if the attachment is not an image
        if (img == null) {
            throw new IllegalArgumentException();
        }
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w > h && w > 320) {
            img = img.getScaledInstance(320, -1, Image.SCALE_SMOOTH);
        } else if (h > 320) {
            img = img.getScaledInstance(-1, 320, Image.SCALE_SMOOTH);
        }

        return img;
    }

    /**
     * Diposes the cached image.
     *
     */
    private void dispose() {
        Image image = getImage();
        if (image != null) {
            image.flush();
        }
        setImage(null);
        setFileName(null);
    }

    /**
     *
     * @param fileName
     */
    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     *
     */
    private String getFileName() {
        return this.fileName;
    }

    /**
     *
     * @param image
     */
    private void setImage(Image image) {
        this.image = image;
    }

    /**
     *
     */
    private Image getImage() {
        return this.image;
    }

    /**
     *
     */
    private String getWaypoint() {
        return this.waypoint;
    }

    /**
     *
     * @param file
     */
    private void setFile(File file) {
        this.file = file;
    }

    /**
     *
     */
    private File getFile() {
        return this.file;
    }

    /**
     *
     * @param evt
     */
    private void showPopup(MouseEvent evt) {
        if (!isEnabled()) {
            return;
        }

        int row = rowAtPoint(evt.getPoint());
        if (row == -1) {
            return;
        }

        int[] rows = getSelectedRows();
        if (rows.length <= 1) {
            setRowSelectionInterval(row, row);
            rows = getSelectedRows();
        } else {
            boolean rowSelected = false;
            for (int r : rows) {
                if (r == row) {
                    rowSelected = true;
                    break;
                }
            }
            if (!rowSelected) {
                setRowSelectionInterval(row, row);
                rows = getSelectedRows();
            }
        }

        if (this.popup == null) {
            this.popup = new JPopupMenu() {
                    /** */
                    @Override
                    public void addNotify() {
                        super.addNotify();

                        SwingUtilities.updateComponentTreeUI(this);
                    }
                };

            JMenuItem deleteMI =
                new JMenuItem(I18N.get("AttachmentTable.menu.1"));
            deleteMI.addActionListener(new ActionListener() {
                    /** */
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        deleteSelectedFiles();
                    }
                });
            JMenuItem openFolderMI =
                new JMenuItem(I18N.get("AttachmentTable.menu.2"));
            openFolderMI.addActionListener(new ActionListener() {
                    /** */
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        openSelectedFile(true);
                    }
                });
            JMenuItem openMI =
                new JMenuItem(I18N.get("AttachmentTable.menu.0"));
            openMI.addActionListener(new ActionListener() {
                    /** */
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        openSelectedFile(false);
                    }
                });

            this.popup.add(openMI);
            this.popup.add(openFolderMI);
            this.popup.addSeparator();
            this.popup.add(deleteMI);
        }

        this.popup.getSubElements()[0].getComponent().
            setEnabled(rows.length == 1);

        this.popup.show(this, evt.getX(), evt.getY());
    }

    /**
     *
     */
    private void serializeNote() {
        if (getRowCount() == 0) { // tmp, no deletion check
            new File("data/" + this.waypoint + "/files/.meta").delete();
            new File("data/" + this.waypoint + "/files").delete();
            new File("data/" + this.waypoint).delete();
            return;
        }

        ObjectOutputStream oos = null;
        Map<File, String> map = new HashMap<File, String>();
        for (int i = 0; i < getRowCount(); i++) {
            map.put((File) getValueAt(i, 0),
                    (String) getValueAt(i, 1));
        }
        try {
            FileOutputStream fos =
                new FileOutputStream("data/" + this.waypoint +
                                     "/files/.meta");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     *
     */
    private void reload() {
        for (int i = getRowCount() - 1; i >= 0; i--) {
            MODEL.removeRow(i);
        }
        File root = new File("data/" + this.waypoint + "/files");
        if (!root.exists()) {
            return;
        }
        File[] files = root.listFiles();
        for (File file : files) {
            if (!file.getName().equals(".meta")) {
                MODEL.addRow(new Object[]{file, null});
            }
        }

        File noteDat = new File(root, ".meta");
        if (noteDat.exists()) {
            ObjectInputStream ois = null;
            try {
                FileInputStream fis = new FileInputStream(noteDat);
                ois = new ObjectInputStream(fis);
                Map map = (Map) ois.readObject();
                for (int i = 0; i < getRowCount(); i++) {
                    setValueAt(map.get(getValueAt(i, 0)), i, 1);
                }
            } catch (ClassNotFoundException e) {
            } catch (IOException e) {
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        EventQueue.invokeLater(new Runnable() {
                /** */
                @Override
                public void run() {
                    packColumn(0);
                }
            });
    }

    /**
     *
     * @param file
     */
    private void copy(File file) {
        FileChannel in = null;
        FileChannel out = null;
        try {
            File newFile = new File("data/" + this.waypoint + "/files");
            if (!newFile.exists() && !newFile.mkdirs()) {
                return;
            }
            newFile = new File(newFile, file.getName());
            in = new FileInputStream(file).getChannel();
            out = new FileOutputStream(newFile).getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1000000); // 1MB
            while (true) {
                buffer.clear();
                if (in.read(buffer) < 0){
                    break;
                }
                buffer.flip();
                out.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * Packs the specified column.
     *
     * @param index column index
     * @return column width
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
            renderer.getTableCellRendererComponent(this,
                                                   col.getHeaderValue(),
                                                   false, false, 0, 0);
        int offset = 10;
        width = comp.getPreferredSize().width + offset;
        for (int i = 0; i < getRowCount(); i++) {
            renderer = getCellRenderer(i, index);
            comp = renderer.getTableCellRendererComponent(this,
                                                          getValueAt(i, index),
                                                          false, false, i,
                                                          index);
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
    private class AttachmentTableModel extends DefaultTableModel {
        /**
         * Creates a AttachmentTableModel.
         *
         */
        public AttachmentTableModel() {
            setColumnIdentifiers(COLUMN_IDENTIFIERS);
        }

        /** */
        @Override
        public int getColumnCount() {
            return COLUMN_IDENTIFIERS.length;
        }

        /** */
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1;
        }
    }

    /**
     *
     */
    private static class AttachmentTableCellRenderer extends DefaultTableCellRenderer {
        /** */
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                                                hasFocus, row, column);
            if (column == 0) {
                File file = (File) value;
                ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().
                    getSystemIcon(file);
                if (table.isEnabled()) {
                    setIcon(icon);
                    if (isSelected) {
                        setForeground(UIManager.getColor("Table.selectionForeground"));
                    } else {
                        setForeground(UIManager.getColor("Table.foreground"));
                    }
                } else {
                    setIcon(Icons.getDisabledIcon(icon));
                    setForeground(UIManager.getColor("Table.disabledForeground"));
                }
                setText(file.getName());
            } else {
                setIcon(null);
            }

            return this;
        }
    }

    /**
     * Returns a file of the selection row. Returns null if no row is selected,
     *
     */
    private File getSelectedFile() {
        int row = getSelectedRow();
        if (row == -1) {
            return null;
        }

        File file = (File) getValueAt(row, 0);
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Opens the selected file.
     *
     */
    private void openSelectedFile(boolean parent) {
        if (!isEnabled()) { // returns immediately if this table is not enabled
            return;
        }

        File file = getSelectedFile();
        if (file == null) {
            return;
        }
        try { // seems open(File) has a nasty bug
            if (parent) {
                Desktop.getDesktop().open(file.getParentFile()); 
            } else {
                Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected files.
     *
     */
    private void deleteSelectedFiles() {
        String message = I18N.get("AttachmentTable.message.1");
        String title =
            UIManager.getString("OptionPane.titleText", Pref.getLocale());
        int option =
            JOptionPane.showConfirmDialog(getTopLevelAncestor(), message,
                                          title, JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        int[] rows = getSelectedRows();
        for (int i = rows.length - 1; i >= 0; i--) {
            int row = convertRowIndexToModel(rows[i]);
            File file = (File) MODEL.getValueAt(row, 0);
            if (!file.delete()) {
                file.deleteOnExit(); // last resort
            }
            MODEL.removeRow(row);
        }
        serializeNote();
    }
}
