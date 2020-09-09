/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import java.awt.Color;
import java.awt.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

/**
 *
 * @author Administrator
 */
public class CreateHistChart {

    public static ChartPanel createChartPanel(HistogramDataset histogramDataset, String curveName) {
        ChartPanel panel = new ChartPanel(getChart(histogramDataset, curveName));
        panel.setPreferredSize(new Dimension(300, 300));
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    public static ChartPanel createChartPanel(ChartPanel chartPanel, Color fColor, Color bColor) {
        String curveName = chartPanel.getChart().getXYPlot().getDomainAxis(0).getLabel();
        HistogramDataset dataset = (HistogramDataset) chartPanel.getChart().getXYPlot().getDataset();

        JFreeChart chart = getChart(dataset, curveName);

        ChartPanel localChartPanel = new ChartPanel(chart);
        XYPlot plot = (XYPlot) localChartPanel.getChart().getPlot();
        if (bColor != null) {
            plot.setBackgroundPaint(bColor);

        }

        XYBarRenderer render = (XYBarRenderer) plot.getRenderer();
        render.setSeriesPaint(0, fColor);

        return localChartPanel;
    }

    public static JFreeChart getChart(HistogramDataset histogramDataset, String curveName) {
        JFreeChart chart = ChartFactory.createHistogram("",
                curveName, "count", histogramDataset,
                PlotOrientation.VERTICAL, true, true, false);
        return configChart(chart);
    }

    public static JFreeChart getChart(HistogramDataset histogramDataset) {
        JFreeChart chart = ChartFactory.createHistogram("",
                "value", "count", histogramDataset,
                PlotOrientation.VERTICAL, true, true, false);
        return configChart(chart);
    }

    public static JFreeChart getChart(String title, HistogramDataset histogramDataset) {

        JFreeChart chart = ChartFactory.createHistogram(title,
                "values", "counts", histogramDataset,
                PlotOrientation.VERTICAL, true, true, true);
        return configChart(chart);
    }

    public static JFreeChart getChart(double[] data, String curveName) {
        HistogramDataset histogramDataset = CreateHistDataset.getHistDataset(data, curveName, 10);
        return getChart(curveName, histogramDataset);
    }

    public static JFreeChart getChart(double[] data1, String curveName1,
            double[] data2, String curveName2, int bins) {
        HistogramDataset histogramDataset = CreateHistDataset.getHistDataset(data1, curveName1,
                data2, curveName2, bins);
        String title = curveName1 + "--" + curveName2;
        return getChart(title, histogramDataset);
    }

    public static JFreeChart getChart(double[][] xData, String[] xCurveNames,
            double[] yData, String yCurveName, int bins) {
        HistogramDataset histogramDataset = CreateHistDataset.getHistDataset(xData, xCurveNames,
                yData, yCurveName, bins);
        return getChart("", histogramDataset);
    }

    private static JFreeChart configChart(JFreeChart chart) {

        chart.getLegend().setVisible(false);

        XYPlot xYPlot = (XYPlot) chart.getPlot();
        xYPlot.setDomainPannable(true);
        xYPlot.setRangePannable(true);
        xYPlot.setForegroundAlpha(0.85f);
        xYPlot.setForegroundAlpha(0.85F);

//        NumberAxis localNumberAxis = (NumberAxis)xYPlot.getRangeAxis();
//        localNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYBarRenderer localXYBarRenderer = (XYBarRenderer) xYPlot.getRenderer();
        localXYBarRenderer.setDrawBarOutline(false);
        localXYBarRenderer.setBarPainter(new StandardXYBarPainter());
        localXYBarRenderer.setShadowVisible(false);
        return chart;
    }

}
