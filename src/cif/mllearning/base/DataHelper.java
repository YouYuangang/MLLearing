/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.base.Global;
import cif.mllearning.MLGlobal;
import javax.swing.JOptionPane;
import org.openide.windows.WindowManager;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.5
 */
public class DataHelper {

    private MLDataModel mlModel;
    private RawCurveDataHelper curveHelper;
    private RawTableDataHelper tableHelper;
    private RawTextDataHelper textHelper;
    private int[] oilXVariableColumnIndices;
    private int oilYVariableColumnIndex;
    private int[] lithVariableColumnIndices;
    private int lithYVariableColumnIndex;
    
    private int[] realRowIndices = null;

    public DataHelper(MLDataModel mlModel) {
        this.mlModel = mlModel;
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                curveHelper = new RawCurveDataHelper(mlModel);
                break;
            case MLDataModel.FROM_TABLE:
                tableHelper = new RawTableDataHelper(mlModel);
                break;
            case MLDataModel.FROM_TEXT:
                textHelper = new RawTextDataHelper(mlModel);
                break;
        }
        formColumnIndices();
        realRowIndices = null;
    }
    public double getDepthLevel(){
       
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                return curveHelper.getDepthLevel();
                
            case MLDataModel.FROM_TABLE:
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "未实现从表格获取采样率");
                return 0.0;
            case MLDataModel.FROM_TEXT:
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "未实现从文本获取采样率");
                return 0.0;
            default:
                JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "无法获取采样率");
                return 0.0;
        }
    }

    private void formColumnIndices() {
        int xCountOil = 0;
        int xCountLith = 0;
        int yOilIndex = -1;
        int yLithIndex = -1;
        Variable[] variables = mlModel.getVariables();
        for (int i = 0;i<variables.length;i++) {
            Variable variable = variables[i];
            if (variable.flag == MLDataModel.X_VARIABLE_OIL) {
                xCountOil++;
            } else if (variable.flag == MLDataModel.Y_VARIABLE_OIL) {
                yOilIndex = i;
            }else if(variable.flag == MLDataModel.X_VARIABLE_LITH){
                xCountLith++;
            }else if(variable.flag == MLDataModel.Y_VARIABLE_LITH){
                yLithIndex = i;
            }
        }
        oilXVariableColumnIndices = new int[xCountOil];
        lithVariableColumnIndices = new int[xCountLith];
        
        int xVarIndex = 0, varIndex = 0;
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].flag == MLDataModel.X_VARIABLE_OIL) {
                realXVariableColumnIndices[xVarIndex++] = i;
                realVariableColumnIndices[varIndex++] = i;
            } else if (variables[i].flag == MLDataModel.Y_VARIABLE) {
                realYVariableColumnIndex = i;
                realVariableColumnIndices[varIndex++] = i;
            }
        }
    }
    public String getRealXVariableName(int realIndex){
        return mlModel.getVariables()[realXVariableColumnIndices[realIndex]].name;
    }
    public int getRealXVariableCount() {
        return realXVariableColumnIndices.length;
    }

    public int getRealVariableCount() {
        return realVariableColumnIndices.length;
    }

    public int getRealRowCount() {
        int count = 0;
        for (boolean flag : mlModel.dataRowSelectedFlags) {
            if (flag) {
                count++;
            }
        }
        return count;
    }
    
    public void getLabeledY(int[] desiredY){
        int count = 0;
        for(int i = 0;i<mlModel.dataRowSelectedFlags.length;i++){
            if(mlModel.dataRowSelectedFlags[i]){
                desiredY[count++] = mlModel.dataLabelAs[i];
            }
        }
    
    }

    public int readRealXData(int realXVariableIndex, double[] buffer) {
        return readRealDataFromRawIndex(realXVariableColumnIndices[realXVariableIndex], buffer);
    }

    public int readRealYData(double[] buffer) {
        return readRealDataFromRawIndex(realYVariableColumnIndex, buffer);
    }

    public int readRealData(int realVariableIndex, double[] buffer) {
        return readRealDataFromRawIndex(realVariableColumnIndices[realVariableIndex], buffer);
    }

    public int readValidXData(int realXVariableIndex, double[] buffer) {
        return readValidDataFromRawIndex(realXVariableColumnIndices[realXVariableIndex], buffer);
    }

    public int readValidTData(double[] buffer) {
        return readValidDataFromRawIndex(realYVariableColumnIndex, buffer);
    }

    public int readValidData(int realVariableIndex, double[] buffer) {
        return readValidDataFromRawIndex(realVariableColumnIndices[realVariableIndex], buffer);
    }

    public int readRealYString(String[] buffer) {
        if (realYVariableColumnIndex < 0) {
            return 0;
        }
        int m = 0;
        for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
            if (mlModel.dataRowSelectedFlags[i]) {
                buffer[m] = getRawStringData(realYVariableColumnIndex, i);
                m++;
            }
        }
        return m;
    }

    public void readRealRowXData(int rowIndex, double[] buffer) {
        formRowIndices();
        for (int i = 0; i < realXVariableColumnIndices.length; i++) {
            buffer[i] = getRawDoubleData(realXVariableColumnIndices[i], realRowIndices[rowIndex]);
        }
    }

    public double readRealYData(int rowIndex) {
        formRowIndices();
        return getRawDoubleData(realYVariableColumnIndex, realRowIndices[rowIndex]);
    }

    public String readRealYString(int rowIndex) {
        if (realYVariableColumnIndex < 0) {
            return "";
        }
        formRowIndices();
        return getRawStringData(realYVariableColumnIndex, realRowIndices[rowIndex]);
    }

    public void readRealRowData(int rowIndex, double[] buffer) {
        formRowIndices();
        for (int i = 0; i < realVariableColumnIndices.length; i++) {
            buffer[i] = getRawDoubleData(realVariableColumnIndices[i], realRowIndices[rowIndex]);
        }
    }

    private void formRowIndices() {
        if (realRowIndices != null) {
            return;
        }
        realRowIndices = new int[getRealRowCount()];
        int index = 0;
        for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
            if (mlModel.dataRowSelectedFlags[i]) {
                realRowIndices[index++] = i;
            }
        }
    }

    public int getRawDataCount() {
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                return curveHelper.getCurveSampleCount();
            case MLDataModel.FROM_TABLE:
                return tableHelper.getRecordCount();
            case MLDataModel.FROM_TEXT:
                return textHelper.getRowCount();
        }
        return 0;
    }

    public double getRawDoubleData(int variableIndex, int dataIndex) {
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                return curveHelper.getCurveData(variableIndex, dataIndex);
            case MLDataModel.FROM_TABLE:
                return toDouble(tableHelper.getTableData(dataIndex, variableIndex));
            case MLDataModel.FROM_TEXT:
                return toDouble(textHelper.getTextData(dataIndex, variableIndex));
        }
        return Global.NULL_DOUBLE_VALUE;
    }

    public String getRawStringData(int variableIndex, int dataIndex) {
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                return Float.toString(curveHelper.getCurveData(variableIndex, dataIndex));
            case MLDataModel.FROM_TABLE:
                return tableHelper.getTableData(dataIndex, variableIndex);
            case MLDataModel.FROM_TEXT:
                return textHelper.getTextData(dataIndex, variableIndex);
        }
        return "";
    }

    private int readRealDataFromRawIndex(int index, double[] buffer) {
        int m = 0;
        for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
            if (mlModel.dataRowSelectedFlags[i]) {
                buffer[m] = getRawDoubleData(index, i);
                m++;
            }
        }
        return m;
    }

    //这个函数过滤掉了 -9999.0的值
    private int readValidDataFromRawIndex(int index, double[] buffer) {
        int m = 0;
        for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
            if (mlModel.dataRowSelectedFlags[i] && getRawDoubleData(index, i) != MLGlobal.INVALID_VALUE) {
                buffer[m++] = getRawDoubleData(index, i);
            }
        }
        return m;
    }

    /**
     * 获取数据行所对应的深度值
     */
    public double readRealDepthValue(int rowIndex) {
        String result = "";
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                result = Double.toString(curveHelper.getDepth(rowIndex));
                break;
            case MLDataModel.FROM_TABLE:
                result = tableHelper.getTableData(rowIndex, 0);
                break;
            case MLDataModel.FROM_TEXT:
                result = textHelper.getTextData(rowIndex, 0);
                break;
        }
        return Double.valueOf(result);
    }

    private double toDouble(String s) {
        double d;
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            d = Global.NULL_FLOAT_VALUE;
        }
        return d;
    }

    public String getCurveUnit(int index) {
        String unitStr = "";
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                unitStr = curveHelper.getCurveUnit(index);
                break;
            case MLDataModel.FROM_TABLE:
                unitStr = tableHelper.getFieldUnit(index);
                break;
            case MLDataModel.FROM_TEXT:
                unitStr = textHelper.getColumnUnit(index);
                break;
        }
        return unitStr;

    }
}
