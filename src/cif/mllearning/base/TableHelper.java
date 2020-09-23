/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.base.Global;
import cif.dataengine.io.LogCategory;
import cif.dataengine.io.LogTable;
import cif.dataengine.io.TableFields;
import cif.dataengine.io.TableRecords;
import cif.mllearning.MLGlobal;
import cif.mllearning.configure.LoadConfigure;
import javax.swing.JOptionPane;
import org.openide.windows.WindowManager;

/**
 *
 * @author Administrator
 */
public class TableHelper {
    public MLDataModel mlModel;
    public DataHelper dataHelper;
    public TableHelper(MLDataModel mlModel){
        this.mlModel = mlModel;
        dataHelper = new DataHelper(mlModel);
    }
    /**
     * 根据解释结论表给数据打标签，tableName为解释结论表的名字，通过这个表填充mlModel.dataLabelAs[] 数组
     * @param tableName
     * @return 
     */
    public int fillDataLabelFromTable(String tableName) {
        for(int i = 0;i<mlModel.dataRowSelectedFlags.length;i++){
            mlModel.dataRowSelectedFlags[i] = false;
        }
        LogCategory category = mlModel.inputDataPath.getCategory();
        LogTable labelTable = category.getLogCommonTable(tableName);
        int row = labelTable.getRowCount();
        int col = labelTable.getColumnCount();
        TableRecords tableRecord = new TableRecords();
        labelTable.readTableRecords(tableRecord);
        float[] sdepth = new float[row];
        float[] edepth = new float[row];
        String[] label = new String[row];
        int sIndex = 0;
        int eIndex = 0;
        int labelIndex = 0;
        int rowUsed = 0;//记录有多少行表记录的标签 是 配置文件中的提供的标签

        for (int i = 0; i < row; i++) {
            labelIndex = 0;
            sdepth[i] = tableRecord.getRecordFloatData(i, 0);
            edepth[i] = tableRecord.getRecordFloatData(i, 1);
            label[i] = tableRecord.getRecordStringData(i, 3);

            sIndex = (int) ((sdepth[i] - mlModel.curveStdep) / dataHelper.getDepthLevel());
            eIndex = (int) ((edepth[i] - mlModel.curveStdep) / dataHelper.getDepthLevel());
            //表记录标签在数组中的索引
            for (int j = 0; j < LoadConfigure.colorLayers.size(); j++) {
                if (label[i].equals(LoadConfigure.colorLayers.get(j).nameOfLayer)) {
                    labelIndex = j;
                    break;
                }

            }
            if (labelIndex > 0) {
                rowUsed++;
            }

            if (sIndex >= 0 && eIndex >= 0 && sIndex <= eIndex) {

                for (int j = sIndex; j <= eIndex; j++) {
                    mlModel.dataLabelAs[j] = labelIndex;
                    mlModel.dataRowSelectedFlags[j] = true;
                }
            }
        }
        return rowUsed;
    }
    /**
     * 根据提供的tableName即分类结果所在表格，填充mlModel.classifyResult
     * @param tableName
     * @return 
     */
    public int fillClassifyResultFromTable(String tableName){
        DataHelper dataHelper = new DataHelper(mlModel);
        int rowUsed = dataHelper.getRawDataCount();
        mlModel.classifyResult = new int[rowUsed];
        for(int i = 0;i<mlModel.classifyResult.length;i++){
            mlModel.classifyResult[i] = 0;
        }
        LogCategory category = mlModel.inputDataPath.getCategory();
        LogTable labelTable = category.getLogCommonTable(tableName);
        if(labelTable == null){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "不存在分类结果表");
        }
        int row = labelTable.getRowCount();
        int col = labelTable.getColumnCount();
        TableRecords tableRecord = new TableRecords();
        labelTable.readTableRecords(tableRecord);
        int resIndex = 0;
        int label = 0;
        int maxLabel = 0;
        float depth;
        int count = 0;
        
