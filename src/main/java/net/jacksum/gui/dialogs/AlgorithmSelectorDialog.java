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
package net.jacksum.gui.dialogs;

import net.jacksum.gui.interfaces.AlgorithmSelectorDialogInterface;
import net.jacksum.gui.interfaces.AlgorithmSelectionInterface;
import net.jacksum.gui.models.AlgorithmsModel;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import net.jacksum.actions.info.algo.AlgoInfoAction;
import net.jacksum.actions.info.algo.AlgoInfoActionParameters;
import net.jacksum.actions.info.help.Help;
import net.jacksum.actions.info.help.NothingFoundException;
import net.jacksum.cli.Verbose;
import net.loefflmann.sugar.util.ExitException;

/**
 *
 * @author Johann N. Löfflmann
 */
public class AlgorithmSelectorDialog extends javax.swing.JDialog implements AlgorithmSelectionInterface, TableModelListener {

    private final AlgorithmsModel tableModel;
    private final TableRowSorter<AlgorithmsModel> sorter;

    /**
     * Creates a new Dialog to select algorithms.
     * @param dialogInterface the Java interface for the dialog.
     * @param modal whether the frame should be modal
     */
    public AlgorithmSelectorDialog(AlgorithmSelectorDialogInterface dialogInterface, boolean modal) {
        super(dialogInterface.getFrame(), modal);
        tableModel = new AlgorithmsModel();
        tableModel.addTableModelListener((TableModelListener)this);
        sorter = new TableRowSorter<>(tableModel);

        initComponents();
        // we hide the description
        table.removeColumn(table.getColumnModel().getColumn(2));
        helpTextArea.putClientProperty( "FlatLaf.style", "font: $monospaced.font" );     
        implTextArea.putClientProperty( "FlatLaf.style", "font: $monospaced.font" );
        table.setRowSorter(sorter);
        //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        adjustColumnWidths();
        registerManpages();
        registerImplDetails();
        registerFilter();
    }

    interface AlgoInfoActionParametersExtended extends AlgoInfoActionParameters {
        public void setAlgorithmIdentifier(String identifier);
    }
    private AlgoInfoActionParametersExtended params;
    
    private void registerImplDetails() {
        params = new AlgoInfoActionParametersExtended() {
            private String identifier;
            
            @Override
            public boolean isList() {
                return false;
            }

            @Override
            public boolean isInfoMode() {
                return true;
            }

            @Override
            public String getAlgorithmIdentifier() {
                return identifier;
            }

            @Override
            public boolean isAlternateImplementationWanted() {
                return false;
            }

            @Override
            public Verbose getVerbose() {
                return new Verbose();
            }

            @Override
            public void setAlgorithmIdentifier(String identifier) {
                this.identifier = identifier;
            }
        };

    }
    
    String currentAlgorithmIdentifier;
    public String getCurrentAlgoritmIdentifier() {
        return currentAlgorithmIdentifier;
    }
    
