/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 *
 * @author wangcaizhi
 * @create 2019.4.9
 */
public class EMClusterFunction extends Function {

    private int maxIteration = 100;
    private int clusterCount = 15;
    private int[] clusterResult = null;

    @Override
    public boolean setParameters(Frame parentWindow) {
        Object[] paras = mlModel.getParameters(this.getClass().getSimpleName());
        int count0 = (paras == null ? clusterCount : (int) paras[0]);
        String input = JOptionPane.showInputDialog(parentWindow, "聚类数：", count0);
        if (input != null && !input.trim().isEmpty()) {
            int count = FunTools.toInt(input);
            if (count < 1) {
                return false;
            } else {
                clusterCount = count;
                Object[] params = new Object[]{clusterCount};
                mlModel.setParameters(this.getClass().getSimpleName(), params);
                return true;
            }
        }
        return false;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        Instances dataSet = formDataSet();
        EM em = new EM();
        em.setNumClusters(clusterCount);
        em.setMaximumNumberOfClusters(10);
        em.buildClusterer(dataSet);
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(em);
        eval.evaluateClusterer(new Instances(dataSet));
        println(eval.clusterResultsToString());

        if (clusterResult == null || clusterResult.length != dataSet.size()) {
            clusterResult = new int[dataSet.size()];
        }

        for (int i = 0; i < dataSet.size(); i++) {
            clusterResult[i] = em.clusterInstance(dataSet.get(i));
        }

        mlModel.clusterResult = clusterResult;
        mlModel.clusterCount = clusterCount;
        printLine();

        return 1;
    }

    private Instances formDataSet() {
        int varCount = dataHelper.getOilXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        ArrayList<Attribute> atts = new ArrayList<>();
        for (int i = 0; i < varCount; i++) {
            atts.add(new Attribute("x" + i));
        }
        Instances dataSet = new Instances("relation", atts, rowCount);

        for (int row = 0; row < rowCount; row++) {
            double[] buffer = new double[varCount];
            dataHelper.readRealRowOilXData(row, buffer);
            dataSet.add(new DenseInstance(1.0, buffer));
        }
        return dataSet;
    }
}
