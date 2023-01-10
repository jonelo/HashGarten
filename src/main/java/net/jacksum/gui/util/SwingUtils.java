/*

  HashGarten 0.12.0 - a GUI to calculate and verify hashes, powered by Jacksum
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
    // Credits: https://stackoverflow.com/questions/1248386/how-do-i-determine-which-monitor-a-swing-mouse-event-occurs-in
    public static void centerJFrameOnTheDisplayWhereTheMouseIs(JFrame jframe) {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPoint = pointerInfo.getLocation();

        GraphicsEnvironment e
                = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GraphicsDevice[] devices = e.getScreenDevices();

        Rectangle currentDisplayBounds = null;
        int displayTotalWidth = 0;
        int currentDisplayTotalWidth = 0;

        // now get the configurations for each device
        for (GraphicsDevice device : devices) {

            GraphicsConfiguration[] configurations
                    = device.getConfigurations();
            for (GraphicsConfiguration config : configurations) {
                Rectangle gcBounds = config.getBounds();

                displayTotalWidth += gcBounds.width;
                if (gcBounds.contains(currentPoint)) {
                    currentDisplayBounds = gcBounds;
                    currentDisplayTotalWidth = displayTotalWidth;
                }
            }
        }

        if (currentDisplayBounds == null) {
            //not found, get the bounds for the default currentDisplayBounds
            GraphicsDevice device = e.getDefaultScreenDevice();
            currentDisplayBounds = device.getDefaultConfiguration().getBounds();
        }

        // some math to find the new point on the (multi) display where the mouse pointer actually is
        int x = Math.max(currentDisplayTotalWidth - currentDisplayBounds.width + (currentDisplayBounds.width / 2) - (jframe.getWidth() / 2), 0);
        int y = Math.max((currentDisplayBounds.height / 2) - (jframe.getHeight() / 2), 0);

        jframe.setLocation(new Point(x, y));
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
