/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import cif.mllearning.base.UpdatePanelFlag;
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
   
    private Normalization normalization;
    private int hiddenNeuronCount = 16;
    private double learningRate = 0.05;
    private double maxError = 0.005;
    private int maxIteration = 1000;
    
    private int[] desiredY;
    
    
    
    @Override
    public boolean setParameters(Frame parentWindow) {
        BP_ANNDialog dialog = new BP_ANNDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        /////////////////////////
        int xCount = dataHelper.getRealXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        desiredY = new int[rowCount];
        dataHelper.getLabeledY(desiredY);
        
        int yCount = LoadConfigure.colorLayers.size();
        /////////////////////////

        Object[] paras = mlModel.getParameters(this.getClass().getSimpleName());
        hiddenNeuronCount = (int) (Math.sqrt(xCount + yCount) + 5);
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
        if (flag == Function.GENERATE_MODEL) {
            printDataMessage();
            DataSet dataSet = formLearningDataSet();
            MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(dataSet.getInputSize(), hiddenNeuronCount,8, dataSet.getOutputSize());
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
            String filePath = FunTools.getModelPath() + File.separator + FunTools.getModelFileName("Classfy_BP", mlModel);
            neuralNet.save(filePath);
            
           
            FunTools.saveModelAuxFile(false, filePath, mlModelHelper, normalization,this);
            
            
            
            int[] py = computeY(neuralNet, dataSet);
            int correctCount = FunTools.computeEquivalenceCount(desiredY, py);
            StringBuilder sb = new StringBuilder();
            sb.append("总数： ").append(py.length);
            sb.append(", 正确个数: ").append(correctCount);
            sb.append(", 正确率： ").append(String.format("%.2f", correctCount * 100.0 / py.length)).append("%\n");
            printHighlight(sb.toString());
            println("Save Model: " + filePath);
            printLine();
            return 0;
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

    }

    private void printDataMessage() {
        printHighlight("Variables:\n");
        String[] xVarNames = mlModelHelper.getRealXVariableNames();
        String yVarName = mlModelHelper.getRealYVariableName();
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

    private DataSet formLearningDataSet() {
        int xCount = dataHelper.getRealXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        normalization = new Normalization(xCount, -1);
        desiredY = new int[rowCount];
        dataHelper.getLabeledY(desiredY);
        
        int yCount = LoadConfigure.colorLayers.size();
        DataSet dataSet = new DataSet(xCount, yCount);

        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readRealXData(col, buffer);
            String variableName = dataHelper.getRealXVariableName(col);
            normalization.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        for (int row = 0; row < desiredY.length; row++) {
            int val = desiredY[row];
            dataSet.get(row).getDesiredOutput()[val] = 1;
        }
       
        return dataSet;
    }
    
    private DataSet formToClassifyDataSet() {
        int xCount = dataHelper.getRealXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        normalization = new Normalization(xCount, -1);
        
        int yCount = LoadConfigure.colorLayers.size();
        DataSet dataSet = new DataSet(xCount, yCount);
        
        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readRealXData(col, buffer);
            String variableName = dataHelper.getRealXVariableName(col);
            normalization.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        return dataSet;
    }
    
}
