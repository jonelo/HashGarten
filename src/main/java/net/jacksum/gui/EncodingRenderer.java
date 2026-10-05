/*

  HashGarten 0.20.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2026 Dipl.-Inf. (FH) Johann N. Löfflmann,
  All Rights Reserved, <https://jacksum.net>.

  This program is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License, or (at your option) any later
  version.

  This program is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
  details.

  You should have received a copy of the GNU General Public License along with
  this program. If not, see <https://www.gnu.org/licenses/>.

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
