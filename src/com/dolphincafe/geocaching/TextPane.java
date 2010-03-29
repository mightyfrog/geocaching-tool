package com.dolphincafe.geocaching;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 *
 */
class TextPane extends JTextPane {
    //
    private final Map<String, String> SMILEY_MAP =
        new HashMap<String, String>();

    /**
     *
     */
    TextPane() {
        setContentType("text/html");
        setEditable(false);
        setDragEnabled(true);


        addMouseListener(new MouseAdapter() {
                /** */
                @Override
                public void mousePressed(MouseEvent evt) {
                    if (evt.isPopupTrigger()) {
                        showPopup(evt);
                    }
                }

                /** */
                @Override
                public void mouseReleased(MouseEvent evt) {
                    if (evt.isPopupTrigger()) {
                        showPopup(evt);
                    }
                }
            });
        addHyperlinkListener(new HyperlinkListener() {
                /** */
                @Override
                public void hyperlinkUpdate(HyperlinkEvent evt) {
                    if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        try {
                            Desktop.getDesktop().browse(evt.getURL().toURI());
                        } catch (URISyntaxException e) {
                        } catch (IOException e) {
                        }
                    }
                }
            });

        SMILEY_MAP.put("[:)]", getIconResourceURL("icon_smile.gif"));
        SMILEY_MAP.put("[:(]", getIconResourceURL("icon_smile_sad.gif"));
        SMILEY_MAP.put("[:D]", getIconResourceURL("icon_smile_big.gif"));
        SMILEY_MAP.put("[8)]", getIconResourceURL("icon_smile_shy.gif"));
        SMILEY_MAP.put("[8D]", getIconResourceURL("icon_smile_cool.gif"));
        SMILEY_MAP.put("[:O]", getIconResourceURL("icon_smile_shocked.gif"));
        SMILEY_MAP.put("[:I]", getIconResourceURL("icon_smile_blush.gif"));
        SMILEY_MAP.put("[:(!]", getIconResourceURL("icon_smile_angry.gif"));
        SMILEY_MAP.put("[:P]", getIconResourceURL("icon_smile_tongue.gif"));
        SMILEY_MAP.put("[xx(]", getIconResourceURL("icon_smile_dead.gif"));
        SMILEY_MAP.put("[}:)]", getIconResourceURL("icon_smile_evil.gif"));
        SMILEY_MAP.put("[|)]", getIconResourceURL("icon_smile_sleepy.gif"));
        SMILEY_MAP.put("[;)]", getIconResourceURL("icon_smile_wink.gif"));
        SMILEY_MAP.put("[:X]", getIconResourceURL("icon_smile_kisses.gif"));
        SMILEY_MAP.put("[:o)]", getIconResourceURL("icon_smile_clown.gif"));
        SMILEY_MAP.put("[^]", getIconResourceURL("icon_smile_approve.gif"));
        SMILEY_MAP.put("[B)]", getIconResourceURL("icon_smile_blackeye.gif"));
        SMILEY_MAP.put("[V]", getIconResourceURL("icon_smile_disapprove.gif"));
        SMILEY_MAP.put("[8]", getIconResourceURL("icon_smile_8ball.gif"));
        SMILEY_MAP.put("[?]", getIconResourceURL("icon_smile_question.gif"));
    }

    /** */
    @Override
    public void setText(String text) {
        if (text == null) {
            super.setText(null);
        } else {
            for (String key : SMILEY_MAP.keySet()) {
                text = text.replace(key, SMILEY_MAP.get(key));
            }
            // JDK bug java.lang.ArrayIndexOutOfException, test w/ GCRR8V
            super.setText(text); 
            setCaretPosition(0);
        }
    }

    //
    //
    //

    /**
     *
     * @param fileName icon file name
     */
    private String getIconResourceURL(String fileName) {
        return "<img src=\"" +
            getClass().getResource("icons/" + fileName) +
            "\"/>";
    }

    /**
     *
     */
    private void showPopup(MouseEvent evt) {
        JPopupMenu popup = getPopupMenu();
        popup.show(this, evt.getX(), evt.getY());
    }

    /**
     *
     */
    private JPopupMenu getPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem mi = new JMenuItem() {
                {
                    setText(I18N.get("DescriptionPane.menu.0")); // CHANGE CHANGE
                    addActionListener(new ActionListener() {
                            /** */
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                copy();
                            }
                        });
                }
            };
        mi.setEnabled(getCaret().getDot() != getCaret().getMark());
        popup.add(mi);

        return popup;
    }
}
