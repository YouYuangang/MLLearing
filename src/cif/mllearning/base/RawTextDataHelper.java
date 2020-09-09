/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.3
 */
public class RawTextDataHelper {
    private MLDataModel mlModel;
    private int[] columnIndices;

    public RawTextDataHelper(MLDataModel mlModel) {
        this.mlModel = mlModel;
        Variable[] variables = mlModel.getVariables();
        columnIndices = new int[variables.length];
        for (int i = 0; i < variables.length; i++) {
            columnIndices[i] = getColumnIndex(variables[i].name);
        }
    }

    public int getColumnCount() {
        return columnIndices.length;
    }

    public String getColumnName(int columnIndex) {
        return (String) mlModel.dataGridModel.getValueAt(0, columnIndices[columnIndex]);
    }

    public String getColumnUnit(int columnIndex) {
       return (String) mlModel.dataGridModel.getValueAt(1, columnIndices[columnIndex]);
    }

    public int getRowCount() {
        return mlModel.dataGridModel.getRowCount()-2;
    }

    public String getTextData( int rowIndex, int columnIndex) {
          return (String) mlModel.dataGridModel.getValueAt(rowIndex+2, columnIndices[columnIndex]);
    }

    private int getColumnIndex( String columnName) {
        for (int i = 0; i < mlModel.dataGridModel.getColumnCount(); i++) {
            String line1Name = (String) mlModel.dataGridModel.getValueAt(0, i);
            if (line1Name.equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
}
