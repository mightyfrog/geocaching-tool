package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 *
 */
class NotePanel extends JPanel {
    //
    private final TextArea TA = new TextArea() {
            {
                addMouseListener(new MouseAdapter() {
                        /** */
                        @Override
                        public void mouseClicked(MouseEvent evt) {
                            if (isEnabled() && evt.getClickCount() == 2) {
                                editNote();
                            }
                        }
                    });
            }

            /** */
            @Override
            public Color getBackground() {
                return NotePanel.this.getBackground();
            }
        };

    private final Map<String, String> NOTE_MAP = new HashMap<String, String>();

    //
    private String waypoint = null;

    //
    private Component focusOwner = null;

    /**
     *
     */
    public NotePanel() {
        String title = I18N.get("NotePanel.border.title.0");
        setBorder(BorderFactory.createTitledBorder(title));
        setLayout(new BorderLayout());

        TA.setEditable(false);
        TA.setFocusable(false);
        TA.setLineWrap(true);
        TA.setWrapStyleWord(true);
        TA.setDragEnabled(true);

        add(new JScrollPane(TA));
    }

    //
    //
    //

    /**
     *
     */
    void reload(String waypoint) {
        this.waypoint = waypoint;
        String note = NOTE_MAP.get(this.waypoint);
        if (note == null) {
            note = load();
        }
        TA.setText(note);
        TA.setCaretPosition(0);
        NOTE_MAP.put(this.waypoint, note);
    }

    //
    //
    //

    /**
     *
     */
    private void editNote() {
        saveFocusOwner();

        if (this.waypoint == null) {
            returnFocus();
            return;
        }

        TextArea editTextArea = new TextArea() {
                {
                    setPreferredSize(new Dimension(600, 300));
                    setLineWrap(true);
                    setWrapStyleWord(true);
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
                                requestFocusInWindow();
                            }
                        }).start();
                }

            };
        if (TA.isEnabled()) {
            editTextArea.setText(TA.getText());
        } else {
            editTextArea.setText(null);
        }
        CacheBean bean = GPXParser.singleton().getCacheBean(this.waypoint);
        String name = bean.getName();
        String placedBy = bean.getPlacedBy();
        String title =
            I18N.get("NotePanel.dialog.title.0", this.waypoint, name, placedBy);
        int option = JOptionPane.showOptionDialog(getTopLevelAncestor(),
                                                  new JScrollPane(editTextArea),
                                                  title,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  null, null, null);
        if (option == JOptionPane.OK_OPTION) {
            String text = editTextArea.getText();
            TA.setText(text);
            TA.setEnabled(true);
            TA.setCaretPosition(0);
            store(text);
        }

        returnFocus();
    }

    /**
     *
     * @param text
     */
    private void store(String text) {
        NOTE_MAP.put(this.waypoint, text);
        File file = new File("data/" + this.waypoint);
        if (!file.exists() && !file.mkdirs()) {
            // TODO: do somethig here
            return;
        }
        ObjectOutputStream oos = null;
        try {
            file = new File(file, "note.ser");
            OutputStream os = new FileOutputStream(file);
            oos = new ObjectOutputStream(os);
            oos.writeObject(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     *
     *
     */
    private String load() {
        String str = null;
        File file = new File("data/" + this.waypoint + "/note.ser");
        if (file.exists()) {
            InputStream is = null;
            ObjectInputStream ois = null;
            try {
                is = new FileInputStream(file);
                ois = new ObjectInputStream(is);
                str = (String) ois.readObject();
            } catch (ClassNotFoundException e) {
            } catch (IOException e) {
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        return str;
    }

    /**
     * Returns the focus owner at the moment when the button is clicked.
     *
     */
    private void saveFocusOwner() {
        this.focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().
            getFocusOwner();
    }

    /**
     * Returns the focus to the previous owner.
     *
     */
    private void returnFocus() {
        EventQueue.invokeLater(new Runnable() {
                /** */
                @Override
                public void run() {
                    NotePanel.this.focusOwner.requestFocusInWindow();
                }
            });
    }
}
