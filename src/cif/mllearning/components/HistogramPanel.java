/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.components;

import cif.mllearning.base.CreateHistChart;
import cif.mllearning.base.CreateHistDataset;
import cif.mllearning.base.DataHelper;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.Variable;
import cif.mllearning.utils.ExportImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.openide.windows.WindowManager;

/**
 *
 * @author wangcaizhi
 *
 */
public class HistogramPanel extends PagePanel {

    private MLDataModel mlModel;
    private DataHelper dataHelper;
    private final DataPanelTableModel tableModel = new DataPanelTableModel();

    private int binCount = 40;
    private double startDepth;
    private double endDepth;

    private int variableCount;

    private double[] buffer;

    private Variable[] variables;

    private ChartPanel[] chartPanels;

    private final JPanel insertPanel = new JPanel();

    private boolean removeInvalidValue = true;

    private double histWidthPercent = 1;
    private int histWidth;
    private int histHeight = 300;

    //用来记录修改直方图的表格中的value
    private List<Vector<Object>> tableValueList;

    private Color fColor = new Color(9, 154, 203);
    private Color bColor;

    /**
     * Creates new form HistogramPanel
     */
    public HistogramPanel() {
        initComponents();
    }

    public void setMLModel(MLDataModel mlModel) {
        this.mlModel = mlModel;
        if (mlModel.getVariables() == null) {
            JOptionPane.showMessageDialog(this, "没有选择数据！");
            return;
        }
        initData();
        initChartPanels();
        initMainPanel();

    }

    private void initData() {

        this.startDepth = mlModel.curveStdep;
        this.endDepth = mlModel.curveEndep;
        this.variables = mlModel.getVariables();

        this.dataHelper = new DataHelper(mlModel);
        this.variableCount = dataHelper.getUsedVariableCount();
        //this.rowCount = dataHelper.getRealRowCount();

        this.tableModel.setMLModel(mlModel);

        if (chartPanels == null || chartPanels.length != variableCount) {
            this.chartPanels = new ChartPanel[variableCount];
        }

        this.buffer = new double[mlModel.dataRowSelectedFlags.length];
        String startDep = String.format("%.3f", startDepth);
        String endDep = String.format("%.3f", endDepth);
        this.layerStartDepth.setText(startDep);
        this.layerEndDepth.setText(endDep);

        this.binsFiled.setText(binCount + "");

        histWidth = (int) (mainPanel.getWidth() * 0.95 * histWidthPercent);
    }

    private void initChartPanels() {
        HistogramDataset histogramDataset = new HistogramDataset();

        for (int i = 0, j = 0; j < variableCount; i++, j++) {
            if (chartPanels[i] == null) {
                chartPanels[i] = new ChartPanel(CreateHistChart.getChart(histogramDataset, ""));
                chartPanels[i].setPreferredSize(new Dimension(histWidth, histHeight));
                chartPanels[i].setMouseWheelEnabled(true);

                addMouseClickListener(chartPanels[i]);

            }

        }

        updateChartPanels(binCount);
    }

    private void reCreateChartPanels() {

        histWidth = (int) (mainPanel.getWidth() * 0.95 * histWidthPercent);

        HistogramDataset histogramDataset = new HistogramDataset();

        for (int i = 0, j = 0; j < variableCount; i++, j++) {
            chartPanels[i] = new ChartPanel(CreateHistChart.getChart(histogramDataset, ""));
            chartPanels[i].setPreferredSize(new Dimension(histWidth, histHeight));
            chartPanels[i].setMouseWheelEnabled(true);
            addMouseClickListener(chartPanels[i]);
        }

        updateChartPanels(binCount);
    }

    private void updateChartPanels(int binCount) {
        if (dataHelper.getRealRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Warning：不允许将数据全部设置无效！");
            return;
        }

        HistogramDataset histogramDataset;
        Variable yVariable = null;
        int yIndex = 0;//记录y在mlModel.variables中的索引;
        boolean meetY = false;
        int validIndex = 0;
        
        for(int i = 0;i<dataHelper.getUsedVariableCount();i++){
            int len = dataHelper.readUsedData(i, this.buffer);
            double[] temp = Arrays.copyOf(this.buffer, len);
            histogramDataset = CreateHistDataset.getHistDataset(temp, dataHelper.getUsedVariableName(i), binCount);
            chartPanels[i].setChart(CreateHistChart.getChart(histogramDataset, variables[i].name));
        }
        
        /*for (int i = 0, moveX = 0; i < variables.length; i++) {
            if (variables[i].flag == MLDataModel.X_VARIABLE) {
                int len = dataHelper.readUsedData(validIndex, this.buffer);
                double[] temp = Arrays.copyOf(this.buffer, len);
                histogramDataset = CreateHistDataset.getHistDataset(temp, variables[i].name, binCount);
                chartPanels[moveX].setChart(CreateHistChart.getChart(histogramDataset, variables[i].name));
                moveX++;
                ++validIndex;
                if (!meetY) {
                    ++yIndex;
                }
            } else if (variables[i].flag == MLDataModel.Y_VARIABLE) {
                yVariable = variables[i];
                validIndex++;
                meetY = true;
            }

        }

        if (yVariable != null) {
            int len = dataHelper.readValidData(yIndex, this.buffer);
            double[] temp = Arrays.copyOf(this.buffer, len);
            histogramDataset = CreateHistDataset.getHistDataset(temp, yVariable.name, binCount);
            chartPanels[variableCount - 1].setChart(CreateHistChart.getChart(histogramDataset, yVariable.name));
        }*/

        changeHistForegroundColor(fColor);
        changeHistBackgroundColor(bColor);
    }

