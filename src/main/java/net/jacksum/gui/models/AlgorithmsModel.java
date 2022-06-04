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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import net.jacksum.JacksumAPI;
import net.jacksum.gui.interfaces.AlgorithmSelectionInterface;

/**
 *
 * @author Johann N. Löfflmann
 */
public class AlgorithmsModel extends AbstractTableModel implements AlgorithmSelectionInterface {

    String[] columnNames = new String[]{
        "Choose", "Algorithm Id" //, "Description", "Width in bits"
    };
    Class[] types = new Class[]{
        Boolean.class, String.class //, String.class, Integer.class
    };
    boolean[] canEdit = new boolean[]{
        true, false //, false, false
    };

    private List<Object[]> tableData = null;

    private int firstTrue = -1; // the first row that has been enabled by the user    

    public AlgorithmsModel() {
        Map<String, String> idAndNames = JacksumAPI.getAvailableAlgorithms();
        tableData = new ArrayList<>();

        for (Map.Entry<String, String> entry : idAndNames.entrySet()) {
            tableData.add(new Object[]{Boolean.FALSE, entry.getKey()}); //, entry.getValue(), 0 });
        }
        // workaround for bug in Jacksum 3.3.0
        if (!idAndNames.containsKey("sm3")) {
            tableData.add(new Object[]{Boolean.FALSE, "sm3"});
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class getColumnClass(int col) {
        //return getValueAt(0, col).getClass();
        return types[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        // The data/cell address is constant,
        // no matter where the cell appears onscreen.
        return canEdit[col];
    }

    @Override
    public int getRowCount() {
        return tableData.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return tableData.get(row)[col];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        tableData.get(row)[col] = value;
        fireTableCellUpdated(row, col);

    }

    @Override
    public String getSelection() {
        List<String> list = new ArrayList<>();
        for (Object[] row : tableData) {
            if (row[0] == Boolean.TRUE) {
                list.add((String) row[1]);
            }
        }
        return String.join("+", list);
    }

    @Override
    public void setSelection(String algosString) {
        firstTrue = -1;
        String[] algos = algosString.split("\\+");
        Map<String, Boolean> map = new HashMap<>();
        for (String name : algos) {
            map.put(name, null);
        }
        for (int row = 0; row < tableData.size(); row++) {

            Boolean value = map.containsKey(tableData.get(row)[1]);
            if (firstTrue == -1 && value.equals(true)) {
                firstTrue = row;
            }
            setValueAt(value, row, 0);
        }

    }

    public int getFirstTrue() {
        return firstTrue;
    }

    public int getSelectedCount() {
        int selected = 0;
        for (Object[] row : tableData) {
            if (row[0] == Boolean.TRUE) {
                selected++;
            }
        }
        return selected;
    }

    @Override
    public int getDataSize() {
        return tableData.size();
    }

}
