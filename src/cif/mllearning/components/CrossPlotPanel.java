/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.components;

import cif.mllearning.base.CreateHistChart;
import cif.mllearning.base.CreateHistDataset;
import cif.mllearning.base.CreateScatterPlotData;
import cif.mllearning.base.CreateScatterPlotPanel;
import cif.mllearning.base.DataHelper;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.Variable;
import cif.mllearning.utils.ExportImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.windows.WindowManager;

/**
 *
 * @author Dell
 */
public class CrossPlotPanel extends PagePanel {

    private MLDataModel mlModel;
    private DataHelper dataHelper;
    private CreateScatterPlotData scatterPlotData;

    private final int CROSSPLOT_WIDTH = 300;
    private final int STUFFPLOT_WIDTH = 100;
    private int GapBetweenCharts = 15;

    private int binCount = 40;

    private double[] buffer;

    private ChartPanel[][] chartPanels;

    private Variable yVariable;
    private int yIndex;

    private final JPanel insertPanel = new JPanel();

    private Color fColor = new Color(9, 154, 203);
    private Color bColor = new Color(192, 192, 192);

    public CrossPlotPanel() {
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

        this.dataHelper = new DataHelper(mlModel);

        if (this.chartPanels == null || this.chartPanels.length != dataHelper.getRealVariableCount()) {
            this.chartPanels = new ChartPanel[dataHelper.getRealVariableCount()][dataHelper.getRealVariableCount()];
        }

        if (this.buffer == null || this.buffer.length != dataHelper.getRawDataCount()) {
            this.buffer = new double[dataHelper.getRawDataCount()];
        }

        if (this.scatterPlotData == null || this.scatterPlotData.getMLModel() != this.mlModel) {
            this.scatterPlotData = new CreateScatterPlotData(mlModel);
        }

        String startDep = String.format("%.3f", mlModel.curveStdep);
        String endDep = String.format("%.3f", mlModel.curveEndep);
        this.layerStartDepth.setText(startDep);
        this.layerEndDepth.setText(endDep);

        this.binsFiled.setText(binCount + "");

        for (int i = 0; i < mlModel.getVariables().length; i++) {
            if (mlModel.getVariables()[i].flag == MLDataModel.Y_VARIABLE) {
                yVariable = mlModel.getVariables()[i];
                yIndex = i;
                break;
            }
        }

    }

    private void initChartPanels() {
        HistogramDataset histogramDataset = new HistogramDataset();
        XYDataset dataset = new XYSeriesCollection();
        for (int i = 0; i < chartPanels.length; i++) {
            for (int j = 0; j < chartPanels[0].length; j++) {
                if (i == j) {
                    if (chartPanels[i][j] == null) {
                        chartPanels[i][j] = CreateHistChart.createChartPanel(histogramDataset, mlModel.getVariables()[i].name);

                    }
                } else {
                    if (chartPanels[i][j] == null) {
                        chartPanels[i][j] = CreateScatterPlotPanel.createChartPanel(dataset);
                        addMouseListener(chartPanels[i][j]);

                    }
                }
            }
        }

        updateCharts(binCount);

    }

    private void initMainPanel() {

        int variableCount = dataHelper.getRealVariableCount();
        int width = variableCount * (CROSSPLOT_WIDTH + 10) + STUFFPLOT_WIDTH;
        insertPanel.setPreferredSize(new Dimension(width, width));
        insertPanel.setLayout(new GridLayout(chartPanels.length, chartPanels.length, GapBetweenCharts, GapBetweenCharts));
        insertPanel.removeAll();
        for (int i = 0; i < chartPanels.length; i++) {
            for (int j = 0; j < chartPanels[0].length; j++) {
               if(i!=j){
                   insertPanel.add(chartPanels[i][j]);
               } 
            }
        }
        mainPanel.getViewport().add(insertPanel);

    }

