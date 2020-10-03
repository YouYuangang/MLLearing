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
    public final static String OIL_RES_TABLE_CLASSIFY = "含油性分类结果";
    public final static String OIL_RES_TABLE_CLUSTER = "含油性聚类结果";
    public final static String[] OIL_FEILDSNAME_CLASSIY = new String[]{"开始深度","结束深度","分类结果"};
    public final static String[] OIL_FEILDSNAME_CLUSTER = new String[]{"开始深度","结束深度","聚类结果"};
    
    public final static String LITH_RES_TABLE_CLASSIFY = "岩性分类结果";
    public final static String LITH_RES_TABLE_CLUSTER = "岩性聚类结果";
    public final static String[] LITH_FEILDSNAME_CLASSIY = new String[]{"开始深度","结束深度","分类结果"};
    public final static String[] LITH_FEILDSNAME_CLUSTER = new String[]{"开始深度","结束深度","聚类结果"};
    
    public static BufferedWriter bfw = null;
    public static String trainedModelPath;
    
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
            bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Global.getInstallationPath()+ File.separator + "mylog.txt"))));
            /*String confPath = cifInstallPath + File.separator + "layers.json";
            File confFile = new File(confPath);
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
                        colorLayers.add(new ColorLayer(name, red, green, blue));
                    }
                    Frame parent = WindowManager.getDefault().getMainWindow();
                    JOptionPane.showMessageDialog(parent, "加载配置文件成功共"+colorLayers.size()+"层");
                } catch (Exception e) {
                    e.printStackTrace();
                    Frame parent = WindowManager.getDefault().getMainWindow();
                    JOptionPane.showMessageDialog(parent, "解析配置文件出错");
                }

            }*/

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
