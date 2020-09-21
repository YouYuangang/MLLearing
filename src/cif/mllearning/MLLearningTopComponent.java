/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning;
import cif.base.Global;
import cif.dataengine.DataPath;
import cif.dataengine.io.LogCategory;
import cif.dataengine.io.LogTable;
import cif.dataengine.io.TableRecords;
import cif.datautil.datatreeview.dialogs.SelectDataTreeNodeDialog;
import cif.mllearning.base.DataHelper;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.MLDataModelHelper;
import cif.mllearning.base.RawCurveDataHelper;
import cif.mllearning.base.TableHelper;
import cif.mllearning.base.UpdatePanelFlag;
import cif.mllearning.base.Variable;
import cif.mllearning.components.CrossPlotPanel;
import cif.mllearning.components.DataPanel;
import cif.mllearning.components.HistogramPanel;
import cif.mllearning.components.MessagePanel;
import cif.mllearning.components.PagePanel;
import cif.mllearning.components.PlotPanel;
import cif.mllearning.components.ProgressDialog;
import cif.mllearning.configure.LoadConfigure;
import cif.mllearning.functions.FunTools;
import cif.mllearning.functions.Function;
import cif.mllearning.functions.FunctionProxy;
import cif.mllearning.inputdata.ChooseClassLabelJDialog;
import cif.mllearning.inputdata.InputDataDialog;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.InputStream;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//cif.mllearning//MLLearning//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MLLearningTopComponent",
        iconBase = "cif/mllearning/icons/learning16.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "cif.mllearning.MLLearningTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MLLearningAction",
        preferredID = "MLLearningTopComponent"
)
@Messages({
    "CTL_MLLearningAction=MLLearning",
    "CTL_MLLearningTopComponent=MLLearning Window",
    "HINT_MLLearningTopComponent=This is a MLLearning window"
})
public final class MLLearningTopComponent extends TopComponent {

    private static final int VARIABLE_SELECTION_CHANGED_EVENT = 0;
    private static final int PAGE_CHANGED_EVENT = 1;
    private static final int VARIABLE_COUNT_CHANGED_EVENT = 2;
    private static final int INPUT_CHANGED_EVENT = 3;

    private int learningMode = MLGlobal.PREDICTING_MODE;
    private final MLDataModel mlModel = new MLDataModel();
    private final VariableTableModel variableTableModel = new VariableTableModel(mlModel);
    private final static int MAIN_PAGE_PANE = 0;
    private final static int SUB_PAGE_PANE = 1;
    private final static int BOTH_PAGE_PANE = 2;
    private boolean maskTabbedPaneEvent = false;
    private final MLGlobal mlGlobal = new MLGlobal();
    private boolean isPagePaneSplited = false;

    
    /**
     *
     */
    public MLLearningTopComponent() {
        initComponents();
        setName(Bundle.CTL_MLLearningTopComponent());
        setToolTipText(Bundle.HINT_MLLearningTopComponent());
        maskTabbedPaneEvent = true;
        updateMainPagePanels();
        updateFunctions();
        maskTabbedPaneEvent = false;
       LoadConfigure loadConfigure = new LoadConfigure();
       /*variableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    yButton.setEnabled(variableTable.getSelectedRowCount() == 1);
                    variableTableRowSelected();
                    
                }
            }
        })*/;
        setSubTabbedPaneVisuable(false);
    }

    private void addLeftPagePanel(int index, PagePanel panel) {
        if (index < 0) {
            index = mainTabbedPane.getTabCount();
        }
        mainTabbedPane.insertTab(panel.getTitle(),
                new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/" + panel.getIconName())),
                panel, null, index);
    }

