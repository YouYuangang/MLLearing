/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.mllearning.functions.som.SOM;
import java.awt.Frame;
import javax.swing.JOptionPane;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.31
 */
public class SOMClusterFunction extends Function {

    private int cluserCount = 15;
    private double learningRate = 0.9d;
    private int maxIteration = 30;
    private int mapSize = 13;
    private int neighborhood = 3;
    private boolean isHierarchicalCluster = false;

    @Override
    public boolean setParameters(Frame parentWindow) {
        int varCount = dataHelper.getOilXVariableCount();
        if (varCount < 2) {
            JOptionPane.showMessageDialog(parentWindow, "数据列数最小为2列");
            return false;
        }
        SOMClusterDialog dialog = new SOMClusterDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        Object[] paras = mlModel.getParameters(this.getClass().getSimpleName());
        dialog.setClusterCount(paras == null ? cluserCount : (int) paras[0]);
        dialog.setLearningRate(paras == null ? learningRate : (double) paras[1]);
        dialog.setIteration(paras == null ? maxIteration : (int) paras[2]);
        dialog.setNeighborhood(paras == null ? neighborhood : (int) paras[3]);
        dialog.setHierarchicalCluster(paras == null ? isHierarchicalCluster : (boolean) paras[4]);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == BP_ANNDialog.RET_OK) {
            cluserCount = dialog.getClusterCount();
            mapSize = cluserCount / varCount + (cluserCount % varCount == 0 ? 0 : 1);
            learningRate = dialog.getLearningRate();
            maxIteration = dialog.getIteration();
            neighborhood = dialog.getNeighborhood();
            isHierarchicalCluster = dialog.isHierarchicalCluster();
            Object[] params = new Object[]{cluserCount, learningRate, maxIteration, neighborhood, isHierarchicalCluster};
            mlModel.setParameters(this.getClass().getSimpleName(), params);
            return true;
        }
        return false;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        double[][] xData = formXData();
        SOM som = new SOM(xData, xData[0].length, mapSize, maxIteration, neighborhood);
        som.train();
        int[] nodes = som.getNodes();
        println("调整类别数为：" + (xData[0].length * mapSize));
        println("Self Organizing Feature Map：");
        println("各类别计数：");
        // Determine the number of observations assigned to each node
        int[] counts = new int[xData[0].length * mapSize];
        for (int i = 0; i < nodes.length; i++) {
            counts[nodes[i]] += 1;
        }
        for (int i = 0; i < counts.length; i++) {
            print(toStr(counts[i]));
            if ((i + 1) % mapSize == 0) {
                println("");
            }
        }
        println("\nSOM计算结束");
        printLine();
        return 1;
    }

    private double[][] formXData() {
        int varCount = dataHelper.getOilXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        double[][] xData = new double[rowCount][varCount];
        double[] buffer = new double[rowCount];
        for (int var = 0; var < varCount; var++) {
            dataHelper.readOilXData(var, buffer);
            for (int row = 0; row < rowCount; row++) {
                xData[row][var] = buffer[row];
            }
        }
        return xData;
    }
}
