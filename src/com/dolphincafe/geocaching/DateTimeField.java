package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton ;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Shigehiro Soejima
 */
class DateTimeField extends JPanel {
    private final DataField FIELD = new DataField();
    private final JButton BUTTON = new JButton(Icons.DATE) {
            /** */
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = FIELD.getPreferredSize();
                dim.width = dim.height;

                return dim;
            }

            /** */
            @Override
            public void requestFocus() {
                saveFocusOwner();
                super.requestFocus();
            }
        };

    //
    private EditorPanel editor = null;

    //
    private long time = 0L;

    //
    private Component focusOwner = null;

    /**
     *
     */
    DateTimeField() {
        setLayout(new BorderLayout());

        this.editor = new EditorPanel();

        add(FIELD);
        add(BUTTON, BorderLayout.EAST);

        BUTTON.setToolTipText(I18N.get("DateTimeField.button.text.0"));
        BUTTON.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (getTime() != 0L) {
                        DateTimeField.this.editor.setTime(getTime());
                        GCOrganizer gco = (GCOrganizer) getTopLevelAncestor();
                        CacheBean bean = gco.getCacheBean();
                        String title =
                            I18N.get("DateTimeField.EditorPanel.dialog.title.0",
                                     bean.getWaypoint());
                        int option =
                            JOptionPane.showOptionDialog(gco,
                                                         DateTimeField.this.editor,
                                                         title,
                                                         JOptionPane.OK_CANCEL_OPTION,
                                                         JOptionPane.PLAIN_MESSAGE,
                                                         null, null, null);
                        if (option == JOptionPane.OK_OPTION) {
                            long time = DateTimeField.this.editor.getTime();
                            setTime(time);
                            bean.setFoundOn(time);
                            gco.fireCacheDataUpdate();
                        }
                        returnFocus();
                    }
                }
            });
    }

    /** */
    @Override
    public void updateUI() {
        super.updateUI();

        if (this.editor != null) {
            SwingUtilities.updateComponentTreeUI(this.editor);
        }
    }

    //
    //
    //

    /**
     * Sets the time text.
     *
     * @param time
     */
    void setTime(long time) {
        this.time = time;
        FIELD.setText("" + FormatUtil.formatDateTime(time));
        FIELD.setCaretPosition(0);
    }

    /**
     * Returns the time text.
     *
     */
    long getTime() {
        return this.time;
    }

    /**
     * Decides whether this field is editable or not.
     *
     * @param editable
     */
    void setEditable(boolean editable) {
        FIELD.setEditable(editable);
    }

    /**
     * Tests whether this field is editable or not.
     *
     */
    boolean isEditable() {
        return FIELD.isEditable();
    }

    //
    //
    //

    /**
     * Returns the focus owner at the moment when the button is clicked.
     *
     */
    private void saveFocusOwner() {
        this.focusOwner =
            KeyboardFocusManager.getCurrentKeyboardFocusManager().
            getFocusOwner();
    }

    /**
     * Returns the focus to the previous owner.
     *
     */
    private void returnFocus() {
        EventQueue.invokeLater(new Runnable() {
                /** */
                @Override
                public void run() {
                    if (DateTimeField.this.focusOwner != null) {
                        DateTimeField.this.focusOwner.requestFocusInWindow();
                    }
                }
            });
    }

    //
    //
    //

    /**
     *
     */
    private static class EditorPanel extends JPanel {
        //
        private JSpinner dateSpinner = null;

        /**
         *
         */
        EditorPanel() {
            this.dateSpinner = new JSpinner(new SpinnerDateModel());
            this.dateSpinner.setEditor(new DateEditor(this.dateSpinner,
                                                      "yyyy/MM/dd HH:mm"));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.gridwidth = GridBagConstraints.RELATIVE;
            add(new JLabel(I18N.get("DateTimeField.EditorPanel.label.0")), gbc);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            add(this.dateSpinner, gbc);
        }

        //
        //
        //

        /**
         * Returns time in millis.
         *
         */
        long getTime() {
            return ((SpinnerDateModel) this.dateSpinner.getModel()).
                getDate().getTime();
        }

        /**
         * Sets time in millis.
         *
         * @param time
         */
        void setTime(long time) {
            ((SpinnerDateModel) this.dateSpinner.getModel()).
                setValue(new Date(time));
        }
    }
}
