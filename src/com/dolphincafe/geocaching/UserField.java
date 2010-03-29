package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import javax.swing.JButton ;
import javax.swing.JPanel;

/**
 *
 * @author Shigehiro Soejima
 */
class UserField extends JPanel {
    private final DataField FIELD = new DataField();
    private final JButton BUTTON = new JButton(Icons.PROFILE) {
            /** */
            @Override
            public Dimension getPreferredSize() {
                Dimension dim = FIELD.getPreferredSize();
                dim.width = dim.height;

                return dim;
            }

            /** */
            @Override
            public void setPreferredSize(Dimension dim) {
            }
        };

    /**
     *
     */
    UserField() {
        setLayout(new BorderLayout());

        add(FIELD);
        add(BUTTON, BorderLayout.EAST);

        BUTTON.setToolTipText(I18N.get("UserField.button.text.0"));
        BUTTON.addActionListener(new ActionListener() {
                /** */
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (!getUser().equals("")) {
                        viewProfile();
                    }
                }
            });
    }

    //
    //
    //

    /**
     * Sets the user text.
     *
     * @param user
     */
    void setUser(String user) {
        FIELD.setText(user);
        FIELD.setCaretPosition(0);
    }

    /**
     * Returns the user text.
     *
     */
    String getUser() {
        return FIELD.getText().trim();
    }

    /**
     * Decides whether this field is editable or not.
     *
     * @param editable
     */
    void setEditable(boolean editable) {
        FIELD.setEditable(editable);
    }

    /**
     * Tests whether this field is editable or not.
     *
     */
    boolean isEditable() {
        return FIELD.isEditable();
    }

    /**
     *
     */
    private void viewProfile() {
        String user = FIELD.getText();
        if (user == null) {
            return;
        }
        try {
            user = URLEncoder.encode(user, "iso-8859-1");
            String url = "http://www.geocaching.com/profile?u=" + user;
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // shadows UnsupportedEncodingException
            e.printStackTrace();
        }
    }
}
