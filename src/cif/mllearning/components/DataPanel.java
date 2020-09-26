/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.components;

import cif.mllearning.MLGlobal;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.UpdatePanelFlag;
import cif.mllearning.base.Variable;
import cif.mllearning.inputdata.ChooseLabelJDialog;
import cif.mllearning.inputdata.FilterDataJDialog;
import cif.mllearning.inputdata.FilterDataJDialog2;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.JTableHeader;
import org.openide.windows.WindowManager;

/**
 *
 * @author wangcaizhi
 * @create 2019.2.24
 */
public class DataPanel extends PagePanel {

    /**
     * Creates new form DataPanel
     */
    private final DataPanelTableModel tableModel = new DataPanelTableModel();
    public MLDataModel mlModel = null;

    public DataPanel() {
        initComponents();
        JTableHeader header = dataTable.getTableHeader();
        //表头增加监听
        header.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                Frame parent = WindowManager.getDefault().getMainWindow();
                switch (mlModel.dataFrom) {
                    case MLDataModel.FROM_CURVE:
                        if (col >= 3&&col < 0+3+mlModel.getVariables().length) {

                            int vIndex = col - 3;

                            MLDataModel model = tableModel.getmlModel();
                            String title = model.getVariables()[vIndex].name;
                            FilterDataJDialog2 filterData = new FilterDataJDialog2(parent, title + "范围设置", true);
                            filterData.setLocationRelativeTo(parent);
                            filterData.setVisible(true);
                            if (filterData.returnStatus == 1) {
                                int count = 0;
                                double minData = Double.valueOf(filterData.getMinData());
                                double maxData = Double.valueOf(filterData.getMaxData());

                                boolean[] flag = model.dataRowSelectedFlags;
                                for (int i = 0; i < flag.length; i++) {
                                    double cur = Double.valueOf((String) tableModel.getValueAt(i, col));
                                    if ((cur < minData || cur > maxData) && (flag[i] == true)) {
                                        flag[i] = false;
                                        count++;
                                    }
                                }
                                tableModel.fireTableDataChanged();
                                JOptionPane.showMessageDialog(parent, "共剔除" + count + "个数据");
                            }

                        }
                        break;
                    default:
                        JOptionPane.showMessageDialog(parent, "未实现该功能");
                        break;

                }
                return;

                /*if (col >= 2) {
                    
                 if((col==tableModel.getColumnCount()-1)&&tableModel.getmlModel().clusterResult!=null){
                 if(tableModel.getmlModel().learningMode == MLGlobal.CLUSTERING_MODE){
                 Frame parent = WindowManager.getDefault().getMainWindow();
                 ClusterToLayerJDialog clusterToLayerD = new ClusterToLayerJDialog(parent,true);
                 clusterToLayerD.setLocationRelativeTo(parent);
                 clusterToLayerD.setModel(tableModel.getmlModel());
                 clusterToLayerD.setVisible(true);
                            
                 return;
                 }
                 }
                 int vIndex = col - 2;
                 Frame parent = WindowManager.getDefault().getMainWindow();
                 MLDataModel model = tableModel.getmlModel();
                 String title = model.getVariables()[vIndex].name;
                 FilterDataJDialog2 filterData = new FilterDataJDialog2(parent, title + "范围设置", true);
                 filterData.setLocationRelativeTo(parent);
                 filterData.setVisible(true);
                 if (filterData.returnStatus == 1) {
                 int count = 0;
                 double minData = Double.valueOf(filterData.getMinData());
                 double maxData = Double.valueOf(filterData.getMaxData());

                 boolean[] flag = model.dataRowSelectedFlags;
                 for (int i = 0; i < flag.length; i++) {
                 double cur = Double.valueOf((String) tableModel.getValueAt(i, col));
                 if ((cur < minData || cur > maxData) && (flag[i] == true)) {
                 flag[i] = false;
                 count++;
                 }
                 }
                 tableModel.fireTableDataChanged();
                 JOptionPane.showMessageDialog(parent, "共剔除" + count + "个数据");
                 }
                 }*/
            }
        });

    }
    public void updateDataPanel(){
        tableModel.fireTableStructureChanged();
    }

    public void setMLModel(MLDataModel mlModel) {
        if (mlModel.dataFrom < 0) {
            return;
        }
        this.mlModel = mlModel;
        tableModel.setMLModel(mlModel);
        //filterDataByDefalut();
        tableModel.fireTableStructureChanged();
        filterDataByDefalut();
        //dataTable.getColumnModel().getColumn(0).setPreferredWidth(40);
    }

    public void filterDataByDefalut() {
        Double invalidNum = MLGlobal.INVALID_VALUE;

        MLDataModel model = tableModel.getmlModel();
        Variable[] variables = model.getVariables();
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                for (int i = 0; i < model.dataRowSelectedFlags.length; i++) {
                    for (int j = 0; j < variables.length; j++) {
                        if (variables[j].flag != MLDataModel.UNSEL_VARIABLE) {
                            try {
                                String cur = (String) tableModel.getValueAt(i, j + 2);
                                Double curNum = Double.valueOf(cur);
                                if (Math.abs(curNum - invalidNum) < 10e-4) {
                                    if (model.dataRowSelectedFlags[i] == true) {
                                        model.dataRowSelectedFlags[i] = false;
                                        break;
                                    }

                                }
                            } catch (NumberFormatException e) {
                                break;
                            }

                        }
                    }
                }
            case MLDataModel.FROM_TEXT:
                for (int i = 0; i < model.dataRowSelectedFlags.length; i++) {
                    for (int j = 0; j < variables.length; j++) {
                        if (variables[j].flag != MLDataModel.UNSEL_VARIABLE) {
                            String cur = (String) tableModel.getValueAt(i, j + 1);
                            try {
                                Double curNum = Double.valueOf(cur);
                                if (Math.abs(curNum - invalidNum) < 10e-4) {
                                    if (model.dataRowSelectedFlags[i] == true) {
                                        model.dataRowSelectedFlags[i] = false;
                                        break;
                                    }

                                }
                            } catch (Exception e) {
                                break;
                            }

                        }
                    }
                }

        }
        tableModel.fireTableDataChanged();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        labelAs = new javax.swing.JButton();
        clearLabelBtn = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        filterDataBtn = new javax.swing.JButton();
        enableButton = new javax.swing.JButton();
        disableButton = new javax.swing.JButton();
        enableAllButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        labelAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/labelAs.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(labelAs, org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.labelAs.text")); // NOI18N
        labelAs.setFocusable(false);
        labelAs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelAs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        labelAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelAsActionPerformed(evt);
            }
        });
        jToolBar1.add(labelAs);

        clearLabelBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/clearLable.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(clearLabelBtn, org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.clearLabelBtn.text")); // NOI18N
        clearLabelBtn.setFocusable(false);
        clearLabelBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearLabelBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearLabelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearLabelBtnActionPerformed(evt);
            }
        });
        jToolBar1.add(clearLabelBtn);
        jToolBar1.add(jSeparator3);

        filterDataBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/filterData.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(filterDataBtn, org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.filterDataBtn.text")); // NOI18N
        filterDataBtn.setToolTipText(org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.filterDataBtn.toolTipText")); // NOI18N
        filterDataBtn.setFocusable(false);
        filterDataBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        filterDataBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        filterDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterDataBtnActionPerformed(evt);
            }
        });
        jToolBar1.add(filterDataBtn);

        enableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/enabled16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(enableButton, org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.enableButton.text")); // NOI18N
        enableButton.setToolTipText(org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.enableButton.toolTipText")); // NOI18N
        enableButton.setFocusable(false);
        enableButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        enableButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        enableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(enableButton);

        disableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/disabled16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(disableButton, org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.disableButton.text")); // NOI18N
        disableButton.setToolTipText(org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.disableButton.toolTipText")); // NOI18N
        disableButton.setFocusable(false);
        disableButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        disableButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        disableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disableButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(disableButton);

        enableAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/allEnabled16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(enableAllButton, org.openide.util.NbBundle.getMessage(DataPanel.class, "DataPanel.enableAllButton.text")); // NOI18N
        enableAllButton.setFocusable(false);
        enableAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        enableAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        enableAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableAllButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(enableAllButton);

        add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        dataTable.setModel(tableModel);
        dataTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        dataTable.setAutoscrolls(false);
        jScrollPane1.setViewportView(dataTable);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void disableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disableButtonActionPerformed
        int[] rows = dataTable.getSelectedRows();
        for (int row : rows) {
            tableModel.setRowEnabled(row, false);
            tableModel.fireTableCellUpdated(row, 0);
        }
        UpdatePanelFlag.HistogramUpdateFlag = true;
        UpdatePanelFlag.CrossPlotUpdateFlag = true;
        UpdatePanelFlag.PlotPanelUpdateFlag = true;
    }//GEN-LAST:event_disableButtonActionPerformed

    private void enableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableButtonActionPerformed
        int[] rows = dataTable.getSelectedRows();
        for (int row : rows) {
            tableModel.setRowEnabled(row, true);
            tableModel.fireTableCellUpdated(row, 0);
        }
        UpdatePanelFlag.HistogramUpdateFlag = true;
        UpdatePanelFlag.CrossPlotUpdateFlag = true;
        UpdatePanelFlag.PlotPanelUpdateFlag = true;
    }//GEN-LAST:event_enableButtonActionPerformed

    private void enableAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableAllButtonActionPerformed
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            tableModel.setRowEnabled(row, true);
            tableModel.fireTableCellUpdated(row, 0);
        }
        UpdatePanelFlag.HistogramUpdateFlag = true;
        UpdatePanelFlag.CrossPlotUpdateFlag = true;
        UpdatePanelFlag.PlotPanelUpdateFlag = true;
    }//GEN-LAST:event_enableAllButtonActionPerformed

    private void filterDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterDataBtnActionPerformed
        // TODO add your handling code here:
        MLDataModel model = tableModel.getmlModel();
        if (model == null) {
            JOptionPane.showMessageDialog(this, "请先导入数据");
            return;
        } else {
            Frame parent = WindowManager.getDefault().getMainWindow();
            FilterDataJDialog filterDialog = new FilterDataJDialog(parent, true);
            filterDialog.setLocationRelativeTo(parent);
            filterDialog.setVisible(true);
            if (filterDialog.getReturnStatus() == FilterDataJDialog.RET_OK) {
                String input = filterDialog.getInput();
                Double invalidNum = Double.valueOf(input);
                Variable[] variables = model.getVariables();
                int count = 0;
                for (int i = 0; i < model.dataRowSelectedFlags.length; i++) {
                    for (int j = 0; j < variables.length; j++) {
                        if (variables[j].flag != MLDataModel.UNSEL_VARIABLE) {
                            String cur = (String) tableModel.getValueAt(i, j + 2);
                            Double curNum = Double.valueOf(cur);
                            if (Math.abs(curNum - invalidNum) < 10e-4) {
                                model.dataRowSelectedFlags[i] = false;
                                count++;
                                break;
                            }
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "共" + count + "个数据被剔除:");
            }
            tableModel.fireTableDataChanged();
            UpdatePanelFlag.HistogramUpdateFlag = true;
            UpdatePanelFlag.CrossPlotUpdateFlag = true;
            UpdatePanelFlag.PlotPanelUpdateFlag = true;

        }
    }//GEN-LAST:event_filterDataBtnActionPerformed

    private void labelAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelAsActionPerformed
        // TODO add your handling code here:
        int[] rows = dataTable.getSelectedRows();
        if (rows.length > 0) {
            Frame parent = WindowManager.getDefault().getMainWindow();
            ChooseLabelJDialog chooseLabelJDialog = new ChooseLabelJDialog(parent, true);
            chooseLabelJDialog.setLocationRelativeTo(parent);
            chooseLabelJDialog.setVisible(true);
            if (chooseLabelJDialog.retStatu == ChooseLabelJDialog.OK) {
                for (int row : rows) {
                    mlModel.dataRowSelectedFlags[row] = true;
                    mlModel.dataLabelAs[row] = chooseLabelJDialog.getChooseBoxIndex();
                    //tableModel.fireTableCellUpdated(row, 0);
                }
            }

            tableModel.fireTableDataChanged();
        }

    }//GEN-LAST:event_labelAsActionPerformed

    private void clearLabelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearLabelBtnActionPerformed
        // TODO add your handling code here:
        if(mlModel.dataLabelAs!=null){
            for(int i = 0;i<mlModel.dataLabelAs.length;i++){
                mlModel.dataLabelAs[i] = -1;
            }
            tableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_clearLabelBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearLabelBtn;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton disableButton;
    private javax.swing.JButton enableAllButton;
    private javax.swing.JButton enableButton;
    private javax.swing.JButton filterDataBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton labelAs;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getTitle() {
        return "数据列表";
    }

    @Override
    public String getIconName() {
        return "dataList16.png";
    }

    @Override
    public String getID() {
        return "dataList";
    }

}
