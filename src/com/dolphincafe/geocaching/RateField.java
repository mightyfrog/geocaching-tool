package com.dolphincafe.geocaching;

import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

/**
 *
 */
class RateField extends JTextField {
    //
    private String rate = null;

    /**
     *
     */
    RateField() {
        setEditable(false);
    }

    /** */
    @Override
    public String getToolTipText() {
        return getText();
    }

    /** */
    @Override
    public void setText(String text) {
        this.rate = text;
        repaint();
    }

    /** */
    @Override
    public String getText() {
        return this.rate;
    }

    /** */
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        if (this.rate == null) {
            return;
        }

        Image img = null;
        if (isEnabled()) {
            if (this.rate.equals("1")) {
                img = Icons.STAR1.getImage();
            } else if (this.rate.equals("1.5")) {
                img = Icons.STAR15.getImage();
            } else if (this.rate.equals("2")) {
                img = Icons.STAR2.getImage();
            } else if (this.rate.equals("2.5")) {
                img = Icons.STAR25.getImage();
            } else if (this.rate.equals("3")) {
                img = Icons.STAR3.getImage();
            } else if (this.rate.equals("3.5")) {
                img = Icons.STAR35.getImage();
            } else if (this.rate.equals("4")) {
                img = Icons.STAR4.getImage();
            } else if (this.rate.equals("4.5")) {
                img = Icons.STAR45.getImage();
            } else if (this.rate.equals("5")) {
                img = Icons.STAR5.getImage();
            } else {
                // invalid rate
            }
        } else {
            if (this.rate.equals("1")) {
                img = Icons.getDisabledIcon(Icons.STAR1).getImage();
            } else if (this.rate.equals("1.5")) {
                img = Icons.getDisabledIcon(Icons.STAR15).getImage();
            } else if (this.rate.equals("2")) {
                img = Icons.getDisabledIcon(Icons.STAR2).getImage();
            } else if (this.rate.equals("2.5")) {
                img = Icons.getDisabledIcon(Icons.STAR25).getImage();
            } else if (this.rate.equals("3")) {
                img = Icons.getDisabledIcon(Icons.STAR3).getImage();
            } else if (this.rate.equals("3.5")) {
                img = Icons.getDisabledIcon(Icons.STAR35).getImage();
            } else if (this.rate.equals("4")) {
                img = Icons.getDisabledIcon(Icons.STAR4).getImage();
            } else if (this.rate.equals("4.5")) {
                img = Icons.getDisabledIcon(Icons.STAR45).getImage();
            } else if (this.rate.equals("5")) {
                img = Icons.getDisabledIcon(Icons.STAR5).getImage();
            } else {
                // invalid rate
            }
        }

        Rectangle rect = null;
        try {
            rect = modelToView(0);
        } catch (BadLocationException e) {
            // shouldn't happen
        }
        if (rect != null) {
            g.drawImage(img, rect.x, rect.y, this);
        }
    }
}
