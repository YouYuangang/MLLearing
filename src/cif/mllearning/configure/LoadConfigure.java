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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.openide.windows.WindowManager;

/**
 *
 * @author Administrator
 */

public class LoadConfigure {
    public static String OIL_MODEL_NAME_BP = "oil_bp.model";
    public final static String LITH_MODEL_NAME_BP = "lith_bp.mode";
    public static final String LOG_FILENAME = "mylog.txt";
    public static final String FILE_NAMES_COLOR = "layers.json";
    
    public static BufferedWriter bfw = null;
    public static String trainedModelPath;
    public static HashMap<String,Color> nameColorMap = new HashMap<>();
    
    static {
        //StringBuilder confStr = new StringBuilder(1024);
        //BufferedReader bfr = null;
        try {
            String cifInstallPath = Global.getInstallationPath();
            trainedModelPath = cifInstallPath +File.separator+"modelSaveDir";//创建一个modelSaveDir文件夹保存训练的模型
            File modelSaveDir = new File(trainedModelPath);
            if(!modelSaveDir.exists()){
                modelSaveDir.mkdir();
            }
            //日志输出流用于debug;
            bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Global.getInstallationPath()+ File.separator + LOG_FILENAME))));
            String confPath = cifInstallPath + File.separator + FILE_NAMES_COLOR;
            File confFile = new File(confPath);
            StringBuffer confStr = new StringBuffer(1024);
            BufferedReader bfr = null;
            if (!confFile.exists()) {
                Frame parent = WindowManager.getDefault().getMainWindow();
                JOptionPane.showMessageDialog(parent, "配置文件不存在"+confPath);
            } else {
                InputStream ins = new FileInputStream(confFile);
                bfr = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
                String temp = bfr.readLine();
                while (temp != null) {
                    confStr.append(temp);
                    temp = bfr.readLine();
                }
                try {
                    JSONArray jsonArray = JSON.parseArray(confStr.toString());
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        int red = jsonObject.getInteger("red");
                        int green = jsonObject.getInteger("green");
                        int blue = jsonObject.getInteger("blue");
                        nameColorMap.put(name,new Color(red,green,blue));
                    }
                    Frame parent = WindowManager.getDefault().getMainWindow();
                    JOptionPane.showMessageDialog(parent, "加载颜色配置文件成功共"+nameColorMap.size()+"层");
                } catch (Exception e) {
                    e.printStackTrace();
                    Frame parent = WindowManager.getDefault().getMainWindow();
                    JOptionPane.showMessageDialog(parent, "解析配置文件出错");
                }

            }

        } catch (Exception e) {

        } finally {
            /*if (bfr != null) {
                try {
                    bfr.close();
                } catch (Exception e) {
                }
            }*/
        }
        
    }
    public static void writeLog(String text){
        try{
            bfw.write(text+"\n");
            bfw.flush();
        }catch(Exception e){
        }
    }
    
}