    private void initMainPanel() {

        int width = (int) (mainPanel.getWidth() * 0.5);
        int rows = mainPanel.getWidth() / histWidth;
        int height = (variableCount / rows + 1) * (histHeight + 10);

        insertPanel.setPreferredSize(new Dimension(width, height));
        insertPanel.setLayout(new FlowLayout());
        insertPanel.removeAll();
        for (int i = 0; i < variableCount; i++) {
            insertPanel.add(chartPanels[i]);
        }
        mainPanel.getViewport().add(insertPanel);
    }

    //这里负责处理直方图变换重画的问题 repaint
    public void repaintHistPanel() {
        reCreateChartPanels();
        initMainPanel();
    }

    private void changeHistBackgroundColor(ChartPanel chartPanel, Color color) {
        if (color == null) {
            return;
        }
        XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
        plot.setBackgroundPaint(color);
    }

    // change Hist background Color
    private void changeHistBackgroundColor(Color color) {
        if (chartPanels == null) {
            return;
        }
        for (int i = 0; i < chartPanels.length; i++) {
            changeHistBackgroundColor(chartPanels[i], color);
        }
    }

    private void changeHistForegroundColor(ChartPanel chartPanel, Color color) {
        if (color == null) {
            return;
        }
        JFreeChart chart = chartPanel.getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        XYBarRenderer render = (XYBarRenderer) plot.getRenderer();
        render.setSeriesPaint(0, color);
    }

    // change hist foreground color
    private void changeHistForegroundColor(Color color) {
        if (chartPanels == null) {
            return;
        }
        for (int i = 0; i < chartPanels.length; i++) {
            changeHistForegroundColor(chartPanels[i], color);
        }
    }

    private Paint getBackgroundPaint() {
        if (chartPanels == null) {
            return null;
        }
        return chartPanels[0].getChart().getPlot().getBackgroundPaint();
    }

    private Paint getForegroundPaint() {
        if (chartPanels == null) {
            return null;
        }
        XYPlot plot = (XYPlot) chartPanels[0].getChart().getPlot();
        return plot.getRenderer().getSeriesPaint(0);
    }

    /**
     * 通过获取编辑后的tableValueList来设置那些无效值
     */
    private void setValueInvalid() {
        for (int i = 0; i < chartPanels.length; i++) {
            double min = Double.valueOf((String) tableValueList.get(i).get(1));
            double max = Double.valueOf((String) tableValueList.get(i).get(2));
            for (int j = 0; j < tableModel.getRowCount(); j++) {
                double value = Double.valueOf((String) tableModel.getValueAt(j, i + 2));
                //如果值不满足min-max，就将其设置为无效
                if (value < min || value > max) {
                    tableModel.setRowEnabled(j, false);
                    tableModel.fireTableCellUpdated(j, 0);
                }
            }
        }

    }