    private void registerFilter() {
        filterTextField.getDocument().addDocumentListener(
                new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                newFilter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                newFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                newFilter();
            }
        });
    }

    private void newFilter() {
        RowFilter<AlgorithmsModel, Object> rf;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filterTextField.getText());
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

    private void adjustColumnWidths() {
        // column widths
        TableColumn column;
        for (int i = 0; i < 2; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(30); // first column is narrower
            } else {
                column.setPreferredWidth(100);
            }
        }

    }

    private void registerManpages() {
        // show the help dependent on the algorithm
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // System.out.println(event);

                int viewRow = table.getSelectedRow();
                if (viewRow < 0) {
                    // Selection got filtered away.
                    helpTextArea.setText("");
                    implTextArea.setText("");
                } else {
                    int modelRow = table.convertRowIndexToModel(viewRow);

                    boolean isAdjusting = event.getValueIsAdjusting();
                    // do some actions here, for example
                    // print first column value from selected row
                    if (!isAdjusting) {
                        String algoSelected = tableModel.getValueAt(modelRow, 1).toString();
                        if (algoSelected.equalsIgnoreCase("blake3")) {
                            algoSelected = "blake3-256"; // required for the Help search to not match blake384 by accident
                        }
                        //System.out.println(algoSelected);
                        try {
                            helpTextArea.setText(Help.searchHelp("en", algoSelected, true));
                            helpTextArea.setCaretPosition(0);
                                                        
                            StringBuilder buffer = new StringBuilder();
                            params.setAlgorithmIdentifier(algoSelected);
                            AlgoInfoAction action = new AlgoInfoAction(params);
                            try {
                                action.perform(buffer);
                                implTextArea.setText(buffer.toString());
                                implTextArea.setCaretPosition(0);    
                            } catch (ExitException ex) {
                                Logger.getLogger(AlgorithmSelectorDialog.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } catch (NothingFoundException | IOException e) {
                            System.err.println(e);
                        }
                    }

                }

            }
        });
    }

    @Override
    public String getSelection() {
        return tableModel.getSelection();
    }

    @Override
    public void setSelection(String algos) {
        tableModel.setSelection(algos);

        // make sure that the first enabled row is selected
        int row = tableModel.getFirstTrue();
        if (row > -1) {
            int viewRow = table.convertRowIndexToView(row);
            table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
            table.scrollRectToVisible(new Rectangle(table.getCellRect(viewRow, 1, true)));
        }
    }

    @Override
    public void tableChanged(TableModelEvent evt) {
        // int row = evt.getFirstRow();
        //int column = evt.getColumn();
        //AlgorithmsModel model = (AlgorithmsModel)evt.getSource();
        //String columnName = model.getColumnName(column);
        //Object data = tableModel.getValueAt(row, column);

        algorithmCountLabel.setText(String.format("%d / %d algorithms picked", tableModel.getSelectedCount(), getDataSize()));
    }

    @Override
    public int getDataSize() {
        return tableModel.getDataSize();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableScrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable(){

            //Implement table cell tool tips.           
            public String getToolTipText(MouseEvent e) {

                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    // comment row, exclude heading

                    int modelRow = table.convertRowIndexToModel(rowIndex);
                    tip = getModel().getValueAt(modelRow, 2).toString();
                    //tip = getValueAt(rowIndex, colIndex).toString();

                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }
                return tip;
            }
        }
        ;
        FilterLabel = new javax.swing.JLabel();
        algorithmCountLabel = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();
        showAllButton = new javax.swing.JButton();
        showCheckedButton = new javax.swing.JButton();
        showUncheckedButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        selectNoneButton = new javax.swing.JButton();
        checkButton = new javax.swing.JButton();
        uncheckButton = new javax.swing.JButton();
        toggleButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        helpPanel = new javax.swing.JPanel();
        helpScrollPane = new javax.swing.JScrollPane();
        helpTextArea = new javax.swing.JTextArea();
        implPanel = new javax.swing.JPanel();
        implScrollPane = new javax.swing.JScrollPane();
        implTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select algorithms");

        table.setAutoCreateRowSorter(true);
        table.setModel(tableModel);
        tableScrollPane.setViewportView(table);

        FilterLabel.setText("Filter:");

        algorithmCountLabel.setText("xxx/xxx algorithms picked");

        filterTextField.setToolTipText("Regular expressions are allowed!");

        showAllButton.setText("Show all");
        showAllButton.setToolTipText("Show all available algorithms");
        showAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllButtonActionPerformed(evt);
            }
        });

        showCheckedButton.setText("Show checked");
        showCheckedButton.setToolTipText("Show checked algorithms only");
        showCheckedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCheckedButtonActionPerformed(evt);
            }
        });

        showUncheckedButton.setText("Show unchecked");
        showUncheckedButton.setToolTipText("Show unchecked algorithms only");
        showUncheckedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showUncheckedButtonActionPerformed(evt);
            }
        });

        selectAllButton.setText("Select all");
        selectAllButton.setToolTipText("Select all algorithms");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        selectNoneButton.setText("Select none");
        selectNoneButton.setToolTipText("Select none of the algorithms");
        selectNoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectNoneButtonActionPerformed(evt);
            }
        });

        checkButton.setText("Check");
        checkButton.setToolTipText("Tick all checkboxes of the current selection");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        uncheckButton.setText("Uncheck");
        uncheckButton.setToolTipText("Erase all checks in the checkboxes of the current selection");
        uncheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uncheckButtonActionPerformed(evt);
            }
        });

        toggleButton.setText("Toggle");
        toggleButton.setToolTipText("Toglggle all checks on all checkboxes of the current selection");
        toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.setToolTipText("Reset both selection and ticks");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Do yo want to cancel this operation?");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("Ok");
        okButton.setToolTipText("Return all algorithms that have been checked");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        helpTextArea.setEditable(false);
        helpTextArea.setColumns(20);
        helpScrollPane.setViewportView(helpTextArea);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
            .addGroup(helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(helpPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(helpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 369, Short.MAX_VALUE)
            .addGroup(helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, helpPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(helpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Manpage", helpPanel);

        implTextArea.setEditable(false);
        implTextArea.setColumns(20);
        implTextArea.setRows(5);
        implScrollPane.setViewportView(implTextArea);

        javax.swing.GroupLayout implPanelLayout = new javax.swing.GroupLayout(implPanel);
        implPanel.setLayout(implPanelLayout);
        implPanelLayout.setHorizontalGroup(
            implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 690, Short.MAX_VALUE)
            .addGroup(implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(implPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(implScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        implPanelLayout.setVerticalGroup(
            implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 369, Short.MAX_VALUE)
            .addGroup(implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, implPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(implScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Implementation Details", implPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(FilterLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(filterTextField))
                        .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(algorithmCountLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(showAllButton)
                            .addComponent(selectAllButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(selectNoneButton)
                                .addGap(18, 18, 18)
                                .addComponent(checkButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(uncheckButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(toggleButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(showCheckedButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(showUncheckedButton)))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableScrollPane)
                    .addComponent(jTabbedPane1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FilterLabel)
                    .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showAllButton)
                    .addComponent(showCheckedButton)
                    .addComponent(showUncheckedButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton)
                    .addComponent(algorithmCountLabel)
                    .addComponent(resetButton)
                    .addComponent(selectAllButton)
                    .addComponent(selectNoneButton)
                    .addComponent(checkButton)
                    .addComponent(uncheckButton)
                    .addComponent(toggleButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void showAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllButtonActionPerformed
        filterTextField.setText("");
    }//GEN-LAST:event_showAllButtonActionPerformed

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
        int[] selectedRows = table.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            tableModel.setValueAt(Boolean.TRUE, modelRow, 0);
        }
    }//GEN-LAST:event_checkButtonActionPerformed

    private void uncheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uncheckButtonActionPerformed
        int[] selectedRows = table.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            tableModel.setValueAt(Boolean.FALSE, modelRow, 0);
        }
    }//GEN-LAST:event_uncheckButtonActionPerformed

    private void showCheckedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCheckedButtonActionPerformed
        filterTextField.setText("true");
    }//GEN-LAST:event_showCheckedButtonActionPerformed

    private void showUncheckedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUncheckedButtonActionPerformed
        filterTextField.setText("false");
    }//GEN-LAST:event_showUncheckedButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        table.selectAll();
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void selectNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectNoneButtonActionPerformed
        table.clearSelection();
    }//GEN-LAST:event_selectNoneButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        filterTextField.setText("");
        table.clearSelection();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(Boolean.FALSE, i, 0);
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    private void toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonActionPerformed
        int[] selectedRows = table.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = table.convertRowIndexToModel(selectedRow);            
            tableModel.setValueAt(!(Boolean)tableModel.getValueAt(modelRow, 0), modelRow, 0);
        }
    }//GEN-LAST:event_toggleButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel FilterLabel;
    private javax.swing.JLabel algorithmCountLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton checkButton;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JScrollPane helpScrollPane;
    private javax.swing.JTextArea helpTextArea;
    private javax.swing.JPanel implPanel;
    private javax.swing.JScrollPane implScrollPane;
    private javax.swing.JTextArea implTextArea;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton selectNoneButton;
    private javax.swing.JButton showAllButton;
    private javax.swing.JButton showCheckedButton;
    private javax.swing.JButton showUncheckedButton;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JButton toggleButton;
    private javax.swing.JButton uncheckButton;
    // End of variables declaration//GEN-END:variables
}
