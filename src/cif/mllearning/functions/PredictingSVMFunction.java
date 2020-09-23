/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import java.awt.Frame;
import java.io.File;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.23
 */
public class PredictingSVMFunction extends Function {

    private final svm_parameter param = new svm_parameter();
    private final static String[] SVR_TYPES = new String[]{"NU_SVR", "EPSILON_SVR"};
    private final static String[] KERNEL_TYPES = new String[]{"线性核", "多项式核", "RBF函数", "Sigmoid核"};
    private Normalization normalization ;
    private double[] desiredY;

    public PredictingSVMFunction() {
        param.svm_type = svm_parameter.NU_SVR;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0.1; // 1/num_features            // change it 
        param.coef0 = 0;
        param.nu = 0.3;  /// change it,by wangcaizhi 
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
        dialog.setEpsilon(parameters == null ? param.p : (double) parameters[5]);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == RegressDialog.RET_OK) {
            int svmIndex = dialog.getSvmSelectedIndex();
            switch (svmIndex) {
                case 0:
                    param.svm_type = svm_parameter.NU_SVR;
                    break;
                case 1:
                    param.svm_type = svm_parameter.EPSILON_SVR;
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
            param.p = dialog.getEpsilon();
            Object[] paras = new Object[]{svmIndex, kernelIndex, param.degree, param.gamma, param.nu, param.p};
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
        String filePath = FunTools.getModelPath() + File.separator + FunTools.getModelFileName("Predict_SVM", mlModel);
        svm.svm_save_model(filePath, model);
        FunTools.saveModelAuxFile(true, filePath, mlModelHelper, normalization,this);
        double[] py = new double[problem.y.length];
        for (int i = 0; i < problem.x.length; i++) {
            py[i] = svm.svm_predict(model, problem.x[i]);
        }
        normalization.unnormalizeYVar(py);
        printHighlight("R: " + MathBase.corrCoeff(desiredY, py) + "\n");
        println("Save Model: " + filePath);
        printLine();
        return 0;
    }

    private svm_problem buildSVMProblem() {
        svm_problem problem = new svm_problem();
        int rowCount = dataHelper.getRealRowCount();
        int xVarCount = dataHelper.getRealXVariableCount();
        normalization = new Normalization(xVarCount, -1);
        String[] xVarNames = mlModelHelper.getRealXVariableNames();
        problem.l = rowCount;
        problem.x = new svm_node[rowCount][xVarCount];
        problem.y = new double[rowCount];
        double[] buffer = new double[rowCount];
        printHighlight("Data Statistics:\n");
        for (int col = 0; col < xVarCount; col++) {
            dataHelper.readRealXData(col, buffer);
            String variableName = dataHelper.getRealXVariableName(col);
            printCurveStatistics(xVarNames[col], buffer);
            normalization.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                problem.x[row][col] = new svm_node();
                problem.x[row][col].index = col;
                problem.x[row][col].value = buffer[row];
            }
        }
        dataHelper.readRealYData(buffer);
        desiredY = MathBase.copy(buffer);
        printCurveStatistics(mlModelHelper.getRealYVariableName(), buffer);
        normalization.normalizeYVar(buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
        System.arraycopy(buffer, 0, problem.y, 0, rowCount);
        return problem;
    }

    private void printDataMessage() {
        printHighlight("Variables:\n");
        String[] xVarNames = mlModelHelper.getRealXVariableNames();
        String yVarName = mlModelHelper.getRealYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
    }

    private void printCurveStatistics(String varName, double[] data) {
        // Min Value      Average       Max Value   Standard Deviation
        StringBuilder sb = new StringBuilder(200);
        sb.append(toStr("Name")).append("\t").append(toStr("Min Value")).append("\t").append(toStr("Average")).append("\t").append(toStr("Max Value"));
        sb.append("\t").append(toStr("Std Dev"));
        println(sb.toString());

        print(toStr(varName));
        print("\t");
        print(toStr(MathBase.minimum(data)));
        print("\t");
        print(toStr(MathBase.mean(data)));
        print("\t");
        print(toStr(MathBase.maximum(data)));
        print("\t");
        println(toStr(MathBase.standardDeviation(data)));
    }
}
