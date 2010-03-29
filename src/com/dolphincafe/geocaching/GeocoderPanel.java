package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Shigehiro Soejima
 */
class GeocoderPanel extends JPanel {
    //
    private static final String SERVICE_URL =
        "http://www.geocoding.jp/api/?q=";
    //
    private JComboBox addressCmb = new JComboBox();

    private JButton convertButton = null;
    private CoordinateField coordField = new CoordinateField();

    /**
     * Creates a GeocoderPanel.
     *
     */
    GeocoderPanel() {
        setLayout(new BorderLayout());

        add(createAddressPanel());
        add(this.coordField, BorderLayout.SOUTH);
    }

    /** */
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.width = 500;

        return dim;
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
                    GeocoderPanel.this.addressCmb.requestFocusInWindow();
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
    private JPanel createAddressPanel() {
        JPanel panel = new JPanel();

        this.convertButton = new JButton(new AbstractAction() {
                {
                    putValue(NAME, I18N.get("GeocoderPanel.button.text.0"));
                }

                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    getCoordinates();
                }
            });

        this.addressCmb.setEditable(true);
        this.addressCmb.addItemListener(new ItemListener() {
                /** */
                @Override
                public void itemStateChanged(ItemEvent evt) {
                    if (GeocoderPanel.this.addressCmb.isPopupVisible()) {
                        //GeocoderPanel.this.convertButton.doClick();
                        getCoordinates();
                    }
                }
            });

        panel.setLayout(new BorderLayout(6, 6));
        panel.add(new JLabel(I18N.get("GeocoderPanel.label.0")),
                  BorderLayout.WEST);
        panel.add(this.addressCmb);
        panel.add(this.convertButton, BorderLayout.EAST);

        return panel;
    }

    /**
     *
     * @param address
     */
    private void addAddress(String address) {
        this.addressCmb.addItem(address);
    }

    /**
     *
     * @param latitude
     * @param longitude
     */
    private void setCoordinates(double latitude, double longitude) {
        if (latitude * longitude == 0.0) {
            JOptionPane.showMessageDialog(this,
                                          I18N.get("GeocoderPanel.message.0"));
        }
        this.coordField.setCoordinates(latitude, longitude);
    }

    /**
     *
     */
    private void getCoordinates() {
        URL url = null;
        final ArrayList<String> DUPLICATES = new ArrayList<String>();
        BufferedInputStream bis = null;
        final double[] coords = new double[2];
        try {
            String address = (String) this.addressCmb.getSelectedItem();
            if (address == null) {
                this.addressCmb.requestFocusInWindow();
                return;
            }
            try {
                address = URLEncoder.encode(address, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // won't happen
            }
            url = new URL(SERVICE_URL + address);
            bis = new BufferedInputStream(url.openStream());
            SAXParserFactory f = SAXParserFactory.newInstance();
            SAXParser p = f.newSAXParser();
            p.parse(bis, new DefaultHandler() {
                    private boolean latEl = false;
                    private boolean lonEl = false;
                    private boolean choice = false;

                    /** */
                    @Override
                    public void startElement(String uri,
                                             String localName,
                                             String qName,
                                             Attributes attributes)
                        throws SAXException {
                        if (qName.equals("lat")) {
                            this.latEl = true;
                        } else if (qName.equals("lng")) {
                            this.lonEl = true;
                        } else if (qName.equals("choice")) {
                            this.choice = true;
                        }
                    }

                    /** */
                    @Override
                    public void characters(char[] ch, int start, int length)
                        throws SAXException {
                        if (this.choice) {
                            String s = new String(ch, start, length);
                            DUPLICATES.add(s);
                        } else {
                            if (this.lonEl) {
                                String s = new String(ch, start, length);
                                coords[1] = Double.parseDouble(s);
                            } else if (this.latEl) {
                                String s = new String(ch, start, length);
                                coords[0] = Double.parseDouble(s);
                            } else if (!this.latEl && !this.lonEl) {
                                return;
                            } else {
                                throw new SAXException(); // done
                            }
                        }
                        this.latEl = false;
                        this.lonEl = false;
                        this.choice = false;
                    }
                });
        } catch (ParserConfigurationException e) {
            e.printStackTrace(); // shouldn't happen
        } catch (IOException e) {
            // show error message here
        } catch (SAXException e) {
            // ignore, shouldn't happen
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }

        if (DUPLICATES.size() != 0) {
            this.addressCmb.removeAllItems();
            for (String s : DUPLICATES) {
                addAddress(s);
            }
            this.addressCmb.showPopup();
        } else {
            setCoordinates(coords[0], coords[1]);
            this.convertButton.requestFocusInWindow();
        }
    }
}
