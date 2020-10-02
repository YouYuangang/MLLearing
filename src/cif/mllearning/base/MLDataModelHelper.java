/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.base.Global;
import cif.dataengine.DataPath;
import cif.mllearning.MLGlobal;
import cif.mllearning.configure.LoadConfigure;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.openide.windows.WindowManager;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.22
 */
public class MLDataModelHelper {

    private MLDataModel mlModel;

    public MLDataModelHelper(MLDataModel mlModel) {
        this.mlModel = mlModel;
    }

    /*public int getRealXVariableCount() {
        int xVarCount = 0;
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE) {
                xVarCount++;
            }
        }
        return xVarCount;
    }*/
    public static void saveMlModelToFile(MLDataModel mlModel){
        String cifInstallPath = Global.getInstallationPath();
        String confPath = cifInstallPath + File.separator + MLGlobal.MLMODEL_FILENAME;
        FileOutputStream ops = null;
        BufferedWriter bfw = null;
        try{
            ops = new FileOutputStream(confPath);
            bfw = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            bfw.write("dataFrom,"+mlModel.dataFrom);
            bfw.newLine();
            if(mlModel.dataFrom == MLDataModel.FROM_CURVE||mlModel.dataFrom == MLDataModel.FROM_TABLE){
                bfw.write("dataPath,"+mlModel.inputDataPath);
            }else{
                bfw.write("filePath,"+mlModel.inputFilePath);
            }
            bfw.newLine();
            bfw.write("x,"+mlModel.getVariables().length);
            bfw.newLine();
            for(int i = 0;i<mlModel.getVariables().length;i++){
                bfw.write(mlModel.getVariables()[i].name);
                bfw.newLine();
            }
            if(mlModel.dataFrom == MLDataModel.FROM_CURVE){
                bfw.write(""+mlModel.curveStdep+","+mlModel.curveEndep);
            }
            
        }catch(IOException e){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "保存mlModel出错");
        }finally{
            try{
                if(bfw!=null){
                    bfw.close();
                }
                
            }catch(Exception e){
                
            }
            
        }
    }
    public static boolean loadMlModelFromFile(MLDataModel mlModel){
        String cifInstallPath = Global.getInstallationPath();
        String confPath = cifInstallPath + File.separator + MLGlobal.MLMODEL_FILENAME;
        FileInputStream fis = null;
        BufferedReader bfr = null;
        File file = new File(confPath);
        if(!file.exists()){
            return false;
        }
        try{
            fis = new FileInputStream(file);
            bfr = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
            
            String temp = bfr.readLine();
            String[] atemp = temp.split(",");
            mlModel.dataFrom = Integer.parseInt(atemp[1]);
            
            temp = bfr.readLine();
            atemp = temp.split(",");
            if(mlModel.dataFrom == MLDataModel.FROM_CURVE||mlModel.dataFrom == MLDataModel.FROM_TABLE){
                mlModel.inputDataPath = new DataPath(atemp[1]);
            }else{
                mlModel.inputFilePath = atemp[1];
            }
            
            temp = bfr.readLine();
            atemp = temp.split(",");
            int xcount = Integer.parseInt(atemp[1]);
            mlModel.variables = new Variable[xcount];
            for(int i =0;i<xcount;i++){
                temp = bfr.readLine();
                mlModel.variables[i] = new Variable(temp,MLDataModel.X_VARIABLE_OIL);
            }
            if(mlModel.dataFrom == MLDataModel.FROM_CURVE){
                temp = bfr.readLine();
                atemp = atemp = temp.split(",");
            }
            mlModel.curveStdep = Double.parseDouble(atemp[0]);
            mlModel.curveEndep = Double.parseDouble(atemp[1]);
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "导入mlModel出错");
            return false;
        }finally{
            if(bfr!=null){
                try{
                    bfr.close();
                }catch(IOException e){
                    
                }
            }
            
        } 
    }
   
    public int getOilXVariableCount(){
        int xVarCount = 0;
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE_OIL) {
                xVarCount++;
            }
        }
        return xVarCount;
    }
    public int getLithXVariableCount(){
        int xVarCount = 0;
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE_LITH) {
                xVarCount++;
            }
        }
        return xVarCount;
    }

    public int getRealVariableCount() {
        int varCount = 0;
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag > 0) {
                varCount++;
            }
        }
        return varCount;
    }

    /*public String[] getRealXVariableNames() {
        String[] names = new String[getRealXVariableCount()];
        Variable[] variables = mlModel.getVariables();
        int index = 0;
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE) {
                names[index++] = variable.name;
            }
        }
        return names;
    }*/
    
    public String[] getOilXVariableNames(){
        String[] names = new String[getOilXVariableCount()];
        Variable[] variables = mlModel.getVariables();
        int index = 0;
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE_OIL) {
                names[index++] = variable.name;
            }
        }
        return names;
    }
    
    public String[] getLithXVariableNames(){
        String[] names = new String[getLithXVariableCount()];
        Variable[] variables = mlModel.getVariables();
        int index = 0;
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.X_VARIABLE_LITH) {
                names[index++] = variable.name;
            }
        }
        return names;
    }

    public String getOilYVariableName() {
        Variable[] variables = mlModel.getVariables();
        for (Variable variable : variables) {
            if (variable.flag == MLDataModel.Y_VARIABLE_OIL) {
                return variable.name;
            }
        }
        return "没有y变量";
    }

    public String[] getRealVariableNames() {
        String[] names = new String[getRealVariableCount()];
        Variable[] variables = mlModel.getVariables();
        int index = 0;
        for (Variable variable : variables) {
            if (variable.flag > 0) {
                names[index++] = variable.name;
            }
        }
        return names;
    }

    public String formString(String[] strs, String separator) {
        if (strs.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strs[0]);
            for (int i = 1; i < strs.length; i++) {
                sb.append(separator).append(strs[i]);
            }
            return sb.toString();
        }
    }

}
