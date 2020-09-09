/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning;

import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.Variable;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wangcaizhi
 * @create 2019.2.24
 */
class VariableTableModel extends AbstractTableModel {

    private static final Class[] TYPES = new Class[]{java.lang.String.class, java.lang.String.class};
    private static final String[] PREDICTING_CLASSIFICATION_COLUMN_NAMES = {"变量", "名称"};
    private static final String[] CLUSTERING_COLUMN_NAMES = {"序号", "名称"};
    private int learningMode;
    private final ArrayList<VariableEx> usedVariables = new ArrayList<>();
    private MLDataModel mlModel;

    public VariableTableModel(MLDataModel mlModel) {
        this.mlModel = mlModel;
    }

    public void setLearningMode(int mode) {
        learningMode = mode;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return TYPES[columnIndex];
    }

    @Override
    public int getRowCount() {
        return usedVariables.size();
    }

    @Override
    public int getColumnCount() {
        if (learningMode == MLGlobal.CLUSTERING_MODE) {
            return CLUSTERING_COLUMN_NAMES.length;
        } else {
            return PREDICTING_CLASSIFICATION_COLUMN_NAMES.length;
        }
    }

    public void refreshViewData() {
        usedVariables.clear();
        int yIndex = -1;
        Variable[] variables = mlModel.getVariables();
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].flag > 0) {
                if (variables[i].flag == MLDataModel.Y_VARIABLE) {
                    yIndex = i;
                } else {
                    usedVariables.add(new VariableEx(variables[i], i));
                }
            }
        }
        if (yIndex >= 0) {
            usedVariables.add(new VariableEx(variables[yIndex], yIndex));
        } else {
            usedVariables.get(usedVariables.size() - 1).variable.flag = MLDataModel.Y_VARIABLE;
        }
    }

    public void setY(int rowIndex) {
        for (int i = 0; i < usedVariables.size(); i++) {
            Variable variable = usedVariables.get(i).variable;
            if (i == rowIndex) {
                variable.flag = MLDataModel.Y_VARIABLE;
            } else {
                variable.flag = MLDataModel.X_VARIABLE;
            }
        }
        refreshViewData();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Variable variable = usedVariables.get(rowIndex).variable;
        switch (columnIndex) {
            case 0:
                if (learningMode == MLGlobal.CLUSTERING_MODE) {
                    return rowIndex + 1;
                } else {
                    return rowIndex == usedVariables.size() - 1 ? "Y" : "X" + rowIndex;
                }
            case 1:
                return variable.name;
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int column) {
        if (learningMode == MLGlobal.CLUSTERING_MODE) {
            return CLUSTERING_COLUMN_NAMES[column];
        } else {
            return PREDICTING_CLASSIFICATION_COLUMN_NAMES[column];
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public int getVariableIndex(int rowIndex) {
        return usedVariables.get(rowIndex).index;
    }

}

class VariableEx {

    public Variable variable;
    public int index;

    public VariableEx(Variable variable, int index) {
        this.variable = variable;
        this.index = index;
    }
}
