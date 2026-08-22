/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.jacksum.gui;

import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import net.jacksum.formats.Encoding;

/**
 *
 * @author Johann
 */
public class EncodingRenderer extends DefaultListCellRenderer {

    private final ResourceBundle iso3166;

    public EncodingRenderer(ResourceBundle iso3166) {
        super();
        this.iso3166 = iso3166;
    }

    EncodingRenderer() {
        super();
        this.iso3166 = null;
    }
    
    public ResourceBundle getISO3166ResourceBundle() {
        return iso3166;
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);

        if (value != null) {            
            label.setText(((Encoding)value).getDescription());
        }
        return label;
    }

}
