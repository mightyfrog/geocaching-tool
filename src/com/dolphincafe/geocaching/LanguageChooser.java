package com.dolphincafe.geocaching;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 */
class LanguageChooser extends JPanel {
    //
    private final JComboBox CB;

    /**
     *
     */
    public LanguageChooser() {
        setLayout(new BorderLayout(2, 0));

        CB = new JComboBox();
        final ListCellRenderer renderer = CB.getRenderer();
        CB.setRenderer(new ListCellRenderer() {
                /** */
                @Override
                public Component getListCellRendererComponent(JList list,
                                                              Object value,
                                                              int index,
                                                              boolean isSelected,
                                                              boolean cellHasFocus) {
                    Locale l = (Locale) value;
                    String text = null;
                    if (l == null || l == Locale.ROOT) {
                        text = I18N.get("LanguageChooser.item.0");
                    } else {
                        text = l.getDisplayName(Pref.getLocale());
                    }

                    return renderer.getListCellRendererComponent(list, text,
                                                                 index,
                                                                 isSelected,
                                                                 cellHasFocus);
                }
            });
        add(CB);
        fillItems();
    }

    //
    //
    //

    /**
     *
     */
    String getSelectedItem() {
        if (CB.getSelectedItem() == null || CB.getSelectedIndex() == 0) {
            return "";
        }
        return CB.getSelectedItem().toString();
    }

    /**
     *
     * @param locale
     */
    void setSelectedItem(String locale) {
        CB.setSelectedItem(locale);
    }

    //
    //
    //

    /**
     *
     */
    private void fillItems() {
        Locale[] locales = I18N.getAvailableLocales();
        for (Locale l : locales) {
            CB.addItem(l);
        }
        CB.setSelectedItem(Pref.getLocale());
    }
}
