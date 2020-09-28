/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.mllearning.MLGlobal;
import java.util.Arrays;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author 10797
 */
public class CreateScatterPlotData {

    private MLDataModel mlModel;
    private DataHelper dataHelper;
    private Variable[] variables;
    private double[] data;

    private int col;
    private int row;

    public MLDataModel getMLModel() {
        return this.mlModel;
    }

    public String getVariableName(int index) {
        return this.variables[index].name;
    }

    public String getVariableNameAndUnit(int index) {
        int indexInImported = dataHelper.getUsedVIndexInImported(index);
        String unitStr = dataHelper.getCurveUnit(indexInImported);
        if (unitStr.equals("")) {
            return this.variables[indexInImported].name;
        } else {
            return String.format("%s(%s) ", this.variables[indexInImported].name, dataHelper.getCurveUnit(indexInImported));
        }
    }

    public CreateScatterPlotData(MLDataModel mlModel) {
        this.mlModel = mlModel;
        this.dataHelper = new DataHelper(mlModel);
        this.variables = mlModel.getVariables();

        this.col = dataHelper.getUsedVariableCount() + 1;
        this.row = dataHelper.getRealRowCount();

        this.data = new double[dataHelper.getRawDataCount()];
    }

    public void updatePlotData(MLDataModel mLDataModel){
        this.mlModel=mLDataModel;
        this.dataHelper = new DataHelper(mlModel);
        this.data = new double[dataHelper.getRawDataCount()];
    }
    
    public XYDataset createDataset(int index1, int index2) {

        int len = dataHelper.readUsedData(index1, data);
        double[] array1 = Arrays.copyOf(data, len);
        len = dataHelper.readUsedData(index2, data);
        double[] array2 = Arrays.copyOf(data, len);

        XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
        XYSeries localXYSeries1 = new XYSeries("");

        for (int i = 0; i < row; i++) {
            if (array1[i] != MLGlobal.INVALID_VALUE && array2[i] != MLGlobal.INVALID_VALUE) {
                localXYSeries1.add(array1[i], array2[i]);
            }
        }

        localXYSeriesCollection.addSeries(localXYSeries1);

        return localXYSeriesCollection;
    }

    public XYDataset createClusterDataset(int index1, int index2) {

        int len = dataHelper.readUsedData(index1, data);
        double[] array1 = Arrays.copyOf(data, len);
        len = dataHelper.readUsedData(index2, data);
        double[] array2 = Arrays.copyOf(data, len);

        XYSeriesCollection localXYSeriesCollection = new XYSeriesCollection();
        XYSeries[] localXYSeries = new XYSeries[mlModel.clusterCount];
        for (int i = 0; i < localXYSeries.length; i++) {
            localXYSeries[i] = new XYSeries("Type" + i);
        }
        int tempIndex = 0;
        for (int i = 0; i < dataHelper.getRawDataCount(); i++) {
            
//            if (array1[i] != MLGlobal.INVALID_VALUE && array2[i] != MLGlobal.INVALID_VALUE) {
//                localXYSeries[mlModel.clusterResult[i]].add(array1[i], array2[i]);
//            }
            if(mlModel.dataRowSelectedFlags[i]==true){
                int whichCluster = mlModel.clusterResult[i];
                XYSeries whichSeries = localXYSeries[whichCluster];
                whichSeries.add(array1[tempIndex], array2[tempIndex]);
                tempIndex++;
            }
            
        }

        for (XYSeries s : localXYSeries) {
            localXYSeriesCollection.addSeries(s);
        }

        return localXYSeriesCollection;
    }

    /**
     * for debug
     */
    private void printCurveInfo(String curveName, double[][] data) {
        System.out.println(curveName);
        int max = Math.min(30, data[0].length);
        for (int i = 0; i < max; i++) {
            System.out.println(data[0][i] + " ," + data[1][i]);
        }
    }

}
