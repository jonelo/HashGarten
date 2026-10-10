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
package net.jacksum.gui.dialogs;

import net.jacksum.gui.interfaces.AlgorithmSelectorDialogInterface;
import net.jacksum.gui.interfaces.AlgorithmSelectionInterface;
import net.jacksum.gui.models.AlgorithmsTableModel;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import net.jacksum.HashFunctionFactory;
import net.jacksum.actions.info.algo.AlgoInfoAction;
import net.jacksum.actions.info.algo.AlgoInfoActionParameters;
import net.jacksum.actions.info.help.Help;
import net.jacksum.actions.info.help.NothingFoundException;
import net.jacksum.cli.Verbose;
import net.jacksum.gui.models.CustomizedAlgorithmsTableModel;
import net.jacksum.parameters.Sequence;
import net.jacksum.parameters.ParameterException;
import net.loefflmann.sugar.util.ExitException;

/**
 *
 * @author Johann N. Löfflmann
 */
public class AlgorithmSelectorDialog extends javax.swing.JDialog implements AlgorithmSelectionInterface, TableModelListener {

    // whether the dialog has been left by pressing Ok; false means cancelled resp. closed
    private boolean okPressed = false;

    private final AlgorithmsTableModel algorithmsTableModel;
    private final CustomizedAlgorithmsTableModel customizedAlgorithmsTableModel;
    private final TableRowSorter<AlgorithmsTableModel> altorithmsTableRowSorter;

    /**
     * Creates a new Dialog to select algorithms.
     * @param dialogInterface the Java interface for the dialog.
     * @param modal whether the frame should be modal
     */
    public AlgorithmSelectorDialog(AlgorithmSelectorDialogInterface dialogInterface, boolean modal) {
        super(dialogInterface.getFrame(), modal);
        algorithmsTableModel = new AlgorithmsTableModel();
        algorithmsTableModel.addTableModelListener((TableModelListener)this);
        altorithmsTableRowSorter = new TableRowSorter<>(algorithmsTableModel);
        
        customizedAlgorithmsTableModel = new CustomizedAlgorithmsTableModel();

        initComponents();
        // we hide the description
        algorithmsTable.removeColumn(algorithmsTable.getColumnModel().getColumn(2));
        helpTextArea.putClientProperty( "FlatLaf.style", "font: $monospaced.font" );     
        implTextArea.putClientProperty( "FlatLaf.style", "font: $monospaced.font" );
        net.jacksum.gui.util.SwingUtils.installCopyContextMenu(helpTextArea);
        net.jacksum.gui.util.SwingUtils.installCopyContextMenu(implTextArea);
        net.jacksum.gui.util.SwingUtils.installEditContextMenu(filterTextField);
        algorithmsTable.setRowSorter(altorithmsTableRowSorter);
        filterToolTip = filterTextField.getToolTipText();
        //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        adjustColumnWidths();
        registerManpages();
        registerImplDetails();
        registerFilter();
    }

    @Override
    public int getSelectedCount() {
          return algorithmsTableModel.getSelectedCount();
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

   

            @Override
            public Sequence getSequence() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void setSequence(Sequence sequence) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public boolean isSequence() {
                return false;
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

    // null: all rows, TRUE: only the ticked ones, FALSE: only the unticked ones (Show checked resp.
    // Show unchecked); it is combined with the text of the filter field
    private Boolean checkedFilter = null;

    private void newFilter() {
        java.util.List<RowFilter<AlgorithmsTableModel, Integer>> filters = new java.util.ArrayList<>();
        // If current expression doesn't parse, don't update, but tell the user why.
        // It is compiled without the "(?i)" prefix first, so that the position of the error
        // refers to the text in the field.
        String text = filterTextField.getText();
        try {
            java.util.regex.Pattern.compile(text, java.util.regex.Pattern.CASE_INSENSITIVE);
            // the algorithm id only, case-insensitive: without a column index the filter would
            // also search the description, which is hidden, and the column of the check boxes
            filters.add(RowFilter.regexFilter("(?i)" + text, 1));
        } catch (java.util.regex.PatternSyntaxException e) {
            markFilterError(e);
            return;
        }
        clearFilterError();
        if (checkedFilter != null) {
            Boolean wanted = checkedFilter;
            filters.add(new RowFilter<AlgorithmsTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends AlgorithmsTableModel, ? extends Integer> entry) {
                    return wanted.equals(entry.getValue(0));
                }
            });
        }
        altorithmsTableRowSorter.setRowFilter(RowFilter.andFilter(filters));
        updateAlgorithmCountLabel();
    }
    
