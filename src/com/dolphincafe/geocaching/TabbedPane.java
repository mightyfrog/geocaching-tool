package com.dolphincafe.geocaching;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 *
 * @author Shigehiro Soejima
 */
class TabbedPane extends JTabbedPane implements ChangeListener {
    private final CacheInfoPanel CACHE_INFO_PANEL = new CacheInfoPanel();

    private final JCheckBox RESIZE_CHK_BOX = new JCheckBox() {
            {
                setText(I18N.get("TabbedPane.checkbox.text.1"));
                setOpaque(false);
                setBorder(BorderFactory.createEmptyBorder(3, 2, 2, 2)); // tmp
                setSelected(Pref.getAutoResizeColumns());
            }

            /** */
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
                Pref.setAutoResizeColumns(selected);
                CACHE_INFO_PANEL.setAutoResizeColumns(selected);
            }

            /** */
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                setResizeCheckBoxRect(getBounds());
            }
        };

    private Point pressedPoint = null;
    private Rectangle resizeCheckBoxRect = null;

    private String waypoint = null;

    /**
     *
     */
    public TabbedPane() {
        addTab(CACHE_INFO_PANEL.getTitle(), CACHE_INFO_PANEL.getIcon(),
               CACHE_INFO_PANEL);

        addChangeListener(this);
        addMouseListener(new MouseAdapter() {
                /** */
                @Override
                public void mousePressed(MouseEvent evt) {
                    dispatchMouseEventToCheckBox(evt);
                }

                /** */
                @Override
                public void mouseReleased(MouseEvent evt) {
                    dispatchMouseEventToCheckBox(evt);
                }

                /** */
                @Override
                public void mouseExited(MouseEvent evt) {
                    dispatchMouseEventToCheckBox(evt);
                }
            });
        addMouseMotionListener(new MouseMotionAdapter() {
                /** */
                @Override
                public void mouseDragged(MouseEvent evt) {
                    dispatchMouseEventToCheckBox(evt);
                }

                /** */
                @Override
                public void mouseMoved(MouseEvent evt) {
                    dispatchMouseEventToCheckBox(evt);
                }
            });

        RESIZE_CHK_BOX.addMouseListener(new MouseAdapter() {
                /** */
                @Override
                public void mousePressed(MouseEvent evt) {
                    AbstractButton b = (AbstractButton) evt.getSource();
                    ButtonModel model = b.getModel();
                    setPressedPoint(evt.getPoint());
                    if (getResizeCheckBoxRect().contains(evt.getPoint())) {
                        model.setArmed(evt.getModifiers() == 16); // TODO: remove hack
                        model.setPressed(evt.getModifiers() == 16);
                        repaint(getResizeCheckBoxRect());
                    }
                }

                /** */
                @Override
                public void mouseReleased(MouseEvent evt) {
                    if (getResizeCheckBoxRect().contains(evt.getPoint())) {
                        AbstractButton b = (AbstractButton) evt.getSource();
                        ButtonModel model = b.getModel();
                        b.setSelected(b.isSelected()); // ???
                        model.setRollover(true);
                        repaint(getResizeCheckBoxRect());
                    }
                }

                /** */
                @Override
                public void mouseExited(MouseEvent evt) {
                    AbstractButton b = (AbstractButton) evt.getSource();
                    ButtonModel model = b.getModel();
                    model.setRollover(false);
                    repaint(getResizeCheckBoxRect());
                }
            });
        RESIZE_CHK_BOX.addMouseMotionListener(new MouseMotionAdapter() {
                /** */
                @Override
                public void mouseDragged(MouseEvent evt) {
                    AbstractButton b = (AbstractButton) evt.getSource();
                    ButtonModel model = b.getModel();
                    if (getResizeCheckBoxRect().contains(evt.getPoint()) &&
                        getResizeCheckBoxRect().contains(getPressedPoint())) {
                        if (evt.getModifiers() == 16) {
                            model.setArmed(true);
                            model.setPressed(true);
                        }
                    } else {
                        model.setArmed(false);
                        model.setPressed(false);
                        model.setRollover(false);
                    }
                    repaint(getResizeCheckBoxRect());
                }

                /** */
                @Override
                public void mouseMoved(MouseEvent evt) {
                    AbstractButton b = (AbstractButton) evt.getSource();
                    ButtonModel model = b.getModel();
                    if (getResizeCheckBoxRect().contains(evt.getPoint())) {
                        if (b.isRolloverEnabled() &&
                            !SwingUtilities.isLeftMouseButton(evt)) {
                            model.setRollover(true);
                        }
                    } else {
                        if (b.isRolloverEnabled()) {
                            model.setRollover(false);
                        }
                    }
                    repaint(getResizeCheckBoxRect());
                }
            });
    }

    /**
     *
     * @param file
     */
    public void addCaches(File file) {
        CACHE_INFO_PANEL.addCaches(file);
    }

    /**
     *
     * @param waypoint
     */
    public void update(String waypoint) {
        this.waypoint = waypoint;
        Object obj = getSelectedComponent();
        TabPanel tp = null;
        if (obj instanceof JScrollPane) {
            tp = (TabPanel) ((JScrollPane) obj).getViewport().getView();
        } else {
            tp = (TabPanel) obj;
        }
        tp.update(waypoint);
    }

    /** */
    @Override
    public void stateChanged(ChangeEvent evt) {
        RESIZE_CHK_BOX.setEnabled(getSelectedIndex() == 0);
        update(this.waypoint);
    }

    /** */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = getBounds().width;
        int y = 0;
        int w = RESIZE_CHK_BOX.getPreferredSize().width;
        int h = RESIZE_CHK_BOX.getPreferredSize().height;
        int gap = 3;
        x = x - w - gap;
        Rectangle rect = getBoundsAt(getTabCount() - 1);
        if (x < rect.x + rect.width) {
            x = rect.x + rect.width + 3;
        }
        SwingUtilities.paintComponent(g, RESIZE_CHK_BOX, getGlassPane(),
                                      x, y, w, h);
    }

    //
    //
    //

    /**
     *
     */
    boolean isAutoResizeColumns() {
        return RESIZE_CHK_BOX.isSelected();
    }

    /**
     *
     */
    String[] getFilteredWaypoints() {
        return CACHE_INFO_PANEL.getFilteredWaypoints();
    }

    //
    //
    //

    /**
     *
     * @param evt
     */
    protected void dispatchMouseEventToCheckBox(MouseEvent evt) {
        if (!RESIZE_CHK_BOX.isEnabled()) { // don't have to check both
            return;
        }
        evt = new MouseEvent(RESIZE_CHK_BOX, evt.getID(), evt.getWhen(),
                             evt.getModifiers(), evt.getX(), evt.getY(),
                             evt.getClickCount(), evt.isPopupTrigger(),
                             evt.getButton());
        RESIZE_CHK_BOX.dispatchEvent(evt);
    }

    //
    //
    //

    /**
     *
     */
    private Point getPressedPoint() {
        return this.pressedPoint;
    }

    /**
     *
     * @param pressedPoint
     */
    private void setPressedPoint(Point pressedPoint) {
        this.pressedPoint = pressedPoint;
    }

    /**
     *
     */
    private JComponent getGlassPane() {
        return (JComponent) getRootPane().getGlassPane();
    }

    /**
     *
     */
    private Rectangle getResizeCheckBoxRect() {
        return this.resizeCheckBoxRect;
    }

    /**
     *
     * @param resizeCheckBoxRect
     */
    private void setResizeCheckBoxRect(Rectangle resizeCheckBoxRect) {
        this.resizeCheckBoxRect = resizeCheckBoxRect;
    }
}
