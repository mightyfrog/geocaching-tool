package com.dolphincafe.geocaching;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 *
 *
 */
class DataField extends JTextField {
    //
    protected static final DataFieldListener DF_LISTENER =
        new DataFieldListener();

    /**
     *
     */
    public DataField() {
        setEditable(false);
        setDragEnabled(true);
        addMouseListener(DF_LISTENER);
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
    void showPopup(MouseEvent evt) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem copyMI = new JMenuItem() {
                {
                    setText(I18N.get("DataField.menu.0"));
                    addActionListener(new ActionListener() {
                            /** */
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                copy();
                            }
                        });
                }
            };
        copyMI.setEnabled(getCaret().getDot() != getCaret().getMark());
        popup.add(copyMI);

        if (isEditable()) {
            JMenuItem pasteMI = new JMenuItem() {
                    {
                        setText(I18N.get("DataField.menu.1"));
                        addActionListener(new ActionListener() {
                                /** */
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    paste();
                                }
                            });
                    }
                };
            popup.add(pasteMI);
        }

        popup.show(this, evt.getX(), evt.getY());
    }

    /**
     *
     */
    private static class DataFieldListener implements MouseListener {
        /** */
        @Override
        public void mouseEntered(MouseEvent evt) {
            // no-op
        }

        /** */
        @Override
        public void mousePressed(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                DataField df = (DataField) evt.getSource();
                df.showPopup(evt);
            }
        }

        /** */
        @Override
        public void mouseClicked(MouseEvent evt) {
            // no-op
        }

        /** */
        @Override
        public void mouseReleased(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                DataField df = (DataField) evt.getSource();
                df.showPopup(evt);
            }
        }

        /** */
        @Override
        public void mouseExited(MouseEvent evt) {
            // no-op
        }
    }
}
