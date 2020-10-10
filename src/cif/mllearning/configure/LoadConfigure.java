/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.configure;
import cif.base.Global;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.awt.Color;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.openide.windows.WindowManager;

/**
 * 
 * @author Y.G. YOU 2020.10.9
 * 
 */

public class LoadConfigure {
    public static final String LOG_FILENAME = "myLog.txt";
    public static final String FILE_NAMES_COLOR = "colorForlayers.json";
    
    public static String CONFIG_DIR_NAME = "mllearningConfig";
    public static String MLLEARNING_CONFIG_PATH = null;
    public static String MODEL_SAVED_PATH = null;
    public static BufferedWriter bfw = null;
    public static HashMap<String,Color> nameColorMap = new HashMap<>();
    
    static {
        checkAndCreateNeedDir();
        createLogStream();
        loadLayerColorFile();
    }
    public static void writeLog(String text){
        try{
            bfw.write(text+"\n");
            bfw.flush();
        }catch(Exception e){
        }
    }
    public static void writeErrorLog(String text){
        try{
            bfw.write("**********Error**********"+"\n"+text+"\n");
            bfw.flush();
        }catch(Exception e){
        }
    }
    public static int checkAndCreateNeedDir(){
            String cifInstallPath = Global.getInstallationPath();
            MLLEARNING_CONFIG_PATH = cifInstallPath+File.separator+CONFIG_DIR_NAME;
            MODEL_SAVED_PATH = MLLEARNING_CONFIG_PATH +File.separator+"modelSaveDir";//创建一个modelSaveDir文件夹保存训练的模型
            File configDir = new File(MLLEARNING_CONFIG_PATH);
            File modelSaveDir = new File(MODEL_SAVED_PATH);
            if(!configDir.exists()){
                configDir.mkdir();
            }
            if(!modelSaveDir.exists()){
                modelSaveDir.mkdir();
            }
            return 1;
            //日志输出流用于debug;
            
    }
    public static int createLogStream(){
        try{
             bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    new File(MLLEARNING_CONFIG_PATH+ File.separator + LOG_FILENAME))));
             return 1;
        }catch(Exception e){
            return -1;
        }
           
    }
    public static int loadLayerColorFile() {

        String layerColorConfPath = MLLEARNING_CONFIG_PATH + File.separator + FILE_NAMES_COLOR;
        File layerConfFile = new File(layerColorConfPath);
        StringBuffer confStr = new StringBuffer(1024);
        BufferedReader bfr = null;
        if (!layerConfFile.exists()) {
            Frame parent = WindowManager.getDefault().getMainWindow();
            JOptionPane.showMessageDialog(parent, "颜色配置文件不存在" + layerColorConfPath);
            writeErrorLog("颜色配置文件不存在！");
            return -1;
        } else {
            try {
                InputStream ins = new FileInputStream(layerConfFile);
                bfr = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
                String temp = bfr.readLine();
                while (temp != null) {
                    confStr.append(temp);
                    temp = bfr.readLine();
                }
            } catch (IOException e) {
                writeErrorLog("读入颜色配置文件出错！");
            }

            try {
                JSONArray jsonArray = JSON.parseArray(confStr.toString());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    int red = jsonObject.getInteger("red");
                    int green = jsonObject.getInteger("green");
                    int blue = jsonObject.getInteger("blue");
                    nameColorMap.put(name, new Color(red, green, blue));
                }
                Frame parent = WindowManager.getDefault().getMainWindow();
                JOptionPane.showMessageDialog(parent, "加载颜色配置文件成功共" + nameColorMap.size() + "层");
                return 1;

            } catch (Exception e) {
                e.printStackTrace();
                Frame parent = WindowManager.getDefault().getMainWindow();
                JOptionPane.showMessageDialog(parent, "解析颜色配置文件(Json)出错");
                writeErrorLog("解析颜色配置文件(Json)出错");
                return -1;
            } finally {
                try {
                    if (bfr != null) {
                        bfr.close();
                    }
                } catch (Exception e) {

                }

            }

        }
    }
}
