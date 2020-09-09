/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.mllearning.base.Cluster;
import cif.mllearning.base.Indice;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instances;

/**
 *
 * @author 10797
 */
public class DensityBasedClusterFunction extends Function {

    private int clusterCount = 12;
    private double minStdDev = 1.0E-6;
    private int[] clusterResult = null;

    @Override
    public boolean setParameters(Frame parentWindow) {
        DensityBasedDialog dialog = new DensityBasedDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        Object[] paras = mlModel.getParameters(this.getClass().getSimpleName());
        dialog.setClusterCount(paras == null ? clusterCount : (int) paras[0]);
        dialog.setMinStdDev(paras == null ? minStdDev : (double) paras[1]);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == BP_ANNDialog.RET_OK) {
            clusterCount = dialog.getClusterCount();
            minStdDev = dialog.getMinStdDev();
            Object[] params = new Object[]{clusterCount, minStdDev};
            mlModel.setParameters(this.getClass().getSimpleName(), params);
            return true;
        }
        return false;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        Instances dataSet = formDataSet();
        MakeDensityBasedClusterer DB = new MakeDensityBasedClusterer();
        DB.setMinStdDev(minStdDev);
        DB.setNumClusters(clusterCount);
        DB.buildClusterer(dataSet);

        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(DB);
        eval.evaluateClusterer(new Instances(dataSet));
        println(eval.clusterResultsToString());

        
        
        if (clusterResult == null || clusterResult.length != dataSet.size()) {
            clusterResult = new int[dataSet.size()];
        }

        for (int i = 0; i < dataSet.size(); i++) {
            clusterResult[i] = DB.clusterInstance(dataSet.get(i));
        }
        
        
            //cluster validity
//        Cluster[] clusters = new Cluster[clusterCount];
//        for (int i = 0; i < clusters.length; i++) {
//            clusters[i] = new Cluster();
//        }
//
//        for (int i = 0; i < dataSet.size(); i++) {
//            clusterResult[i] = DB.clusterInstance(dataSet.get(i));
//
//            clusters[clusterResult[i]].getInstances().add(dataSet.get(i));
//            if (clusters[clusterResult[i]].getCentroide() == null) {
//                DB.distributionForInstance(dataSet.get(i));
//                clusters[clusterResult[i]].setCentroide(DB.getClusterer().getClusterCentroids().get(clusterResult[i]));
//            }
//        }
//
//        Indice indice = ClusterValidity.calcularDunn(clusters, new EuclideanDistance());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularSilhouette(clusters, new EuclideanDistance());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularBDDunn(clusters, new EuclideanDistance());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularBDSilhouette(clusters, new EuclideanDistance());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularDavidBouldin(clusters, new EuclideanDistance());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularCalinskiHarabasz(clusters, new EuclideanDistance());
//        displayResult(indice);
        
        

        mlModel.clusterResult = clusterResult;
        mlModel.clusterCount = clusterCount;
        printLine();
        return 1;
    }

      private void displayResult(Indice indice) {
        println(indice.name);
        println("time: " + Long.toString(indice.time));
        println("result: " + Double.toString(indice.result));

    }
    
    
    private Instances formDataSet() {
        int varCount = dataHelper.getRealVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        ArrayList<Attribute> atts = new ArrayList<>();
        for (int i = 0; i < varCount; i++) {
            atts.add(new Attribute("x" + i));
        }
        Instances dataSet = new Instances("relation", atts, rowCount);

        for (int row = 0; row < rowCount; row++) {
            double[] buffer = new double[varCount];
            dataHelper.readRealRowData(row, buffer);
            dataSet.add(new DenseInstance(1.0, buffer));
        }
        return dataSet;
    }

}
