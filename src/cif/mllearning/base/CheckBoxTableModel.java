/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *数据适配类，为InputDataDialog相关控件提供数据，显示某测井具有的曲线名称
 * @author wangcaizhi
 * @create 2019.2.25
 */
public class CheckBoxTableModel extends AbstractTableModel {

    private final ArrayList<CBTableRow> data = new ArrayList<>();
    private static final Class[] TYPES = new Class[]{
        java.lang.String.class, java.lang.Boolean.class,String.class,String.class,String.class
    };
    private static final String[] COLUMN_NAMES = {"名称", "选择","开始深度","结束深度","采样间隔"};

    public void add(String item) {
        data.add(new CBTableRow(item));
    }
    public void add(String item,boolean isSelected) {
        data.add(new CBTableRow(item, isSelected));
    }
    public void add(String item,boolean isSelected,String sDepth,String eDepth,String interval){
        data.add(new CBTableRow(item,isSelected,sDepth,eDepth,interval));
    }

    public int getSelectedCount() {
        int count = 0;
        for (CBTableRow row : data) {
            if (row.isSelected) {
                count++;
            }
        }
        return count;
    }

    public void removeAll() {
        data.clear();
    }

    public boolean isSelected(int index) {
        return data.get(index).isSelected;
    }

    public void select(String name) {
        for (CBTableRow row : data) {
            if (row.name.equals(name)) {
                row.isSelected = true;
                break;
            }
        }
    }

    public void select(int index) {
        data.get(index).isSelected = true;
    }

    public void setSelection(int index, boolean b) {
        data.get(index).isSelected = b;
    }

    public String getName(int index) {
        return data.get(index).name;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return TYPES[columnIndex];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public String[] getSelectedNames() {
        String[] selectedNames = new String[getSelectedCount()];
        int index = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected) {
                selectedNames[index] = data.get(i).name;
                index++;
            }
        }
        return selectedNames;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CBTableRow row = data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.name;
            case 1:
                return row.isSelected;
            case 2:
                return row.sDepth;
            case 3: return row.eDepth;
            case 4: return row.interval;
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            data.get(rowIndex).isSelected = (boolean) aValue;
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    public void selectAll() {
        for (CBTableRow row : data) {
            row.isSelected = true;
        }
    }

    public void selectNone() {
        for (CBTableRow row : data) {
            row.isSelected = false;
        }
    }

    public void invertSelection() {
        for (CBTableRow row : data) {
            row.isSelected = !row.isSelected;
        }
    }

}

class CBTableRow {

    public String name;
    public boolean isSelected;
    public String sDepth;
    public String eDepth;
    public String interval;

    public CBTableRow(String name) {
        this.name = name;
        this.isSelected = false;
    }

    public CBTableRow(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }
    public CBTableRow(String name, boolean isSelected,String sDepth,String eDepth,String interval) {
        this.name = name;
        this.isSelected = isSelected;
        this.sDepth = sDepth;
        this.eDepth = eDepth;
        this.interval = interval;
    }
}
