/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.components;

import java.awt.Color;
import javax.swing.JColorChooser;

/**
 *
 * @author 10797
 */
public class CrossplotSettingDialog extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final int RET_OK = 1;

    private int returnStatus = RET_CANCEL;
    //foreground color
    private Color fColor;
    //background color
    private Color bColor;

    /**
     * Creates new form CrossplotSettingDialog
     */
    public CrossplotSettingDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("设置");
        initComponents();
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    public Color getForegroundColor() {
        return this.fColor == null ? Color.red : this.fColor;
    }

    public Color getBackgroundColor() {
        return this.bColor == null ? Color.white : this.bColor;
    }

    public void setForeGroundColor(Color c) {
        this.fColor = c;
        this.fColorLabel.setBackground(c);
    }

    public void setBackgroundColor(Color c) {
        this.bColor = c;
        this.bColorLabel.setBackground(c);
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        fBtn = new javax.swing.JButton();
        bBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        fColorLabel = new javax.swing.JLabel();
        bColorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(fBtn, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.fBtn.text")); // NOI18N
        fBtn.setPreferredSize(new java.awt.Dimension(40, 18));
        fBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bBtn, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.bBtn.text")); // NOI18N
        bBtn.setToolTipText(org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.bBtn.toolTipText")); // NOI18N
        bBtn.setPreferredSize(new java.awt.Dimension(40, 18));
        bBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okBtn, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.okBtn.text")); // NOI18N
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelBtn, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.cancelBtn.text")); // NOI18N
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        fColorLabel.setBackground(new java.awt.Color(102, 255, 102));
        org.openide.awt.Mnemonics.setLocalizedText(fColorLabel, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.fColorLabel.text")); // NOI18N
        fColorLabel.setToolTipText(org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.fColorLabel.toolTipText")); // NOI18N
        fColorLabel.setOpaque(true);
        fColorLabel.setPreferredSize(new java.awt.Dimension(120, 18));

        bColorLabel.setBackground(new java.awt.Color(255, 102, 102));
        org.openide.awt.Mnemonics.setLocalizedText(bColorLabel, org.openide.util.NbBundle.getMessage(CrossplotSettingDialog.class, "CrossplotSettingDialog.bColorLabel.text")); // NOI18N
        bColorLabel.setOpaque(true);
        bColorLabel.setPreferredSize(new java.awt.Dimension(120, 18));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(63, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okBtn)
                .addGap(70, 70, 70)
                .addComponent(cancelBtn)
                .addGap(94, 94, 94))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(fBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(bBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bColorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okBtn)
                    .addComponent(cancelBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        // TODO add your handling code here:
        doClose(RET_OK);
    }//GEN-LAST:event_okBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        // TODO add your handling code here:
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void fBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fBtnActionPerformed
        // TODO add your handling code here:
        this.fColor = JColorChooser.showDialog(this, "颜色选择器", getForegroundColor());
        fColorLabel.setBackground(this.fColor);
    }//GEN-LAST:event_fBtnActionPerformed

    private void bBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBtnActionPerformed
        // TODO add your handling code here:
        this.bColor = JColorChooser.showDialog(this, "颜色选择器", getBackgroundColor());
        bColorLabel.setBackground(this.bColor);
    }//GEN-LAST:event_bBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CrossplotSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CrossplotSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CrossplotSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CrossplotSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CrossplotSettingDialog dialog = new CrossplotSettingDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBtn;
    private javax.swing.JLabel bColorLabel;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton fBtn;
    private javax.swing.JLabel fColorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton okBtn;
    // End of variables declaration//GEN-END:variables
}
