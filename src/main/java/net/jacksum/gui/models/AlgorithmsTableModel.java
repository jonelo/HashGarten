/*

  HashGarten 0.20.0 - a GUI to calculate and verify hashes, powered by Jacksum
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import net.jacksum.JacksumAPI;
import net.jacksum.gui.interfaces.AlgorithmSelectionInterface;

/**
 *
 * @author Johann N. Löfflmann
 */
public class AlgorithmsTableModel extends AbstractTableModel implements AlgorithmSelectionInterface {

    String[] columnNames = new String[]{
        "Choose", "Algorithm Id", "Description" //, "Width in bits"
    };
    Class[] types = new Class[]{
        Boolean.class, String.class, String.class //, String.class, Integer.class
    };
    boolean[] canEdit = new boolean[]{
        true, false, false
    };

    private List<Object[]> tableData = null;

    private int firstTrue = -1; // the first row that has been enabled by the user    

    public AlgorithmsTableModel() {
        tableData = new ArrayList<>();
        Map<String, String> idAndNames = JacksumAPI.getAvailableAlgorithms();        
        Map<String, String> idAndNamesHMACs = JacksumAPI.getAvailableHMACs();
        

        for (Map.Entry<String, String> entry : idAndNames.entrySet()) {
            tableData.add(new Object[]{Boolean.FALSE, entry.getKey(), entry.getValue()}); //, entry.getValue(), 0 });            
        }
        for (Map.Entry<String, String> entry : idAndNamesHMACs.entrySet()) {
            tableData.add(new Object[]{Boolean.FALSE, entry.getKey(), entry.getValue()}); //, entry.getValue(), 0 });            
        }
        
        // workaround for bug in Jacksum 3.3.0
        //if (!idAndNames.containsKey("sm3")) {
        //    tableData.add(new Object[]{Boolean.FALSE, "sm3"});
        //}
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
            if (Boolean.TRUE.equals(row[0])) {
                list.add((String) row[1]);
            }
        }
        return String.join("+", list);
    }

    @Override
    public void setSelection(String algosString) {
        firstTrue = -1;
        Set<String> wanted = new HashSet<>();
        for (String algo : algosString.split("\\+")) {
            String canonical = toCanonicalAlgorithmId(algo.trim());
            if (canonical != null) {
                wanted.add(canonical);
            }
        }
        for (int row = 0; row < tableData.size(); row++) {

            boolean value = wanted.contains(tableData.get(row)[1]);
            if (firstTrue == -1 && value) {
                firstTrue = row;
            }
            setValueAt(value, row, 0);
        }

    }

    /**
     * Resolves an algorithm id as it can appear on the command line onto the canonical id that
     * this model uses as the key of its rows.
     *
     * Jacksum's Parameters.getAlgorithm() reports what the user resp. the file browser integration
     * has entered, and that can be an alias ("sha1" for "sha-1") or it can differ in case, while
     * this model is keyed by the canonical ids of JacksumAPI.getAvailableAlgorithms(). Without
     * resolving them no row would be ticked at all, and pressing Ok would then silently wipe the
     * algorithm selection.
     *
     * @param algorithm the algorithm id, an alias is allowed
     * @return the canonical algorithm id, or null if the algorithm is unknown
     */
    private String toCanonicalAlgorithmId(String algorithm) {
        if (algorithm.isEmpty()) {
            return null;
        }
        // Jacksum resolves algorithm ids in lower case only
        String candidate = algorithm.toLowerCase(Locale.US);
        for (Object[] row : tableData) {
            if (candidate.equals(row[1])) {
                return candidate;
            }
        }
        // it is not a canonical id, so let Jacksum find out whether it is an alias
        try {
            return JacksumAPI.getChecksumInstance(candidate).getName();
        } catch (Exception e) {
            // an unknown algorithm cannot be ticked
            return null;
        }
    }

    public int getFirstTrue() {
        return firstTrue;
    }

    @Override
    public int getSelectedCount() {
        int selected = 0;
        for (Object[] row : tableData) {
            if (Boolean.TRUE.equals(row[0])) {
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
