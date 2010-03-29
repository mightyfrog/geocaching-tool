package com.dolphincafe.geocaching;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

/**
 *
 */
class TextArea extends JTextArea { // TODO: rewrite me
    /**
     *
     */
    TextArea() {
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

    /** */
    @Override
    public void setText(String text) {
        super.setText(text);
        setCaretPosition(0);
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
        JMenuItem copyMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("TextArea.menu.0"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    copy();
                }
            });
        JMenuItem cutMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("TextArea.menu.1"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    cut();
                }
            });
        JMenuItem pasteMI = new JMenuItem(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("TextArea.menu.2"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    paste();
                }
            });

        copyMI.setEnabled(getCaret().getDot() != getCaret().getMark());
        cutMI.setEnabled(isEditable() &&
                         getCaret().getDot() != getCaret().getMark());
        pasteMI.setEnabled(isEditable());

        menu.add(copyMI);
        menu.add(cutMI);
        menu.add(pasteMI);

        menu.show(this, evt.getX(), evt.getY());
    }
}
