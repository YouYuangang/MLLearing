/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.dataengine.io.LogCategory;
import cif.dataengine.io.LogTable;
import cif.dataengine.io.TableFields;
import cif.dataengine.io.TableRecords;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.3
 */
public class RawTableDataHelper {

    private MLDataModel mlModel;
    private int[] columnIndices;
    private TableRecords tableRecords = new TableRecords();

    public RawTableDataHelper(MLDataModel mlModel) {
        this.mlModel = mlModel;
        LogCategory category = mlModel.inputDataPath.getCategory();
        LogTable logTable = category.getLogCommonTable(mlModel.logTableName);
        if (logTable == null) {
            logTable = category.getLogDiscreteTable(mlModel.logTableName);
        }
        logTable.readTableRecords(tableRecords);
        TableFields tableFields = tableRecords.getTableFields();
        Variable[] variables = mlModel.getVariables();
        columnIndices = new int[variables.length];
        for (int i = 0; i < variables.length; i++) {
            columnIndices[i] = getFieldIndexFromDisplayName(tableFields, variables[i].name);
        }
    }

    public int getFieldCount() {
        return columnIndices.length;
    }

    public String getFieldName(int fieldIndex) {
        TableFields tableFields = tableRecords.getTableFields();
        return tableFields.getName(columnIndices[fieldIndex]);
    }

    public String getFieldUnit(int fieldIndex) {
        TableFields tableFields = tableRecords.getTableFields();
        return tableFields.getUnit(columnIndices[fieldIndex]);
    }

    public int getRecordCount() {
        return tableRecords.getRecordsNum();
    }

    public String getTableData( int recordIndex, int fieldIndex) {
          return tableRecords.getRecordString(recordIndex, columnIndices[fieldIndex])[0];
    }

    private int getFieldIndexFromDisplayName(TableFields tableFields, String displayName) {
        for (int i = 0; i < tableFields.getFieldNum(); i++) {
            if (tableFields.getDisplayName(i).equals(displayName)) {
                return i;
            }
        }
        return -1;
    }
}
