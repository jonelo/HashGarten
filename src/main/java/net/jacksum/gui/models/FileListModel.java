/*

  HashGarten 0.9.0 - a GUI to calculate and verify hashes, powered by Jacksum
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
package net.jacksum.gui.models;

import javax.swing.DefaultListModel;

/**
 *
 * @author Johann N. Löfflmann
 */
public class FileListModel extends DefaultListModel implements Cloneable {

    private FileListModel backup;

    public void backup() {
        backup = (FileListModel) this.clone();
    }

    public void restore() {
        copyFrom(backup);
    }

    public FileListModel() {
        super();
    }

    /**
     * Private constructor used for cloning
     *
     * @param model the instance to clone.
     */
    private FileListModel(FileListModel model) {
        super();
        copyFrom(model);
    }

    public void copyFrom(FileListModel model) {
        clear();
        for (int i = 0; i < model.size(); i++) {
            // make a real copy with the new Bookmark constructor
            // don't copy just the references
            addElement(new String((String) model.getElementAt(i)));
        }
    }

    @Override
    public Object clone() {
        return new FileListModel(this);
    }
}
