/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.base.Global;
import cif.mllearning.MLGlobal;
import java.util.HashMap;
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
    public int oilYVariableColumnIndex = -1;
    private int[] lithXVariableColumnIndices;
    public int lithYVariableColumnIndex = -1;
    private int[] usedVariableColumnIndeices;
    private int[] realRowIndices = null;
    
    HashMap<String,Integer> oilStringIntMap = null;
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
        formRowIndices();
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
        int usedVariableCount = 0;
        
        Variable[] variables = mlModel.getVariables();
        for (int i = 0;i<variables.length;i++) {
            Variable variable = variables[i];
            if (variable.flag == MLDataModel.X_VARIABLE_OIL||variable.flag == MLDataModel.X_VARIABLE_ALL) {
                xCountOil++;
            }else if(variable.flag == MLDataModel.X_VARIABLE_LITH||variable.flag == MLDataModel.X_VARIABLE_ALL){
                xCountLith++;
            }
            if(variable.flag != MLDataModel.UNSEL_VARIABLE&&variable.flag != MLDataModel.Y_VARIABLE_OIL&&variable.flag != MLDataModel.Y_VARIABLE_LITH){
                usedVariableCount++;
            }
        }
        oilXVariableColumnIndices = new int[xCountOil];
        lithXVariableColumnIndices = new int[xCountLith];
        usedVariableColumnIndeices = new int[usedVariableCount];
        int xVarIndexForOil = 0, xVarIndexForLith = 0,usedVarIndex = 0;
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].flag == MLDataModel.X_VARIABLE_OIL||variables[i].flag == MLDataModel.X_VARIABLE_ALL) {
                oilXVariableColumnIndices[xVarIndexForOil++] = i;
            } else if (variables[i].flag == MLDataModel.Y_VARIABLE_OIL) {
                oilYVariableColumnIndex = i;
                
            }else if(variables[i].flag == MLDataModel.X_VARIABLE_LITH||variables[i].flag == MLDataModel.X_VARIABLE_ALL){
                lithXVariableColumnIndices[xVarIndexForLith++] = i;
            }else if(variables[i].flag == MLDataModel.Y_VARIABLE_LITH){
                lithYVariableColumnIndex = i;
            }
            if(variables[i].flag != MLDataModel.UNSEL_VARIABLE&&variables[i].flag != MLDataModel.Y_VARIABLE_OIL&&variables[i].flag != MLDataModel.Y_VARIABLE_LITH){
                usedVariableColumnIndeices[usedVarIndex++] = i;
            }
        }
        
        int index = 0;
        oilStringIntMap = new HashMap<>();
        String oiltype = null;
        for(int i = 0;i<realRowIndices.length;i++){
            oiltype = getRawStringData(oilYVariableColumnIndex, realRowIndices[i]);
            if(oilStringIntMap.containsKey(oiltype)){
                continue;
            }else{
                oilStringIntMap.put(oiltype, index++);
            }
        }
    }
    /*public String getRealXVariableName(int realIndex){
        return mlModel.getVariables()[realXVariableColumnIndices[realIndex]].name;
    }*/
    public String getOilXVariableName(int indexOfused){
        return mlModel.getVariables()[oilXVariableColumnIndices[indexOfused]].name;
        
    }
    public String getLithXVariableName(int indexOfused){
        return mlModel.getVariables()[lithXVariableColumnIndices[indexOfused]].name;
    }
    public String getUsedVariableName(int indexOfused){
        return mlModel.getVariables()[usedVariableColumnIndeices[indexOfused]].name;
    }
    /*public int getRealXVariableCount() {
        return realXVariableColumnIndices.length;
    }*/
    
    public int getOilXVariableCount(){
        return oilXVariableColumnIndices.length;
    }
    public int getLithXVariableCount(){
        return lithXVariableColumnIndices.length;
    }

    public int getUsedVariableCount() {
        return usedVariableColumnIndeices.length;
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

    public int readOilXData(int indexInused, double[] buffer) {
        for(int i = 0;i<realRowIndices.length;i++){
            buffer[i] = getRawDoubleData(oilXVariableColumnIndices[indexInused],realRowIndices[i]);
        }
        return realRowIndices.length;
    }

    public int readOilYData(int indexInused,int[] buffer) {
        for(int i = 0;i<realRowIndices.length;i++){
            String temp = getRawStringData(oilXVariableColumnIndices[indexInused],realRowIndices[i]);
            buffer[i] = oilStringIntMap.get(temp);
        }
        return realRowIndices.length;
    }
    
    public int readOilYData(int indexInused,double[] buffer) {
        for(int i = 0;i<realRowIndices.length;i++){
            String temp = getRawStringData(oilXVariableColumnIndices[indexInused],realRowIndices[i]);
            buffer[i] = (double)oilStringIntMap.get(temp);
        }
        return realRowIndices.length;
    }

    public int readUsedData(int indexInUsed, double[] buffer) {
        for(int i = 0;i<realRowIndices.length;i++){
            buffer[i] = getRawDoubleData(usedVariableColumnIndeices[indexInUsed],realRowIndices[i]);
        }
        return realRowIndices.length;
    }

    /*public int readValidOilXData(int indexInused, double[] buffer) {
        return readValidDataFromRawIndex(oilXVariableColumnIndices[indexInused], buffer);
    }

    public int readValidOilYData(double[] buffer) {
        return readValidDataFromRawIndex(oilYVariableColumnIndex, buffer);
    }
    
    public int readValidLithXData(int indexInused,double [] buffer){
        return readValidDataFromRawIndex(lithXVariableColumnIndices[indexInused], buffer);
    }
    
    public int readValidLithYData(int indexInused,double [] buffer){
        return readValidDataFromRawIndex(lithYVariableColumnIndex, buffer);
    }*/

    /*public int readValidData(int realVariableIndex, double[] buffer) {
        return readValidDataFromRawIndex(realVariableColumnIndices[realVariableIndex], buffer);
    }*/

    /*public int readRealYString(String[] buffer) {
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
    }*/

    public void readRealRowOilXData(int rowIndex, double[] buffer) {
        //formRowIndices();
        for (int i = 0; i < oilXVariableColumnIndices.length; i++) {
            buffer[i] = getRawDoubleData(oilXVariableColumnIndices[i], realRowIndices[rowIndex]);
        }
    }
    
    public void readRealRowLithXData(int rowIndex, double[] buffer) {
        //formRowIndices();
        for (int i = 0; i < lithXVariableColumnIndices.length; i++) {
            buffer[i] = getRawDoubleData(oilXVariableColumnIndices[i], realRowIndices[rowIndex]);
        }
    }

    public int readRealOilYData(int rowIndex) {
        //formRowIndices();
        
        return oilStringIntMap.get(getRawStringData(oilYVariableColumnIndex, realRowIndices[rowIndex]));
    }

    public String readRealOilYString(int rowIndex) {
        if (oilYVariableColumnIndex < 0) {
            return "Oil_Y不存在";
        }
        //formRowIndices();
        return getRawStringData(oilYVariableColumnIndex, realRowIndices[rowIndex]);
    }
    
    public double readRealLithYData(int rowIndex) {
        //formRowIndices();
        return getRawDoubleData(lithYVariableColumnIndex, realRowIndices[rowIndex]);
    }

    public String readRealLithYString(int rowIndex) {
        if (oilYVariableColumnIndex < 0) {
            return "Oil_Y不存在";
        }
        //formRowIndices();
        return getRawStringData(lithYVariableColumnIndex, realRowIndices[rowIndex]);
    }

    /*public void readRealRowData(int rowIndex, double[] buffer) {
        formRowIndices();
        for (int i = 0; i < realVariableColumnIndices.length; i++) {
            buffer[i] = getRawDoubleData(realVariableColumnIndices[i], realRowIndices[rowIndex]);
        }
    }*/

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

    /*private int readRealDataFromRawIndex(int index, double[] buffer) {
        int m = 0;
        for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
            if (mlModel.dataRowSelectedFlags[i]) {
                buffer[m] = getRawDoubleData(index, i);
                m++;
            }
        }
        return m;
    }*/

    //这个函数过滤掉了 -99999.0的值
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
    
    public String getUsedCurveUnit(int index) {
        String unitStr = "";
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
                unitStr = curveHelper.getCurveUnit(usedVariableColumnIndeices[index]);
                break;
            case MLDataModel.FROM_TABLE:
                unitStr = tableHelper.getFieldUnit(usedVariableColumnIndeices[index]);
                break;
            case MLDataModel.FROM_TEXT:
                unitStr = textHelper.getColumnUnit(usedVariableColumnIndeices[index]);
                break;
        }
        return unitStr;

    }
    
    public int getUsedVIndexInImported(int index){
        return usedVariableColumnIndeices[index];
    }
}