    private void setRightPagePanel(PagePanel panel) {
        subTabbedPane.addTab(panel.getTitle(),
                new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/" + panel.getIconName())), panel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topButtonGroup = new javax.swing.ButtonGroup();
        mainSplitPane = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        variableTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        functionComboBox = new javax.swing.JComboBox<String>();
        selectVariableButton = new javax.swing.JButton();
        yButton = new javax.swing.JButton();
        tabbedPaneSplitPane = new javax.swing.JSplitPane();
        mainTabbedPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        subTabbedPane = new javax.swing.JTabbedPane();
        mainToolBar = new javax.swing.JToolBar();
        inputDataButton = new javax.swing.JButton();
        manageModelButton = new javax.swing.JButton();
        pagePaneSplitedToggleButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        predictingToggleButton = new javax.swing.JToggleButton();
        classificationToggleButton = new javax.swing.JToggleButton();
        clusteringToggleButton = new javax.swing.JToggleButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        gnerateModel = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        clearClusterBtn = new javax.swing.JButton();
        save = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.BorderLayout());

        mainSplitPane.setDividerLocation(298);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.jLabel1.text")); // NOI18N

        variableTable.setModel(variableTableModel);
        jScrollPane1.setViewportView(variableTable);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.jLabel2.text")); // NOI18N

        functionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                functionComboBoxActionPerformed(evt);
            }
        });

        selectVariableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/selectItem16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(selectVariableButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.selectVariableButton.text")); // NOI18N
        selectVariableButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        selectVariableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectVariableButtonActionPerformed(evt);
            }
        });

        yButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/y.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(yButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.yButton.text")); // NOI18N
        yButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        yButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(functionComboBox, 0, 237, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(yButton, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectVariableButton)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(functionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(selectVariableButton)
                        .addComponent(jLabel1))
                    .addComponent(yButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainSplitPane.setLeftComponent(jPanel1);

        tabbedPaneSplitPane.setDividerLocation(300);

        mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainTabbedPaneStateChanged(evt);
            }
        });
        tabbedPaneSplitPane.setLeftComponent(mainTabbedPane);

        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(subTabbedPane, java.awt.BorderLayout.CENTER);

        tabbedPaneSplitPane.setRightComponent(jPanel2);

        mainSplitPane.setRightComponent(tabbedPaneSplitPane);

        add(mainSplitPane, java.awt.BorderLayout.CENTER);

        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);

        inputDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/openDataSource24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(inputDataButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.inputDataButton.text")); // NOI18N
        inputDataButton.setFocusable(false);
        inputDataButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        inputDataButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        inputDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputDataButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(inputDataButton);

        manageModelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/model24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(manageModelButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.manageModelButton.text")); // NOI18N
        manageModelButton.setFocusable(false);
        manageModelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        manageModelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(manageModelButton);

        pagePaneSplitedToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/windowSplited24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(pagePaneSplitedToggleButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.pagePaneSplitedToggleButton.text")); // NOI18N
        pagePaneSplitedToggleButton.setFocusable(false);
        pagePaneSplitedToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pagePaneSplitedToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pagePaneSplitedToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagePaneSplitedToggleButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(pagePaneSplitedToggleButton);
        mainToolBar.add(jSeparator1);

        topButtonGroup.add(predictingToggleButton);
        predictingToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/predict24.png"))); // NOI18N
        predictingToggleButton.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(predictingToggleButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.predictingToggleButton.text")); // NOI18N
        predictingToggleButton.setFocusable(false);
        predictingToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        predictingToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                predictingToggleButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(predictingToggleButton);

        topButtonGroup.add(classificationToggleButton);
        classificationToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/classification24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(classificationToggleButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.classificationToggleButton.text")); // NOI18N
        classificationToggleButton.setFocusable(false);
        classificationToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        classificationToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classificationToggleButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(classificationToggleButton);

        topButtonGroup.add(clusteringToggleButton);
        clusteringToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/cluster24.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(clusteringToggleButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.clusteringToggleButton.text")); // NOI18N
        clusteringToggleButton.setFocusable(false);
        clusteringToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        clusteringToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clusteringToggleButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(clusteringToggleButton);
        mainToolBar.add(jSeparator2);

        gnerateModel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/gneratemodel.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(gnerateModel, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.gnerateModel.text")); // NOI18N
        gnerateModel.setFocusable(false);
        gnerateModel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gnerateModel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gnerateModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gnerateModelActionPerformed(evt);
            }
        });
        mainToolBar.add(gnerateModel);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/run.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(runButton, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.runButton.text")); // NOI18N
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        runButton.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(runButton);

        clearClusterBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/CancelLabel.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(clearClusterBtn, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.clearClusterBtn.text")); // NOI18N
        clearClusterBtn.setFocusable(false);
        clearClusterBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearClusterBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearClusterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearClusterBtnActionPerformed(evt);
            }
        });
        mainToolBar.add(clearClusterBtn);

        save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/save.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(save, org.openide.util.NbBundle.getMessage(MLLearningTopComponent.class, "MLLearningTopComponent.save.text")); // NOI18N
        save.setFocusable(false);
        save.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        save.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        mainToolBar.add(save);

        add(mainToolBar, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void predictingToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_predictingToggleButtonActionPerformed
        learningModeChanged(MLGlobal.PREDICTING_MODE);
    }//GEN-LAST:event_predictingToggleButtonActionPerformed

    private void clusteringToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clusteringToggleButtonActionPerformed
        learningModeChanged(MLGlobal.CLUSTERING_MODE);
    }//GEN-LAST:event_clusteringToggleButtonActionPerformed

    private void learningModeChanged(int mode) {
        splitPagePane(false);
        pagePaneSplitedToggleButton.setSelected(false);
        learningMode = mode;
        mlModel.learningMode=mode;
        variableTableModel.setLearningMode(learningMode);
        variableTableModel.fireTableStructureChanged();
        switch(mode){
            case MLGlobal.PREDICTING_MODE:
                yButton.setVisible(true);
                yButton.setText("设置为y");
                break;
            case MLGlobal.CLASSIFYING_MODE:
                yButton.setVisible(true);
                yButton.setText("加载标签");
                break;
            case MLGlobal.CLUSTERING_MODE:
                yButton.setVisible(false);
                break;
        }
        UpdatePanelFlag.DataPanelUpdateFlag = true;
        UpdatePanelFlag.HistogramUpdateFlag = true;
        UpdatePanelFlag.CrossPlotUpdateFlag = true;
        UpdatePanelFlag.PlotPanelUpdateFlag = true;
        updateMainPagePanels();
        updateFunctions();
    }
    private void inputDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputDataButtonActionPerformed
        Frame parent = WindowManager.getDefault().getMainWindow();
        InputDataDialog dialog = new InputDataDialog(parent, true);
        dialog.setLocationRelativeTo(parent);
        dialog.setMLModel(mlModel);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == InputDataDialog.RET_OK) {
            if(mlModel.dataRowSelectedFlags!=null){
                for(int i = 0;i<mlModel.dataRowSelectedFlags.length;i++){
                    mlModel.dataRowSelectedFlags[i] = true;
                
                }
            }
            
            mlModel.predictResult = null;
            mlModel.classifyResult = null;
            mlModel.clusterResult = null;
            selectPagePanel(mlGlobal.dataPanel);
            variableTableModel.refreshViewData();
            variableTableModel.fireTableDataChanged();
            UpdatePanelFlag.DataPanelUpdateFlag = true;
            UpdatePanelFlag.HistogramUpdateFlag = true;
            UpdatePanelFlag.CrossPlotUpdateFlag = true;
            UpdatePanelFlag.PlotPanelUpdateFlag = true;
            updatePagePanels();
            
        }
    }//GEN-LAST:event_inputDataButtonActionPerformed

    private void selectVariableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectVariableButtonActionPerformed
        Frame parent = WindowManager.getDefault().getMainWindow();
        SelectVariableDialog dialog = new SelectVariableDialog(parent, true);
        for (Variable variable : mlModel.getVariables()) {
            dialog.addVariable(variable.name, variable.flag);
        }
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == SelectVariableDialog.RET_OK) {
            Variable[] variables = mlModel.getVariables();
            for (int i = 0; i < variables.length; i++) {
                if (dialog.isSelected(i)) {
                    if (variables[i].flag < 0) {
                        variables[i].flag = MLDataModel.X_VARIABLE;
                    }
                } else {
                    variables[i].flag = MLDataModel.UNSEL_VARIABLE;
                }
            }
            variableTableModel.refreshViewData();
            variableTableModel.fireTableDataChanged();

            UpdatePanelFlag.HistogramUpdateFlag = true;
            UpdatePanelFlag.CrossPlotUpdateFlag = true;
            UpdatePanelFlag.PlotPanelUpdateFlag = true;
            updatePagePanels();
        }
    }//GEN-LAST:event_selectVariableButtonActionPerformed

    private void yButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yButtonActionPerformed
        /*int selectedRow = variableTable.getSelectedRow();
        variableTableModel.setY(selectedRow);
        variableTableModel.fireTableDataChanged();
        int selectedIndex = variableTableModel.getRowCount() - 1;
        variableTable.setRowSelectionInterval(selectedIndex, selectedIndex);
        */
        if(mlModel==null){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "请先导入数据！");
            return;
        }
        LogCategory category = null;
        int i = 0;
        DataHelper dataHepler = new DataHelper(mlModel);
        switch(learningMode){
            
                
            case MLGlobal.PREDICTING_MODE:
                i = variableTable.getSelectedRow();
                if(i>=0){
                    variableTableModel.setY(i);
                }
                break;
                
            case MLGlobal.CLASSIFYING_MODE:
                category = mlModel.inputDataPath.getCategory();
                
                ChooseClassLabelJDialog chooseClassLabelJDialog = new ChooseClassLabelJDialog(WindowManager.getDefault().getMainWindow(),true);
                
                LogTable table = null;
                for(i = 0;i<category.getLogCommonTableCount();i++){
                    table = category.getLogCommonTable(i);
                    chooseClassLabelJDialog.addItem(table.getName());
                }
                chooseClassLabelJDialog.setVisible(true);
                if(chooseClassLabelJDialog.retStatu == Global.RET_OK){
                    String choosedLabel = chooseClassLabelJDialog.getSelectedTable();
                    TableHelper tableHelper = new TableHelper(mlModel);
                    int rowUsed = tableHelper.fillDataLabelFromTable(choosedLabel);
                   
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow()," 采样率："+dataHepler.getDepthLevel()+"有效行数："+rowUsed);
                    
                    UpdatePanelFlag.DataPanelUpdateFlag = true;
                    updatePagePanels();
      
                }
                break;
                
        }
    }//GEN-LAST:event_yButtonActionPerformed

    private void pagePaneSplitedToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagePaneSplitedToggleButtonActionPerformed
        boolean toSplit = pagePaneSplitedToggleButton.isSelected();
        splitPagePane(toSplit);
    }//GEN-LAST:event_pagePaneSplitedToggleButtonActionPerformed

    private void splitPagePane(boolean toSplit) {
        if (toSplit == isPagePaneSplited) {
            return;
        }
        setSubTabbedPaneVisuable(toSplit);
        if (toSplit) {
            PagePanel panel = (PagePanel) mainTabbedPane.getSelectedComponent();
            mainTabbedPane.remove(panel);
            setRightPagePanel(panel);
        } else {
            PagePanel panel = (PagePanel) subTabbedPane.getSelectedComponent();
            subTabbedPane.remove(panel);
            int index = getOriginalMainPagePanelLocation(panel.getID());
            maskTabbedPaneEvent = true;
            addLeftPagePanel(index, panel);
            maskTabbedPaneEvent = false;
        }
        isPagePaneSplited = toSplit;
    }
    private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainTabbedPaneStateChanged

            updatePagePanels();
    }//GEN-LAST:event_mainTabbedPaneStateChanged

    private void classificationToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classificationToggleButtonActionPerformed
        learningModeChanged(MLGlobal.CLASSIFYING_MODE);
    }//GEN-LAST:event_classificationToggleButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        if(mlModel.variables == null){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "请先导入数据！");
            return;
        }
        selectPagePanel(mlGlobal.messagePanel);
        int index = functionComboBox.getSelectedIndex();
        Function function = null;
        
        if (learningMode == MLGlobal.CLASSIFYING_MODE) {
            try {
                function = (Function) mlGlobal.getFunctionProxys(learningMode)[index].classType.newInstance();
                function.setRunModel(Function.RUN_MODEL);
                JFileChooser jfc = new JFileChooser(new File(FunTools.getModelPath()));
                int retStatus = jfc.showDialog(this, "选择");
                if(retStatus == JFileChooser.CANCEL_OPTION){
                    return;
                }
                File moldelFile = jfc.getSelectedFile();
                function.modelPath = moldelFile.getAbsolutePath();
                FunTools.checkXsAreRightAndOrder(moldelFile.getAbsolutePath()+"Aux", mlModel, variableTableModel,mlGlobal);
            } catch (InstantiationException | IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
            executeFunction(function, Function.RUN_MODEL);
        }else{
            try {
                function = (Function) mlGlobal.getFunctionProxys(learningMode)[index].classType.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
            executeFunction(function, Function.RUN_MODEL);
        }

    }//GEN-LAST:event_runButtonActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if (!pagePaneSplitedToggleButton.isSelected()) {
            tabbedPaneSplitPane.setDividerLocation(999999);
        }
    }//GEN-LAST:event_formComponentResized

    private void clearClusterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearClusterBtnActionPerformed
        // TODO add your handling code here:
        switch(learningMode){
            case MLGlobal.PREDICTING_MODE :
                if(mlModel.predictResult!=null){
                    mlModel.predictResult = null;
                    
                    UpdatePanelFlag.DataPanelUpdateFlag = true;
                    UpdatePanelFlag.HistogramUpdateFlag = false;
                    UpdatePanelFlag.CrossPlotUpdateFlag = true;
                    UpdatePanelFlag.PlotPanelUpdateFlag = true;
                    updatePagePanels();
                }
                break;
            case MLGlobal.CLASSIFYING_MODE:
                if(mlModel.classifyResult!=null){
                    mlModel.classifyResult = null;
                    UpdatePanelFlag.DataPanelUpdateFlag = true;
                    UpdatePanelFlag.HistogramUpdateFlag = false;
                    UpdatePanelFlag.CrossPlotUpdateFlag = true;
                    UpdatePanelFlag.PlotPanelUpdateFlag = true;
                    updatePagePanels();
                }
                break;
            case MLGlobal.CLUSTERING_MODE:
                if(mlModel.clusterResult!=null){
                    mlModel.clusterResult = null;
                    LoadConfigure.clusterLayerRelation = null;
                    UpdatePanelFlag.DataPanelUpdateFlag = true;
                    UpdatePanelFlag.HistogramUpdateFlag = false;
                    UpdatePanelFlag.CrossPlotUpdateFlag = true;
                    UpdatePanelFlag.PlotPanelUpdateFlag = true;
                    updatePagePanels();
                }
                break;
        }
    }//GEN-LAST:event_clearClusterBtnActionPerformed

    private void functionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_functionComboBoxActionPerformed
        // TODO add your handling code here:
    
        
    }//GEN-LAST:event_functionComboBoxActionPerformed

    private void gnerateModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gnerateModelActionPerformed
        // TODO add your handling code here:
        selectPagePanel(mlGlobal.messagePanel);
        int index = functionComboBox.getSelectedIndex();
        Function function = null;
        try {
            function = (Function) mlGlobal.getFunctionProxys(learningMode)[index].classType.newInstance();
            function.setRunModel(Function.GENERATE_MODEL);
            
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        executeFunction(function,Function.GENERATE_MODEL);
    }//GEN-LAST:event_gnerateModelActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        // TODO add your handling code here:
        TableHelper tableHelper = new TableHelper(mlModel);
        switch(learningMode){
            case MLGlobal.CLASSIFYING_MODE:
                tableHelper.saveToTableFromClassifyRes("分类结果");
                break;
            case MLGlobal.CLUSTERING_MODE:
                tableHelper.saveToTableFromClusterRes("聚类结果");
                break;
            default:
                
        }
        
    }//GEN-LAST:event_saveActionPerformed

    private void executeFunction(Function function,int runModelflag) {
        Frame parent = WindowManager.getDefault().getMainWindow();
        final ProgressDialog progressDialog = new ProgressDialog(parent, true);

        function.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if (propertyName.startsWith("print")) {
                    MessagePanel messagePanel = (MessagePanel) mlGlobal.messagePanel;
                    messagePanel.print(evt.getPropertyName(), (String) evt.getNewValue());
                } else if (propertyName.equals("progressPrint")) {
                    progressDialog.setMessage(evt.getNewValue().toString());
                } else if (propertyName.equals("done")) {
                    progressDialog.doClose();
                }
            }
        });
        if (mlModel.getVariables() == null) {
            JOptionPane.showMessageDialog(parent, "请先选择数据！");
            return;
        }
        function.setMLModel(mlModel);
        if (learningMode == MLGlobal.PREDICTING_MODE || learningMode == MLGlobal.CLASSIFYING_MODE) {
            MLDataModelHelper mlModelHelper = new MLDataModelHelper(mlModel);
            int xVarCount = mlModelHelper.getRealXVariableCount();
            if (xVarCount == 0) {
                JOptionPane.showMessageDialog(parent, "仅选择一个变量，无法操作！");
                return;
            }
        }
        switch(learningMode){
            case MLGlobal.PREDICTING_MODE:
                function.setParameters(parent);
                break;
            case MLGlobal.CLASSIFYING_MODE:
                if(runModelflag == Function.GENERATE_MODEL){
                   function.setParameters(parent); 
                }
                break;
            case MLGlobal.CLUSTERING_MODE:
                function.setParameters(parent); 
                break;
        }
        
        function.execute();
        progressDialog.setVisible(true);
        UpdatePanelFlag.DataPanelUpdateFlag = true;
        UpdatePanelFlag.CrossPlotUpdateFlag = true;
        UpdatePanelFlag.PlotPanelUpdateFlag = true;
        updatePagePanels();
        
    }

    private void updateMainPagePanels() {
        mainTabbedPane.removeAll();
        for (PagePanel pagePanel : mlGlobal.getPagePanels(learningMode)) {
            addLeftPagePanel(-1, pagePanel);
        }
    }

    private void updateFunctions() {
        functionComboBox.removeAllItems();
        for (FunctionProxy functionProxy : mlGlobal.getFunctionProxys(learningMode)) {
            functionComboBox.addItem(functionProxy.displayName);
        }
    }

    private void selectPagePanel(PagePanel panel) {
        if (mainTabbedPane.getComponentZOrder(panel) >= 0) {
            mainTabbedPane.setSelectedComponent(panel);
        }
    }

    private void variableTableRowSelected() {
        updatePagePanels();
    }
    
   

    public void updatePagePanels(){
        int mainOrSubPagePane = 2;
        PagePanel[] panels = getActivePagePanel(mainOrSubPagePane);
        for (PagePanel pagePanel : panels) {
            if (pagePanel instanceof DataPanel) {
                if (UpdatePanelFlag.DataPanelUpdateFlag) {
                    DataPanel panel = (DataPanel) pagePanel;
                    panel.setMLModel(mlModel);
                    UpdatePanelFlag.DataPanelUpdateFlag = false;
                }
            } else if (pagePanel instanceof HistogramPanel) {
                if (UpdatePanelFlag.HistogramUpdateFlag) {
                    HistogramPanel panel = (HistogramPanel) pagePanel;
                    panel.setMLModel(mlModel);
                    UpdatePanelFlag.HistogramUpdateFlag = false;
                    //panel.setSelectedVariableIndices(getSelectedVariableIndices());
                }
            } else if (pagePanel instanceof CrossPlotPanel) {
                if (UpdatePanelFlag.CrossPlotUpdateFlag) {
                    CrossPlotPanel panel = (CrossPlotPanel) pagePanel;
                    panel.setMLModel(mlModel);
                    UpdatePanelFlag.CrossPlotUpdateFlag = false;
                    //panel.setSelectedVariableIndices(getSelectedVariableIndices());
                }
            }else if (pagePanel instanceof PlotPanel) {
                if ( UpdatePanelFlag.PlotPanelUpdateFlag) {
                    PlotPanel panel = (PlotPanel) pagePanel;
                    panel.setMLModel(mlModel);
                    UpdatePanelFlag.PlotPanelUpdateFlag = false;
                    //panel.setSelectedVariableIndices(getSelectedVariableIndices());
                }
            }
        }
    }

    private int[] getSelectedVariableIndices() {
        int[] selectedRows = variableTable.getSelectedRows();
        int[] indices = new int[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            indices[i] = variableTableModel.getVariableIndex(selectedRows[i]);
        }
        return indices;
    }

    private PagePanel[] getActivePagePanel(int mainOrSubPagePane) {
        PagePanel leftPanel = null, rightPanel = null;
        if (mainOrSubPagePane == MAIN_PAGE_PANE || mainOrSubPagePane == BOTH_PAGE_PANE) {
            leftPanel = (PagePanel) mainTabbedPane.getSelectedComponent();
        }
        if (mainOrSubPagePane == SUB_PAGE_PANE || mainOrSubPagePane == BOTH_PAGE_PANE) {
            rightPanel = (PagePanel) subTabbedPane.getSelectedComponent();
        }
        if (leftPanel != null && rightPanel != null) {
            return new PagePanel[]{leftPanel, rightPanel};
        } else if (leftPanel != null) {
            return new PagePanel[]{leftPanel};
        } else if (rightPanel != null) {
            return new PagePanel[]{rightPanel};
        } else {
            return new PagePanel[0];
        }
    }

    private void setSubTabbedPaneVisuable(boolean b) {
        tabbedPaneSplitPane.setDividerSize(b ? 7 : 0);
        tabbedPaneSplitPane.setDividerLocation(b ? (this.getSize().width - mainSplitPane.getDividerLocation()) / 2 : 999999);
    }

    private int getOriginalMainPagePanelLocation(String panelID) {
        int tabIdx = 0;
        for (PagePanel pagePanel : mlGlobal.getPagePanels(learningMode)) {
            String pageId = pagePanel.getID();
            String tabId = ((PagePanel) mainTabbedPane.getComponentAt(tabIdx)).getID();
            if (pageId.equals(tabId)) {
                tabIdx++;
                if (tabIdx >= mainTabbedPane.getTabCount()) {
                    break;
                }
            } else if (pageId.equals(panelID)) {
                break;
            }
        }
        return tabIdx;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton classificationToggleButton;
    private javax.swing.JButton clearClusterBtn;
    private javax.swing.JToggleButton clusteringToggleButton;
    private javax.swing.JComboBox<String> functionComboBox;
    private javax.swing.JButton gnerateModel;
    private javax.swing.JButton inputDataButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JButton manageModelButton;
    private javax.swing.JToggleButton pagePaneSplitedToggleButton;
    private javax.swing.JToggleButton predictingToggleButton;
    private javax.swing.JButton runButton;
    private javax.swing.JButton save;
    private javax.swing.JButton selectVariableButton;
    private javax.swing.JTabbedPane subTabbedPane;
    private javax.swing.JSplitPane tabbedPaneSplitPane;
    private javax.swing.ButtonGroup topButtonGroup;
    private javax.swing.JTable variableTable;
    private javax.swing.JButton yButton;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
