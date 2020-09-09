/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.mllearning.MLGlobal;
import javax.swing.Renderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.statistics.HistogramDataset;

/**
 *
 * @author Administrator
 */
public class CreateHistDataset {

    /**
     * 得到过滤掉无效数据的数据集
     * @param data 原始数据
     * @param curveName 曲线名
     * @param bins 直方图道数
     */
    public static HistogramDataset getHistDataset(double[] data, String curveName, int binCount) {        

        HistogramDataset histDataset = new HistogramDataset();       
        histDataset.addSeries("", data, binCount);   
       
        return histDataset;
    }
  
    
    
//    public static HistogramDataset getHistDataset(double[] data, String curveName, int bins) {
//        int validCount = 0;
//        for (int i = 0; i < data.length; i++) {
//            if (data[i] != MLGlobal.INVALID_VALUE) {
//                ++validCount;
//            }
//        }
//
//        HistogramDataset histDataset = new HistogramDataset();
//
//        if (validCount != 0) {
//            double[] buffer = new double[validCount];
//            validCount = 0;
//            for (int i = 0; i < data.length; i++) {
//                if (data[i] != MLGlobal.INVALID_VALUE) {
//                    buffer[validCount++] = data[i];
//                }
//            }
//            histDataset.addSeries(curveName, buffer, bins);
//        }
//
//        return histDataset;
//    }
    
    /**
     * 获取未过滤无效数值的数据集
     * 
     * @param data 原始数据
     * @param curveName 曲线名
     * @param bins 直方图道数
     */
    
    public static HistogramDataset getRowHistDataset(double[] data, String curveName, int bins){
        HistogramDataset histDataset = new HistogramDataset();
        histDataset.addSeries(curveName,data, bins);
        return histDataset;
    }

    /**
     * 这个返回的数据集用来创建subChart，把给定曲线的直方图信息绘制到一张图里，
     * 在一个数据集里面添加两条曲线信息
     *
     * @param data1 变量x数据集
     * @param data2 变量y数据集
     * @param bins 直方图绘制的道数
     */
    public static HistogramDataset getHistDataset(double[] data1, String data1Name,
            double[] data2, String data2Name, int bins) {
        HistogramDataset histDataset = new HistogramDataset();
        histDataset.addSeries(data1Name, data1, bins);
        histDataset.addSeries(data2Name, data2, bins);
        return histDataset;
    }

    public static HistogramDataset getHistDataset(double[] data1, double[] data2, int bins) {
        HistogramDataset histDataset = new HistogramDataset();
        histDataset.addSeries("curveName", data1, bins);
        histDataset.addSeries("curveName", data2, bins);
        return histDataset;
    }

    /**
     * 这个返回的数据集用来创建mainChart，把所有的曲线的直方图信息都绘制到一张图里
     *
     * @param xDataset 变量x数据集
     * @param yDataset 变量y数据集
     * @param bins 直方图绘制的道数
     */
    public static HistogramDataset getHistDataset(double[][] xDataset, String[] xDataNames,
            double[] yDataset, String yDataName, int bins) {
        HistogramDataset histogramDataset = new HistogramDataset();
        for (int i = 0; i < xDataset.length; i++) {
            histogramDataset.addSeries(xDataNames[i], xDataset[i], bins);
        }
        histogramDataset.addSeries(yDataName, yDataset, bins);

        return histogramDataset;
    }

    public static HistogramDataset getHistDataset(double[][] xDataset, double[] yDataset, int bins) {
        HistogramDataset histogramDataset = new HistogramDataset();
        for (int i = 0; i < xDataset.length; i++) {
            histogramDataset.addSeries("curveName", xDataset[i], bins);
        }
        histogramDataset.addSeries("curveName", yDataset, bins);

        return histogramDataset;
    }
}
