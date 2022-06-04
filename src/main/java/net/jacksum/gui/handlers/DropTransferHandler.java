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
package net.jacksum.gui.handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

public class DropTransferHandler extends TransferHandler {
    //private static final long serialVersionUID = 1L;

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        for (DataFlavor dataFlavor : info.getDataFlavors()) {
            try {

                if (dataFlavor.equals(DataFlavor.javaFileListFlavor)) {

                    Transferable transferable = info.getTransferable();
                    List<File> data;
                    try {
                        data = (List) transferable.getTransferData(dataFlavor);
                    } catch (UnsupportedFlavorException | IOException e) {
                        return false;
                    }

                    List<String> filenames = new ArrayList<>();
                    data.forEach(file -> {
                        filenames.add(file.getAbsolutePath());
                    });

                    if (info.getComponent() instanceof JList) {
                        JList jList = (JList) info.getComponent();
                        DefaultListModel defaultListModel = (DefaultListModel) jList.getModel();
                        JList.DropLocation dropLocation = (JList.DropLocation) info.getDropLocation();
                        defaultListModel.addAll(dropLocation.getIndex(), filenames);
                        return true;
                    } else if (info.getComponent() instanceof JTextField) {
                        JTextField jTextField = (JTextField) info.getComponent();
                        jTextField.setText(filenames.get(0));
                        return true;
                    }

                }

            } catch (Exception e) {
                System.out.println(e);
            }

        }
        return false;

    }

}