    private void setValueInvalid(double leftValue, double rightValue, boolean reverse, int curveIndex) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double value = Double.valueOf((String) tableModel.getValueAt(i, curveIndex + 2));
            //如果值不满足min-max，就将其设置为无效
            if (!reverse) {
                if (value <= leftValue || rightValue <= value) {
                    tableModel.setRowEnabled(i, false);
                    tableModel.fireTableCellUpdated(i, 0);
                }
            } else {
                if (leftValue < value && value < rightValue) {
                    tableModel.setRowEnabled(i, false);
                    tableModel.fireTableCellUpdated(i, 0);
                }
            }
        }

    }

    /**
     * 负责维护表数据列表
     *
     * 数据列表里面包含三个object：curveName、histXStartValue、histXEndValue，填充数据填充的就是这三个数据
     */
    private void maintainTableValueList() {
        if (tableValueList != null) {
            tableValueList.clear();
        }

        for (int i = 0; i < chartPanels.length; i++) {
            HistogramDataset dataset = (HistogramDataset) chartPanels[i].getChart().getXYPlot().getDataset();
            double startX = dataset.getStartXValue(0, 0);
            double endX = dataset.getEndXValue(0, dataset.getItemCount(0) - 1);

            //初始化表格中的数据
            if (tableValueList == null) {
                tableValueList = new ArrayList<>();
                Vector<Object> v = new Vector<>();
                for (int j = 0; j < 3; j++) {
                    v.add("");
                }
                tableValueList.add(v);
            } else if (tableValueList.size() <= i) {
                Vector<Object> v = new Vector<>();
                for (int j = 0; j < 3; j++) {
                    v.add("");
                }
                tableValueList.add(v);
            }

            //填充表项数据
            tableValueList.get(i).set(0, mlModel.getVariables()[i].name);
            tableValueList.get(i).set(1, String.format("%.4f", startX));
            tableValueList.get(i).set(2, String.format("%.4f", endX));
        }
    }

    private void addMouseClickListener(ChartPanel chart) {

        Frame parent = WindowManager.getDefault().getMainWindow();

        chart.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {

                if (cme.getTrigger().getClickCount() >= 2) {

                    int curveIndex = mlModel.getCurveIndex(chart.getChart().getXYPlot().getDomainAxis(0).getLabel());
                    ChartPanel chartPanel = CreateHistChart.createChartPanel(chart, fColor, bColor);

                    HistDatePreprocessDIalog dialog = new HistDatePreprocessDIalog(parent, true);
                    dialog.setChartPanel(chartPanel);
                    dialog.setColor(fColor);
                    dialog.setVisible(true);

                    if (dialog.getReturnStatus() == HistDatePreprocessDIalog.RET_OK) {
                        double leftValue = dialog.getLeftValue();
                        double rightValue = dialog.getRightValue();
                        boolean reverse = dialog.getReverse();
                        setValueInvalid(leftValue, rightValue, reverse, curveIndex);
                        updateChartPanels(binCount);
                    }
//                    old version 
//                    HistBoundValueSettingDialog dialog = new HistBoundValueSettingDialog(parent, true);
//                    dialog.setLocationRelativeTo(null);
//                    maintainTableValueList();
//
//                    dialog.setList(tableValueList);
//                    dialog.setVisible(true);
//                    if (dialog.getReturnStatus() == HistBoundValueSettingDialog.RET_OK) {
//                        tableValueList = dialog.getList();
//                        setValueInvalid();
//                        updateChartPanels(binCount);
//                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {
                return;
            }
        });

    }

    public void setSelectedVariableIndices(int[] indices) {
//        Variable[] variables =  mlModel.getVariables();
//        textArea.setText("");
//        for (int i = 0; i < indices.length; i++) {
//            textArea.append(variables[indices[i]].name+"\n");
//        }
//        textArea.append("============================================================\n");
//       DataHelper dataHelper = new DataHelper(mlModel);
//       for(int i=0;i<dataHelper.getRawDataCount();i++){
//           for(int j=0;j<indices.length;j++){
//               textArea.append(Double.toString(dataHelper.getRawDoubleData(indices[j], i))+"   ");
//           }
//           textArea.append("\n");
//       }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JScrollPane();
        layerDepthLabel = new javax.swing.JLabel();
        layerStartDepth = new javax.swing.JTextField();
        toLabel = new javax.swing.JLabel();
        layerEndDepth = new javax.swing.JTextField();
        binsLabel = new javax.swing.JLabel();
        binsFiled = new javax.swing.JTextField();
        applyBtn = new javax.swing.JButton();
        settingsBtn = new javax.swing.JButton();
        exportImg = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        mainPanel.setBorder(null);

        org.openide.awt.Mnemonics.setLocalizedText(layerDepthLabel, org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.layerDepthLabel.text")); // NOI18N

        layerStartDepth.setText(org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.layerStartDepth.text")); // NOI18N
        layerStartDepth.setPreferredSize(new java.awt.Dimension(80, 21));
        layerStartDepth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layerStartDepthActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(toLabel, org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.toLabel.text")); // NOI18N

        layerEndDepth.setText(org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.layerEndDepth.text")); // NOI18N
        layerEndDepth.setPreferredSize(new java.awt.Dimension(80, 21));
        layerEndDepth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layerEndDepthActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(binsLabel, org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.binsLabel.text")); // NOI18N

        binsFiled.setText(org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.binsFiled.text")); // NOI18N
        binsFiled.setPreferredSize(new java.awt.Dimension(40, 21));

        org.openide.awt.Mnemonics.setLocalizedText(applyBtn, org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.applyBtn.text")); // NOI18N
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBtnActionPerformed(evt);
            }
        });

        settingsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/setting.jpg"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(settingsBtn, org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.settingsBtn.text")); // NOI18N
        settingsBtn.setPreferredSize(new java.awt.Dimension(24, 24));
        settingsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsBtnActionPerformed(evt);
            }
        });

        exportImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/exportImg.jpg"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(exportImg, org.openide.util.NbBundle.getMessage(HistogramPanel.class, "HistogramPanel.exportImg.text")); // NOI18N
        exportImg.setPreferredSize(new java.awt.Dimension(24, 24));
        exportImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportImgActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(layerDepthLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layerStartDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layerEndDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(binsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(binsFiled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(applyBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 171, Short.MAX_VALUE)
                .addComponent(exportImg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(settingsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(mainPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportImg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(layerDepthLabel)
                        .addComponent(layerStartDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(toLabel)
                        .addComponent(layerEndDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(binsLabel)
                        .addComponent(binsFiled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(applyBtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
        // TODO add your handling code here:

        String startDepthStr = layerStartDepth.getText();
        String endDepthStr = layerEndDepth.getText();
        String binsString = binsFiled.getText();

        double stDep = Double.valueOf(startDepthStr);
        double endDep = Double.valueOf(endDepthStr);

        if (stDep > endDep) {
            JOptionPane.showMessageDialog(this, "请重新选择深度范围: 终止深度大于起始深度！");
            return;
        } else if (stDep < mlModel.curveStDepBound) {
            JOptionPane.showMessageDialog(this, "请重新选择深度范围：起始深度超出曲线深度范围！");
            return;
        } else if (endDep > mlModel.curveEndDepBound) {
            JOptionPane.showMessageDialog(this, "请重新选择深度范围：终止深度超出曲线深度范围！");
            return;
        }

        this.mlModel.curveStdep = stDep;
        this.mlModel.curveEndep = endDep;

        try {
            binCount = Integer.valueOf(binsString);
        } catch (Exception e) {
            System.out.println("data format error, please input a integer");
        }

        if (buffer.length != dataHelper.getRawDataCount()) {
            this.buffer = new double[dataHelper.getRawDataCount()];
        }

        if (mlModel.dataRowSelectedFlags.length != dataHelper.getRawDataCount()) {
            mlModel.dataRowSelectedFlags = new boolean[dataHelper.getRawDataCount()];
            for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
                mlModel.dataRowSelectedFlags[i] = true;
            }
        }
        mlModel.predictResult = null;
        mlModel.classifyResult = null;
        mlModel.clusterResult = null;
        
        //updateCharts(xData, yData, bins);
        //updateData();
        updateChartPanels(binCount);
    }//GEN-LAST:event_applyBtnActionPerformed

    private void layerStartDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layerStartDepthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_layerStartDepthActionPerformed

    private void layerEndDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layerEndDepthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_layerEndDepthActionPerformed

    private void settingsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsBtnActionPerformed
        // TODO add your handling code here:
        Frame parent = WindowManager.getDefault().getMainWindow();
        HistSettingDialog dialog = new HistSettingDialog(parent, true);
        dialog.setLocationRelativeTo(parent);
        dialog.setWidthValue(histWidthPercent);
        dialog.setBackgroundColor((Color) getBackgroundPaint());
        dialog.setForeGroundColor((Color) getForegroundPaint());
        dialog.setVisible(true);
        dialog.setFocusable(true);
        if (dialog.getReturnStatus() == HistSettingDialog.RET_OK) {
            histWidthPercent = dialog.getWidthValue();
            repaintHistPanel();
            this.fColor = dialog.getForegroundColor();
            this.bColor = dialog.getBackgroundColor();
            changeHistForegroundColor(dialog.getForegroundColor());
            changeHistBackgroundColor(dialog.getBackgroundColor());
        }

    }//GEN-LAST:event_settingsBtnActionPerformed

    private void exportImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportImgActionPerformed
        // TODO add your handling code here:
        ExportImage.exportImage(insertPanel, this);
    }//GEN-LAST:event_exportImgActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyBtn;
    private javax.swing.JTextField binsFiled;
    private javax.swing.JLabel binsLabel;
    private javax.swing.JButton exportImg;
    private javax.swing.JLabel layerDepthLabel;
    private javax.swing.JTextField layerEndDepth;
    private javax.swing.JTextField layerStartDepth;
    private javax.swing.JScrollPane mainPanel;
    private javax.swing.JButton settingsBtn;
    private javax.swing.JLabel toLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getTitle() {
        return "直方图";
    }

    @Override
    public String getIconName() {
        return "histogram.png";
    }

    @Override
    public String getID() {
        return "histogram";
    }

}
