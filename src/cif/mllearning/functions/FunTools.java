/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.base.Global;
import cif.baseutil.PathUtil;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.MLDataModelHelper;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.23
 */
public class FunTools {

    public static String MODEL_EXTENSION = ".model";

    public static int toInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return Global.NULL_INT_VALUE;
        }
    }

    public static float toFloat(String s) {
        try {
            return Float.valueOf(s);
        } catch (NumberFormatException e) {
            return Global.NULL_FLOAT_VALUE;
        }
    }

    public static double toDouble(String s) {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            return Global.NULL_DOUBLE_VALUE;
        }
    }

    public static String getModelFileName(String functionName, MLDataModel mlModel) {
        String dataFrom = "";
        switch (mlModel.dataFrom) {
            case MLDataModel.FROM_CURVE:
            case MLDataModel.FROM_TABLE:
                dataFrom = mlModel.inputDataPath.getWell().getWellName();
                break;
            case MLDataModel.FROM_TEXT:
                dataFrom = PathUtil.getBaseName(mlModel.inputFilePath);
                break;
        }
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        sb.append(functionName).append("_").append(dataFrom).append("_").append(dateFormat.format(new Date())).append(MODEL_EXTENSION);
        return sb.toString();
    }

    public static String getModelPath() {
        return Global.getUserResourcePath(Global.PATH_ML_MODEL);
    }

    public static void saveModelAuxFile(boolean isYExisted, String modelFilePath, MLDataModelHelper mlModelHelper, Normalization normalization) {
        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelFilePath + "Aux"), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            if (isYExisted) {
                String[] names = mlModelHelper.getRealXVariableNames();
                sb.append("x,").append(names.length).append("\n");
                for (int i = 0; i < names.length; i++) {
                    sb.append(names[i]).append(",").append(normalization.getXVarLower(i)).append(",").append(normalization.getXVarUpper(i)).append("\n");
                }
                sb.append("y,1\n");
                sb.append(mlModelHelper.getRealYVariableName()).append(",").append(normalization.getYVarLower()).append(",").append(normalization.getYVarUpper()).append("\n");
            } else {
                String[] names = mlModelHelper.getRealVariableNames();
                sb.append("x,").append(names.length).append("\n");
                for (int i = 0; i < names.length; i++) {
                    sb.append(names[i]).append(",").append(normalization.getVarLower(i)).append(",").append(normalization.getVarUpper(i)).append("\n");
                }
            }
            outputWriter.write(sb.toString());
            outputWriter.close();
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                outputWriter.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public static HashMap<String, Integer> createItemCodeTable(String[] items) {
        HashMap<String, Integer> map = new HashMap<>();
        int val = 0;
        for (String item : items) {
            if (!map.containsKey(item)) {
                map.put(item, val++);
            }
        }
        return map;
    }

    public static int compete(double[] array) {
        double max = array[0];
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                index = i;
                max = array[i];
            }
        }
        return index;
    }

    public static String getItemFromCode(HashMap<String, Integer> map, int code) {
        for (Map.Entry entry : map.entrySet()) {
            if ((Integer) entry.getValue() == code) {
                return (String) entry.getKey();
            }
        }
        return null;
    }

    public static int computeEquivalenceCount(String[] y1s, String[] y2s) {
        int correctCount = 0;
        for (int i = 0; i < y1s.length; i++) {
            if (y1s[i].equals(y2s[i])) {
                correctCount++;
            }
        }
        return correctCount;
    }
    
     public static int computeEquivalenceCount(double[] y1s, double[] y2s) {
        int correctCount = 0;
        for (int i = 0; i < y1s.length; i++) {
            if (Math.abs(y1s[i]-y2s[i])<0.00001) {
                correctCount++;
            }
        }
        return correctCount;
    }

}
