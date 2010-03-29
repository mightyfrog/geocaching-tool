package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton ;
import javax.swing.JPanel;

/**
 * Groundspeak gpx hint encoding/decoding text filed.
 *
 * @author Shigehiro Soejima
 */
class HintField extends JPanel {
    private final DataField FIELD = new DataField();
    private final JButton BUTTON = new JButton(Icons.TOGGLE_HINT) {
            /** */
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = FIELD.getPreferredSize();
                dim.width = dim.height;

                return dim;
            }

            /** */
            @Override
            public void setPreferredSize(Dimension dim) {
            }
        };

    /**
     *
     */
    public HintField() {
        setLayout(new BorderLayout());

        BUTTON.setToolTipText(I18N.get("HintField.button.text.0"));
        BUTTON.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    toggle();
                }
            });

        add(FIELD);
        add(BUTTON, BorderLayout.EAST);
    }

    //
    //
    //

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

    /**
     * Sets the hint text.
     *
     * @param hint
     */
    void setHint(String hint) {
        FIELD.setText(hint);
        FIELD.setCaretPosition(0);
    }

    /**
     * Returns the hint text.
     *
     */
    String getHint() {
        return FIELD.getText();
    }

    /**
     * Toggle hint encoding.
     *
     */
    void toggle() {
        char[] c = getHint().toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (!Character.isLetter(c[i])) {
                continue;
            }
            int n = (int) c[i];
            if (n <= (int) 'M') {
                c[i] = (char) (n + 13);
            } else if (n <= (int) 'Z') {
                c[i] = (char) (n - 13);
            } else if (n <= 'm') {
                c[i] = (char) (n + 13);
            } else {
                c[i] = (char) (n - 13);
            }
        }

        setHint(new String(c));
    }
}
