/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.components;

import cif.mllearning.MLGlobal;
import cif.mllearning.base.RawCurveDataHelper;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.RawTableDataHelper;
import cif.mllearning.base.RawTextDataHelper;
import cif.mllearning.configure.LoadConfigure;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.3
 */
class DataPanelTableModel extends AbstractTableModel {

    private MLDataModel mlModel;
    private RawCurveDataHelper curveHelper;
    private RawTableDataHelper tableHelper;
    private RawTextDataHelper textHelper;
    private int dataFrom = -1;

    public void setMLModel(MLDataModel mlModel) {
        if (mlModel == null) {
            return;
        }
        this.mlModel = mlModel;
        dataFrom = mlModel.dataFrom;
        switch (dataFrom) {
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
        if (mlModel.dataRowSelectedFlags == null || mlModel.dataRowSelectedFlags.length != this.getRowCount()) {
            mlModel.dataRowSelectedFlags = new boolean[getRowCount()];
            for (int i = 0; i < mlModel.dataRowSelectedFlags.length; i++) {
                mlModel.dataRowSelectedFlags[i] = true;
            }
        }
        if (mlModel.dataLabelAs == null || mlModel.dataLabelAs.length != this.getRowCount()) {
            mlModel.dataLabelAs = new int[getRowCount()];
            for (int i = 0; i < mlModel.dataLabelAs.length; i++) {
                mlModel.dataLabelAs[i] = 0;
            }
        }
        

    }

    @Override
    public int getRowCount() {
        switch (dataFrom) {
            case MLDataModel.FROM_CURVE:
                return curveHelper.getCurveSampleCount();
            case MLDataModel.FROM_TABLE:
                return tableHelper.getRecordCount();
            case MLDataModel.FROM_TEXT:
                return textHelper.getRowCount();
            default:
                return 0;
        }
    }

//    @Override
//    public int getColumnCount() {
//        switch (dataFrom) {
//            case MLDataModel.FROM_CURVE:
//                return curveHelper.getCurveCount() + 2;
//            case MLDataModel.FROM_TABLE:
//                return tableHelper.getFieldCount() + 1;
//            case MLDataModel.FROM_TEXT:
//                return textHelper.getColumnCount() + 1;
//            default:
//                return 0;
//        }
//    }
    @Override
    public int getColumnCount() {
        int colCount = 0;
        switch (dataFrom) {
            case MLDataModel.FROM_CURVE:
                colCount = 2+curveHelper.getCurveCount()+1;
                
                break;
            case MLDataModel.FROM_TABLE:
                colCount = 1+tableHelper.getFieldCount()+1;
                break;
            case MLDataModel.FROM_TEXT:
                colCount = 1+textHelper.getColumnCount()+1;
                break;
            default:
                return 0;
        }
        
        switch (mlModel.learningMode) {
            case MLGlobal.CLASSIFYING_MODE:
                if (mlModel.classifyResult != null) {
                    ++colCount;
                }
                break;
            case MLGlobal.CLUSTERING_MODE:
                if (mlModel.clusterResult != null) {
                    ++colCount;
                }
                break;
            case MLGlobal.PREDICTING_MODE:
                if (mlModel.predictResult != null) {
                    ++colCount;
                }
                break;
        }

        return colCount;
    }

//    @Override
//    public Object getValueAt(int rowIndex, int columnIndex) {
//        if (columnIndex == 0) {
//            return mlModel.dataRowSelectedFlags[rowIndex] ? "" : " X";
//        } else {
//            columnIndex -= 1;
//            switch (dataFrom) {
//                case MLDataModel.FROM_CURVE:
//                    if (columnIndex == 0) {
//                        return Double.toString(curveHelper.getDepth(rowIndex));
//                    } else {
//                        return String.format("%.4f", curveHelper.getCurveData(columnIndex - 1, rowIndex));
//                    }
//                case MLDataModel.FROM_TABLE:
//                    return tableHelper.getTableData(rowIndex, columnIndex);
//                case MLDataModel.FROM_TEXT:
//                    return textHelper.getTextData(rowIndex, columnIndex);
//                default:
//                    return "";
//            }
//        }
//    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        /*if (columnIndex == 0) {
            return mlModel.dataRowSelectedFlags[rowIndex] ? ("  "+(rowIndex+1)) : ("X "+(rowIndex+1));
        } else {

            if (columnIndex == getColumnCount() - 1) {
                switch (mlModel.learningMode) {
                    case MLGlobal.CLASSIFYING_MODE:
                        if (mlModel.classifyResult != null) {
                            return mlModel.classifyResult[rowIndex];
                        }
                        break;
                    case MLGlobal.CLUSTERING_MODE:
                        if (mlModel.clusterResult != null) {
                            return mlModel.clusterResult[rowIndex];
                        }
                        break;
                    case MLGlobal.PREDICTING_MODE:
                        if (mlModel.predictResult != null) {
                            return mlModel.predictResult[rowIndex];
                        }
                        break;
                }
            }*/

            
            switch (dataFrom) {
                case MLDataModel.FROM_CURVE:
                    if (columnIndex == 0) {
                        if(mlModel.dataRowSelectedFlags[rowIndex]){
                            return rowIndex+1+"";
                        }else{
                            return "X  "+(rowIndex+1);
                        }
                        
                    }else if(columnIndex == 1){
                        return Double.toString(curveHelper.getDepth(rowIndex));
                    }else if(columnIndex>=(0+2)&&columnIndex<(0+2+mlModel.getVariables().length)){
                        return String.format("%.4f", curveHelper.getCurveData(columnIndex - 2, rowIndex));
                    }else if(columnIndex == 0+2+mlModel.getVariables().length)
                    {
                        if(mlModel.dataLabelAs[rowIndex] == 0 ){
                            return "无";  
                        }else{
                            int layerIndex = mlModel.dataLabelAs[rowIndex];
                            return LoadConfigure.colorLayers.get(layerIndex).nameOfLayer;
                        }
                    }else if(columnIndex == 0+2+mlModel.getVariables().length+1){
                        if(mlModel.learningMode == MLGlobal.CLASSIFYING_MODE){
                            int layerIndex = mlModel.classifyResult[rowIndex];
                            return LoadConfigure.colorLayers.get(layerIndex).nameOfLayer;
                        }else if(mlModel.learningMode == MLGlobal.CLUSTERING_MODE){
                            return ""+mlModel.clusterResult[rowIndex];
                        }
                    }else{
                        return "";
                    }
                case MLDataModel.FROM_TABLE:
                    if (columnIndex == 0) {
                        return rowIndex+1+"";
                    }else if((columnIndex>=0+1)&&(columnIndex<0+1+tableHelper.getFieldCount())){
                       return tableHelper.getTableData(rowIndex-1, columnIndex); 
                    }else if(columnIndex == 0+1+tableHelper.getFieldCount()){
                        return "无";
                    }else if(columnIndex == 0+1+tableHelper.getFieldCount()+1){
                        return "未实现的功能";
                    }else{
                        return "未实现的功能";
                    }
                    
                case MLDataModel.FROM_TEXT:
                    if (columnIndex == 0) {
                        return rowIndex+1+"";
                    }else if((columnIndex>=0+1)&&(columnIndex<0+1+tableHelper.getFieldCount())){
                       return textHelper.getTextData(rowIndex-1, columnIndex); 
                    }else if(columnIndex == 0+1+textHelper.getColumnCount()){
                        return "无";
                    }else if(columnIndex == 0+1+textHelper.getColumnCount()+1){
                        return "未实现的功能";
                    }else{
                        return "未实现的功能";
                    }
            
            }
            return "";
    }