    // the tooltip of the filter field while its expression is valid
    private String filterToolTip;

    /**
     * Flags the filter field because its expression is not valid: FlatLaf paints an error
     * outline around it and the tooltip shows the error and where it is. No color is set
     * explicitly, so the outline follows the current theme.
     *
     * @param e the error of the expression
     */
    private void markFilterError(java.util.regex.PatternSyntaxException e) {
        filterTextField.putClientProperty(com.formdev.flatlaf.FlatClientProperties.OUTLINE,
                com.formdev.flatlaf.FlatClientProperties.OUTLINE_ERROR);
        String pattern = e.getPattern();
        StringBuilder tip = new StringBuilder("<html>Invalid regular expression: ")
                .append(escapeHtml(e.getDescription()))
                .append("<pre>").append(escapeHtml(pattern));
        int index = e.getIndex();
        if (index >= 0) {
            tip.append('\n').append(" ".repeat(Math.min(index, pattern.length()))).append('^');
        }
        filterTextField.setToolTipText(tip.append("</pre></html>").toString());
    }

    private void clearFilterError() {
        filterTextField.putClientProperty(com.formdev.flatlaf.FlatClientProperties.OUTLINE, null);
        filterTextField.setToolTipText(filterToolTip);
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // created on the first click on the help button next to the filter field
    private HelpDialog filterHelpDialog;

    // describes what newFilter() does; the examples match the algorithm ids of Jacksum 4.0.1
    private static final String FILTER_HELP = """
            FILTER

            The filter field narrows down the list of algorithms while you type.

            How it works
              - Only the algorithm ID is searched (e.g. sha3-256, hmac:sha-256),
                not the description.
              - Upper and lower case don't matter: SHA3 finds sha3-256.
              - The text may appear anywhere in the ID: sha3 finds sha3-256 and
                hmac:sha3-256.
              - An empty field shows all algorithms.
              - Ticks are kept while the filter hides an algorithm, so you can
                filter, tick, filter again and tick more.

            Show all, Show checked, Show unchecked
              These buttons clear the filter field. Show checked and Show unchecked
              then show only the ticked resp. unticked algorithms; text that you
              type afterwards filters within them. Reset clears the filter, the
              selection and all ticks.

            Regular expressions
              The text is a regular expression (Java syntax), so some characters
              have a special meaning:

                ^        start of the ID      ^sha3-       sha3-224 ... sha3-512
                $        end of the ID        -256$        blake2b-256, sha-256, ...
                .        any character        md.          md2, md4, md5, md6-...
                ?        optional             ^sha3?-256$  sha-256, sha3-256
                |        or                   crc|adler    adler32, crc8, crc16, ...
                ( )      group                ^(md5|sha-1)$  exactly md5 and sha-1
                [ ]      one of               ^blake2[bs]-256$  blake2b-256, blake2s-256
                \\d       a digit              ^sha\\d?-     sha-1 ... sha3-512
                *, +     repeat 0+ resp. 1+   ^crc\\d+$     crc8, crc16, crc32, ...
                {n,m}    repeat n to m times  ^crc\\d{2}$   crc16, crc24, crc32, crc64
                \\        literal character    \\.  \\+  \\(   a dot, a plus, a parenthesis

            More examples
              ^hmac:              all HMACs
              ^(?!hmac:).*-256$   the 256-bit algorithms without the HMACs
              ^sha-512/           sha-512/224, sha-512/256
              ^haval_256_         haval_256_3, haval_256_4, haval_256_5

            If the expression is incomplete or invalid (e.g. an open parenthesis),
            the list keeps showing the result of the last valid expression.
            """;

    private void updateAlgorithmCountLabel() {
        algorithmCountLabel.setText(String.format("total: %d, visible: %d, checked: %d",
                getDataSize(),
                algorithmsTable.getRowCount(),
                algorithmsTableModel.getSelectedCount()
                
        ));
    }

    private void adjustColumnWidths() {
        // column widths
        TableColumn column;
        for (int i = 0; i < 2; i++) {
            column = algorithmsTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(30); // first column is narrower
            } else {
                column.setPreferredWidth(100);
            } 
       }

    }

