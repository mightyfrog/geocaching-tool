package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 *
 */
class HexCoderPanel extends JPanel implements DocumentListener {
    //
    private TextArea topTextArea = null;
    private TextArea bottomTextArea = null;

    //
    private JButton encodeButton = null;
    private JButton decodeButton = null;

    // should handle both hex and dec
    private final Pattern PATTERN = Pattern.compile("&#x[\\w]*;");

    /**
     * Creates HexCoderPanel.
     *
     */
    public HexCoderPanel() {
        setLayout(new GridBagLayout());

        this.topTextArea = new TextArea();
        this.bottomTextArea = new TextArea();
        this.topTextArea.setLineWrap(true);
        this.bottomTextArea.setLineWrap(true);
        this.topTextArea.getDocument().addDocumentListener(this);
        this.bottomTextArea.getDocument().addDocumentListener(this);

        this.encodeButton = new JButton(new AbstractAction() {
                {
                    putValue(SMALL_ICON, Icons.DOWN);
                    putValue(SHORT_DESCRIPTION, I18N.get("HexCoderPanel.button.text.0"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    encode(getDecodedText());
                }
            });
        this.encodeButton.setEnabled(false);
        //this.encodeButton.setMnemonic('E');

        this.decodeButton = new JButton(new AbstractAction() {
                {
                    putValue(SMALL_ICON, Icons.UP);
                    putValue(SHORT_DESCRIPTION, I18N.get("HexCoderPanel.button.text.1"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    decode(getEncodedText());
                }
            });
        this.decodeButton.setEnabled(false);
        //this.decodeButton.setMnemonic('D');

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.setBorder(BorderFactory.
                     createTitledBorder(I18N.get("HexCoderPanel.border.title.0")));
        p1.add(new JScrollPane(this.topTextArea));
        add(p1, gbc);
        gbc.weighty = 0.0;
        add(createButtonPanel(), gbc);
        gbc.weighty = 1.0;
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        p2.setBorder(BorderFactory.
                     createTitledBorder(I18N.get("HexCoderPanel.border.title.1")));
        p2.add(new JScrollPane(this.bottomTextArea));
        add(p2, gbc);

        setPreferredSize(new Dimension(500, 300));
    }

    /** */
    @Override
    public void changedUpdate(DocumentEvent evt) {
        // no-op
    }

    /** */
    @Override
    public void insertUpdate(DocumentEvent evt) {
        updateButtons();
    }

    /** */
    @Override
    public void removeUpdate(DocumentEvent evt) {
        updateButtons();
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
                    HexCoderPanel.this.topTextArea.requestFocusInWindow();
                }
            }).start();
        SwingUtilities.updateComponentTreeUI(this);
    }

    //
    //
    //

    /**
     *
     */
    private void updateButtons() {
        if (this.bottomTextArea.getText() != null &&
            this.bottomTextArea.getText().length() != 0) {
            this.decodeButton.setEnabled(true);
        } else {
            this.decodeButton.setEnabled(false);
        }
        if (this.topTextArea.getText() != null &&
            this.topTextArea.getText().length() != 0) {
            this.encodeButton.setEnabled(true);
        } else {
            this.encodeButton.setEnabled(false);
        }
    }

    /**
     *
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.weightx = 1.0;

        panel.add(Box.createHorizontalBox(), gbc);
        gbc.weightx = 0.0;
        panel.add(this.encodeButton, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        panel.add(this.decodeButton, gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        panel.add(Box.createHorizontalBox(), gbc);

        return panel;
    }

    /**
     *
     */
    private String getEncodedText() {
        return this.bottomTextArea.getText();
    }

    /**
     *
     */
    private String getDecodedText() {
        return this.topTextArea.getText();
    }

    /**
     *
     * @param text
     */
    private void decode(String text) {
        Matcher m = PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String s = m.group();
            int index = text.indexOf("x");
            s = s.substring(index, s.length() - 1);
            sb.appendCodePoint(Integer.decode("0" + s));
        }

        this.topTextArea.setText(sb.toString());
    }

    /**
     *
     * @param text
     */
    private void encode(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            int codePoint = Character.codePointAt(text, i);
            sb.append("&#x" + Integer.toHexString(codePoint) + ";");
        }

        this.bottomTextArea.setText(sb.toString());
    }
}
