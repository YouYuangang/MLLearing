/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import cif.loglab.regression.Regression;
import cif.loglab.regression.RegressionUtil;
import java.awt.Frame;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.20
 */
public class RegressFunction extends Function {

    private int regressXD = RegressionUtil.REG_2D;
    private int yConversion;
    private int formulaIndex;

    @Override
    protected Integer doInBackground() throws Exception {
        double[][] xData = formXData();
        double[] yData = formYData();
        proceedStatistics(xData, yData);
        RegressionUtil regUtil = new RegressionUtil();
        String[] xVarNames = mlModelHelper.getRealXVariableNames();
        String yVarName = mlModelHelper.getRealYVariableName();
        regUtil.setVariableNames(xVarNames, yVarName);
        regUtil.todo(xData, yData, regressXD, formulaIndex, yConversion);
        printHighlight(regUtil.getExpression(Regression.L_JAVA) + "\n");
        printHighlight("R: " + toStr(regUtil.getR2()) + "\n");
        println("ResidualSumOfSquares: " + toStr(regUtil.getResidualSumOfSquares()));
        println("StandardErrorOfEstimate: " + toStr(regUtil.getStandardErrorOfEstimate()));
        printLine();
        done();
        return 0;
    }

    private double[][] formXData() {
        int xVarCount = dataHelper.getRealXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        double[][] xData = new double[xVarCount][rowCount];
        for (int xVar = 0; xVar < xVarCount; xVar++) {
            dataHelper.readRealXData(xVar, xData[xVar]);
        }
        return xData;
    }

    private double[] formYData() {
        int rowCount = dataHelper.getRealRowCount();
        double[] yData = new double[rowCount];
        dataHelper.readRealYData(yData);
        return yData;
    }

    @Override
    public boolean setParameters(Frame parentWindow) {
        int xVarCount = mlModelHelper.getRealXVariableCount();
        switch (xVarCount) {
            case 1:
                regressXD = RegressionUtil.REG_2D;
                break;
            default:
                regressXD = RegressionUtil.REG_MD;
                break;
        }
        RegressDialog dialog = new RegressDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.listFormulas(regressXD);
        Object[] parameters = mlModel.getParameters(this.getClass().getSimpleName());
        if (parameters != null) {
            if ((int) parameters[0] == regressXD) {
                dialog.setSelectedIndex((int) parameters[1]);
                dialog.setLnY((boolean) parameters[2]);
            }
        }
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == RegressDialog.RET_OK) {
            yConversion = dialog.getLnY() ? RegressionUtil.P_y_ln : RegressionUtil.P_y;
            formulaIndex = dialog.getSelectedIndex();
            Object[] paras = new Object[]{regressXD, formulaIndex, dialog.getLnY()};
            mlModel.setParameters(this.getClass().getSimpleName(), paras);
            return true;
        }
        return false;
    }

    protected void proceedStatistics(double[][] xData, double[] yData) {
        printHighlight("Variables:\n");
        String[] xVarNames = mlModelHelper.getRealXVariableNames();
        String yVarName = mlModelHelper.getRealYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
        printHighlight("Data Statistics:\n");
        // Min Value      Average       Max Value   Standard Deviation
        StringBuilder sb = new StringBuilder(200);
        sb.append(toStr("Name")).append("\t").append(toStr("Min Value")).append("\t").append(toStr("Average")).append("\t").append(toStr("Max Value"));
        sb.append("\t").append(toStr("Std Dev"));
        println(sb.toString());
        for (int i = 0; i < xVarNames.length; i++) {
            print(toStr(xVarNames[i]));
            print("\t");
            print(toStr(MathBase.minimum(xData[i])));
            print("\t");
            print(toStr(MathBase.mean(xData[i])));
            print("\t");
            print(toStr(MathBase.maximum(xData[i])));
            print("\t");
            println(toStr(MathBase.standardDeviation(xData[i])));
        }
        print(toStr(yVarName));
        print("\t");
        print(toStr(MathBase.minimum(yData)));
        print("\t");
        print(toStr(MathBase.mean(yData)));
        print("\t");
        print(toStr(MathBase.maximum(yData)));
        print("\t");
        println(toStr(MathBase.standardDeviation(yData)));
        //Correlation Matrix
        printHighlight("Correlation Matrix\n");
        //  x1  x2  x3  x4  y
        print(toStr(" "));
        for (String xVarName : xVarNames) {
            print("\t");
            print(toStr(xVarName));
        }
        print("\t");
        println(toStr(yVarName));
        for (int row = 0; row < xData.length; row++) {
            print(toStr(xVarNames[row]));
            for (int col = 0; col < xData.length; col++) {
                print("\t");
                if (row == col) {
                    print(toStr(1));
                } else {
                    print(toStr(MathBase.corrCoeff(xData[row], xData[col])));
                }
            }
            print("\t");
            println(toStr(MathBase.corrCoeff(xData[row], yData)));
        }
        print(toStr(yVarName));
        for (double[] xData1 : xData) {
            print("\t");
            print(toStr(MathBase.corrCoeff(yData, xData1)));
        }
        print("\t");
        println(toStr(1));
    }
}
