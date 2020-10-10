/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.baseutil.components.datagrid.DataGridModel;
import cif.dataengine.DataPath;
import java.util.HashMap;

/**
 *
 * @author wangcaizhi
 * @create 2019.2.22
 */
public class MLDataModel {

    public final static int FROM_CURVE = 0;
    public final static int FROM_TABLE = 1;
    public final static int FROM_TEXT = 2;
    public final static int UNSEL_VARIABLE = -1;
    
    public final static int X_VARIABLE_OIL = 1;
    public final static int Y_VARIABLE_OIL = 2;
    public final static int X_VARIABLE_LITH = 3;
    public final static int Y_VARIABLE_LITH = 4;
    public final static int X_VARIABLE_ALL = 5;
    public final static int Y_VARIABLE_ALL = 6;
    public final static String MLMODEL_FILENAME = "mlModelTemp.txt";
    public int dataFrom = -1;
    public DataPath inputDataPath;

    // 存储曲线被使用的那段数据的起始深度 
    public double curveStdep;
    public double curveEndep;

    // 存储曲线的起始深度
    public double curveStDepBound;
    public double curveEndDepBound;
    public String logTableName;
    public String inputFilePath;
    public DataGridModel dataGridModel;
    public Variable[] variables = null;
    public boolean[] dataRowSelectedFlags;
    public int[] dataLabelAs;

    public int learningMode;

    //保存聚类结果
    public int[] clusterResultOil = null;
    public int [] clusterResultLith= null;
    
    public String[] classifyResultOil = null;
    public String[] classifyResultLith = null;
    
    public double[] predictResult = null;

    //记录聚类数
    public int clusterCountOil = 0;
    public int clusterCountLith = 0;
    public HashMap<String,Integer> StringIntMapForOil = null;
    public HashMap<String,Integer> StringIntMapForLith = null;
    
    public void clearResult() {
        clusterResultOil = null;
        clusterResultLith = null;
        classifyResultOil = null;
        classifyResultLith = null;
        predictResult = null;

        clusterCountOil = 0;
        clusterCountLith = 0;
        StringIntMapForOil = null;
        StringIntMapForLith = null;
    }
    public void setVariableNames(String[] names) {
        variables = new Variable[names.length];
        for (int i = 0; i < variables.length; i++) {
            variables[i] = new Variable(names[i], X_VARIABLE_OIL);
        }
    }

    public Variable[] getVariables() {
        return variables;
    }
    ////////////////////////////////////////////////////////////////////
    private HashMap<String,Object[]> parameterMap = new HashMap<>();

    public Object[] getParameters(String className) {
        return parameterMap.get(className);
    }

    public void setParameters(String className, Object[] paramters) {
        parameterMap.remove(className);
        parameterMap.put(className, paramters);
    }

    public int getCurveIndex(String curveName) {
        for(int i=0;i<variables.length;i++){
            if(variables[i].name.equals(curveName)){
                return i;
            }
        }
        return -1;
    }
}
