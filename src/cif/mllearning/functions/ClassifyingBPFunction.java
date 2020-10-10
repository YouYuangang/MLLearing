/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import cif.mllearning.base.DataHelper;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.MLDataModelHelper;
import cif.mllearning.base.TableHelper;
import cif.mllearning.base.UpdatePanelFlag;
import cif.mllearning.base.Variable;
import cif.mllearning.configure.LoadConfigure;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JOptionPane;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.openide.windows.WindowManager;

/**
 *在dobackground方法中，如果是生成模式则产生模型，如果是运行模式则运行模型
 * 在生成模式中，需要形成X数据，并整理Y数据，Y数据需要处理，将文本map到int，然后训练
 * 在运行模式中，需要读入模型配置文件，检查当前是否导入了模型需要的变量，并读入网络输出int
 * 到实际标签String的关系，形成Map;
 * 
 * @author Y.G. YOU
 * @create 2020.10.08
 * 
 */
public class ClassifyingBPFunction extends Function {
    //网络默认参数
    private int hiddenNeuronCount = 16;
    private double learningRate = 0.05;
    private double maxError = 0.005;
    private int maxIteration = 200;
    
    //网络输入输出节点数
    public int XcountOil = 0;
    public int YcountOil = 0;
    public int XcountLith = 0;
    public int YcountLith = 0;
    
    //是否进行含油性模型训练
    public boolean oilTrainFlag = false;
    //是否进行岩性模型训练
    public boolean lithTrainFlag = false;
    
    //保存用到变量的名字，进行数据归一化
    private Normalization normalizationForOil;
    private Normalization normalizationForLith;
    
    //样本标签文本到int的map
    public HashMap<String,Integer> stringIntMapForOil = null;
    public HashMap<String,Integer> stringIntMapForLith = null;
    
    //样本标签对应的int
    private int[] desiredYOil = null;
    private int[] desiredYLith = null;
    
   //生成模型后，将模型保存到磁盘，下面常量提供名字
    public static final String OIL_MODEL_NAME_BP = "oil_bp.model";
    public static final String LITH_MODEL_NAME_BP = "lith_bp.mode"; 
 
    //模型文件，运行时需要加载它们
    public File oilModelFile = null;
    public File lithModelFile = null;
    
    //运行后将结果保存到当前井次的通用表，下面常量提供名字
    public static final String OIL_CLASSIFY_TABLENAME_BP = "oilClassifyByBP";
    public static final String LITH_CLASSIFY_TABLENAME_BP = "lithClassifyByBP";
    
    //将运行结果保存到当前井次的通用表格
    public TableHelper tableHelper = null;
    
    @Override
    public void setMLModel(MLDataModel mlModel) {
        this.mlModel = mlModel;
        mlModelHelper = new MLDataModelHelper(mlModel);
        dataHelper = new DataHelper(mlModel);
        tableHelper = new TableHelper(mlModel,dataHelper);
        if(flag == Function.GENERATE_MODEL){
            this.XcountOil = dataHelper.getOilXVariableCount();
            this.XcountLith = dataHelper.getLithXVariableCount();
            stringIntMapForOil = mlModel.StringIntMapForOil;
            stringIntMapForLith = mlModel.StringIntMapForLith;
        }
    }
    public ClassifyingBPFunction(){
        
    }
    @Override
    public boolean setParameters(Frame parentWindow) {
        BP_ANNDialog dialog = new BP_ANNDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        
        Object[] paras = mlModel.getParameters(this.getClass().getSimpleName());
        hiddenNeuronCount = (int) (2*MathBase.maximum(new int[]{XcountOil,XcountLith}));
        dialog.setHiddenNeuronCount(paras == null ? hiddenNeuronCount : (int) paras[0]);
        dialog.setLearningRate(paras == null ? learningRate : (double) paras[1]);
        dialog.setMaxError(paras == null ? maxError : (double) paras[2]);
        dialog.setMaxIteration(paras == null ? maxIteration : (int) paras[3]);

        dialog.setVisible(true);
        if (dialog.getReturnStatus() == BP_ANNDialog.RET_OK) {
            hiddenNeuronCount = dialog.getHiddenNeuronCount();
            learningRate = dialog.getLearningRate();
            maxError = dialog.getMaxError();
            maxIteration = dialog.getMaxIteration();
            Object[] params = new Object[]{hiddenNeuronCount, learningRate, maxError, maxIteration};
            mlModel.setParameters(this.getClass().getSimpleName(), params);
            return true;
        }
        return false;
    }