    public void setRowEnabled(int rowIndex, boolean b) {
        mlModel.dataRowSelectedFlags[rowIndex] = b;
    }

//    @Override
//    public String getColumnName(int column) {
//        if (column == 0) {
//            return "标志";
//        } else {
//            column -= 1;
//            switch (dataFrom) {
//                case MLDataModel.FROM_CURVE:
//                    if (column == 0) {
//                        return nameUnitToString("深度", curveHelper.getDepthUnit());
//                    } else {
//                        return nameUnitToString(curveHelper.getCurveName(column - 1), curveHelper.getCurveUnit(column - 1));
//                    }
//                case MLDataModel.FROM_TABLE:
//                    return nameUnitToString(tableHelper.getFieldName(column), tableHelper.getFieldUnit(column));
//                case MLDataModel.FROM_TEXT:
//                    return nameUnitToString(textHelper.getColumnName(column), textHelper.getColumnUnit(column));
//                default:
//                    return "";
//            }
//        }
//    }
    @Override
    public String getColumnName(int column) {
        if(column == 0){
            return "标志/序号";
        }
        switch(dataFrom){
            case MLDataModel.FROM_CURVE: {
                if (column == 1) {
                    return nameUnitToString("深度", curveHelper.getDepthUnit());
                }
                if (column >= (0 + 2) && column < (0 + 2 + mlModel.getVariables().length)) {
                    return nameUnitToString(curveHelper.getCurveName(column - 2), curveHelper.getCurveUnit(column - 2));
                }
                if (column == 0 + 2 + mlModel.getVariables().length) {
                    return "标签";    
                }
                if(column == 0 + 2 + mlModel.getVariables().length +1){
                    
                    if (mlModel.learningMode == MLGlobal.PREDICTING_MODE) {
                        return "模型预测值";
                    } else if (mlModel.learningMode == MLGlobal.CLASSIFYING_MODE) {
                        return "标签";
                    } else if (mlModel.learningMode == MLGlobal.CLUSTERING_MODE) {
                        return "Cluster";
                    }
                }
                return "";
            }
            case MLDataModel.FROM_TABLE:{
                if(column>=(0+1)&&column<(0+1+mlModel.getVariables().length)){
                   return nameUnitToString(tableHelper.getFieldName(column-1), tableHelper.getFieldUnit(column-1)); 
                }else if(column == 0+1+mlModel.getVariables().length){
                    return "标签";
                }else if(column == 0+1+mlModel.getVariables().length+1){
                    if (mlModel.learningMode == MLGlobal.PREDICTING_MODE) {
                        return "模型预测值";
                    } else if (mlModel.learningMode == MLGlobal.CLASSIFYING_MODE) {
                        return "分类结果";
                    } else if (mlModel.learningMode == MLGlobal.CLUSTERING_MODE) {
                        return "聚类结果";
                    }
                } 
            }
            case MLDataModel.FROM_TEXT:{
                if(column>=(0+1)&&column<(0+1+mlModel.getVariables().length)){
                    return nameUnitToString(textHelper.getColumnName(column-1), textHelper.getColumnUnit(column-1));
                }else if(column == 0+1+mlModel.getVariables().length){
                    return "标签";
                }else if(column == 0+1+mlModel.getVariables().length+1){
                    if (mlModel.learningMode == MLGlobal.PREDICTING_MODE) {
                        return "模型预测值";
                    } else if (mlModel.learningMode == MLGlobal.CLASSIFYING_MODE) {
                        return "分类结果";
                    } else if (mlModel.learningMode == MLGlobal.CLUSTERING_MODE) {
                        return "聚类结果";
                    }
                }
            }
            
        }
       return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    private String nameUnitToString(String name, String unit) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (!unit.isEmpty()) {
            sb.append("(").append(unit).append(")");
        }
        return sb.toString();
    }
    
    public MLDataModel getmlModel(){
        return mlModel;
    }

}
