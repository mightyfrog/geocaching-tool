package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 *
 */
class LogPanel extends JPanel {
    private static final String PATH =
        "http://www.geocaching.com/seek/log.aspx?LID=";

    //
    private final TextPane TA = new TextPane() {
            {
                addMouseListener(new MouseAdapter() {
                        /** */
                        @Override
                        public void mouseClicked(MouseEvent evt) {
                            if (isEnabled() && evt.getClickCount() == 2) {
                                open();
                            }
                        }
                    });
            }

            /** */
            @Override
            public Color getBackground() {
                return LogPanel.this.getBackground();
            }
        };

    private boolean isOpeningInBrowser = false;

    /**
     *
     */
    LogPanel() {
        String title = I18N.get("LogPanel.border.text.0");
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new BorderLayout());

        TA.setFocusable(false);

        add(new JScrollPane(TA));
    }

    /** */
    @Override
    public void updateUI() {
        super.updateUI();

        if (TA != null) { // updates html markup
            try {
                Document doc = TA.getDocument();
                setLog(doc.getText(0, doc.getLength()));
            } catch (BadLocationException e) {
                // shouldn't happen
                e.printStackTrace();
            }
        }
    }

    //
    //
    //

    /**
     *
     * @param log
     */
    void setLog(String log) {
        if (log == null) { // happens when .gpx of the cache you've not found is added
            TA.setText(null);
            return;
        }
        //String color = Integer.toHexString(0xffffff & getBackground().getRGB());
        log = "<span style=\"font-family: " + getFont().getFamily() +
            "; font-size:" + getFont().getSize() + "pt\">" + log + "</span>";
        TA.setText(log);
    }

    //
    //
    //

    /**
     *
     */
    private void open() {
        if (this.isOpeningInBrowser) {
            return;
        }
        this.isOpeningInBrowser = true;
        try {
            CacheBean bean =
                ((GCOrganizer) getTopLevelAncestor()).getCacheBean();
            if (bean == null || bean.getLogId() == 0L) {
                return;
            }
            String path = PATH + bean.getLogId();
            Desktop.getDesktop().browse(new URL(path).toURI());
        } catch (MalformedURLException e) {
        } catch (URISyntaxException e) {
        } catch (IOException e) {
        } finally {
            this.isOpeningInBrowser = false;
        }
    }

}