    @Override
    protected Integer doInBackground() throws Exception { 
        if(dataHelper.oilYVariableColumnIndex>=0){
            oilTrainFlag = true;
        }
        if(dataHelper.lithYVariableColumnIndex>=0){
            lithTrainFlag = true;
        }

        if (flag == Function.GENERATE_MODEL) {
            if(oilTrainFlag){
                startOilModelTrain();
            }
            if(lithTrainFlag){
                startLithModelTrain();
            }
        } else {
            String[] needXsOil = oilClassifyCanGoOn();
            String[] needXsLith = lithClassifyCanGoOn();
            
            if(needXsOil!=null){
                doClassifyOil(needXsOil);
            }
            if(needXsLith!=null){
                doClasifyLith(needXsLith);
            }
            
        }
        return 1;
    }
    public void doClassifyOil(String [] needXsOil){  
            print("bp:开始含油性分类\n");
            DataSet needToClassify = formOilToClassifyDataSet(needXsOil);
            MultiLayerPerceptron neuralNet = (MultiLayerPerceptron)NeuralNetwork.createFromFile(oilModelFile);
            print("bp:加载含油性模型成功："+oilModelFile.getAbsolutePath()+"\n");
            int[] yByModel = computeY(neuralNet,needToClassify);
            
            mlModel.classifyResultOil = new String[mlModel.dataRowSelectedFlags.length];
            int j = 0;
            //转换为整型对文本
            HashMap<Integer,String> intStringMap = new HashMap<>();
            for(HashMap.Entry<String,Integer> entry:stringIntMapForOil.entrySet()){
                intStringMap.put(entry.getValue(), entry.getKey());
            }
            for(int i =0;i<mlModel.classifyResultOil.length;i++){
                if(mlModel.dataRowSelectedFlags[i]){ 
                    mlModel.classifyResultOil[i] = intStringMap.get(yByModel[j++]);
                }else{
                    mlModel.classifyResultOil[i] = "无效数据";
                }
            }
            printHighlight("bp:含油性分类完成\n");
            //保存结果到当前井次
            if(mlModel.dataFrom == MLDataModel.FROM_CURVE){
                tableHelper.saveToTableFromClassifyResOil(OIL_CLASSIFY_TABLENAME_BP);
            }else{
                printHighlight("bp:结果没有保存到本地，暂不支持文本数据和表格数据的结果保存\n");
                LoadConfigure.writeErrorLog("bp:结果没有保存到本地，暂不支持文本数据和表格数据的结果保存");
            }
            
            UpdatePanelFlag.DataPanelUpdateFlag = true;
            UpdatePanelFlag.HistogramUpdateFlag = true;
            UpdatePanelFlag.CrossPlotUpdateFlag = true;
            UpdatePanelFlag.PlotPanelUpdateFlag = true;    
    }
    
    public void doClasifyLith(String[] needXsLith) {
        print("bp:开始岩性分类\n");
        DataSet needToClassify = formOilToClassifyDataSet(needXsLith);
        MultiLayerPerceptron neuralNet = (MultiLayerPerceptron) NeuralNetwork.createFromFile(lithModelFile);
        print("bp:加载岩性模型成功："+lithModelFile.getAbsolutePath()+"\n");
        int[] yByModel = computeY(neuralNet, needToClassify);

        //转换为整型对文本
        HashMap<Integer, String> intStringMap = new HashMap<>();
        for (HashMap.Entry<String, Integer> entry : stringIntMapForLith.entrySet()) {
            intStringMap.put(entry.getValue(), entry.getKey());
        }
        
        mlModel.classifyResultLith = new String[mlModel.dataRowSelectedFlags.length];
        int j = 0;
        for (int i = 0; i < mlModel.classifyResultLith.length; i++) {
            if (mlModel.dataRowSelectedFlags[i]) {
                mlModel.classifyResultLith[i] = intStringMap.get(yByModel[j++]);
            }else{
                mlModel.classifyResultLith[i] = "无效数据";
            }
        }
        printHighlight("bp:岩性分类完成！");
        //保存结果到当前井次
        if (mlModel.dataFrom == MLDataModel.FROM_CURVE) {
            tableHelper.saveToTableFromClassifyResOil(LITH_CLASSIFY_TABLENAME_BP);
        } else {
            printHighlight("bp:结果没有保存到本地，暂不支持文本数据和表格数据的结果保存\n");
            LoadConfigure.writeErrorLog("bp:结果没有保存到本地，暂不支持文本数据和表格数据的结果保存");
        }
        
        UpdatePanelFlag.DataPanelUpdateFlag = true;
        UpdatePanelFlag.HistogramUpdateFlag = true;
        UpdatePanelFlag.CrossPlotUpdateFlag = true;
        UpdatePanelFlag.PlotPanelUpdateFlag = true;
    }
    
