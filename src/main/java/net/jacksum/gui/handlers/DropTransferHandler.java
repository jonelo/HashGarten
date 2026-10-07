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
package net.jacksum.gui.handlers;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import net.jacksum.gui.GUIHelper;

/**
 * Accepts files and directories that are dropped onto a JList (inserted at the drop position) or
 * a JTextField (the path replaces the text).
 *
 * The entries of a JList can also be dragged within the list to reorder them, like with the Up
 * and Down buttons. Such a drag carries the indices of the entries only, in a flavor that is
 * known within this JVM only: it can't be dropped onto the Finder resp. the Explorer, so it can
 * never move or copy the files themselves.
 *
 * Everything else is passed on to the transfer handler that the component had before, because
 * setTransferHandler() replaces it: without that, copy, cut and paste would not work anymore
 * in the text fields, and text could not be dragged into them either.
 */
public class DropTransferHandler extends TransferHandler {
    //private static final long serialVersionUID = 1L;

    // the indices of the entries that are dragged within a JList
    private static final DataFlavor LIST_ITEMS =
            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"[I\"", "list entries");

    private final TransferHandler fallback;

    /**
     * @param fallback the original transfer handler of the component, or null
     */
    public DropTransferHandler(TransferHandler fallback) {
        this.fallback = fallback;
    }

    private static boolean isFileDrop(TransferHandler.TransferSupport support) {
        return support.isDrop() && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    // only the file list is a JList with this handler, so a drag of list entries comes from there
    private static boolean isListReorder(TransferHandler.TransferSupport support) {
        return support.isDrop() && support.getComponent() instanceof JList
                && support.isDataFlavorSupported(LIST_ITEMS);
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (isFileDrop(support) || isListReorder(support)) {
            return true;
        }
        return fallback != null && fallback.canImport(support);
    }

    @Override
    public int getSourceActions(JComponent c) {
        int actions = fallback != null ? fallback.getSourceActions(c) : NONE;
        // MOVE for reordering by drag; the fallback's COPY keeps Cmd/Ctrl+C working
        return c instanceof JList ? actions | MOVE : actions;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (!(c instanceof JList)) {
            return null;
        }
        int[] indices = ((JList<?>) c).getSelectedIndices();
        if (indices.length == 0) {
            return null;
        }
        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{LIST_ITEMS};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return LIST_ITEMS.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor)) {
                    throw new UnsupportedFlavorException(flavor);
                }
                return indices;
            }
        };
    }

    @Override
    public void exportToClipboard(JComponent c, Clipboard clipboard, int action) {
        if (fallback != null) {
            fallback.exportToClipboard(c, clipboard, action);
        } else {
            super.exportToClipboard(c, clipboard, action);
        }
    }

    @Override
    public void exportAsDrag(JComponent c, InputEvent e, int action) {
        if (c instanceof JList) {
            // the entries are reordered, see createTransferable()
            super.exportAsDrag(c, e, action);
        } else if (fallback != null) {
            fallback.exportAsDrag(c, e, action);
        } else {
            super.exportAsDrag(c, e, action);
        }
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        if (isListReorder(info)) {
            return moveListEntries(info);
        }
        if (!isFileDrop(info)) {
            return fallback != null && fallback.importData(info);
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
                // never write to System.out here: Jacksum controls the standard streams and may
                // have redirected them to the user's output file
                GUIHelper.debug(e.toString());
            }

        }
        return false;

    }

    /**
     * Moves the dragged entries of a JList to the drop position, in their original order, and
     * keeps them selected. The entries are removed here rather than in exportDone(), which is
     * left as it is: otherwise a MOVE would remove them a second time.
     */
    private static boolean moveListEntries(TransferHandler.TransferSupport info) {
        int[] indices;
        try {
            indices = (int[]) info.getTransferable().getTransferData(LIST_ITEMS);
        } catch (UnsupportedFlavorException | IOException e) {
            GUIHelper.debug(e.toString());
            return false;
        }
        JList jList = (JList) info.getComponent();
        int dropIndex = ((JList.DropLocation) info.getDropLocation()).getIndex();
        int first = moveEntries((DefaultListModel) jList.getModel(), indices, dropIndex);
        jList.setSelectionInterval(first, first + indices.length - 1);
        jList.ensureIndexIsVisible(first);
        return true;
    }

    /**
     * Moves entries of a list model to an insert position.
     *
     * @param model the model
     * @param indices the indices of the entries to move, in ascending order
     * @param dropIndex the insert position, counted before the entries are moved
     * @return the index of the first moved entry afterwards
     */
    static int moveEntries(DefaultListModel model, int[] indices, int dropIndex) {
        List<Object> entries = new ArrayList<>();
        for (int index : indices) {
            entries.add(model.getElementAt(index));
        }
        // from the bottom up, so that the indices that are still to be removed stay valid
        for (int k = indices.length - 1; k >= 0; k--) {
            model.remove(indices[k]);
            if (indices[k] < dropIndex) {
                dropIndex--;
            }
        }
        for (int k = 0; k < entries.size(); k++) {
            model.add(dropIndex + k, entries.get(k));
        }
        return dropIndex;
    }

}
