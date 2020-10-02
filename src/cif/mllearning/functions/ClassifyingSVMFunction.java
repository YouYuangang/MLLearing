/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import java.awt.Frame;
import java.io.File;
import java.util.HashMap;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.30
 *
 */
public class ClassifyingSVMFunction extends Function {

    private final svm_parameter param = new svm_parameter();
    private final static String[] SVR_TYPES = new String[]{"C_SVC", "NU-SVC"};
    private final static String[] KERNEL_TYPES = new String[]{"线性核", "多项式核", "RBF函数", "Sigmoid核"};
    private Normalization normalization;
    private String[] desiredY;
    private HashMap<String, Integer> itemCodeTable;

    public ClassifyingSVMFunction() {
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0.1; // 1/num_features            // change it 
        param.coef0 = 0;
        param.nu = 0.1;  /// change it,by wangcaizhi 
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
    }

    @Override
    public boolean setParameters(Frame parentWindow) {
        SVMDialog dialog = new SVMDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setSvms(SVR_TYPES);
        dialog.setKernels(KERNEL_TYPES);
        Object[] parameters = mlModel.getParameters(this.getClass().getSimpleName());
        dialog.setSvmSelectedIndex(parameters == null ? 0 : (int) parameters[0]);
        dialog.setKernelSelectedIndex(parameters == null ? 2 : (int) parameters[1]);
        dialog.setDegree(parameters == null ? param.degree : (int) parameters[2]);
        dialog.setGamma(parameters == null ? param.gamma : (double) parameters[3]);
        dialog.setNu(parameters == null ? param.nu : (double) parameters[4]);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == RegressDialog.RET_OK) {
            int svmIndex = dialog.getSvmSelectedIndex();
            switch (svmIndex) {
                case 0:
                    param.svm_type = svm_parameter.C_SVC;
                    break;
                case 1:
                    param.svm_type = svm_parameter.NU_SVC;
                    break;
            }
            int kernelIndex = dialog.getKernelSelectedIndex();
            switch (kernelIndex) {
                case 0:
                    param.kernel_type = svm_parameter.LINEAR;
                    break;
                case 1:
                    param.kernel_type = svm_parameter.POLY;
                    break;
                case 2:
                    param.kernel_type = svm_parameter.RBF;
                    break;
                case 3:
                    param.kernel_type = svm_parameter.SIGMOID;
                    break;
            }
            param.degree = dialog.getDegree();
            param.gamma = dialog.getGamma();
            param.nu = dialog.getNu();
            Object[] paras = new Object[]{svmIndex, kernelIndex, param.degree, param.gamma, param.nu};
            mlModel.setParameters(this.getClass().getSimpleName(), paras);
            return true;
        }
        return false;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        printDataMessage();
        svm_problem problem = buildSVMProblem();
        String error_msg = svm.svm_check_parameter(problem, param);
        if (error_msg != null) {
            printError("ERROR: " + error_msg + "\n");
        }
        svm_model model = svm.svm_train(problem, param);
        String filePath = FunTools.getModelPath() + File.separator + FunTools.getModelFileName("Classify_SVM", mlModel);
        svm.svm_save_model(filePath, model);
        FunTools.saveModelAuxFile(filePath,normalization,this);
        double[] py = new double[problem.y.length];
        for (int i = 0; i < problem.x.length; i++) {
            py[i] = svm.svm_predict(model, problem.x[i]);
        }
        int correctCount = FunTools.computeEquivalenceCount(problem.y, py);
        StringBuilder sb = new StringBuilder();
        sb.append("总数： ").append(py.length);
        sb.append(", 正确个数: ").append(correctCount);
        sb.append(", 正确率： ").append(String.format("%.2f", correctCount * 100.0 / py.length)).append("%\n");
        printHighlight(sb.toString());
        println("Save Model: " + filePath);
        printLine();
        return 0;
    }

    private svm_problem buildSVMProblem() {
        svm_problem problem = new svm_problem();
        int rowCount = dataHelper.getRealRowCount();
        int xVarCount = dataHelper.getOilXVariableCount();
        normalization = new Normalization(xVarCount, -1);
        problem.l = rowCount;
        problem.x = new svm_node[rowCount][xVarCount];
        problem.y = new double[rowCount];
        double[] buffer = new double[rowCount];
        printHighlight("Data Statistics:\n");
        for (int col = 0; col < xVarCount; col++) {
            dataHelper.readOilXData(col, buffer);
            String variableName = dataHelper.getOilXVariableName(col);
            normalization.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                problem.x[row][col] = new svm_node();
                problem.x[row][col].index = col;
                problem.x[row][col].value = buffer[row];
            }
        }
        desiredY = new String[rowCount];
        for(int i = 0;i<desiredY.length;i++){
            desiredY[i] = dataHelper.readRealOilYString(i);
        }
        
        itemCodeTable = FunTools.createItemCodeTable(desiredY);
        for (int row = 0; row < rowCount; row++) {
            problem.y[row] = itemCodeTable.get(desiredY[row]);
        }
        return problem;
    }

    private void printDataMessage() {
        printHighlight("Variables:\n");
        String[] xVarNames = mlModelHelper.getOilXVariableNames();
        String yVarName = mlModelHelper.getOilYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
    }
}