    private void updateCharts(int binCount) {

        int realYIndex = 0, yIndex = 0;
        Variable[] variables = mlModel.getVariables();

        this.scatterPlotData = new CreateScatterPlotData(mlModel);

        for (int i = 0, moveX = 0, validIndexX = 0; i < variables.length; i++) {
            if (variables[i].flag == MLDataModel.UNSEL_VARIABLE) {
                continue;
            } else if (variables[i].flag == MLDataModel.Y_VARIABLE) {
                realYIndex = validIndexX;
                yIndex = i;
                validIndexX++;
                continue;
            }
            for (int j = 0, moveY = 0, validIndexY = 0; j < variables.length; j++) {
                if (variables[j].flag == MLDataModel.UNSEL_VARIABLE) {
                    continue;
                } else if (variables[j].flag == MLDataModel.Y_VARIABLE) {
                    validIndexY++;
                    continue;
                }
                if (moveX == moveY) {
                    int len = dataHelper.readValidData(validIndexX, buffer);
                    double[] temp = Arrays.copyOf(buffer, len);
                    HistogramDataset histogramDataset = CreateHistDataset.getHistDataset(temp, mlModel.getVariables()[i].name, binCount);
                    chartPanels[moveX][moveY].setChart(CreateHistChart.getChart(histogramDataset, mlModel.getVariables()[i].name));
                } else {

                    chartPanels[moveX][moveY].setChart(CreateScatterPlotPanel.getChart(validIndexX, validIndexY, i, j, scatterPlotData));

//                    addMouseListener(chartPanels[moveX][moveY]);
                }
                ++validIndexY;
                ++moveY;
            }
            ++validIndexX;
            ++moveX;
        }

        for (int i = 0, moveX = 0, validIndexX = 0; i < variables.length; i++) {
            if (variables[i].flag == MLDataModel.X_VARIABLE) {
                chartPanels[moveX][chartPanels.length - 1].setChart(CreateScatterPlotPanel.getChart(validIndexX, realYIndex, i, yIndex, scatterPlotData));
                ++validIndexX;
                ++moveX;
            } else if (variables[i].flag == MLDataModel.Y_VARIABLE) {
                ++validIndexX;
            }
        }

        for (int i = 0, moveY = 0, validIndexY = 0; i < variables.length; i++) {
            if (variables[i].flag == MLDataModel.X_VARIABLE) {
                chartPanels[chartPanels.length - 1][moveY].setChart(CreateScatterPlotPanel.getChart(realYIndex, validIndexY, yIndex, i, scatterPlotData));
                ++validIndexY;
                ++moveY;
            } else if (variables[i].flag == MLDataModel.Y_VARIABLE) {
                ++validIndexY;
            }
        }

        int len = dataHelper.readValidData(realYIndex, buffer);
        double[] temp = Arrays.copyOf(buffer, len);
        HistogramDataset histogramDataset = CreateHistDataset.getHistDataset(temp, mlModel.getVariables()[yIndex].name, binCount);
        chartPanels[chartPanels.length - 1][chartPanels.length - 1].setChart(CreateHistChart.getChart(histogramDataset, mlModel.getVariables()[yIndex].name));

        changeHistForegroundColor(fColor);
        changeHistBackgroundColor(bColor);

    }

    private Paint getBackgroundPaint() {
        if (chartPanels == null || chartPanels[0] == null) {
            return null;
        }
        return chartPanels[0][0].getChart().getPlot().getBackgroundPaint();
    }

    private Paint getForegroundPaint() {
        if (chartPanels == null || chartPanels[0] == null) {
            return null;
        }
        XYPlot plot = (XYPlot) chartPanels[0][0].getChart().getPlot();
        return plot.getRenderer().getSeriesPaint(0);
    }

