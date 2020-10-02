/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import java.awt.Frame;
import java.io.File;
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
public class PredictingBPFunction extends Function {
    private Normalization normalization;
    private int hiddenNeuronCount = 10;
    private double learningRate = 0.05;
    private double maxError = 0.005;
    private int maxIteration = 1000;
    private double[] desiredY;

    @Override
    public boolean setParameters(Frame parentWindow) {
        BP_ANNDialog dialog = new BP_ANNDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        Object[] paras = mlModel.getParameters(this.getClass().getSimpleName());
        hiddenNeuronCount = (int) (Math.sqrt(dataHelper.getOilXVariableCount() + 1) + 5);
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
        printDataMessage();
        DataSet dataSet = formDataSet();
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(dataSet.getInputSize(), hiddenNeuronCount, dataSet.getOutputSize());
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
        String filePath = FunTools.getModelPath() + File.separator + FunTools.getModelFileName("Predict_BP", mlModel);
        neuralNet.save(filePath);
        FunTools.saveModelAuxFile(filePath,normalization,this);
        double[] py = computeY(neuralNet, dataSet);
        printHighlight("R: " + MathBase.corrCoeff(desiredY, py) + "\n");
        println("Save Model: " + filePath);
        printLine();
        return 0;
    }

    private void printDataMessage() {
        printHighlight("Variables:\n");
        String[] xVarNames = mlModelHelper.getOilXVariableNames();
        String yVarName = mlModelHelper.getOilYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
    }

    private double[] computeY(MultiLayerPerceptron neuralNet, DataSet dataSet) {
        double[] y = new double[dataSet.size()];
        int index = 0;
        for (DataSetRow testSetRow : dataSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            y[index++] = neuralNet.getOutput()[0];
        }
        return y;
    }

    private DataSet formDataSet() {
        int xCount = dataHelper.getOilXVariableCount();
        normalization = new Normalization(xCount, -1);
        DataSet dataSet = new DataSet(xCount, 1);
        println("dataSet OutPutSize;"+dataSet.getOutputSize());
        int rowCount = dataHelper.getRealRowCount();
        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[1];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readRealRowOilXData(col, buffer);
            String variableName = dataHelper.getOilXVariableName(col);
            normalization.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        dataHelper.readOilYData(dataHelper.oilYVariableColumnIndex,buffer);
        desiredY = MathBase.copy(buffer);
        normalization.normalizeYVar(buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
        for (int row = 0; row < rowCount; row++) {
            dataSet.get(row).getDesiredOutput()[0] = buffer[row];
        }
        return dataSet;
    }
}
