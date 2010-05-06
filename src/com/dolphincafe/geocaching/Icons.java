package com.dolphincafe.geocaching;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 *
 */
class Icons {
    static final ImageIcon SIGN = createIcon("sign.png");
    // generic icons
    static final ImageIcon ABOUT = createIcon("about.png");
    static final ImageIcon BROWSER = createIcon("browser.png");
    static final ImageIcon COPY = createIcon("copy.png");
    static final ImageIcon DATE = createIcon("date.png");
    static final ImageIcon DOWN = createIcon("down.png");
    static final ImageIcon FILTER_CONFIG = createIcon("filter_config.png");
    static final ImageIcon FIND = createIcon("find.png");
    static final ImageIcon FRAME_LOGO = createIcon("frame_logo.png");
    static final ImageIcon MEMORY = createIcon("memory.png");
    static final ImageIcon PROFILE = createIcon("profile.png");
    static final ImageIcon TOGGLE_HINT = createIcon("toggle_hint.png");
    static final ImageIcon UP = createIcon("up.png");
    static final ImageIcon LOG_SEARCH = createIcon("log_search.png");
    static final ImageIcon CLEAR = createIcon("clear.png");

    // trackables
    //static final ImageIcon TB_COIN = createIcon("tb_coin.gif");
    //static final ImageIcon TB_DROP = createIcon("dropped_off.gif");
    //static final ImageIcon TB_PICKUP = createIcon("picked_up.gif");
    //static final ImageIcon TB_TRANSFER = createIcon("transfer.gif");
    //static final ImageIcon TB_DISCOVER = createIcon("icon_discovered.gif");

    // map logos
    static final ImageIcon GEOCACHING_MAP = createIcon("geocaching_map.png");
    static final ImageIcon GOOGLE_MAP = createIcon("google_map.png");
    static final ImageIcon YAHOO_MAP = createIcon("yahoo_map.png");

    // cache types
    static final ImageIcon CITO_CACHE = createIcon("cito.png");
    static final ImageIcon EARTH_CACHE = createIcon("earth.png");
    static final ImageIcon EVENT_CACHE = createIcon("event.png");
    static final ImageIcon LETTERBOX_CACHE = createIcon("letterbox.png");
    static final ImageIcon LOCLESS_CACHE = createIcon("locless.png");
    static final ImageIcon MEGA_CACHE = createIcon("mega.png");
    static final ImageIcon MULTI_CACHE = createIcon("multi.png");
    static final ImageIcon PARKING = createIcon("parking.png"); // NOT USED
    static final ImageIcon PROJECT_APE_CACHE = createIcon("project_ape.png");
    static final ImageIcon REF_POINT = createIcon("ref_point.png"); // NOT USED
    static final ImageIcon TRADITIONAL_CACHE = createIcon("traditional.png");
    static final ImageIcon UNKNOWN_CACHE = createIcon("unknown.png"); // quiz
    static final ImageIcon VIRTUAL_CACHE = createIcon("virtual.png");
    static final ImageIcon WEBCAM_CACHE = createIcon("webcam.png");
    static final ImageIcon WHERIGO_CACHE = createIcon("wherigo.png");
    static final ImageIcon LOST_AND_FOUND_CACHE = createIcon("lost_and_found.png");