    private void registerManpages() {
        // show the help dependent on the algorithm
        algorithmsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                // System.out.println(event);

                int viewRow = algorithmsTable.getSelectedRow();
                if (viewRow < 0) {
                    // Selection got filtered away.
                    helpTextArea.setText("");
                    implTextArea.setText("");
                } else {
                    int modelRow = algorithmsTable.convertRowIndexToModel(viewRow);

                    boolean isAdjusting = event.getValueIsAdjusting();
                    // do some actions here, for example
                    // print first column value from selected row
                    if (!isAdjusting) {
                        String algoSelected = algorithmsTableModel.getValueAt(modelRow, 1).toString();
                        if (algoSelected.equalsIgnoreCase("blake3")) {
                            algoSelected = "blake3-256"; // required for the Help search to not match blake384 by accident
                        }

                        //System.out.println(algoSelected);                        
                        try {
                            // fill the help area
                            if (algoSelected.startsWith("hmac:")) {
                                helpTextArea.setText(Help.searchHelp("en", "hmac", false));
                            } else {                            
                                helpTextArea.setText(Help.searchHelp("en", algoSelected, true));
                            }                                                        
                            helpTextArea.setCaretPosition(0);
                            
                            // fill the AlgoInfo
                            StringBuilder buffer = new StringBuilder();
                            params.setAlgorithmIdentifier(algoSelected);
                            AlgoInfoAction action = new AlgoInfoAction(params);

                            // An HMAC is initialized with the process wide key of the
                            // HashFunctionFactory, which Parameters.checked() wipes if no key has
                            // been given (e.g. an empty key field in the Interactive mode). The
                            // details don't depend on the key, so use an empty key for them and
                            // restore the previous state afterwards.
                            boolean hmac = algoSelected.startsWith("hmac:");
                            byte[] previousKey = null;
                            if (hmac) {
                                // setKey() wipes the stored array, so keep a copy of it
                                previousKey = HashFunctionFactory.getKey();
                                previousKey = previousKey == null ? null : previousKey.clone();
                                HashFunctionFactory.setKey(new byte[0]);
                            }
                            try {
                                action.perform(buffer);
                                implTextArea.setText(buffer.toString());
                                implTextArea.setCaretPosition(0);
                            } catch (ExitException | ParameterException ex) {
                                implTextArea.setText(ex.getMessage());
                                net.jacksum.gui.GUIHelper.debug(ex.toString());
                            } finally {
                                if (hmac) {
                                    if (previousKey == null) {
                                        HashFunctionFactory.wipeKey();
                                    } else {
                                        // setKey() stores a copy, so our copy can be wiped
                                        HashFunctionFactory.setKey(previousKey);
                                        java.util.Arrays.fill(previousKey, (byte) 0x00);
                                    }
                                }
                            }

                        } catch (NothingFoundException | IOException e) {
                            // Jacksum controls the standard streams, so don't print there
                            net.jacksum.gui.GUIHelper.debug(e.toString());
                        }
                    }

                }

            }
        });
    }

    @Override
    public String getSelection() {
        return algorithmsTableModel.getSelection();
    }

    /**
     * Whether the user has confirmed the selection by pressing Ok.
     *
     * The dialog instance is reused, so the flag is reset by every setSelection() call. Cancelling
     * and closing the dialog both leave it false.
     *
     * @return true if Ok has been pressed since the last setSelection() call
     */
    public boolean isOkPressed() {
        return okPressed;
    }

    @Override
    public void setSelection(String algos) {
        okPressed = false;
        algorithmsTableModel.setSelection(algos);
        // the dialog is reused, so a filter (e.g. "Show checked") is still active; the row sorter
        // doesn't filter again on its own when the ticks change, so it would show the rows that
        // have been ticked when the dialog was open the last time
        newFilter();

        // make sure that the first enabled row is selected
        int row = algorithmsTableModel.getFirstTrue();
        if (row > -1) {
            int viewRow = algorithmsTable.convertRowIndexToView(row);
            // -1 if the filter hides the row
            if (viewRow > -1) {
                algorithmsTable.getSelectionModel().setSelectionInterval(viewRow, viewRow);
                algorithmsTable.scrollRectToVisible(new Rectangle(algorithmsTable.getCellRect(viewRow, 1, true)));
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent evt) {
        // int row = evt.getFirstRow();
        //int column = evt.getColumn();
        //AlgorithmsModel model = (AlgorithmsModel)evt.getSource();
        //String columnName = model.getColumnName(column);
        //Object data = tableModel.getValueAt(row, column);

        updateAlgorithmCountLabel();
        //algorithmCountLabel.setText(String.format("%d visible, %d checked, %d total", algorithmsTable.getRowCount(), ((AlgorithmSelectionInterface)evt.getSource()).getSelectedCount(), getDataSize()));
    }

    @Override
    public int getDataSize() {
        return algorithmsTableModel.getDataSize();
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
        algorithmsTable = new javax.swing.JTable(){

            //Implement algorithmsTable cell tool tips.           
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    // comment row, exclude heading
                    int modelRow = algorithmsTable.convertRowIndexToModel(rowIndex);
                    tip = getModel().getValueAt(modelRow, 2).toString();
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
        filterHelpButton = new javax.swing.JButton();
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
        jLabel1 = new javax.swing.JLabel();
        customizedAlgorithmsScrollPane = new javax.swing.JScrollPane();
        customizedAlgorithmsTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Algorithms");

        algorithmsTable.setAutoCreateRowSorter(true);
        algorithmsTable.setModel(algorithmsTableModel);
        algorithmsTable.setToolTipText("Tick the algorithms to be used, select a row to see its details");
        tableScrollPane.setViewportView(algorithmsTable);

        FilterLabel.setText("Filter:");

        algorithmCountLabel.setText("xxx/xxx algorithms picked");

        filterTextField.setToolTipText("Filter the algorithms, regular expressions are supported (e.g. ^sha3-)");

        filterHelpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/jacksum/gui/pix16x16/question.png"))); // NOI18N
        filterHelpButton.setToolTipText("What does that mean?");
        filterHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterHelpButtonActionPerformed(evt);
            }
        });

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
        selectAllButton.setToolTipText("Select all rows of the table");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        selectNoneButton.setText("Select none");
        selectNoneButton.setToolTipText("Clear the selection of rows");
        selectNoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectNoneButtonActionPerformed(evt);
            }
        });

        checkButton.setText("Check");
        checkButton.setToolTipText("Tick the checkboxes of the selected rows");
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        uncheckButton.setText("Uncheck");
        uncheckButton.setToolTipText("Untick the checkboxes of the selected rows");
        uncheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uncheckButtonActionPerformed(evt);
            }
        });

        toggleButton.setText("Toggle");
        toggleButton.setToolTipText("Invert the checkboxes of the selected rows");
        toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.setToolTipText("Clear the filter, the selection and all ticks");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Close the dialog without changing the algorithms");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.setToolTipText("Use all algorithms that have been ticked");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        helpTextArea.setEditable(false);
        helpTextArea.setColumns(20);
        helpTextArea.setToolTipText("Description of the selected algorithm from the Jacksum manpage");
        helpScrollPane.setViewportView(helpTextArea);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 651, Short.MAX_VALUE)
            .addGroup(helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(helpPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(helpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
            .addGroup(helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, helpPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(helpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Manpage", helpPanel);

        implTextArea.setEditable(false);
        implTextArea.setColumns(20);
        implTextArea.setRows(5);
        implTextArea.setToolTipText("Implementation details of the selected algorithm");
        implScrollPane.setViewportView(implTextArea);

        javax.swing.GroupLayout implPanelLayout = new javax.swing.GroupLayout(implPanel);
        implPanel.setLayout(implPanelLayout);
        implPanelLayout.setHorizontalGroup(
            implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 651, Short.MAX_VALUE)
            .addGroup(implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(implPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(implScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        implPanelLayout.setVerticalGroup(
            implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 395, Short.MAX_VALUE)
            .addGroup(implPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, implPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(implScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Implementation Details", implPanel);

        jLabel1.setText("Standard Algorithms:");

        customizedAlgorithmsTable.setAutoCreateRowSorter(true);
        customizedAlgorithmsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        customizedAlgorithmsScrollPane.setViewportView(customizedAlgorithmsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(algorithmCountLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(FilterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filterHelpButton))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(customizedAlgorithmsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12)
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(showAllButton)
                        .addComponent(showCheckedButton)
                        .addComponent(showUncheckedButton)
                        .addComponent(FilterLabel)
                        .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterHelpButton))
                    .addComponent(customizedAlgorithmsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, 0, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        okPressed = true;
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void showAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllButtonActionPerformed
        checkedFilter = null;
        filterTextField.setText("");
        newFilter();
    }//GEN-LAST:event_showAllButtonActionPerformed

    private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
        int[] selectedRows = algorithmsTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = algorithmsTable.convertRowIndexToModel(selectedRow);
            algorithmsTableModel.setValueAt(Boolean.TRUE, modelRow, 0);
        }
    }//GEN-LAST:event_checkButtonActionPerformed

    private void uncheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uncheckButtonActionPerformed
        int[] selectedRows = algorithmsTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = algorithmsTable.convertRowIndexToModel(selectedRow);
            algorithmsTableModel.setValueAt(Boolean.FALSE, modelRow, 0);
        }
    }//GEN-LAST:event_uncheckButtonActionPerformed

    private void showCheckedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCheckedButtonActionPerformed
        checkedFilter = Boolean.TRUE;
        filterTextField.setText("");
        newFilter();
    }//GEN-LAST:event_showCheckedButtonActionPerformed

    private void showUncheckedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showUncheckedButtonActionPerformed
        checkedFilter = Boolean.FALSE;
        filterTextField.setText("");
        newFilter();
    }//GEN-LAST:event_showUncheckedButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        algorithmsTable.selectAll();
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void selectNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectNoneButtonActionPerformed
        algorithmsTable.clearSelection();
    }//GEN-LAST:event_selectNoneButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        checkedFilter = null;
        filterTextField.setText("");
        newFilter();
        algorithmsTable.clearSelection();
        for (int i = 0; i < algorithmsTableModel.getRowCount(); i++) {
            algorithmsTableModel.setValueAt(Boolean.FALSE, i, 0);
        }
        // also the algorithms that can't be shown in the table (e.g. "all"), otherwise there
        // would be no way to get rid of them
        algorithmsTableModel.clearUnmatched();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonActionPerformed
        int[] selectedRows = algorithmsTable.getSelectedRows();
        for (int selectedRow : selectedRows) {
            int modelRow = algorithmsTable.convertRowIndexToModel(selectedRow);            
            algorithmsTableModel.setValueAt(!(Boolean)algorithmsTableModel.getValueAt(modelRow, 0), modelRow, 0);
        }
    }//GEN-LAST:event_toggleButtonActionPerformed

    private void filterHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterHelpButtonActionPerformed
        // this dialog is modal, so a help dialog of the main frame would be blocked by it
        if (filterHelpDialog == null) {
            filterHelpDialog = new HelpDialog(this, false);
        }
        filterHelpDialog.setTitle("Help: Filter");
        filterHelpDialog.setText(FILTER_HELP);
        filterHelpDialog.fitWidthToText();
        filterHelpDialog.showOver(this);
    }//GEN-LAST:event_filterHelpButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel FilterLabel;
    private javax.swing.JLabel algorithmCountLabel;
    private javax.swing.JTable algorithmsTable;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton checkButton;
    private javax.swing.JScrollPane customizedAlgorithmsScrollPane;
    private javax.swing.JTable customizedAlgorithmsTable;
    private javax.swing.JButton filterHelpButton;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JScrollPane helpScrollPane;
    private javax.swing.JTextArea helpTextArea;
    private javax.swing.JPanel implPanel;
    private javax.swing.JScrollPane implScrollPane;
    private javax.swing.JTextArea implTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton selectNoneButton;
    private javax.swing.JButton showAllButton;
    private javax.swing.JButton showCheckedButton;
    private javax.swing.JButton showUncheckedButton;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JButton toggleButton;
    private javax.swing.JButton uncheckButton;
    // End of variables declaration//GEN-END:variables
}
