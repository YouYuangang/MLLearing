/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.mllearning.MLGlobal;
import java.awt.Color;
import java.awt.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author 10797
 */
public class CreateScatterPlotPanel {

//    public static ChartPanel clone(ChartPanel chartPanel){
//        ChartPanel newChartPanel;
//        
//        return newChartPanel;
//    }
//    
    public static ChartPanel createChartPanel(XYDataset paramXYDataset) {

        JFreeChart localJFreeChart = createChart(paramXYDataset);

        //localJFreeChart.getLegend().visible = false;
        ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
        localChartPanel.setMouseWheelEnabled(true);
        localChartPanel.setPreferredSize(new Dimension(300, 300));

        //localChartPanel.addChartMouseListener(new MyChartMouseListener(localChartPanel));
        return localChartPanel;
    }

    public static ChartPanel createChartPanel(ChartPanel chartPanel) {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        String xLabel = plot.getDomainAxis(0).getLabel();
        String yLabel = plot.getRangeAxis(0).getLabel();
        XYDataset dataset = chartPanel.getChart().getXYPlot().getDataset();
        JFreeChart chart = createChart(dataset, xLabel, yLabel);

        if (dataset.getSeriesCount() <= 1) {
            chart.getLegend().visible = false;
        }

        ChartPanel localChartPanel = new ChartPanel(chart);
        localChartPanel.setMouseWheelEnabled(true);
        localChartPanel.setPreferredSize(new Dimension(800, 590));

        //localChartPanel.addChartMouseListener(new MyChartMouseListener(localChartPanel));
        return localChartPanel;
    }

    public static ChartPanel createChartPanel(ChartPanel chartPanel, Color fColor, Color bColor) {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        String xLabel = plot.getDomainAxis(0).getLabel();
        String yLabel = plot.getRangeAxis(0).getLabel();
        XYDataset dataset = chartPanel.getChart().getXYPlot().getDataset();
        JFreeChart chart = createChart(dataset, xLabel, yLabel);

        if (dataset.getSeriesCount() <= 1) {
            chart.getLegend().visible = false;
        }

        ChartPanel localChartPanel = new ChartPanel(chart);
        localChartPanel.setMouseWheelEnabled(true);
        localChartPanel.setPreferredSize(new Dimension(800, 590));

        XYPlot xYPlotplot = (XYPlot) localChartPanel.getChart().getPlot();
        xYPlotplot.setBackgroundPaint(bColor);

        XYLineAndShapeRenderer render = (XYLineAndShapeRenderer) xYPlotplot.getRenderer();
        render.setSeriesPaint(0, fColor);
        //localChartPanel.addChartMouseListener(new MyChartMouseListener(localChartPanel));
        return localChartPanel;
    }

    public static ChartPanel createChartPanel(XYDataset paramXYDataset, Dimension dim) {

        ChartPanel chartPanel = createChartPanel(paramXYDataset);
        chartPanel.setPreferredSize(dim);
        return chartPanel;

    }

    private static JFreeChart createChart(XYDataset paramXYDataset, String curve1, String curve2) {
        JFreeChart localJFreeChart = ChartFactory.createScatterPlot("", curve1, curve2, paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
        configChart(localJFreeChart);
        return localJFreeChart;
    }

    private static JFreeChart createChart(XYDataset paramXYDataset) {
        JFreeChart localJFreeChart = ChartFactory.createScatterPlot("", "depth", "value", paramXYDataset, PlotOrientation.VERTICAL, true, true, false);
        configChart(localJFreeChart);
        return localJFreeChart;
    }

    public static void configChart(JFreeChart chart) {
        XYPlot localXYPlot = (XYPlot) chart.getPlot();
        localXYPlot.setDomainCrosshairVisible(true);
        localXYPlot.setDomainCrosshairLockedOnData(true);
        localXYPlot.setRangeCrosshairVisible(true);
        localXYPlot.setRangeCrosshairLockedOnData(true);
        localXYPlot.setDomainZeroBaselineVisible(true);
        localXYPlot.setRangeZeroBaselineVisible(true);
        localXYPlot.setDomainPannable(true);
        localXYPlot.setRangePannable(true);
        NumberAxis localNumberAxis = (NumberAxis) localXYPlot.getDomainAxis();
        localNumberAxis.setAutoRangeIncludesZero(false);
    }

    public static ChartPanel createChartPanel(int realIndex1, int realIndex2, int index1, int index2, CreateScatterPlotData plotData) {

        JFreeChart localJFreeChart = getChart(realIndex1, realIndex2, index1, index2, plotData);

        //localJFreeChart.getLegend().visible = false;
        ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
        localChartPanel.setMouseWheelEnabled(true);
        localChartPanel.setPreferredSize(new Dimension(300, 300));

        //localChartPanel.addChartMouseListener(new MyChartMouseListener(localChartPanel));
        return localChartPanel;
    }

    //获取第index1行，第index2列的数据
    //
    public static JFreeChart getChart(int realIndex1, int realIndex2, int index1, int index2, CreateScatterPlotData plotData) {

        MLDataModel mlModel = plotData.getMLModel();
        XYDataset dataset;
        

        if (mlModel.learningMode == MLGlobal.CLUSTERING_MODE && mlModel.clusterResult != null) {
            dataset = plotData.createClusterDataset(realIndex1, realIndex2);
        } else {
            dataset = plotData.createDataset(realIndex1, realIndex2);
        }

//        JFreeChart localJFreeChart = createChart(dataset, plotData.getVariableName(index1), plotData.getVariableName(index2));       
        JFreeChart localJFreeChart = createChart(dataset, plotData.getVariableNameAndUnit(index1), plotData.getVariableNameAndUnit(index2));

        if (mlModel.learningMode != MLGlobal.CLUSTERING_MODE || mlModel.clusterResult == null) {
            localJFreeChart.getLegend().visible = false;
        }

        return localJFreeChart;
    }
}