    // do not shuffle these smiley icons
    static final ImageIcon SMILEY_SMILE = createIcon("icon_smile.gif");
    static final ImageIcon SMILEY_SMILE_BIG = createIcon("icon_smile_big.gif");
    static final ImageIcon SMILEY_SMILE_COOL = createIcon("icon_smile_cool.gif");
    static final ImageIcon SMILEY_SMILE_BLUSH = createIcon("icon_smile_blush.gif");
    static final ImageIcon SMILEY_SMILE_TONGUE = createIcon("icon_smile_tongue.gif");
    static final ImageIcon SMILEY_SMILE_EVIL = createIcon("icon_smile_evil.gif");
    static final ImageIcon SMILEY_SMILE_WINK = createIcon("icon_smile_wink.gif");
    static final ImageIcon SMILEY_SMILE_CLOWN = createIcon("icon_smile_clown.gif");
    static final ImageIcon SMILEY_SMILE_BLACK_EYE = createIcon("icon_smile_blackeye.gif");
    static final ImageIcon SMILEY_SMILE_EIGHT_BALL = createIcon("icon_smile_8ball.gif");
    static final ImageIcon SMILEY_SMILE_FROWN = createIcon("icon_smile_sad.gif");
    static final ImageIcon SMILEY_SMILE_SHY = createIcon("icon_smile_shy.gif");
    static final ImageIcon SMILEY_SMILE_SHOCKED = createIcon("icon_smile_shock.gif");
    static final ImageIcon SMILEY_SMILE_ANGRY = createIcon("icon_smile_angry.gif");
    static final ImageIcon SMILEY_SMILE_DEAD = createIcon("icon_smile_dead.gif");
    static final ImageIcon SMILEY_SMILE_SLEEPY = createIcon("icon_smile_sleepy.gif");
    static final ImageIcon SMILEY_SMILE_KISSES = createIcon("icon_smile_kisses.gif");
    static final ImageIcon SMILEY_SMILE_APPROVE = createIcon("icon_smile_approve.gif");
    static final ImageIcon SMILEY_SMILE_DISAPPROVE = createIcon("icon_smile_disapprove.gif");
    static final ImageIcon SMILEY_SMILE_QUESTION = createIcon("icon_smile_question.gif");

    // stars large
    static final ImageIcon STAR1 = createIcon("star1.png");
    static final ImageIcon STAR15 = createIcon("star15.png");
    static final ImageIcon STAR2 = createIcon("star2.png");
    static final ImageIcon STAR25 = createIcon("star25.png");
    static final ImageIcon STAR3 = createIcon("star3.png");
    static final ImageIcon STAR35 = createIcon("star35.png");
    static final ImageIcon STAR4 = createIcon("star4.png");
    static final ImageIcon STAR45 = createIcon("star45.png");
    static final ImageIcon STAR5 = createIcon("star5.png");

    // color icon
    static final ImageIcon BLUE_SQUARE = createColorIcon(Color.BLUE);
    static final ImageIcon MAGENTA_SQUARE = createColorIcon(new Color(255, 0, 255)); // magenta
    static final ImageIcon ORANGE_SQUARE = createColorIcon(new Color(255, 165, 0)); // orange
    static final ImageIcon TEAL_SQUARE = createColorIcon(new Color(0, 128, 128)); // teal


    // empty icon
    static final ImageIcon EMPTY = new ImageIcon() {
            /** */
            @Override
            public int getIconHeight() {
                return 16;
            }

            /** */
            @Override
            public int getIconWidth() {
                return 16;
            }
        };

    //
    static final JLabel LABEL = new JLabel();

    /**
     *
     * @param type
     */
    static ImageIcon getCacheTypeIcon(int type) {
        ImageIcon icon = null;
        switch (type) {
        case CacheBean.TYPE_TRADITIONAL:
            icon = TRADITIONAL_CACHE;
            break;
        case CacheBean.TYPE_MULTI:
            icon = MULTI_CACHE;
            break;
        case CacheBean.TYPE_UNKNOWN:
            icon = UNKNOWN_CACHE;
            break;
        case CacheBean.TYPE_WEBCAM:
            icon = WEBCAM_CACHE;
            break;
        case CacheBean.TYPE_VIRTUAL:
            icon = VIRTUAL_CACHE;
            break;
        case CacheBean.TYPE_EVENT:
            icon = EVENT_CACHE;
            break;
        case CacheBean.TYPE_EARTH:
            icon = EARTH_CACHE;
            break;
        case CacheBean.TYPE_LETTERBOX_HYBRID:
            icon = LETTERBOX_CACHE;
            break;
        case CacheBean.TYPE_CITO:
            icon = CITO_CACHE;
            break;
        case CacheBean.TYPE_MEGA:
            icon = MEGA_CACHE;
            break;
        case CacheBean.TYPE_PROJECT_APE:
            icon = PROJECT_APE_CACHE;
            break;
        case CacheBean.TYPE_LOCATIONLESS:
            icon = LOCLESS_CACHE;
            break;
        }

        return icon;
    }
    
