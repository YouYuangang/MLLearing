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
    public final static String OIL_RES_TABLE_CLASSIFY = "含油性分类";
    public final static String OIL_RES_TABLE_CLUSTER = "含油性聚类";
    public final static String[] OIL_FEILDSNAME_CLASSIY = new String[]{"开始深度","结束深度","含油性分类"};
    public final static String[] OIL_FEILDSNAME_CLUSTER = new String[]{"开始深度","结束深度","含油性聚类"};
    
    public final static String LITH_RES_TABLE_CLASSIFY = "岩性分类";
    public final static String LITH_RES_TABLE_CLUSTER = "岩性聚类";
    public final static String[] LITH_FEILDSNAME_CLASSIY = new String[]{"开始深度","结束深度","岩性分类"};
    public final static String[] LITH_FEILDSNAME_CLUSTER = new String[]{"开始深度","结束深度","岩性聚类"};
    public TableHelper(MLDataModel mlModel){
        this.mlModel = mlModel;
        dataHelper = new DataHelper(mlModel);
    }
    public TableHelper(MLDataModel mlModel,DataHelper dataHelper){
        this.mlModel = mlModel;
        this.dataHelper = dataHelper;
    }
    /**
     * 根据解释结论表给数据打标签，tableName为解释结论表的名字，通过这个表填充mlModel.dataLabelAs[] 数组
     * @param tableName
     * @return 
     */
    public int fillDataLabelFromTable(String tableName) {
        /*for(int i = 0;i<mlModel.dataRowSelectedFlags.length;i++){
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
        return rowUsed;*/
        return 0;
    }
    /**
     * 根据提供的tableName即分类结果所在表格，填充mlModel.classifyResult
     * @param tableName
     * @return 
     */
    public int fillOilClassifyResultFromTable(String tableName){
        DataHelper dataHelper = new DataHelper(mlModel); 
        LogCategory category = mlModel.inputDataPath.getCategory();
        LogTable labelTable = category.getLogCommonTable(tableName);
        if(labelTable == null){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "不存在含油性分类结果表");
            return -1;
        }
        int rowUsed = dataHelper.getRawDataCount();
        mlModel.classifyResultOil = new String[rowUsed];
        for(int i = 0;i<mlModel.classifyResultOil.length;i++){
            mlModel.classifyResultOil[i] = " ";
        }
        int row = labelTable.getRowCount();
        int col = labelTable.getColumnCount();
        TableRecords tableRecord = new TableRecords();
        labelTable.readTableRecords(tableRecord);
        float depth;
        int resIndex = 0;//深度对应的索引
        
        String label = " ";     
        int count = 0;
        
        for (int i = 0; i < row; i++) {
            resIndex = 0;
            depth = tableRecord.getRecordFloatData(i, 0);
            label = tableRecord.getRecordStringData(i, 2);
            resIndex = (int)((depth-mlModel.curveStdep)/dataHelper.getDepthLevel());
            
            if(resIndex>=0&&resIndex<mlModel.classifyResultOil.length){
                mlModel.classifyResultOil[resIndex] = label;
                count++;
            }
        }
        UpdatePanelFlag.DataPanelUpdateFlag = true;
        UpdatePanelFlag.reasonForDataPanelUpdate = "dataPanel更新：从表格加载含油性分类表";
        return count;
    }
    /**
     * 根据提供的tableName即聚类结果所在表格，填充mlModel.clusterResult
     * @param tableName
     * @return 
     */
    public int fillClusterResultOilFromTable(String tableName){
        
        LogCategory category = mlModel.inputDataPath.getCategory();
        LogTable clusterTable = category.getLogCommonTable(tableName);     
        if(clusterTable == null){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "不存在聚类结果表");
            return -1;
        }
        DataHelper dataHelper = new DataHelper(mlModel);
        int rowUsed = dataHelper.getRawDataCount();
        mlModel.clusterResultOil = new int[rowUsed];
        for(int i =0;i<mlModel.clusterResultOil.length;i++){
            mlModel.clusterResultOil[i] = -1;
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
            if(resIndex>=0&&resIndex<mlModel.clusterResultOil.length){
                mlModel.clusterResultOil[resIndex] = label;
                count++;
            }
        }
        maxLabel++;
        JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "聚类中心个数"+maxLabel);
        return maxLabel;
    }
    /**
     * 将mlModel的mlModel.classifyResultOil结果存在当前井次的通用表格中，
     * 表格名字由tableName提供
     * @param tableName 提供用来保存结果的表格的名字。
     * @return 成功返回1，失败返回-1
     */
    public int saveToTableFromClassifyResOil(String tableName) {
        if(mlModel.classifyResultOil == null){
            LoadConfigure.writeLog("218 TableHelper.saveToTableFromClassifyResOil:"+tableName+":没有含油性分类结果，无法保存");
            return -1;
        }
        LogCategory logCategory = mlModel.inputDataPath.getCategory();
        if (logCategory.getLogCommonTable(tableName) != null) {
            logCategory.deleteLogging(tableName);
        }
        TableFields tableFields = new TableFields();
        tableFields.init(3);
        tableFields.setName(0, OIL_FEILDSNAME_CLASSIY[0]);
        tableFields.setDataType(0, Global.DATA_DEPTH);
        tableFields.setUnit(0, "米");
        tableFields.setName(1, OIL_FEILDSNAME_CLASSIY[1]);
        tableFields.setDataType(1, Global.DATA_DEPTH);
        tableFields.setUnit(1, "米");
        tableFields.setName(2, OIL_FEILDSNAME_CLASSIY[2]);
        tableFields.setDataType(2, Global.DATA_STRING);
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
                tableRecords.setRecordStringData(i, 2, mlModel.classifyResultOil[indexInClassifyRes]);
                indexInClassifyRes++;
            }
        }
        logCategory.createTable(tableName, "", Global.LOGGING_COMMON_TABLE, tableRecords);
        return 1;
    }
    /**
     * 将mlModel的mlModel.classifyResultOil结果存在当前井次的通用表格中，
     * 表格名字由tableName提供
     * @param tableName 提供用来保存结果的表格的名字。
     * @return 成功返回1，失败返回-1；
     */
    public int saveToTableFromClusterResOil(String tableName) {
        if(mlModel.clusterResultOil == null){
            LoadConfigure.writeLog("218 TableHelper.saveToTableFromClusterResOil:"+tableName+":没有含油性聚类结果，无法保存");
            return -1;
        }
        LogCategory logCategory = mlModel.inputDataPath.getCategory();
        if (logCategory.getLogCommonTable(tableName) != null) {
            logCategory.deleteLogging(tableName);
        }
        TableFields tableFields = new TableFields();
        tableFields.init(3);
        tableFields.setName(0, OIL_FEILDSNAME_CLUSTER[0]);
        tableFields.setDataType(0, Global.DATA_DEPTH);
        tableFields.setName(1, OIL_FEILDSNAME_CLUSTER[1]);
        tableFields.setDataType(1, Global.DATA_DEPTH);
        tableFields.setUnit(1, "米");
        tableFields.setName(2, OIL_FEILDSNAME_CLUSTER[2]);
        tableFields.setDataType(2, Global.DATA_INT);
        tableFields.setUnit(2, "");
        
        TableRecords tableRecords = new TableRecords();
        tableRecords.init(dataHelper.getRealRowCount(), tableFields);
        
        int indexInClusterRes = 0;
        
        //填充数据
        for (int i = 0; i < tableRecords.getRecordsNum(); i++) {
            while(indexInClusterRes<mlModel.dataRowSelectedFlags.length&&mlModel.dataRowSelectedFlags[indexInClusterRes]==false){
                indexInClusterRes++;
            }
            if(indexInClusterRes<mlModel.dataRowSelectedFlags.length){
                tableRecords.setRecordDoubleData(i, 0, mlModel.curveStdep+indexInClusterRes*dataHelper.getDepthLevel());
                tableRecords.setRecordDoubleData(i, 1, mlModel.curveStdep+(indexInClusterRes+1)*dataHelper.getDepthLevel());
                tableRecords.setRecordIntData(i, 2, mlModel.clusterResultOil[indexInClusterRes]);
                indexInClusterRes++;
            }
        }
        logCategory.createTable(tableName, "", Global.LOGGING_COMMON_TABLE, tableRecords);
        return 1;
    }
    
    
}