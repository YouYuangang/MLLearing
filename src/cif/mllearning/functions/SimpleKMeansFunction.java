/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.base.Global;
import cif.dataengine.DataPath;
import cif.dataengine.io.LogCategory;
import cif.dataengine.io.LogTable;
import cif.dataengine.io.TableFields;
import cif.dataengine.io.TableRecords;
import cif.mllearning.MLGlobal;
import cif.mllearning.base.Cluster;
import cif.mllearning.base.Indice;
import java.awt.Frame;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 *
 * @author 10797
 */
public class SimpleKMeansFunction extends Function {

    private int clusterCount = 12;
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
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Instances dataSet = formDataSet();
        SimpleKMeans KM = new SimpleKMeans();
        KM.setNumClusters(clusterCount);

        KM.buildClusterer(dataSet);

        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(KM);
        eval.evaluateClusterer(new Instances(dataSet));
        println(eval.clusterResultsToString());

        if (clusterResult == null || clusterResult.length != dataSet.size()) {
            clusterResult = new int[dataSet.size()];
        }

        //cluster validity
        Cluster[] clusters = new Cluster[clusterCount];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = new Cluster();
        }

        for (int i = 0; i < dataSet.size(); i++) {
            clusterResult[i] = KM.clusterInstance(dataSet.get(i));

            clusters[clusterResult[i]].getInstances().add(dataSet.get(i));
            if (clusters[clusterResult[i]].getCentroide() == null) {
                clusters[clusterResult[i]].setCentroide(KM.getClusterCentroids().get(clusterResult[i]));
            }
        }
        
        
//        // cluster validity functions  
//        Indice indice = ClusterValidity.calcularDunn(clusters, KM.getDistanceFunction());
//
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularSilhouette(clusters, KM.getDistanceFunction());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularBDDunn(clusters, KM.getDistanceFunction());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularBDSilhouette(clusters, KM.getDistanceFunction());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularDavidBouldin(clusters, KM.getDistanceFunction());
//        displayResult(indice);
//        
//        indice=ClusterValidity.calcularCalinskiHarabasz(clusters, KM.getDistanceFunction());
//        displayResult(indice);
        
        
       int[] clusterResult2 = new int[mlModel.dataRowSelectedFlags.length];
       int curIndex = 0;
       for(int i = 0;i<clusterResult2.length;i++){
           if(mlModel.dataRowSelectedFlags[i]==true){
               clusterResult2[i] = clusterResult[curIndex++];
           }else{
               clusterResult2[i] = 100;
           }
       }
        mlModel.clusterResultOil = clusterResult2;
        mlModel.clusterCountOil = clusterCount;
        
        
        /**
         * 这里创建新的表格的问题，先要判断表格是否存在，然后存在的话再删除表格内的所有元素，
         * 我还不如直接就是覆盖的方式创建表格
         * 
         * 所以最终决定就是直接覆盖 然后再填充数据
         */
        
        //这里涉及到数据填充的问题 还没解决 不过应该弄明白以后就很容易解决
        
        LogCategory logCategory = mlModel.inputDataPath.getCategory();
        //LogCategory logCategory = new DataPath("CIF://E:\\postgraduate1\\CIFLog Publish\\demoWorkspace\\吉林油田-黑\\testWell\\test3.cifp").getCategory();
        TableRecords tableRecords = new TableRecords();
        
         
         // 三 列
         //  开始深度   结束深度  聚类结果
          
        int colLength=3;
        TableFields tableFields=new TableFields();
        tableFields.init(colLength); 
        
        String[] names={"startDepth", "endDepth", "clusterResult"};
        String[] units={"meter","meter",""};
        for(int i=0;i<colLength;i++){
            tableFields.setName(i, names[i]);
            tableFields.setDataType(i, Global.DATA_DOUBLE);
            tableFields.setUnit(i, units[i]);
        }
        
        tableRecords.init(4, tableFields);
        tableRecords.setRecordDoubleData(0, 0, 1.0);
        LogTable logTable = logCategory.createTable(MLGlobal.CLUSTER_RESULT_TABLE_NAME, "", Global.LOGGING_COMMON_TABLE, tableRecords);
        

        
        printLine();
        return 1;
    }

    private void displayResult(Indice indice) {
        println(indice.name);
        println("time: " + Long.toString(indice.time));
        println("result: " + Double.toString(indice.result));

    }

    private Instances formDataSet() {
        int varCount = dataHelper.getOilXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        //int rowCount = mlModel.dataRowSelectedFlags.length;
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
