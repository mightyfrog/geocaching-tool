package com.dolphincafe.geocaching;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 *
 */
class FontChooserPanel extends JPanel {
    //
    private JComboBox cmbBox = new JComboBox();
    private JComboBox sizeCmbBox =
        new JComboBox(new Integer[]{9, 10, 11, 12, 14, 16, 18});

    private JTextArea textArea = new TextArea() {
            {
                // do not remove "\n". it's used as buffer to accomodate
                // different-size fonts
                setText(I18N.get("FontChooserPanel.text.0") + "\n");
            }

            /** */
            @Override
            public Color getBackground() {
                return FontChooserPanel.this.getBackground();
            }
        };

    /**
     *
     */
    public FontChooserPanel(final GCOrganizer owner) {
        owner.toggleCursor(true);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 3, 3, 3);

        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts =
            ge.getAvailableFontFamilyNames(Pref.getLocale());
        this.cmbBox = new JComboBox(fonts);
        this.cmbBox.setMaximumRowCount(10);
        this.cmbBox.setSelectedItem(Pref.getFont().getFamily());
        this.sizeCmbBox.setSelectedItem(Pref.getFont().getSize());
        
        ItemListener l = new ItemListener() {
                /** */
                @Override
                public void itemStateChanged(ItemEvent evt) {
                    updateSampleFont();
                }
            };
        this.cmbBox.addItemListener(l);
        this.sizeCmbBox.addItemListener(l);
        
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.textArea.setEditable(false);
        add(new JScrollPane(this.textArea), gbc);
        gbc.gridwidth = 1;
        add(new JLabel(I18N.get("FontChooserPanel.label.0")), gbc);
        add(this.cmbBox, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        add(new JLabel(I18N.get("FontChooserPanel.label.1")), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(this.sizeCmbBox, gbc);
        owner.toggleCursor(false);
    }

    /** */
    @Override
    public void addNotify() {
        super.addNotify();

        SwingUtilities.updateComponentTreeUI(this);
    }

    //
    //
    //

    /**
     *
     */
    int getFontCount() {
        return this.cmbBox.getItemCount();
    }

    /**
     *
     */
    Font getSelectedFont() {
        String name = (String) this.cmbBox.getSelectedItem();
        int size = (Integer) this.sizeCmbBox.getSelectedItem();

        return new Font(name, Font.PLAIN, size);
    }

    //
    //
    //

    /**
     *
     */
    private void updateSampleFont() {
        this.textArea.setFont(getSelectedFont());
        this.textArea.updateUI();
    }
}