    /**
     *
     * @param smiley
     */
    static ImageIcon getSmileyIcon(int smiley) {
        ImageIcon icon = null;
        switch (smiley) {
        case CacheBean.SMILEY_SMILE:
            icon = SMILEY_SMILE;
            break;
        case CacheBean.SMILEY_SMILE_BIG:
            icon = SMILEY_SMILE_BIG;
            break;
        case CacheBean.SMILEY_SMILE_COOL:
            icon = SMILEY_SMILE_COOL;
            break;
        case CacheBean.SMILEY_SMILE_BLUSH:
            icon = SMILEY_SMILE_BLUSH;
            break;
        case CacheBean.SMILEY_SMILE_TONGUE:
            icon = SMILEY_SMILE_TONGUE;
            break;
        case CacheBean.SMILEY_SMILE_EVIL:
            icon = SMILEY_SMILE_EVIL;
            break;
        case CacheBean.SMILEY_SMILE_WINK:
            icon = SMILEY_SMILE_WINK;
            break;
        case CacheBean.SMILEY_SMILE_CLOWN:
            icon = SMILEY_SMILE_CLOWN;
            break;
        case CacheBean.SMILEY_SMILE_BLACK_EYE:
            icon = SMILEY_SMILE_BLACK_EYE;
            break;
        case CacheBean.SMILEY_SMILE_EIGHT_BALL:
            icon = SMILEY_SMILE_EIGHT_BALL;
            break;
        case CacheBean.SMILEY_SMILE_FROWN:
            icon = SMILEY_SMILE_FROWN;
            break;
        case CacheBean.SMILEY_SMILE_SHY:
            icon = SMILEY_SMILE_SHY;
            break;
        case CacheBean.SMILEY_SMILE_SHOCKED:
            icon = SMILEY_SMILE_SHOCKED;
            break;
        case CacheBean.SMILEY_SMILE_ANGRY:
            icon = SMILEY_SMILE_ANGRY;
            break;
        case CacheBean.SMILEY_SMILE_DEAD:
            icon = SMILEY_SMILE_DEAD;
            break;
        case CacheBean.SMILEY_SMILE_SLEEPY:
            icon = SMILEY_SMILE_SLEEPY;
            break;
        case CacheBean.SMILEY_SMILE_KISSES:
            icon = SMILEY_SMILE_KISSES;
            break;
        case CacheBean.SMILEY_SMILE_APPROVE:
            icon = SMILEY_SMILE_APPROVE;
            break;
        case CacheBean.SMILEY_SMILE_DISAPPROVE:
            icon = SMILEY_SMILE_DISAPPROVE;
            break;
        case CacheBean.SMILEY_SMILE_QUESTION:
            icon = SMILEY_SMILE_QUESTION;
            break;
        }

        return icon;
    }

    /**
     *
     * @param icon
     */
    static ImageIcon getDisabledIcon(ImageIcon icon) {
        LABEL.setIcon(icon);
        return (ImageIcon) LABEL.getDisabledIcon();
    }

    /**
     *
     * @param icon
     * @param color
     */
    static ImageIcon getStrikethroughIcon(ImageIcon icon, Color color) {
        BufferedImage img = new BufferedImage(icon.getIconWidth(),
                                              icon.getIconHeight(),
                                              BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(icon.getImage(), 0, 0, null);
        g2.setColor(color);
        g2.drawLine(0, 7, 16, 7);
        g2.drawLine(0, 8, 16, 8);

        return new ImageIcon(img);
    }

    /**
     *
     * @param icon
     */
    static ImageIcon getArchivedIcon(ImageIcon icon) {
        return getStrikethroughIcon(icon, Color.RED);
    }

    //
    //
    //

    /**
     *
     * @param color
     */
    private static ImageIcon createColorIcon(final Color color) {
        return new ImageIcon() {
                /** */
                @Override
                public int getIconHeight() {
                    return 16;
                }
                
                /** */
                @Override
                public int getIconWidth() {
                    return 16;
                }
                
                /** */
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(color);
                    g.translate(x, y);
                    g.fillRect(0, 0, 16, 16);
                    g.setColor(color.darker());
                    g.drawRect(0, 0, 16, 16);
                    g.translate(-x, -y);
                }
            };
    }

    /**
     *
     * @param fileName
     */
    private static ImageIcon createIcon(String fileName) {
        ImageIcon icon = null;
        URL url = Icons.class.getResource("icons/" + fileName);
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            System.err.println("icons/" + fileName + " is missing");
            icon = createColorIcon(Color.RED);
        }

        return icon;
    }
}
