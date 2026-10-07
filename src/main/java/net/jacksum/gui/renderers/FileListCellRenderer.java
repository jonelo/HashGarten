/*

  HashGarten 0.21.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022 Dipl.-Inf. (FH) Johann N. Löfflmann,
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
package net.jacksum.gui.renderers;

import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import net.jacksum.gui.util.MacFileIcons;

/**
 * Renders the entries of the file list (paths as strings) with the icon that the operating
 * system shows for them, e.g. in the Finder resp. in the Explorer. The colors and the font are
 * left to the look and feel, so that the list follows the light and the dark theme.
 *
 * @author Johann N. Löfflmann
 */
public class FileListCellRenderer extends DefaultListCellRenderer {

    private static final int ICON_SIZE = 16;

    // the icons are provided by the native side, and a renderer runs on every repaint
    private final Map<String, Icon> icons = new HashMap<>();

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String path = String.valueOf(value);
        Icon icon = iconFor(path);
        if (icon != null) {
            setIcon(icon);
            // without a tooltip of its own, the list shows its tooltip
            setToolTipText(null);
        } else {
            setIcon(UIManager.getIcon("FileView.fileIcon"));
            setToolTipText(String.format("%s does not exist.", path));
        }
        return this;
    }

    /**
     * Returns the icon of the operating system for a file or a directory.
     *
     * @param path the path of the file or directory
     * @return the icon, or null if the path does not exist
     */
    private Icon iconFor(String path) {
        Icon icon = icons.get(path);
        if (icon != null) {
            return icon;
        }
        File file = new File(path);
        if (!file.exists()) {
            // not cached, so that the icon shows up once the file exists
            return null;
        }
        // on macOS, FileSystemView only knows the generic icons of the look and feel
        icon = MacFileIcons.iconFor(file, ICON_SIZE);
        if (icon == null) {
            FileSystemView view = FileSystemView.getFileSystemView();
            // the overload with a size returns a multi-resolution icon, which is sharp on HiDPI screens
            icon = view.getSystemIcon(file, ICON_SIZE, ICON_SIZE);
            if (icon == null) {
                icon = view.getSystemIcon(file);
            }
        }
        if (icon != null) {
            icons.put(path, icon);
        }
        return icon;
    }
}