        for (int i = 0; i < row; i++) {
            resIndex = 0;
            depth = tableRecord.getRecordFloatData(i, 0);
            label = tableRecord.getRecordIntData(i, 2);
            resIndex = (int)((depth-mlModel.curveStdep)/dataHelper.getDepthLevel());
            
            if(resIndex>=0&&resIndex<mlModel.classifyResult.length){
                mlModel.classifyResult[resIndex] = label;
                count++;
            }
        }
        return count;
    }
    /**
     * 根据提供的tableName即聚类结果所在表格，填充mlModel.clusterResult
     * @param tableName
     * @return 
     */
    public int fillClusterResultFromTable(String tableName){
        DataHelper dataHelper = new DataHelper(mlModel);
        int rowUsed = dataHelper.getRawDataCount();
        mlModel.clusterResult = new int[rowUsed];
        for(int i =0;i<mlModel.clusterResult.length;i++){
            mlModel.clusterResult[i] = -1;
        }
        LogCategory category = mlModel.inputDataPath.getCategory();
        LogTable clusterTable = category.getLogCommonTable(tableName);
        
        if(clusterTable == null){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "不存在聚类结果表");
        }
        int row = clusterTable.getRowCount();
        int col = clusterTable.getColumnCount();
        TableRecords tableRecord = new TableRecords();
        clusterTable.readTableRecords(tableRecord);
        int resIndex = 0;
        int label = 0;
        int maxLabel = 0;
        float depth;
        int count = 0;
        for (int i = 0; i < row; i++) {
            resIndex = 0;
            depth = tableRecord.getRecordFloatData(i, 0);
            label = tableRecord.getRecordIntData(i, 2);
            if(label>maxLabel){
                maxLabel=label;
            }
            resIndex = (int)((depth-mlModel.curveStdep)/dataHelper.getDepthLevel());
            if(resIndex>=0&&resIndex<mlModel.clusterResult.length){
                mlModel.clusterResult[resIndex] = label;
                count++;
            }
        }
        maxLabel++;
        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "聚类中心个数"+maxLabel);
        return maxLabel;
    }
    
    public int saveToTableFromClassifyRes(String tableName) {
        LogCategory logCategory = mlModel.inputDataPath.getCategory();
        if (logCategory.getLogCommonTable(tableName) != null) {
            logCategory.deleteLogging(tableName);
        }
        TableFields tableFields = new TableFields();
        tableFields.init(3);
        tableFields.setName(0, "开始深度");
        tableFields.setDataType(0, Global.DATA_DEPTH);
        tableFields.setUnit(0, "米");
        tableFields.setName(1, "结束深度");
        tableFields.setDataType(1, Global.DATA_DEPTH);
        tableFields.setUnit(1, "米");
        tableFields.setName(2, "分类结果");
        tableFields.setDataType(2, Global.DATA_INT);
        tableFields.setUnit(2, "");
        
        TableRecords tableRecords = new TableRecords();
        tableRecords.init(dataHelper.getRealRowCount(), tableFields);
        
        int indexInClassifyRes = 0;
        
        //填充数据
        for (int i = 0; i < tableRecords.getRecordsNum(); i++) {
            while(indexInClassifyRes<mlModel.dataRowSelectedFlags.length&&mlModel.dataRowSelectedFlags[indexInClassifyRes]==false){
                indexInClassifyRes++;
            }
            if(indexInClassifyRes<mlModel.dataRowSelectedFlags.length){
                tableRecords.setRecordDoubleData(i, 0, mlModel.curveStdep+indexInClassifyRes*dataHelper.getDepthLevel());
                tableRecords.setRecordDoubleData(i, 1, mlModel.curveStdep+(indexInClassifyRes+1)*dataHelper.getDepthLevel());
                tableRecords.setRecordIntData(i, 2, mlModel.classifyResult[indexInClassifyRes]);
                indexInClassifyRes++;
            }
        }
        logCategory.createTable(tableName, "", Global.LOGGING_COMMON_TABLE, tableRecords);
        return 1;
    }
    
    public int saveToTableFromClusterRes(String tableName) {
        LogCategory logCategory = mlModel.inputDataPath.getCategory();
        if (logCategory.getLogCommonTable(tableName) != null) {
            logCategory.deleteLogging(tableName);
        }
        TableFields tableFields = new TableFields();
        tableFields.init(3);
        tableFields.setName(0, "开始深度");
        tableFields.setDataType(0, Global.DATA_DEPTH);
        tableFields.setName(1, "结束深度");
        tableFields.setDataType(1, Global.DATA_DEPTH);
        tableFields.setUnit(1, "米");
        tableFields.setName(2, "聚类结果");
        tableFields.setDataType(2, Global.DATA_INT);
        tableFields.setUnit(2, "");
        
        TableRecords tableRecords = new TableRecords();
        tableRecords.init(dataHelper.getRealRowCount(), tableFields);
        
        int indexInClassifyRes = 0;
        
        //填充数据
        for (int i = 0; i < tableRecords.getRecordsNum(); i++) {
            while(indexInClassifyRes<mlModel.dataRowSelectedFlags.length&&mlModel.dataRowSelectedFlags[indexInClassifyRes]==false){
                indexInClassifyRes++;
            }
            if(indexInClassifyRes<mlModel.dataRowSelectedFlags.length){
                tableRecords.setRecordDoubleData(i, 0, mlModel.curveStdep+indexInClassifyRes*dataHelper.getDepthLevel());
                tableRecords.setRecordDoubleData(i, 1, mlModel.curveStdep+(indexInClassifyRes+1)*dataHelper.getDepthLevel());
                tableRecords.setRecordIntData(i, 2, mlModel.clusterResult[indexInClassifyRes]);
                indexInClassifyRes++;
            }
        }
        logCategory.createTable(tableName, "", Global.LOGGING_COMMON_TABLE, tableRecords);
        return 1;
    }
}