/*

  HashGarten 0.13.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022-2023 Dipl.-Inf. (FH) Johann N. Löfflmann,
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
package net.jacksum.gui.util;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.UIManager;
import net.jacksum.gui.Main;

/**
 *
 * @author Johann N. Löfflmann
 */
public class SwingUtils {

    public static void setNimbusLookAndFeel() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
             for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }
    
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    /**
     * Make a JFrame appear at the center of the current mouse position.
     * @param jframe the JFrame instance
     */
    public static void centerJFrameAtCurrentMousePos(JFrame jframe) {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPoint = pointerInfo.getLocation();

        int x = Math.max(currentPoint.x - jframe.getWidth() / 2, 0);
        int y = Math.max(currentPoint.y - jframe.getHeight() / 2, 0);
        jframe.setLocation(new Point(x, y));
    }

    /**
     * Make a JFrame appear at the center of the screen where the mouse pointer
     * is currently located. Purpose: avoid that the window appear on an
     * unexpected place in a multi monitor environment.
     *
     * @param jframe the JFrame instance
     */
    // credit: https://stackoverflow.com/questions/4627553/show-jframe-in-a-specific-screen-in-dual-monitor-configuration
    public static void centerJFrameOnTheDisplayWhereTheMouseIs(JFrame jframe) {        
        
        // search for the screen device where the mouse pointer is
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();

        // get the mouse pointer        
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        //Point currentMousePoint = pointerInfo.getLocation();
   
        int deviceID = 0;
        // Get the configurations for each device
        boolean locationSet = false;
        for (GraphicsDevice device : gd) {

            GraphicsConfiguration gc = device.getDefaultConfiguration();
            Rectangle b = gc.getBounds();

/*
            Main.debug("Device #" + deviceID);
            Main.debug(String.format("  bounds.x=%s", b.x));
            Main.debug(String.format("  bounds.y=%s", b.y));
            Main.debug(String.format("  bounds.width=%s", b.width));
            Main.debug(String.format("  bounds.height=%s", b.height));
*/
            if (pointerInfo.getDevice() == device) {
/*
                Main.debug(String.format("  Mouse pointer found at device #%s", deviceID));
                Main.debug(String.format("  Mouse pointer is at (%s, %s)", currentMousePoint.x, currentMousePoint.y));
*/
                int x = b.x + (b.width / 2) - (jframe.getSize().width / 2);
                int y = b.y + (b.height / 2) - (jframe.getSize().height / 2);
                jframe.setLocation(x, y);        
                locationSet = true;
            }
            deviceID++;
        }
        if (!locationSet) {
            jframe.setLocationRelativeTo(null);
        }                    
    }

    public static void moveSelectedJListItemUp(JList jList, DefaultListModel model) {
        if (!jList.isSelectionEmpty()) {
            int pos = jList.getSelectedIndex();
            if (pos > 0) {
                swap(jList, model, pos, pos - 1);
                jList.ensureIndexIsVisible(pos - 1);
            }
        }
    }

    public static void moveSelectedJListItemDown(JList jList, DefaultListModel model) {
        if (!jList.isSelectionEmpty()) {
            int pos = jList.getSelectedIndex();
            //DefaultListModel<String> model = (DefaultListModel) fileList.getModel();
            if (pos < model.getSize() - 1) {
                swap(jList, model, pos, pos + 1);
                jList.ensureIndexIsVisible(pos + 1);
            }
        }
    }

    public static void moveSelectedJListItemToTop(JList jList, DefaultListModel model) {
        if (!jList.isSelectionEmpty()) {
            int pos = jList.getSelectedIndex();
            while (pos > 0) {
                swap(jList, model, pos, pos - 1);
                pos--;
            }
            jList.ensureIndexIsVisible(pos);
        }
    }

    public static void moveSelectedJListItemToBottom(JList jList, DefaultListModel model) {

        if (!jList.isSelectionEmpty()) {
            int pos = jList.getSelectedIndex();
            while (pos < model.getSize() - 1) {
                swap(jList, model, pos, pos + 1);
                pos++;
            }
            jList.ensureIndexIsVisible(pos);
        }
    }

    public static void removeSelectedJListItem(JList jList, DefaultListModel model) {
        if (!jList.isSelectionEmpty()) {
            // DefaultListModel<String> model = (DefaultListModel) fileList.getModel();
            int pos = jList.getSelectedIndex();
            model.remove(pos);
            // it was the last item
            if (pos == model.getSize()) {
                pos--;
            }
            if (model.getSize() > 0) {
                jList.setSelectedIndex(pos);
            }
        }
    }

    private static void swap(JList jList, DefaultListModel model, int oldpos, int newpos) {
        Object backup = model.get(newpos);
        model.set(newpos, model.get(oldpos));
        model.set(oldpos, backup);
        jList.setSelectedIndex(newpos);
    }

}
