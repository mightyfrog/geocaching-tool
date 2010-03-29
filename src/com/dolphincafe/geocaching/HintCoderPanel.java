package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 */
class HintCoderPanel extends JPanel {
    //
    private JButton button = null;
    private TextArea textArea = null;

    /**
     *
     */
    HintCoderPanel() {
        setLayout(new BorderLayout());

        this.button = new JButton(new AbstractAction() {
                { 
                    putValue(NAME, I18N.get("HintCoderPanel.button.text.0"));
                    putValue(SMALL_ICON, Icons.TOGGLE_HINT);
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    toggle();
                }
            });

        this.textArea = new TextArea();
        this.textArea.setLineWrap(true);

        JScrollPane sp = new JScrollPane();
        sp.setPreferredSize(new Dimension(300, 200));
        sp.setViewportView(this.textArea);
        add(sp);
        add(this.button, BorderLayout.SOUTH);
    }

    /** */
    @Override
    public void addNotify() {
        super.addNotify();

        new Thread(new Runnable() { // hack
                /** */
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                    HintCoderPanel.this.textArea.requestFocusInWindow();
                }
            }).start();
        SwingUtilities.updateComponentTreeUI(this);
    }

    //
    //
    //

    /**
     * Toggle hint encoding.
     *
     */
    private void toggle() {
        String text = this.textArea.getText();
        if (text == null) {
            return;
        }
        char[] c = text.toCharArray();
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

        this.textArea.setText(new String(c));
    }
}

