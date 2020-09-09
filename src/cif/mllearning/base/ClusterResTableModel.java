/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;
import cif.mllearning.configure.ColorLayer;
import cif.mllearning.MLGlobal;
import cif.mllearning.configure.LoadConfigure;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Administrator
 * 用于将聚类号对应到分层的界面中，提供所有层的名字
 */
public class ClusterResTableModel extends AbstractTableModel{
    private MLDataModel mlModel = null;
    private String[] COLUMN_NAMES= new String[]{"聚类号","选择分层"};
    public ClusterResTableModel(){
        
    }
    public void setModel(MLDataModel mlModel){
        this.mlModel = mlModel;
    }
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    @Override
    public int getRowCount() {
        if(mlModel==null){
            return 0;
        }
        return mlModel.clusterCount; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        
        return 2;
         //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex==0){
            return String.valueOf(rowIndex);
        }else if(columnIndex==1){
            String[] layersNames = new String[LoadConfigure.colorLayers.size()];
            int i = 0;
            for(ColorLayer layer:LoadConfigure.colorLayers){
                layersNames[i] = layer.nameOfLayer;
            }
            return layersNames;
        }else{
            return "null";
        }
    }
    
}
