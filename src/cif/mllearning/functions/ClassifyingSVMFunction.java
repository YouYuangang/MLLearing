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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 *
 * @author wangcaizhi
 * @author Y.G. You
 * @create 2019.3.30
 * 
 */
public class ClassifyingSVMFunction extends Function {
    //SVM参数
    private final svm_parameter param = new svm_parameter();
    private final static String[] SVR_TYPES = new String[]{"C_SVC", "NU-SVC"};
    private final static String[] KERNEL_TYPES = new String[]{"线性核", "多项式核", "RBF函数", "Sigmoid核"};
    
    //生成的模型保存的名字
    public final static String OILMODEL_FILENAME_SVM = "oil_svm.model";
    public final static String LITHMODEL_FILENAME_SVM = "lith_svm.model";
    
    //结果保存到通用表用的名字
    public final static String OIL_CLASSIFY_BY_SVM =  "oilClassifyBySVM";
    public final static String LITH_CLASSIFY_BY_SVM = "lithClassifyBySVM";
    
    //保存用到的变量名字，并归一化
    private Normalization normalizationOil;
    private Normalization normalizationLith;
    
    //样本的分类标签
    private String[] desiredY;
    
    
    //是否进行含油性模型训练、岩性模型训练
    public boolean trainOilFlag = false;
    public boolean trainLithFlag = false;
    
    //模型保存的文件
    public File oilModelFile = null;
    public File lithModelFile = null;
    
    public TableHelper tableHelper = null;
    
    //标签String与int对应关系
    public HashMap<String,Integer> stringIntMapForOil = null;
    public HashMap<String,Integer> stringIntMapForLith = null;
    
    
    public ClassifyingSVMFunction() {
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0.1; // 1/num_features            // change it 
        param.coef0 = 0;
        param.nu = 0.1;  /// change it,by wangcaizhi 
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
    }
    
    @Override
    public void setMLModel(MLDataModel mlModel) {
        this.mlModel = mlModel;
        mlModelHelper = new MLDataModelHelper(mlModel);
        dataHelper = new DataHelper(mlModel);
        tableHelper = new TableHelper(mlModel);
    }

    @Override
    public boolean setParameters(Frame parentWindow) {
        SVMDialog dialog = new SVMDialog(parentWindow, true);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setSvms(SVR_TYPES);
        dialog.setKernels(KERNEL_TYPES);
        Object[] parameters = mlModel.getParameters(this.getClass().getSimpleName());
        dialog.setSvmSelectedIndex(parameters == null ? 0 : (int) parameters[0]);
        dialog.setKernelSelectedIndex(parameters == null ? 2 : (int) parameters[1]);
        dialog.setDegree(parameters == null ? param.degree : (int) parameters[2]);
        dialog.setGamma(parameters == null ? param.gamma : (double) parameters[3]);
        dialog.setNu(parameters == null ? param.nu : (double) parameters[4]);
        dialog.setVisible(true);
        if (dialog.getReturnStatus() == RegressDialog.RET_OK) {
            int svmIndex = dialog.getSvmSelectedIndex();
            switch (svmIndex) {
                case 0:
                    param.svm_type = svm_parameter.C_SVC;
                    break;
                case 1:
                    param.svm_type = svm_parameter.NU_SVC;
                    break;
            }
            int kernelIndex = dialog.getKernelSelectedIndex();
            switch (kernelIndex) {
                case 0:
                    param.kernel_type = svm_parameter.LINEAR;
                    break;
                case 1:
                    param.kernel_type = svm_parameter.POLY;
                    break;
                case 2:
                    param.kernel_type = svm_parameter.RBF;
                    break;
                case 3:
                    param.kernel_type = svm_parameter.SIGMOID;
                    break;
            }
            param.degree = dialog.getDegree();
            param.gamma = dialog.getGamma();
            param.nu = dialog.getNu();
            Object[] paras = new Object[]{svmIndex, kernelIndex, param.degree, param.gamma, param.nu};
            mlModel.setParameters(this.getClass().getSimpleName(), paras);
            return true;
        }
        return false;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        if (flag == Function.GENERATE_MODEL) {
            if (dataHelper.oilYVariableColumnIndex >= 0&&dataHelper.getOilXVariableCount()>=2) {
                trainOilFlag = true;
            }
            if (dataHelper.lithYVariableColumnIndex >= 0&&dataHelper.getLithXVariableCount()>=2) {
                trainLithFlag = true;
            }
            if (trainOilFlag) {
                trainOilModel();
            }
            if (trainLithFlag) {
                trainLithModel();
            }
        }else{
            String[] needOilX = oilClassifyCanGoOn();
            String[] needLithX = lithClassifyCanGoOn();
            
            if(needOilX!=null&&needOilX.length>0){
                
                doOilClassify(needOilX);
            }
            if(needLithX!=null&&needLithX.length>0){
                //doLithClassify(needLithX);
            }
        }
        
        return 0;
        
    }