    public String[] lithClassifyCanGoOn(){
        String[] neededXs = null;
       BufferedReader bfr = null;
       File lithModelConfFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+LITH_MODEL_NAME_BP+"Aux");
       File lithModelFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+LITH_MODEL_NAME_BP);
       if(!lithModelConfFile.exists()||!lithModelFile.exists()){
           LoadConfigure.writeLog("ClassifyingBPFunction 169:岩性模型或其配置文件不存在");
           return null;
       }
       //
       try{
           bfr = new BufferedReader(new InputStreamReader(new FileInputStream(lithModelConfFile)));
           //读取模型需要的变量
           String tempLine = bfr.readLine();
           String[] tempStrArr = tempLine.split(",");
           int xcount = Integer.parseInt(tempStrArr[1]);
           neededXs = new String[xcount];
           for(int i = 0;i<xcount;i++){
               tempLine = bfr.readLine();
               tempStrArr = tempLine.split(",");
               neededXs[i] = tempStrArr[0];
           }
           //读取文本整型对应关系
           tempLine = bfr.readLine();
           tempStrArr = tempLine.split(",");
           stringIntMapForLith = new HashMap<>();
           int stringIntCount = Integer.parseInt(tempStrArr[1]);
           for(int i = 0;i<stringIntCount;i++){
               tempLine = bfr.readLine();
               tempStrArr = tempLine.split(",");
               stringIntMapForLith.put(tempStrArr[0],Integer.parseInt(tempStrArr[1]));    
           }
           mlModel.StringIntMapForLith = stringIntMapForLith;
       }catch(Exception e){
           
           LoadConfigure.writeLog("读入含油性bp模型出错");
       }
       HashSet<String> varNameSet = new HashSet<>();
       for(Variable varT:mlModel.getVariables()){
           varNameSet.add(varT.name);
       }
       for(int i = 0;i<neededXs.length;i++){
           if(!varNameSet.contains(neededXs[i])){
               LoadConfigure.writeLog("缺少模型需要的变量");
               return null;
           }
       }
       return neededXs; 
    }
    
    public String[] oilClassifyCanGoOn(){
       String[] neededXs = null;
       BufferedReader bfr = null;
       File oilModelConfFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+OIL_MODEL_NAME_BP+"Aux");
       File oilModelFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+OIL_MODEL_NAME_BP);
       if(!oilModelConfFile.exists()||!oilModelFile.exists()){
           LoadConfigure.writeLog("classifyingBPFuntction 218:含油性模型或其配置文件不存在");
           return null;
       }else{
           this.oilModelFile = oilModelFile;
       }
       //
       try{
           bfr = new BufferedReader(new InputStreamReader(new FileInputStream(oilModelConfFile),"UTF-8"));
           //读取模型需要的变量
           String tempLine = bfr.readLine();
           String[] tempStrArr = tempLine.split(",");
           int xcount = Integer.parseInt(tempStrArr[1]);
           neededXs = new String[xcount];
           for(int i = 0;i<xcount;i++){
               tempLine = bfr.readLine();
               tempStrArr = tempLine.split(",");
               neededXs[i] = tempStrArr[0];
           }
           //读取文本整型对应关系
           tempLine = bfr.readLine();
           tempStrArr = tempLine.split(",");
           stringIntMapForOil = new HashMap<>();
           int stringIntCount = Integer.parseInt(tempStrArr[1]);
           for(int i = 0;i<stringIntCount;i++){
               tempLine = bfr.readLine();
               tempStrArr = tempLine.split(",");
               stringIntMapForOil.put(tempStrArr[0],Integer.parseInt(tempStrArr[1]));    
           }
           mlModel.StringIntMapForOil = stringIntMapForOil;
       }catch(Exception e){
           LoadConfigure.writeLog("读入含油性bp模型出错");
       }
       HashSet<String> varNameSet = new HashSet<>();
       for(Variable varT:mlModel.getVariables()){
           varNameSet.add(varT.name);
       }
       for(int i = 0;i<neededXs.length;i++){
           if(!varNameSet.contains(neededXs[i])){
               LoadConfigure.writeLog("缺少模型需要的变量");
               return null;
           }
       }
       return neededXs;     
    }
    
    
    public void startOilModelTrain() {
        printOilDataMessage();
        DataSet dataSet = formOilLearningDataSet();
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(dataSet.getInputSize(), hiddenNeuronCount, hiddenNeuronCount - 2, dataSet.getOutputSize());
        MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet.getLearningRule();
        learningRule.setMaxIterations(maxIteration);
        learningRule.addListener(new LearningEventListener() {
            @Override
            public void handleLearningEvent(LearningEvent event) {
                BackPropagation bp = (BackPropagation) event.getSource();
                String str = bp.getCurrentIteration() + ". iteration | Total Error: " + bp.getTotalNetworkError();
                progressPrint(str);
                println(str);
            }
        });
        learningRule.setLearningRate(learningRate);
        learningRule.setMaxError(maxError);
        println("开始训练：");
        neuralNet.learn(dataSet);
        println("完成训练");
        //String filePath = FunTools.getModelPath() + File.separator + FunTools.getModelFileName("Classfy_BP", mlModel);
        String filePath = LoadConfigure.MODEL_SAVED_PATH+File.separator+OIL_MODEL_NAME_BP;
        neuralNet.save(filePath);
        FunTools.saveModelAuxFile(filePath,normalizationForOil,this);

        int[] py = computeY(neuralNet, dataSet);
        int correctCount = FunTools.computeEquivalenceCount(desiredYOil, py);
        StringBuilder sb = new StringBuilder();
        sb.append("总数： ").append(py.length);
        sb.append(", 正确个数: ").append(correctCount);
        sb.append(", 正确率： ").append(String.format("%.2f", correctCount * 100.0 / py.length)).append("%\n");
        printHighlight(sb.toString());
        println("Save Model: " + filePath);
        printLine();
    }
    
    public void startLithModelTrain() {
        printLithDataMessage();
        DataSet dataSet = formLithLearningDataSet();
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(dataSet.getInputSize(), hiddenNeuronCount, hiddenNeuronCount - 2, dataSet.getOutputSize());
        MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet.getLearningRule();
        learningRule.setMaxIterations(maxIteration);
        learningRule.addListener(new LearningEventListener() {
            @Override
            public void handleLearningEvent(LearningEvent event) {
                BackPropagation bp = (BackPropagation) event.getSource();
                String str = bp.getCurrentIteration() + ". iteration | Total Error: " + bp.getTotalNetworkError();
                progressPrint(str);
                println(str);
            }
        });
        learningRule.setLearningRate(learningRate);
        learningRule.setMaxError(maxError);
        println("开始训练：");
        neuralNet.learn(dataSet);
        println("完成训练");
        //String filePath = FunTools.getModelPath() + File.separator + FunTools.getModelFileName("Classfy_BP", mlModel);
        String filePath = LoadConfigure.MODEL_SAVED_PATH+File.separator+LITH_MODEL_NAME_BP;
        neuralNet.save(filePath);
        FunTools.saveModelAuxFile(filePath,normalizationForLith,this);

        int[] py = computeY(neuralNet, dataSet);
        int correctCount = FunTools.computeEquivalenceCount(desiredYLith, py);
        StringBuilder sb = new StringBuilder();
        sb.append("总数： ").append(py.length);
        sb.append(", 正确个数: ").append(correctCount);
        sb.append(", 正确率： ").append(String.format("%.2f", correctCount * 100.0 / py.length)).append("%\n");
        printHighlight(sb.toString());
        println("Save Model: " + filePath);
        printLine();
    }
    
    private void printOilDataMessage() {
        printHighlight("含油性模型训练：Variables:\n");
        String[] xVarNames = mlModelHelper.getOilXVariableNames();
        String yVarName = mlModelHelper.getOilYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
        
    }
    
    private void printLithDataMessage() {
        printHighlight("含油性模型训练：Variables:\n");
        String[] xVarNames = mlModelHelper.getLithXVariableNames();
        String yVarName = mlModelHelper.getLithYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
        
    }

    private int[] computeY(MultiLayerPerceptron neuralNet, DataSet dataSet) {
        int[] y = new int[dataSet.size()];
        int index = 0;
        for (DataSetRow testSetRow : dataSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            int maxIndex = FunTools.compete(neuralNet.getOutput());
            y[index++] = maxIndex;
        }
        return y;
    }

    private DataSet formOilLearningDataSet() {
        
        int xCount = dataHelper.getOilXVariableCount();
        int yCount = mlModel.StringIntMapForOil.size();
        this.XcountOil = xCount;
        this.YcountOil = yCount;
        int rowCount = dataHelper.getRealRowCount();
        
        normalizationForOil = new Normalization(xCount, -1);
        normalizationForOil.StringIntMap = mlModel.StringIntMapForOil;
        
        //获取desiredYOil
        if(dataHelper.oilYVariableColumnIndex>=0){
            desiredYOil = new int[rowCount];
            for(int i = 0;i<desiredYOil.length;i++){
                String OilLabel = dataHelper.getRawStringData(dataHelper.oilYVariableColumnIndex, dataHelper.realRowIndices[i]);
                desiredYOil[i] = mlModel.StringIntMapForOil.get(OilLabel);
            } 
        }
        
        
        
        DataSet dataSet = new DataSet(xCount, yCount);

        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readOilXData(col, buffer);
            String variableName = dataHelper.getOilXVariableName(col);
            normalizationForOil.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        for (int row = 0; row < desiredYOil.length; row++) {
            int val = desiredYOil[row];
            dataSet.get(row).getDesiredOutput()[val] = 1;
        }
        return dataSet;
    }
    //从dataHelper中得到岩性模型训练数据，
    private DataSet formLithLearningDataSet(){
        //获得输入节点，输出节点的个数
        int xCount = dataHelper.getLithXVariableCount();
        int yCount = mlModel.StringIntMapForLith.size();
        this.XcountLith = xCount;
        this.YcountLith = yCount;
        int rowCount = dataHelper.getRealRowCount();
        //记录输入变量的名字与最大最小值，用来保存模型参数
        normalizationForLith = new Normalization(xCount, -1);     
        normalizationForLith.StringIntMap = mlModel.StringIntMapForLith;
        
        if(mlModel.StringIntMapForLith==null||mlModel.StringIntMapForLith.size()==0){
            LoadConfigure.writeLog("ClassifyingBPFunction 429：没有岩性文本与整型对应关系");
        }
        
        //获取desiredYOil
        if(dataHelper.lithYVariableColumnIndex>=0){
            desiredYLith = new int[rowCount];
            for(int i = 0;i<desiredYLith.length;i++){
                String lithLabel = dataHelper.getRawStringData(dataHelper.lithYVariableColumnIndex, dataHelper.realRowIndices[i]);
                desiredYLith[i] = mlModel.StringIntMapForLith.get(lithLabel);
            } 
        }
        
        //初始化网络的输入数据集
        DataSet dataSet = new DataSet(xCount, yCount);
        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        //填充网络的输入数据集
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readLithXData(col, buffer);
            String variableName = dataHelper.getLithXVariableName(col);
            normalizationForLith.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), 
                    MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        for (int row = 0; row < desiredYLith.length; row++) {
            int val = desiredYLith[row];
            dataSet.get(row).getDesiredOutput()[val] = 1;
        }
        LoadConfigure.writeLog("形成岩性模型训练数据：\n"+"样本数："+dataSet.size()+",输入节点个数："
                +dataSet.getInputSize()+"输出节点个数："+dataSet.getOutputSize());
        return dataSet;
    }
    
    private DataSet formOilToClassifyDataSet(String[] needXsOil) {
        int xCount = needXsOil.length;
        int rowCount = dataHelper.getRealRowCount();
        normalizationForOil = new Normalization(xCount, -1);
        
        int yCount = mlModel.StringIntMapForOil.size();
        DataSet dataSet = new DataSet(xCount, yCount);
        
        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readOilXData(col, buffer);
            dataHelper.getUsedDoubleDataByName(needXsOil[col],buffer);
            normalizationForOil.normalizeXVar(needXsOil[col],col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        return dataSet;
    }
    private DataSet formLithToClassifyDataSet(String[] needXsLith) {
        //得到输入输出节点个数
        int xCount = needXsLith.length;
        int yCount = mlModel.StringIntMapForLith.size();
        
        int rowCount = dataHelper.getRealRowCount();
        normalizationForOil = new Normalization(xCount, -1);
        
        //初始化网络的输入数据集
        DataSet dataSet = new DataSet(xCount, yCount); 
        for (int i = 0; i < rowCount; i++) {
            double[] xData = new double[xCount];
            double[] yData = new double[yCount];
            DataSetRow dataSetRow = new DataSetRow(xData, yData);
            dataSet.add(dataSetRow);
        }
        //填充网络输入数据集
        double[] buffer = new double[rowCount];
        for (int col = 0; col < xCount; col++) {
            dataHelper.readLithXData(col, buffer);
            dataHelper.getUsedDoubleDataByName(needXsLith[col],buffer);
            normalizationForOil.normalizeXVar(needXsLith[col],col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                dataSet.get(row).getInput()[col] = buffer[row];
            }
        }
        return dataSet;
    }
}
