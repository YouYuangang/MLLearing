/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.UpdatePanelFlag;
import cif.mllearning.base.Variable;
import cif.mllearning.configure.LoadConfigure;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.openide.windows.WindowManager;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.30
 */
public class ClassifyingBPFunction extends Function {
   
    private Normalization normalizationForOil;
    private Normalization normalizationForLith;
    
    private int hiddenNeuronCount = 16;
    private double learningRate = 0.05;
    private double maxError = 0.005;
    private int maxIteration = 200;
    
    private int[] desiredYOil = null;
    private int[] desiredYLith = null;
    
    public int XcountOil = 0;
    public int YcountOil = 0;
    public int XcountLith = 0;
    public int YcountLith = 0;
    
    public boolean oilFlag = false;
    public boolean lithFlag = false;
    
    @Override
    public boolean setParameters(Frame parentWindow) {
        BP_ANNDialog dialog = new BP_ANNDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        int yCount = mlModel.StringIntMapForOil.size();
        Object[] paras = mlModel.getParameters(this.getClass().getSimpleName());
        hiddenNeuronCount = (int) (Math.sqrt(XcountOil + yCount) + 5);
        dialog.setHiddenNeuronCount(paras == null ? hiddenNeuronCount : (int) paras[0]);
        dialog.setLearningRate(paras == null ? learningRate : (double) paras[1]);
        dialog.setMaxError(paras == null ? maxError : (double) paras[2]);
        dialog.setMaxIteration(paras == null ? maxIteration : (int) paras[3]);

        dialog.setVisible(true);
        if (dialog.getReturnStatus() == BP_ANNDialog.RET_OK) {
            hiddenNeuronCount = dialog.getHiddenNeuronCount();
            learningRate = dialog.getLearningRate();
            maxError = dialog.getMaxError();
            maxIteration = dialog.getMaxIteration();
            Object[] params = new Object[]{hiddenNeuronCount, learningRate, maxError, maxIteration};
            mlModel.setParameters(this.getClass().getSimpleName(), params);
            return true;
        }
        return false;
    }

    @Override
    protected Integer doInBackground() throws Exception { 
        Variable[] variables = mlModel.getVariables();
        for(int i = 0;i<variables.length;i++){
            if(variables[i].flag == MLDataModel.Y_VARIABLE_OIL){
                oilFlag = true;
            }
            if(variables[i].flag == MLDataModel.Y_VARIABLE_LITH){
                lithFlag = true;
            }
        }
        if (flag == Function.GENERATE_MODEL) {
            if(oilFlag){
                startOilModelTrain();
            }
            if(lithFlag){
                //startLithModelTrain();
            }
        } else {
            DataSet needToClassify = formToClassifyDataSet();
            MultiLayerPerceptron neuralNet = (MultiLayerPerceptron)NeuralNetwork.createFromFile(modelPath);
            int[] yByModel = computeY(neuralNet,needToClassify);
            mlModel.classifyResult = new int[mlModel.dataRowSelectedFlags.length];
            int j = 0;
            for(int i =0;i<mlModel.classifyResult.length;i++){
                if(mlModel.dataRowSelectedFlags[i]){
                    mlModel.classifyResult[i] = yByModel[j++];
                }
            }
            printHighlight("数据处理完成！");
            UpdatePanelFlag.DataPanelUpdateFlag = true;
            UpdatePanelFlag.HistogramUpdateFlag = true;
            UpdatePanelFlag.CrossPlotUpdateFlag = true;
            UpdatePanelFlag.PlotPanelUpdateFlag = true;
            return 0;
        }
        return 1;
    }
    
    public void startOilModelTrain() {
        printDataMessage();
        DataSet dataSet = formLearningOilDataSet();
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(dataSet.getInputSize(), hiddenNeuronCount, hiddenNeuronCount - 2, dataSet.getOutputSize());
        MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet.getLearningRule();
        learningRule.setMaxIterations(maxIteration);
        learningRule.addListener(new LearningEventListener() {
            @Override
            public void handleLearningEvent(LearningEvent event) {
                BackPropagation bp = (BackPropagation) event.getSource();
                String str = bp.getCurrentIteration() + ". iteration | Total Error: " + bp.getTotalNetworkError();
                progressPrint(str);
                println(str);
            }
        });
        learningRule.setLearningRate(learningRate);
        learningRule.setMaxError(maxError);
        println("开始训练：");
        neuralNet.learn(dataSet);
        println("完成训练");
        //String filePath = FunTools.getModelPath() + File.separator + FunTools.getModelFileName("Classfy_BP", mlModel);
        String filePath = LoadConfigure.trainedModelPath+File.separator+"oil_bp.model";
        neuralNet.save(filePath);
        FunTools.saveModelAuxFile(filePath,normalizationForOil,this);

        int[] py = computeY(neuralNet, dataSet);
        int correctCount = FunTools.computeEquivalenceCount(desiredYOil, py);
        StringBuilder sb = new StringBuilder();
        sb.append("总数： ").append(py.length);
        sb.append(", 正确个数: ").append(correctCount);
        sb.append(", 正确率： ").append(String.format("%.2f", correctCount * 100.0 / py.length)).append("%\n");
        printHighlight(sb.toString());
        println("Save Model: " + filePath);
        printLine();
    }

    private void printDataMessage() {
        printHighlight("Variables:\n");
        String[] xVarNames = mlModelHelper.getOilXVariableNames();
        String yVarName = mlModelHelper.getOilYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
        
    }

    private int[] computeY(MultiLayerPerceptron neuralNet, DataSet dataSet) {
        int[] y = new int[dataSet.size()];
        int index = 0;
        for (DataSetRow testSetRow : dataSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            int maxIndex = FunTools.compete(neuralNet.getOutput());
            y[index++] = maxIndex;
        }
        return y;
    }

    private DataSet formLearningOilDataSet() {
        
        int xCount = dataHelper.getOilXVariableCount();
        int yCount = mlModel.StringIntMapForOil.size();
        this.XcountOil = xCount;
        this.YcountOil = yCount;
        int rowCount = dataHelper.getRealRowCount();
        
        normalizationForOil = new Normalization(xCount, -1);
        
        //获取desiredYOil
        if(dataHelper.oilYVariableColumnIndex>=0){
            desiredYOil = new int[rowCount];
            for(int i = 0;i<desiredYOil.length;i++){
                String OilLabel = dataHelper.getRawStringData(dataHelper.oilYVariableColumnIndex, dataHelper.realRowIndices[i]);
                desiredYOil[i] = mlModel.StringIntMapForOil.get(OilLabel);
            } 
        }
        
        
        
        DataSet dataSet = new DataSet(xCount, yCount);

        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readOilXData(col, buffer);
            String variableName = dataHelper.getOilXVariableName(col);
            normalizationForOil.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        for (int row = 0; row < desiredYOil.length; row++) {
            int val = desiredYOil[row];
            dataSet.get(row).getDesiredOutput()[val] = 1;
        }
       
        return dataSet;
    }
    
    private DataSet formToClassifyDataSet() {
        int xCount = dataHelper.getOilXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        normalizationForOil = new Normalization(xCount, -1);
        
        int yCount = mlModel.StringIntMapForOil.size();
        DataSet dataSet = new DataSet(xCount, yCount);
        
        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readOilXData(col, buffer);
            String variableName = dataHelper.getOilXVariableName(col);
            normalizationForOil.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        return dataSet;
    }
    
}
