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
    public static HashMap<Integer,Integer> clusterLayerRelation = new HashMap<>();
    public static ArrayList<ColorLayer> colorLayers = new ArrayList<ColorLayer>();
    public static BufferedWriter bfw = null;
    public static String trainedModelPath;
    
    static {
        StringBuilder confStr = new StringBuilder(1024);
        BufferedReader bfr = null;
        try {
            String cifInstallPath = Global.getInstallationPath();
            trainedModelPath = cifInstallPath +File.separator+"modelSaveDir";//创建一个modelSaveDir文件夹保存训练的模型
            File modelSaveDir = new File(trainedModelPath);
            if(!modelSaveDir.exists()){
                modelSaveDir.mkdir();
            }
            String confPath = cifInstallPath + File.separator + "layers.json";
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

            }

        } catch (Exception e) {

        } finally {
            if (bfr != null) {
                try {
                    bfr.close();
                } catch (Exception e) {
                }
            }
        }
        try{
            bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Global.getInstallationPath()+ File.separator + "mylog.txt"))));
        }catch(Exception e){
            
        }finally{
            
        }  
    }
    public static void writeLog(String text){
        try{
            bfw.write(text);
            bfw.flush();
        }catch(Exception e){
        }
    }
    public  String toString(){
        StringBuilder sb = new StringBuilder(1024);
        for(int i = 0;i<colorLayers.size();i++){
            sb.append(colorLayers.get(i).toString());
        }
        return sb.toString();
    }
}
