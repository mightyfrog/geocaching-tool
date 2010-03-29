package com.dolphincafe.geocaching;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 */
class CacheTransferHander extends TransferHandler {
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
            for (Object obj : list) {
                File file = (File) obj;
                String name = file.getName().toLowerCase();
                if (file.isFile() &&
                    (name.endsWith(".gpx") || name.endsWith(".zip"))) {
                    return true;
                }
            }
            return false;
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
    public boolean importData(JComponent comp, Transferable t) {
        if (!comp.isEnabled() || this.list == null) {
            return false;
        }
        List<File> fileList = new ArrayList<File>();
        for (Object obj : this.list) {
            fileList.add((File) obj);
        }
        File[] files = (File[]) fileList.toArray(new File[]{});
        ((CacheTable) comp).addGPXFiles(files);

        return true;
    }

    /** */
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        if (action != TransferHandler.COPY) {
            return;
        }
        CacheTable t = (CacheTable) comp;
        Object obj = t.getValueAt(t.getSelectedRow(), t.getSelectedColumn());
        StringSelection ss = new StringSelection(String.valueOf(obj));
        clip.setContents(ss, ss);
    }
}
