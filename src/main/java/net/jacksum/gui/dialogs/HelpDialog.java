/*

  HashGarten 0.15.0 - a GUI to calculate and verify hashes, powered by Jacksum
  Copyright (c) 2022-2024 Dipl.-Inf. (FH) Johann N. Löfflmann,
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

import java.io.IOException;
import net.jacksum.actions.info.help.Help;
import net.jacksum.actions.info.help.NothingFoundException;
import net.jacksum.gui.interfaces.DialogInterface;


/**
 *
 * @author Johann
 */
public class HelpDialog extends javax.swing.JDialog {

    
    public HelpDialog(DialogInterface dialogInterface, boolean modal) {
        super(dialogInterface.getFrame(), modal);
        initComponents();
        helpTextArea.putClientProperty( "FlatLaf.style", "font: $monospaced.font" );     
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        helpPanel = new javax.swing.JPanel();
        helpScrollPane = new javax.swing.JScrollPane();
        helpTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Help");

        cancelButton.setText("Close");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        helpTextArea.setEditable(false);
        helpTextArea.setColumns(20);
        helpTextArea.setRows(5);
        helpScrollPane.setViewportView(helpTextArea);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpScrollPane)
                .addContainerGap())
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 557, Short.MAX_VALUE)
                        .addComponent(cancelButton))
                    .addComponent(helpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(cancelButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    public void searchHelp(String text) throws NothingFoundException, IOException {
        searchHelp(text, false);
    }
    
    public void searchHelp(String text, boolean strict) throws NothingFoundException, IOException {
        helpTextArea.setText(Help.searchHelp("en", text, strict));
        helpTextArea.setCaretPosition(0);
    }
    
    public void setText(String text) {
        helpTextArea.setText(text);
        helpTextArea.setCaretPosition(0);
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JScrollPane helpScrollPane;
    private javax.swing.JTextArea helpTextArea;
    // End of variables declaration//GEN-END:variables
}
