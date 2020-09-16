/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.22
 */
public class MLDataModelHelper {

    private MLDataModel mlModel;

    public MLDataModelHelper(MLDataModel mlModel) {
        this.mlModel = mlModel;
    }

    public int getRealXVariableCount() {
        int xVarCount = 0;
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE) {
                xVarCount++;
            }
        }
        return xVarCount;
    }

    public int getRealVariableCount() {
        int varCount = 0;
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag > 0) {
                varCount++;
            }
        }
        return varCount;
    }

    public String[] getRealXVariableNames() {
        String[] names = new String[getRealXVariableCount()];
        Variable[] variables = mlModel.getVariables();
        int index = 0;
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE) {
                names[index++] = variable.name;
            }
        }
        return names;
    }

    public String getRealYVariableName() {
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.Y_VARIABLE) {
                return variable.name;
            }
        }
        return "没有y变量";
    }

    public String[] getRealVariableNames() {
        String[] names = new String[getRealVariableCount()];
        Variable[] variables = mlModel.getVariables();
        int index = 0;
        for (Variable variable : variables) {
            if (variable.flag > 0) {
                names[index++] = variable.name;
            }
        }
        return names;
    }

    public String formString(String[] strs, String separator) {
        if (strs.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strs[0]);
            for (int i = 1; i < strs.length; i++) {
                sb.append(separator).append(strs[i]);
            }
            return sb.toString();
        }
    }

}