    private svm_problem buildSVMProblemForOil() {
        svm_problem problem = new svm_problem();
        int rowCount = dataHelper.getRealRowCount();
        int xVarCount = dataHelper.getOilXVariableCount();
        normalizationOil = new Normalization(xVarCount, -1);
        normalizationOil.StringIntMap = dataHelper.stringIntMapForOil;
        problem.l = rowCount;
        problem.x = new svm_node[rowCount][xVarCount];
        problem.y = new double[rowCount];
        double[] buffer = new double[rowCount];
        printHighlight("Data Statistics:\n");
        for (int col = 0; col < xVarCount; col++) {
            dataHelper.readOilXData(col, buffer);
            String variableName = dataHelper.getOilXVariableName(col);
            normalizationOil.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                problem.x[row][col] = new svm_node();
                problem.x[row][col].index = col;
                problem.x[row][col].value = buffer[row];
            }
        }
        
        desiredY = new String[rowCount];
        for(int i = 0;i<desiredY.length;i++){
            desiredY[i] = dataHelper.readRealOilYString(i);
        }
        for (int row = 0; row < rowCount; row++) {
            problem.y[row] = dataHelper.stringIntMapForOil.get(desiredY[row]);
        }
        return problem;
    }
    private void trainOilModel(){
        printOilDataMessage();
        svm_problem problem = buildSVMProblemForOil();
        String error_msg = svm.svm_check_parameter(problem, param);
        if (error_msg != null) {
            printError("ERROR: " + error_msg + "\n");
        }
        svm_model model = svm.svm_train(problem, param);
        String modelFile = LoadConfigure.MODEL_SAVED_PATH + File.separator + OILMODEL_FILENAME_SVM;
        try {
            svm.svm_save_model(modelFile, model);
        } catch (Exception e) {
            LoadConfigure.writeLog("ClassifyingSVMFunction.trainOilModel.176:保存模型出错！");
        }
        
        FunTools.saveModelAuxFile(modelFile,normalizationOil,this);
        double[] py = new double[problem.y.length];
        for (int i = 0; i < problem.x.length; i++) {
            py[i] = svm.svm_predict(model, problem.x[i]);
        }
        int correctCount = FunTools.computeEquivalenceCount(problem.y, py);
        StringBuilder sb = new StringBuilder();
        sb.append("总数： ").append(py.length);
        sb.append(", 正确个数: ").append(correctCount);
        sb.append(", 正确率： ").append(String.format("%.2f", correctCount * 100.0 / py.length)).append("%\n");
        printHighlight(sb.toString());
        println("Save Model: " + modelFile);
        printLine();
        
    }
    private void printOilDataMessage() {
        printHighlight("SVM开始含油性模型训练：Variables:\n");
        String[] xVarNames = mlModelHelper.getOilXVariableNames();
        String yVarName = mlModelHelper.getOilYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
    }
    
    private void trainLithModel(){
        printLithDataMessage();
        svm_problem problem = buildSVMProblemForLith();
        String error_msg = svm.svm_check_parameter(problem, param);
        if (error_msg != null) {
            printError("ERROR: " + error_msg + "\n");
        }
        svm_model model = svm.svm_train(problem, param);
        String modelFile = LoadConfigure.MODEL_SAVED_PATH + File.separator + LITHMODEL_FILENAME_SVM;
        try {
            svm.svm_save_model(modelFile, model);
        } catch (Exception e) {
            LoadConfigure.writeLog("ClassifyingSVMFunction.trainLithModel.176:保存模型出错！");
        }
        
        FunTools.saveModelAuxFile(modelFile,normalizationLith,this);
        double[] py = new double[problem.y.length];
        for (int i = 0; i < problem.x.length; i++) {
            py[i] = svm.svm_predict(model, problem.x[i]);
        }
        int correctCount = FunTools.computeEquivalenceCount(problem.y, py);
        StringBuilder sb = new StringBuilder();
        sb.append("总数： ").append(py.length);
        sb.append(", 正确个数: ").append(correctCount);
        sb.append(", 正确率： ").append(String.format("%.2f", correctCount * 100.0 / py.length)).append("%\n");
        printHighlight(sb.toString());
        println("Save Model: " + modelFile);
        printLine();
        
    }
    private void printLithDataMessage() {
        printHighlight("SVM开始岩性模型训练：Variables:\n");
        String[] xVarNames = mlModelHelper.getLithXVariableNames();
        String yVarName = mlModelHelper.getLithYVariableName();
        println("X: " + mlModelHelper.formString(xVarNames, "\t"));
        println("Y: " + yVarName);
        println("Number of Points: " + dataHelper.getRealRowCount());
    }
    public svm_problem buildSVMProblemForLith(){
        svm_problem problem = new svm_problem();
        int rowCount = dataHelper.getRealRowCount();
        int xVarCount = dataHelper.getLithXVariableCount();
        normalizationLith = new Normalization(xVarCount, -1);
        normalizationLith.StringIntMap = dataHelper.stringIntMapForLith;
        problem.l = rowCount;
        problem.x = new svm_node[rowCount][xVarCount];
        problem.y = new double[rowCount];
        double[] buffer = new double[rowCount];
        printHighlight("Data Statistics:\n");
        for (int col = 0; col < xVarCount; col++) {
            dataHelper.readLithXData(col, buffer);
            String variableName = dataHelper.getLithXVariableName(col);
            normalizationLith.normalizeXVar(variableName,col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                problem.x[row][col] = new svm_node();
                problem.x[row][col].index = col;
                problem.x[row][col].value = buffer[row];
            }
        }
        desiredY = new String[rowCount];
        for(int i = 0;i<desiredY.length;i++){
            desiredY[i] = dataHelper.readRealLithYString(i);
        }
        
        
        for (int row = 0; row < rowCount; row++) {
            problem.y[row] = dataHelper.stringIntMapForLith.get(desiredY[row]);
        }
        return problem;
    }
    /**
     * 判断是否存在含油性分类的模型，导入的数据是否存在模型需要的变量。如果两者都满足，则返回需要的变量数组，
     * 并加载文本与网络输出的对应关系；如果任一条件不满足，返回null表示无法继续操作。
     * @return string[]
     */
    public String[] oilClassifyCanGoOn(){
       String[] neededXs = null;
       BufferedReader bfr = null;
       File oilModelConfFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+OILMODEL_FILENAME_SVM+"Aux");
       File oilModelFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+OILMODEL_FILENAME_SVM);
       if(!oilModelConfFile.exists()||!oilModelFile.exists()){
           LoadConfigure.writeLog("ClassfigyingSVMFunction 288:含油性模型或其配置文件不存在");
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
           LoadConfigure.writeLog("svm.324:读入含油性svm模型出错");
       }
       HashSet<String> varNameSet = new HashSet<>();
       for(Variable varT:mlModel.getVariables()){
           varNameSet.add(varT.name);
       }
       for(int i = 0;i<neededXs.length;i++){
           if(!varNameSet.contains(neededXs[i])){
               LoadConfigure.writeLog("缺少SVM模型需要的变量");
               return null;
           }
       }
       return neededXs; 
    }
    /**
     * 判断是否存在岩性分类的模型，导入的数据是否存在模型需要的变量。如果两者都满足，则返回需要的变量数组，
     * 并加载文本与网络输出的对应关系；如果任一条件不满足，返回null表示无法继续操作。
     * @return string[]
     */
    public String[] lithClassifyCanGoOn(){
        String[] neededXs = null;
       BufferedReader bfr = null;
       File lithModelConfFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+LITHMODEL_FILENAME_SVM+"Aux");
       File lithModelFile = new File(LoadConfigure.MODEL_SAVED_PATH+File.separator+LITHMODEL_FILENAME_SVM);
       if(!lithModelConfFile.exists()||!lithModelFile.exists()){
           LoadConfigure.writeLog("ClassfigyingSVMFunction 344:岩性模型或其配置文件不存在");
           return null;
       }else{
           this.lithModelFile = lithModelFile;
       }
       //
       try{
           bfr = new BufferedReader(new InputStreamReader(new FileInputStream(lithModelConfFile),"UTF-8"));
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
           LoadConfigure.writeLog("svm.324:读入岩性svm模型出错");
       }
       HashSet<String> varNameSet = new HashSet<>();
       for(Variable varT:mlModel.getVariables()){
           varNameSet.add(varT.name);
       }
       for(int i = 0;i<neededXs.length;i++){
           if(!varNameSet.contains(neededXs[i])){
               LoadConfigure.writeLog("svm.391缺少SVM模型需要的变量");
               return null;
           }
       }
       return neededXs; 
    }
    private void doOilClassify(String[] needXs){
        print("SVM:开始含油性分类\n");
        svm_model model = null;
        svm_problem problem = new svm_problem();
        int rowCount = dataHelper.getRealRowCount();
        int xVarCount = needXs.length;
        Normalization tempNormalization = new Normalization(xVarCount, -1);
        problem.l = rowCount;
        problem.x = new svm_node[rowCount][xVarCount];
        problem.y = new double[rowCount];
        double[] buffer = new double[rowCount];
        
        for (int col = 0; col < xVarCount; col++) {
            dataHelper.getUsedDoubleDataByName(needXs[col], buffer); 
            tempNormalization.normalizeXVar(needXs[col], col, buffer, MathBase.minimum(buffer), MathBase.maximum(buffer));
            for (int row = 0; row < rowCount; row++) {
                problem.x[row][col] = new svm_node();
                problem.x[row][col].index = col;
                problem.x[row][col].value = buffer[row];
                
            }
            
        }
        try{
           model = svm.svm_load_model(oilModelFile.getAbsolutePath()); 
           
        }catch(Exception e){
            LoadConfigure.writeLog("***svm.420:加载模型出错");
        }
        if(model == null){
            LoadConfigure.writeLog("***svm.421:加载模型出错");
            return;
        }
        print("SVM:加载模型成功："+oilModelFile.getAbsolutePath()+"\n");
        double[] py = new double[problem.y.length];
        for (int i = 0; i < problem.x.length; i++) {
           
            py[i] = svm.svm_predict(model, problem.x[i]);
            
        }
        mlModel.classifyResultOil = new String[dataHelper.getRawDataCount()];
        HashMap<Integer,String>  intStringMap = new HashMap<>();
        for(HashMap.Entry<String,Integer> temp:stringIntMapForOil.entrySet()){
            intStringMap.put(temp.getValue(), temp.getKey());
        }
        
        int indexInPy = 0;
        for(int i = 0;i<mlModel.classifyResultOil.length;i++){
            if(mlModel.dataRowSelectedFlags[i]){
                mlModel.classifyResultOil[i] = intStringMap.get((int)py[indexInPy++]);
            }else{
                mlModel.classifyResultOil[i] = "无效数据";  
            }
        } 
        printHighlight("SVM:分类完成！\n");
        if(mlModel.dataFrom == MLDataModel.FROM_CURVE){
            tableHelper.saveToTableFromClassifyResOil(OIL_CLASSIFY_BY_SVM);
        }else{
            printHighlight("SVM:分类结果没有保存到本地，暂不支持文本数据和表格数据的结果保存");
            LoadConfigure.writeErrorLog("SVM.486结果没有保存到本地，暂不支持文本数据和表格数据的结果保存");
        }
        
        UpdatePanelFlag.DataPanelUpdateFlag = true;
    }
    
}
