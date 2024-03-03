/*

  HashGarten 0.15.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022-2024 Dipl.-Inf. (FH) Johann N. Löfflmann,
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
/*

  HashGarten 0.15.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022-2024 Dipl.-Inf. (FH) Johann N. Löfflmann,
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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JToggleButton;
import net.jacksum.JacksumAPI;
import net.jacksum.formats.Encoding;

/**
 *
 * @author Johann
 */
public class GUIHelper {
    public static DefaultComboBoxModel buildCharsetsComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        Map<String, Charset> charsets = Charset.availableCharsets();
        for (String charsetName : charsets.keySet()) {
            // charset name
            model.addElement(charsetName);
        }
        return model;
    }
    
    public static DefaultComboBoxModel getEncodingsComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Map<Encoding, String> map = JacksumAPI.getAvailableEncodings();
        for (Map.Entry<Encoding, String> entry : map.entrySet()) {
            String key = Encoding.encoding2String(entry.getKey()); // we store a String representation of the Encoding only
            model.addElement(key);
        }
        return model;
    }
    
    public static DefaultComboBoxModel getAlgorithmsComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Map<String, String> map = JacksumAPI.getAvailableAlgorithms();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            model.addElement(key);
        }
        // workaround for bug in Jacksum 3.3.0
        // sm3 not returned
        //if (!map.containsKey("sm3")) {
        //    model.addElement("sm3");
        //}
        return model;
    }
    
    public static void setIconOfToggleButton(JToggleButton toggleButton) {
        toggleButton.setIcon(toggleButton.isSelected()
                ? new javax.swing.ImageIcon(GUIHelper.class.getResource("/net/jacksum/gui/pix32x32/toggle-on-32x32.png"))
                : new javax.swing.ImageIcon(GUIHelper.class.getResource("/net/jacksum/gui/pix32x32/toggle-off-32x32.png")));
    }

    // since Jacksum controls stdout/stderr, we cannot simply use System.out or System.err
    public static void debug(String message) {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter("hashgarten.log", true))) {
            writer.append(message);
            writer.append("\n");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
