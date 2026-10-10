/*

  HashGarten 0.21.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022-2026 Dipl.-Inf. (FH) Johann N. Löfflmann,
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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            // Nimbus is not available, so fall back to the look and feel of the system
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            net.jacksum.gui.GUIHelper.debug(String.format("Could not set the look and feel: %s", ex));
        }
        //</editor-fold>
    }
    
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            net.jacksum.gui.GUIHelper.debug(String.format("Could not set the look and feel: %s", ex));
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

    // The move methods work on all selected items, the file list allows a multi selection. The
    // items keep their order among each other, and they stay selected after the move.

    public static void moveSelectedJListItemUp(JList jList, DefaultListModel model) {
        int[] selected = jList.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        // an item can't move past the top, nor past a selected item above it that can't move
        int limit = 0;
        for (int k = 0; k < selected.length; k++) {
            if (selected[k] > limit) {
                swap(model, selected[k], selected[k] - 1);
                selected[k]--;
            }
            limit = selected[k] + 1;
        }
        jList.setSelectedIndices(selected);
        jList.ensureIndexIsVisible(selected[0]);
    }

    public static void moveSelectedJListItemDown(JList jList, DefaultListModel model) {
        int[] selected = jList.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        // an item can't move past the bottom, nor past a selected item below it that can't move
        int limit = model.getSize() - 1;
        for (int k = selected.length - 1; k >= 0; k--) {
            if (selected[k] < limit) {
                swap(model, selected[k], selected[k] + 1);
                selected[k]++;
            }
            limit = selected[k] - 1;
        }
        jList.setSelectedIndices(selected);
        jList.ensureIndexIsVisible(selected[selected.length - 1]);
    }

    public static void moveSelectedJListItemToTop(JList jList, DefaultListModel model) {
        int[] selected = jList.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        Object[] items = removeItems(model, selected);
        for (int k = 0; k < items.length; k++) {
            model.add(k, items[k]);
        }
        jList.setSelectionInterval(0, items.length - 1);
        jList.ensureIndexIsVisible(0);
    }

    public static void moveSelectedJListItemToBottom(JList jList, DefaultListModel model) {
        int[] selected = jList.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        Object[] items = removeItems(model, selected);
        int first = model.getSize();
        for (Object item : items) {
            model.addElement(item);
        }
        jList.setSelectionInterval(first, model.getSize() - 1);
        jList.ensureIndexIsVisible(model.getSize() - 1);
    }

    // removes the items at the given ascending indices and returns them in their order
    private static Object[] removeItems(DefaultListModel model, int[] indices) {
        Object[] items = new Object[indices.length];
        for (int k = indices.length - 1; k >= 0; k--) {
            items[k] = model.remove(indices[k]);
        }
        return items;
    }

    /**
     * Removes all selected items, not just the first one: the file list allows a multi selection.
     *
     * @param jList the list
     * @param model the model of the list
     */
    public static void removeSelectedJListItem(JList jList, DefaultListModel model) {
        int[] selected = jList.getSelectedIndices();
        if (selected.length == 0) {
            return;
        }
        // remove from the end, otherwise the remaining indices would shift away
        for (int i = selected.length - 1; i >= 0; i--) {
            model.remove(selected[i]);
        }
        // select what has taken the place of the first removed item
        int pos = Math.min(selected[0], model.getSize() - 1);
        if (pos >= 0) {
            jList.setSelectedIndex(pos);
        }
    }

    /**
     * Gives a text component a context menu with Copy. It copies the selected text, or the
     * whole text if nothing is selected.
     *
     * @param field the text component
     */
    public static void installCopyContextMenu(javax.swing.text.JTextComponent field) {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem copyItem = new javax.swing.JMenuItem("Copy");
        copyItem.addActionListener(e -> {
            String text = field.getSelectedText();
            if (text == null || text.isEmpty()) {
                text = field.getText();
            }
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(text), null);
        });
        menu.add(copyItem);
        // there is nothing to copy as long as nothing has been shown
        menu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                copyItem.setEnabled(field.getDocument().getLength() > 0);
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
            }
        });
        field.setComponentPopupMenu(menu);
    }

    /**
     * Gives a text component a context menu with Cut, Copy, Paste and Clear.
     *
     * @param field the text component
     */
    public static void installEditContextMenu(javax.swing.text.JTextComponent field) {
        javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem cutItem = addEditItem(menu, field, "Cut", field::cut);
        javax.swing.JMenuItem copyItem = addEditItem(menu, field, "Copy", field::copy);
        javax.swing.JMenuItem pasteItem = addEditItem(menu, field, "Paste", field::paste);
        menu.addSeparator();
        javax.swing.JMenuItem clearItem = addEditItem(menu, field, "Clear", () -> field.setText(""));
        menu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                boolean selected = field.getSelectionStart() != field.getSelectionEnd();
                boolean editable = field.isEditable() && field.isEnabled();
                cutItem.setEnabled(editable && selected);
                copyItem.setEnabled(selected);
                pasteItem.setEnabled(editable);
                clearItem.setEnabled(editable && field.getDocument().getLength() > 0);
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
            }
        });
        field.setComponentPopupMenu(menu);
    }

    private static javax.swing.JMenuItem addEditItem(javax.swing.JPopupMenu menu,
            javax.swing.text.JTextComponent field, String label, Runnable action) {
        javax.swing.JMenuItem item = new javax.swing.JMenuItem(label);
        // a right click doesn't focus the field, so the caret wouldn't be visible afterwards
        item.addActionListener(e -> {
            field.requestFocusInWindow();
            action.run();
        });
        menu.add(item);
        return item;
    }

    private static void swap(DefaultListModel model, int oldpos, int newpos) {
        Object backup = model.get(newpos);
        model.set(newpos, model.get(oldpos));
        model.set(oldpos, backup);
    }

}