    // change Hist background Color
    private void changeHistBackgroundColor(Color color) {
        if (chartPanels == null) {
            return;
        }
        for (int i = 0; i < chartPanels.length; i++) {
            for (int j = 0; j < chartPanels[0].length; j++) {
                XYPlot plot = (XYPlot) chartPanels[i][j].getChart().getPlot();
                plot.setBackgroundPaint(color);
            }
        }
    }

    // change hist foreground color
    private void changeHistForegroundColor(Color color) {
        if (chartPanels == null) {
            return;
        }
        for (int i = 0; i < chartPanels.length; i++) {
            for (int j = 0; j < chartPanels[0].length; j++) {
                XYPlot plot = (XYPlot) chartPanels[i][j].getChart().getPlot();
                if (i == j) {
                    XYBarRenderer render = (XYBarRenderer) plot.getRenderer();
                    render.setSeriesPaint(0, color);
                } else {
                    XYLineAndShapeRenderer render = (XYLineAndShapeRenderer) plot.getRenderer();
                    render.setSeriesPaint(0, color);
                }

            }
        }
    }

    // 这里出大问题了  我不知道它应该怎么弄有点儿头疼
    private void addMouseListener(ChartPanel chartPanel) {

        Frame parent = WindowManager.getDefault().getMainWindow();

        chartPanel.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                if (cme.getTrigger().getClickCount() >= 2) {

//                    ChartPanel cp = CreateScatterPlotPanel.createChartPanel(chartPanel.getChart().getXYPlot().getDataset(),
//                            new Dimension(800, 590));
//                    ChartPanel cp = CreateScatterPlotPanel.createChartPanel(chartPanel);
                    ChartPanel cp = CreateScatterPlotPanel.createChartPanel(chartPanel, fColor, bColor);
                    cp.addChartMouseListener(new ChartMouseListener(){
                        @Override
                        public void chartMouseClicked(ChartMouseEvent cme) {
                            Frame parent = WindowManager.getDefault().getMainWindow();
                            JOptionPane.showMessageDialog(parent, "here");
                             //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void chartMouseMoved(ChartMouseEvent cme) {
                          
                        }
                        
                    });

                    XYPlot plot = chartPanel.getChart().getXYPlot();
                    String xLabel = plot.getDomainAxis(0).getLabel();
                    String yLabel = plot.getRangeAxis(0).getLabel();

//                   String yLabelString=plot.getDomainAxis(1).getLabel();
                    ShowChartDialog dialog = new ShowChartDialog(parent, true, cp);
                    dialog.setTitle(String.format("交会图：%s - %s", xLabel, yLabel));
//                    dialog.setChartPanel(cp);                    
                    dialog.setVisible(true);

//                    Dimension dimension = dialog.getMainPanelDim();
//                    Dimension dimension1=dialog.getSize();
//                    System.out.println("");
                    if (dialog.getReturnStatus() == ShowChartDialog.RET_OK) {
                        System.out.println("do nothing");
//                    }

                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {
                return;
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        binsLabel = new javax.swing.JLabel();
        binsFiled = new javax.swing.JTextField();
        applyBtn = new javax.swing.JButton();
        mainPanel = new javax.swing.JScrollPane();
        layerDepthLabel = new javax.swing.JLabel();
        layerStartDepth = new javax.swing.JTextField();
        toLabel = new javax.swing.JLabel();
        layerEndDepth = new javax.swing.JTextField();
        exportImg = new javax.swing.JButton();
        settingBtn = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        org.openide.awt.Mnemonics.setLocalizedText(binsLabel, org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.binsLabel.text")); // NOI18N

        binsFiled.setText(org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.binsFiled.text")); // NOI18N
        binsFiled.setPreferredSize(new java.awt.Dimension(40, 21));

        org.openide.awt.Mnemonics.setLocalizedText(applyBtn, org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.applyBtn.text")); // NOI18N
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(layerDepthLabel, org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.layerDepthLabel.text")); // NOI18N

        layerStartDepth.setText(org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.layerStartDepth.text")); // NOI18N
        layerStartDepth.setPreferredSize(new java.awt.Dimension(80, 21));
        layerStartDepth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layerStartDepthActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(toLabel, org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.toLabel.text")); // NOI18N

        layerEndDepth.setText(org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.layerEndDepth.text")); // NOI18N
        layerEndDepth.setPreferredSize(new java.awt.Dimension(80, 21));

        exportImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/exportImg.jpg"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(exportImg, org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.exportImg.text")); // NOI18N
        exportImg.setPreferredSize(new java.awt.Dimension(24, 24));
        exportImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportImgActionPerformed(evt);
            }
        });

        settingBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cif/mllearning/icons/setting.jpg"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(settingBtn, org.openide.util.NbBundle.getMessage(CrossPlotPanel.class, "CrossPlotPanel.settingBtn.text")); // NOI18N
        settingBtn.setPreferredSize(new java.awt.Dimension(24, 24));
        settingBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
                .addComponent(exportImg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(settingBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportImg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(layerDepthLabel)
                        .addComponent(layerStartDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(toLabel)
                        .addComponent(layerEndDepth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(binsLabel)
                        .addComponent(binsFiled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(applyBtn)
                        .addComponent(settingBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
        // TODO add your handling code here:

        String startDepthStr = layerStartDepth.getText();
        String endDepthStr = layerEndDepth.getText();
        String binsString = binsFiled.getText();
        
        double stDep= Double.valueOf(startDepthStr);
        double endDep= Double.valueOf(endDepthStr);
        
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

        if (this.buffer == null || this.buffer.length != dataHelper.getRawDataCount()) {
            this.buffer = new double[dataHelper.getRawDataCount()];
        }

        if (mlModel.dataRowSelectedFlags.length != dataHelper.getRawDataCount()) {
            mlModel.dataRowSelectedFlags = new boolean[dataHelper.getRawDataCount()];
            for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
                mlModel.dataRowSelectedFlags[i] = true;
            }
        }

        scatterPlotData.updatePlotData(mlModel);

//        int val = mlModel.dataRowSelectedFlags.length;
        updateCharts(binCount);
    }//GEN-LAST:event_applyBtnActionPerformed

    private void exportImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportImgActionPerformed
        // TODO add your handling code here:
        ExportImage.exportImage(insertPanel, this);
    }//GEN-LAST:event_exportImgActionPerformed

    private void settingBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingBtnActionPerformed
        // TODO add your handling code here:
        Frame parent = WindowManager.getDefault().getMainWindow();
        CrossplotSettingDialog dialog = new CrossplotSettingDialog(parent, true);
        dialog.setLocationRelativeTo(parent);
        dialog.setBackgroundColor((Color) getBackgroundPaint());
        dialog.setForeGroundColor((Color) getForegroundPaint());
        dialog.setVisible(true);
        dialog.setFocusable(true);
        if (dialog.getReturnStatus() == HistSettingDialog.RET_OK) {
            this.fColor = dialog.getForegroundColor();
            this.bColor = dialog.getBackgroundColor();

            changeHistForegroundColor(this.fColor);
            changeHistBackgroundColor(this.bColor);
        }
    }//GEN-LAST:event_settingBtnActionPerformed

    private void layerStartDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layerStartDepthActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_layerStartDepthActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyBtn;
    private javax.swing.JTextField binsFiled;
    private javax.swing.JLabel binsLabel;
    private javax.swing.JButton exportImg;
    private javax.swing.JLabel layerDepthLabel;
    private javax.swing.JTextField layerEndDepth;
    private javax.swing.JTextField layerStartDepth;
    private javax.swing.JScrollPane mainPanel;
    private javax.swing.JButton settingBtn;
    private javax.swing.JLabel toLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getTitle() {
        return "交会图";
    }

    @Override
    public String getIconName() {
        return "crossPlot16.png";
    }

    @Override
    public String getID() {
        return "crossPlot";
    }

}
