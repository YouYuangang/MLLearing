/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.inputdata;

import cif.base.Global;
import cif.baseutil.PathUtil;
import cif.baseutil.components.datagrid.DataGrid;
import cif.baseutil.components.datagrid.DataGridModel;
import cif.dataengine.DataPath;
import cif.dataengine.io.LogCategory;
import cif.dataengine.io.LogTable;
import cif.dataengine.io.Logging;
import cif.dataengine.io.TableFields;
import cif.dataengine.io.TableRecords;
import cif.datautil.datatreeview.dialogs.SelectDataTreeNodeDialog;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.CheckBoxTableModel;
import cif.mllearning.base.Variable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author wangcaizhi
 * @create 2019.2.21
 */
public class InputDataDialog extends javax.swing.JDialog {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;

    private final static String COMMON_TABLE = "通用表格:";
    private final static String DISCRETE_TABLE = "离散表格:";
    private DataGridModel dataGridModel = new DataGridModel();
    private final JScrollPane dataGridScrollPane = new JScrollPane();
    private final DataGrid dataGrid = new DataGrid(dataGridScrollPane, dataGridModel);
    private final CheckBoxTableModel variableTableModel = new CheckBoxTableModel();
    private DataPath dataPath;
    private MLDataModel mlModel;

    /**
     * Creates new form InputDataDialog
     */
    public InputDataDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        dataFromComboBox.addItem("测井曲线");
        dataFromComboBox.addItem("表格曲线");
        dataFromComboBox.addItem("文本数据");
        dataGridScrollPane.setViewportView(dataGrid);
        dataGridParentPanel.add(dataGridScrollPane, BorderLayout.CENTER);
        dataGrid.addTablePasteOriginalActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillTextColumnToVariableTable();
            }
        });
        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }

    public void setMLModel(MLDataModel mlModel) {
        this.mlModel = mlModel;
        if (mlModel.dataFrom < 0) {
            return;
        }
        dataFromComboBox.setSelectedIndex(mlModel.dataFrom);
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                dataPath = mlModel.inputDataPath;
                dataPathTextField.setText(dataPath.toString());
                fillCurvesToVariableTable(dataPath);
                fillDepthTextFields(dataPath);
                stdepTextField.setText(Double.toString(mlModel.curveStdep));
                endepTextField.setText(Double.toString(mlModel.curveEndep));
                break;
            case MLDataModel.FROM_TABLE:
                dataPath = mlModel.inputDataPath;
                dataPathTextField.setText(dataPath.toString());
                logTableComboBox.setEnabled(true);
                fillLogTableComboBox(dataPath);
                setLogTableComboBoxSelection(mlModel.logTableName);
                break;
            case MLDataModel.FROM_TEXT:
                filePathTextField.setText(mlModel.inputFilePath == null ? "" : mlModel.inputFilePath);
                dataGridModel = mlModel.dataGridModel;
                dataGrid.setModel(dataGridModel);
                fillTextColumnToVariableTable();
                break;
        }
        variableTableModel.selectNone();
        for (Variable variable : mlModel.getVariables()) {
            variableTableModel.select(variable.name);
        }
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

        jLabel5 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        dataFromComboBox = new javax.swing.JComboBox<>();
        dataSourceLabel = new javax.swing.JLabel();
        dataPathTextField = new javax.swing.JTextField();
        openDataSourceButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        variableTable = new javax.swing.JTable();
        fileTextPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        dataGridParentPanel = new javax.swing.JPanel();
        pasteButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        depthRangePanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        stdepTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        endepTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        selectAllButton = new javax.swing.JButton();
        invertSelectionButton = new javax.swing.JButton();
        selectNoneButton = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        logTableComboBox = new javax.swing.JComboBox<>();
        filePathTextField = new javax.swing.JTextField();
        refreshButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel5.text")); // NOI18N

        setTitle(org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(okButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel1.text")); // NOI18N

        dataFromComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataFromComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(dataSourceLabel, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.dataSourceLabel.text")); // NOI18N

        dataPathTextField.setEditable(false);
        dataPathTextField.setText(org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.dataPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(openDataSourceButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.openDataSourceButton.text")); // NOI18N
        openDataSourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openDataSourceButtonActionPerformed(evt);
            }
        });

        variableTable.setModel(variableTableModel);
        jScrollPane1.setViewportView(variableTable);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel3.text")); // NOI18N

        dataGridParentPanel.setLayout(new java.awt.BorderLayout());

        pasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/paste16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(pasteButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.pasteButton.text")); // NOI18N
        pasteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout fileTextPanelLayout = new javax.swing.GroupLayout(fileTextPanel);
        fileTextPanel.setLayout(fileTextPanelLayout);
        fileTextPanelLayout.setHorizontalGroup(
            fileTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dataGridParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(fileTextPanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pasteButton))
            .addGroup(fileTextPanelLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        fileTextPanelLayout.setVerticalGroup(
            fileTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fileTextPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(fileTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(pasteButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataGridParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4))
        );

        depthRangePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.depthRangePanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel9.text")); // NOI18N

        stdepTextField.setText(org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.stdepTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel10.text")); // NOI18N

        endepTextField.setText(org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.endepTextField.text")); // NOI18N

        javax.swing.GroupLayout depthRangePanelLayout = new javax.swing.GroupLayout(depthRangePanel);
        depthRangePanel.setLayout(depthRangePanelLayout);
        depthRangePanelLayout.setHorizontalGroup(
            depthRangePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(depthRangePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stdepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        depthRangePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {endepTextField, stdepTextField});

        depthRangePanelLayout.setVerticalGroup(
            depthRangePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(depthRangePanelLayout.createSequentialGroup()
                .addGroup(depthRangePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(stdepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(endepTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 7, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel8.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(selectAllButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.selectAllButton.text")); // NOI18N
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(invertSelectionButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.invertSelectionButton.text")); // NOI18N
        invertSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertSelectionButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(selectNoneButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.selectNoneButton.text")); // NOI18N
        selectNoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectNoneButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.jLabel2.text")); // NOI18N

        logTableComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logTableComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logTableComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(logTableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2))
        );

        filePathTextField.setText(org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.filePathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(InputDataDialog.class, "InputDataDialog.refreshButton.text")); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fileTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(dataSourceLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dataFromComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dataPathTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filePathTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(openDataSourceButton))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 323, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(depthRangePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectAllButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(invertSelectionButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectNoneButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dataFromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dataSourceLabel)
                        .addComponent(dataPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(openDataSourceButton))
                    .addComponent(filePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileTextPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(selectAllButton)
                    .addComponent(invertSelectionButton)
                    .addComponent(selectNoneButton)
                    .addComponent(refreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(depthRangePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        mlModel.dataFrom = dataFromComboBox.getSelectedIndex();
        if (variableTableModel.getSelectedCount() == 0) {
            JOptionPane.showMessageDialog(this, "没有选择数据！");
            return;
        }
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                mlModel.inputDataPath = dataPath;
                double stdep = toDouble(stdepTextField.getText());
                double endep = toDouble(endepTextField.getText());
                if (stdep < 0 || endep < 0 || stdep > endep) {
                    JOptionPane.showMessageDialog(this, "深度范围存在错误！");
                    return;
                }
                mlModel.curveStdep = stdep;
                mlModel.curveEndep = endep;
                break;
            case MLDataModel.FROM_TABLE:
                mlModel.inputDataPath = dataPath;
                String[] strs = splitToDoubleWordsWithSpace((String) logTableComboBox.getSelectedItem());
                mlModel.logTableName = strs[1];
                break;
            case MLDataModel.FROM_TEXT:
                mlModel.inputFilePath = filePathTextField.getText();
                mlModel.dataGridModel = dataGridModel;
                break;
        }
        mlModel.setVariableNames(variableTableModel.getSelectedNames());
        mlModel.classifyResult = null;
        mlModel.clusterResult = null;
        mlModel.predictResult = null;
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void dataFromComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataFromComboBoxActionPerformed
        int typeIndex = dataFromComboBox.getSelectedIndex();
        variableTableModel.removeAll();
        variableTableModel.fireTableDataChanged();
        fileTextPanel.setVisible(typeIndex == MLDataModel.FROM_TEXT);
        depthRangePanel.setVisible(typeIndex == MLDataModel.FROM_CURVE);
        tablePanel.setVisible(typeIndex == MLDataModel.FROM_TABLE);
        dataPathTextField.setVisible(typeIndex == MLDataModel.FROM_CURVE || typeIndex == MLDataModel.FROM_TABLE);
        filePathTextField.setVisible(typeIndex == MLDataModel.FROM_TEXT);
        refreshButton.setVisible(typeIndex == MLDataModel.FROM_TEXT);
        switch (typeIndex) {
            case MLDataModel.FROM_CURVE:
                if (!dataPathTextField.getText().isEmpty()) {
                    fillCurvesToVariableTable(dataPath);
                    fillDepthTextFields(dataPath);
                }
                break;
            case MLDataModel.FROM_TABLE:
                boolean isExisted = !dataPathTextField.getText().isEmpty();
                logTableComboBox.setEnabled(isExisted);
                if (isExisted) {
                    fillLogTableComboBox(dataPath);
                }
                break;
            case MLDataModel.FROM_TEXT:
                if (dataGrid.getRowCount() > 2) {
                    fillTextColumnToVariableTable();
                }
                break;
        }
    }//GEN-LAST:event_dataFromComboBoxActionPerformed

    private void openDataSourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openDataSourceButtonActionPerformed
        int typeIndex = dataFromComboBox.getSelectedIndex();
        switch (typeIndex) {
            case MLDataModel.FROM_CURVE:
            case MLDataModel.FROM_TABLE:
                SelectDataTreeNodeDialog dialog = new SelectDataTreeNodeDialog(this, true);
                dialog.setTreeLevel(DataPath.DATA_CATEGORY_LEVEL);
                dialog.treeExpandTo(null);
                dialog.setVisible(true);
                if (dialog.getReturnStatus() == Global.RET_OK) {
                    dataPath = dialog.getSelectionDataPath();
                    if (dataPath != null) {
                        dataPathTextField.setText(dataPath.toString());
                        if (typeIndex == MLDataModel.FROM_CURVE) {
                            fillCurvesToVariableTable(dataPath);
                            fillDepthTextFields(dataPath);
                        } else {
                            logTableComboBox.setEnabled(true);
                            fillLogTableComboBox(dataPath);
                        }
                    }
                }
                break;
            case MLDataModel.FROM_TEXT:
                JFileChooser fileChooser = new JFileChooser(filePathTextField.getText());
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Tab分隔txt文件", "txt");
                fileChooser.setFileFilter(txtFilter);
                FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("逗号分隔csv文件", "csv");
                fileChooser.setFileFilter(csvFilter);
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    filePathTextField.setText(file.getAbsolutePath());
                    openTextFile(file);
                    fillTextColumnToVariableTable();
                }
                break;
        }
    }//GEN-LAST:event_openDataSourceButtonActionPerformed

    private void logTableComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logTableComboBoxActionPerformed
        int selectedIndex = logTableComboBox.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        LogCategory category = dataPath.getCategory();
        String[] strs = splitToDoubleWordsWithSpace((String) logTableComboBox.getSelectedItem());
        LogTable logTable;
        if (strs[0].equals(COMMON_TABLE)) {
            logTable = category.getLogCommonTable(strs[1]);
        } else {
            logTable = category.getLogDiscreteTable(strs[1]);
        }
        TableFields tableFields = new TableFields();
        logTable.readTableFields(tableFields);
        variableTableModel.removeAll();
        for (int i = 0; i < tableFields.getFieldNum(); i++) {
            variableTableModel.add(tableFields.getDisplayName(i));
            variableTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_logTableComboBoxActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        variableTableModel.selectAll();
        variableTableModel.fireTableDataChanged();
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void invertSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertSelectionButtonActionPerformed
        variableTableModel.invertSelection();
        variableTableModel.fireTableDataChanged();
    }//GEN-LAST:event_invertSelectionButtonActionPerformed

    private void selectNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectNoneButtonActionPerformed
        variableTableModel.selectNone();
        variableTableModel.fireTableDataChanged();
    }//GEN-LAST:event_selectNoneButtonActionPerformed

    private void pasteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteButtonActionPerformed
        dataGrid.paste();
    }//GEN-LAST:event_pasteButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        fillTextColumnToVariableTable();
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void setLogTableComboBoxSelection(String tableName) {
        for (int i = 0; i < logTableComboBox.getItemCount(); i++) {
            String[] strs = splitToDoubleWordsWithSpace(logTableComboBox.getItemAt(i));
            if (strs[1].equals(tableName)) {
                logTableComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private DataGridModel formTableDataGridModel() {
        DataGridModel dataModel = new DataGridModel();
        LogCategory category = dataPath.getCategory();
        String[] strs = splitToDoubleWordsWithSpace((String) logTableComboBox.getSelectedItem());
        LogTable logTable;
        if (strs[0].equals(COMMON_TABLE)) {
            logTable = category.getLogCommonTable(strs[1]);
        } else {
            logTable = category.getLogDiscreteTable(strs[1]);
        }
        TableRecords tableRecords = new TableRecords();
        logTable.readTableRecords(tableRecords);
        TableFields tableFields = tableRecords.getTableFields();
        int index = 0;
        int selectedCount = variableTableModel.getSelectedCount();
        String[] names = new String[selectedCount];
        String[] units = new String[selectedCount];
        for (int i = 0; i < tableFields.getFieldNum(); i++) {
            if (variableTableModel.isSelected(i)) {
                names[index] = tableFields.getDisplayName(i);
                units[index] = tableFields.getUnit(i);
                index++;
            }
        }
        dataModel.addData(names);
        dataModel.addData(units);
        for (int row = 0; row < tableRecords.getRecordsNum(); row++) {
            String[] values = new String[selectedCount];
            index = 0;
            for (int i = 0; i < tableFields.getFieldNum(); i++) {
                if (variableTableModel.isSelected(i)) {
                    values[index++] = tableRecords.getRecordString(row, i)[0];
                }
            }
            dataModel.addData(values);
        }
        return dataModel;
    }

    private void openTextFile(File file) {
        try {
            if (PathUtil.getExtension(file.getName()).toLowerCase().equals("csv")) {
                dataGrid.openTextFile(file, ",");
            } else {
                dataGrid.openTextFile(file, "\t");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "读取数据文件发生错误！");
        }
    }

    private void fillTextColumnToVariableTable() {
        variableTableModel.removeAll();
        if (dataGrid.getRowCount() > 1) {
            for (int col = 0; col < dataGrid.getColumnCount(); col++) {
                String columnName = (String) dataGrid.getValueAt(0, col);
                variableTableModel.add(columnName);
            }
        }
        variableTableModel.fireTableDataChanged();
    }

    private String[] splitToDoubleWordsWithSpace(String str) {
        int loc = str.indexOf(" ");
        if (loc < 0) {
            return null;
        }
        String[] strs = new String[2];
        strs[0] = str.substring(0, loc);
        strs[1] = str.substring(loc + 1);
        return strs;
    }

    private void fillCurvesToVariableTable(DataPath dataPath) {
        LogCategory category = dataPath.getCategory();
        variableTableModel.removeAll();
        for (int i = 0; i < category.getLogCurve1DCount(); i++) {
            Logging logging = category.getLogCurve1D(i);
            Double sDepth = logging.getLoggingProperties().getStartDepth();
            Double eDepth = logging.getLoggingProperties().getEndDepth();
            Double interval = logging.getLoggingProperties().getDepthLevel();
            variableTableModel.add(logging.getName(),false,String.format("%.2f",sDepth),String.format("%.2f",eDepth),String.format("%.2f",interval));
        }
        variableTableModel.fireTableDataChanged();
    }

    private void fillDepthTextFields(DataPath dataPath) {
        LogCategory category = dataPath.getCategory();
        stdepTextField.setText(Double.toString(category.getCategoryStartDepth()));
        endepTextField.setText(Double.toString(category.getCategoryEndDepth()));

        mlModel.curveStDepBound = Double.valueOf(stdepTextField.getText());
        mlModel.curveEndDepBound = Double.valueOf(endepTextField.getText());
    }

    private void fillLogTableComboBox(DataPath dataPath) {
        LogCategory category = dataPath.getCategory();
        logTableComboBox.removeAllItems();
        for (int i = 0; i < category.getLogDiscreteTableCount(); i++) {
            Logging logging = category.getLogDiscreteTable(i);
            logTableComboBox.addItem(DISCRETE_TABLE + " " + logging.getName());
        }
        for (int i = 0; i < category.getLogCommonTableCount(); i++) {
            Logging logging = category.getLogCommonTable(i);
            logTableComboBox.addItem(COMMON_TABLE + " " + logging.getName());
        }
    }

    private double toDouble(String s) {
        double val;
        try {
            val = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            val = Global.NULL_DOUBLE_VALUE;
        }
        return val;
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

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
            java.util.logging.Logger.getLogger(InputDataDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InputDataDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InputDataDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InputDataDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                InputDataDialog dialog = new InputDataDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> dataFromComboBox;
    private javax.swing.JPanel dataGridParentPanel;
    private javax.swing.JTextField dataPathTextField;
    private javax.swing.JLabel dataSourceLabel;
    private javax.swing.JPanel depthRangePanel;
    private javax.swing.JTextField endepTextField;
    private javax.swing.JTextField filePathTextField;
    private javax.swing.JPanel fileTextPanel;
    private javax.swing.JButton invertSelectionButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> logTableComboBox;
    private javax.swing.JButton okButton;
    private javax.swing.JButton openDataSourceButton;
    private javax.swing.JButton pasteButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton selectNoneButton;
    private javax.swing.JTextField stdepTextField;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JTable variableTable;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
