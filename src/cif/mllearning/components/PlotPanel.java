/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.components;

import cif.base.Global;
import cif.cifplotdata.CifPlotDataSourceFactory;
import cif.dataengine.io.LogCategory;
import cif.dataengine.io.Logging;
import cif.dataengine.io.TableFields;
import cif.dataengine.io.TableRecords;
import cif.mllearning.MLGlobal;
import cif.mllearning.base.DataHelper;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.TableHelper;
import cif.mllearning.base.Variable;
import cif.mllearning.configure.LoadConfigure;
import cif.mllearning.functions.ClassifyingBPFunction;
import cif.mllearning.functions.ClassifyingSVMFunction;
import com.flyfox.baseplot.data.PlotDataSource;
import com.flyfox.baseplot.data.PlotDataSourceManager;
import com.flyfox.logplot.canvas.LogPlotCanvas;
import com.flyfox.logplot.shape.curve.commoncurve.CommonCurve;
import com.flyfox.logplot.shape.curve.commoncurve.CommonCurveHead;
import com.flyfox.logplot.shape.curve.commoncurve.CommonCurveInfo;
import com.flyfox.logplot.shape.curve.typecurve.CategoryItem;
import com.flyfox.logplot.shape.curve.typecurve.TypeCurve;
import com.flyfox.logplot.shape.curve.typecurve.TypeCurveHead;
import com.flyfox.logplot.shape.curve.typecurve.TypeCurveInfo;
import com.flyfox.logplot.shape.track.Track;
import com.flyfox.logplot.util.ScaleType;
import com.flyfox.plotutil.util.AppConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.openide.windows.WindowManager;

/**
 *
 * @author Dell
 */
public class PlotPanel extends PagePanel {

    private LogPlotCanvas canvas;
    private MLDataModel mlModel;
    private DataHelper dataHelper;
    private DataPanelTableModel tableModel = new DataPanelTableModel();

    //聚类结果和标准结果列都能使用同一个tableFileds
    private TableFields tableFields;
    public int dataSourceIndex;
    private TypeCurve standardClusterResultTypeCurve;
    private TypeCurve clusterResultTypeCurve;

    private int stanadardCluterCount;
    private int standardClusterResultColIndex = -1;
    private double accuracy;

    public String[] oilResNameAllMethods = new String[]{ClassifyingBPFunction.OIL_CLASSIFY_TABLENAME_BP,ClassifyingSVMFunction.OIL_CLASSIFY_BY_SVM};
    

    /**
     * Creates new form PlotPanel
     */
    public PlotPanel() {
        initComponents();
        this.setLayout(new BorderLayout());
        this.add(new PlotTopPanel(this), BorderLayout.NORTH);
        /*JButton testButton = new JButton("删除table测试");
        //this.add(testButton,BorderLayout.NORTH);
        testButton.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                Frame parent = WindowManager.getDefault().getMainWindow();
                LogCategory logCategory = mlModel.inputDataPath.getCategory();
                 if (logCategory.getLogCommonTable("Cluster") != null) {
                     logCategory.deleteLogging("Cluster");
                     JOptionPane.showMessageDialog(parent, "尝试删除Cluster");
                 }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                 //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //To change body of generated methods, choose Tools | Templates.
            }
            
        });*/
    }

    public void setMLModel(MLDataModel mlModel) {
        this.mlModel = mlModel;
        if(mlModel.dataFrom == MLDataModel.FROM_TEXT){
            JOptionPane.showMessageDialog(this, "暂不支持从文本绘图");
            return;
        }
        if (mlModel.getVariables() == null) {
            JOptionPane.showMessageDialog(this, "请选择数据！");
            return;
        }
        this.dataHelper = new DataHelper(mlModel);
        this.tableModel.setMLModel(mlModel);
        
        
        
        initCanvas();
    }

    private void initCanvas() {

        if (canvas == null) {
            canvas = new LogPlotCanvas(new CifPlotDataSourceFactory(), true, true, false);
            this.add(canvas, BorderLayout.CENTER);

        }
        
        canvas.clearAll();
        canvas.setDepth(mlModel.curveStdep, mlModel.curveEndep);
        // 设置plot数据源
        PlotDataSourceManager plotDataSourceManager = canvas.getWell().getDataSourceManager();
        PlotDataSource plotDataSource = plotDataSourceManager.addDataSource(mlModel.inputDataPath.toString(), mlModel.inputDataPath.toString());
        dataSourceIndex = plotDataSourceManager.getDataSources().indexOf(plotDataSource);
        
        
        Variable[] variables = mlModel.getVariables();
        Variable yVariable = null;

        //深度道
        canvas.addTrack(AppConstants.TRACK_TYPE_DEPTH, true);

        standardClusterResultColIndex = 0;
        //添加曲线 这里面有一个数据偏移的问题，曲线的数据是从2开始的，所以有一个2的偏移量
        /*for (int i = 0; i < variables.length; i++) {
            if (tableModel.getColumnName(i + 2).equals(MLGlobal.STANDARD_CLUSTER_RESULT_CURVE_NAME)) {
                standardClusterResultColIndex = i + 2;

                //找到聚类标准列里面的最大值  这个里面
                for (int j = 0; j < tableModel.getRowCount(); j++) {
                    if (stanadardCluterCount < Double.valueOf((String) tableModel.getValueAt(j, i + 2))) {
                        stanadardCluterCount = Double.valueOf((String) tableModel.getValueAt(j, i + 2)).intValue();
                    }
                }

                continue;
            }
            if (variables[i].flag == MLDataModel.X_VARIABLE) {
                addTrackAndCurve(variables[i], dataIndex);
            } else if (variables[i].flag == MLDataModel.Y_VARIABLE) {
                yVariable = variables[i];
            }
        }
        
        
        //找到聚类标准列里面的最大值  这个里面
        if (mlModel.clusterResult != null) {
            for (int j = 0; j < tableModel.getRowCount(); j++) {
                if (stanadardCluterCount < Double.valueOf((String) tableModel.getValueAt(j, standardClusterResultColIndex))) {
                    stanadardCluterCount = Double.valueOf((String) tableModel.getValueAt(j, standardClusterResultColIndex)).intValue();
                }
            }
        }*/
        
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].flag != MLDataModel.UNSEL_VARIABLE) {
                addTrackAndCurve(variables[i], dataSourceIndex);
            } 
        }

        //这里添加曲线的时候 可以通过一个曲线模板来做这个事儿，先加入一个模板 然后再添加后面两个类别曲线
        //这样能够考虑到美观的问题
        //addTrackAndCurve(yVariable, dataIndex);

        //现在出问题的地方就在于不能创建label表
        //addTrackAndTable(dataSourceIndex);
        
        canvas.update(true, true);

    }

    //添加道以及对应的常规曲线
    private void addTrackAndCurve(Variable variable, int dataSourceIndex) {

        if (variable == null) {
            return;
        }

        Track track = canvas.addTrack(AppConstants.TRACK_TYPE_LINEAR, true);

        String curveName = variable.name;

        LogCategory logCategory = mlModel.inputDataPath.getCategory();
        Logging logging = logCategory.getLogCurve(curveName);
        String unit = logging.getLoggingProperties().getDepthUnit();

        CommonCurveInfo commonCurveInfo = new CommonCurveInfo();
        CommonCurve commonCurve = track.addCurve(commonCurveInfo);
        CommonCurveHead commonCurveHead = commonCurve.getCurveHead();
        commonCurveHead.setDataPathIndex(dataSourceIndex);
        commonCurveHead.setName(curveName);
        commonCurveHead.setLabel(curveName);
        commonCurveHead.setScaleType(ScaleType.Custom);
        commonCurveHead.setUnit(unit);

    }

    /**
     * 添加道、创建表、往道里面添加类别曲线
     */
    private void addTrackAndTable(int dataSourceIndex) {
        LogCategory logCategory = mlModel.inputDataPath.getCategory();

        //创建 标准聚类结果表及添加其typeCurve
        //判断曲线中是否有standardClusterResult        
        /*if (standardClusterResultColIndex > 0) {
            createClusterResultTable(logCategory, standardClusterResultColIndex, MLGlobal.STANDARD_CLUSTER_RESULT_TABLE_NAME);
            standardClusterResultTypeCurve = addTrackAndTypeCurve(MLGlobal.STANDARD_CLUSTER_RESULT_CURVE_NAME,
                    MLGlobal.STANDARD_CLUSTER_RESULT_TABLE_NAME, dataIndex);

        }*/

        //创建 聚类结果表及添加其typeCurve
        /*if (mlModel.clusterResult != null) {
            createClusterResultTable(logCategory, standardClusterResultColIndex, MLGlobal.CLUSTER_RESULT_TABLE_NAME);
            clusterResultTypeCurve = addTrackAndTypeCurve(MLGlobal.CLUSTER_RESULT_TABLE_NAME,
                    MLGlobal.CLUSTER_RESULT_TABLE_NAME, dataSourceIndex);

        }*/
    }

    //添加道以及对应的类别曲线
    private TypeCurve addTrackAndTypeCurve(String curveName, String tableName, int dataSourceIndex) {
        Track track = canvas.addTrack(AppConstants.TRACK_TYPE_BKANK, true);
        track.setTrackWidth(20);

        TypeCurveInfo typeCurveInfo = new TypeCurveInfo();
        TypeCurve typeCurve = track.addCurve(typeCurveInfo);
        TypeCurveHead typeCurveHead = typeCurve.getCurveHead();
        typeCurveHead.setDataPathIndex(dataSourceIndex);
        typeCurveHead.setName(tableName);
        typeCurveHead.setLabel(curveName);

        typeCurve.setStartDepthName(tableFields.getName(0));
        typeCurve.setEndDepthName(tableFields.getName(1));
        typeCurve.setCategoryName(tableFields.getName(2));

        typeCurveHead.setScaleType(ScaleType.Custom);

        //判断当前传入曲线名是什么，然后依据这个做出后续的判断
        int clusterCount = mlModel.clusterCountOil;
        if (curveName == MLGlobal.STANDARD_CLUSTER_RESULT_CURVE_NAME) {
            clusterCount = stanadardCluterCount + 1;
        }

        typeCurve.setCategoryItems(createCategoryItems(clusterCount));
        typeCurve.setDrawLabel(false);

        return typeCurve;
    }
    
//根据表名绘制
public int loadAndPaintOilClassifyRes(String tableName){
        TableHelper tableHelper = new TableHelper(mlModel);
        int retSta = tableHelper.fillOilClassifyResultFromTable(tableName);
        if(mlModel.classifyResultOil == null||retSta<0){
            LoadConfigure.writeLog("PlotPanel:286,没有含油性分类结果，无法绘制");
            return 0;
        }
        Track track = canvas.addTrack(AppConstants.TRACK_TYPE_BKANK, true);
        track.setTrackWidth(20);
        TypeCurveInfo typeCurveInfo = new TypeCurveInfo();
        TypeCurve typeCurve = track.addCurve(typeCurveInfo);
        TypeCurveHead typeCurveHead = typeCurve.getCurveHead();
        typeCurveHead.setDataPathIndex(dataSourceIndex);
        typeCurveHead.setName(tableName);
        typeCurveHead.setLabel(tableName.substring(tableName.indexOf("By")));
        typeCurve.setStartDepthName(TableHelper.OIL_FEILDSNAME_CLASSIY[0]);
        typeCurve.setEndDepthName(TableHelper.OIL_FEILDSNAME_CLASSIY[1]);
        typeCurve.setCategoryName(TableHelper.OIL_FEILDSNAME_CLASSIY[2]);
        typeCurveHead.setScaleType(ScaleType.Custom);
        
        HashSet<String> oilMap = new HashSet<String>();
        for(int i = 0;i<mlModel.classifyResultOil.length;i++){
            oilMap.add(mlModel.classifyResultOil[i]);
        }
        ArrayList<CategoryItem> categoryList = new ArrayList<>();
        Iterator<String> iterator = oilMap.iterator();
        while(iterator.hasNext()){
            String nameOfLayer = iterator.next();
            CategoryItem item = new CategoryItem();
            item.setPropertyValue(nameOfLayer);
            item.setBackgroundColor(LoadConfigure.nameColorMap.get(nameOfLayer));
            item.setWidth(100);
            categoryList.add(item);
        }
        
        typeCurve.setCategoryItems(categoryList);
        typeCurve.setDrawLabel(false);
        canvas.update(true, true);
        return 1;
    }

public int loadAndPaintOilClusterRes(String tableName,int dataSourceIndex){
        TableHelper tableHelper = new TableHelper(mlModel);
        int clusterCount = tableHelper.fillClusterResultOilFromTable(tableName);
        if(mlModel.clusterResultOil == null){
            LoadConfigure.writeLog("PlotPanel 327:没有含油性聚类结果，无法绘制");
            return -1;
        }
        Track track = canvas.addTrack(AppConstants.TRACK_TYPE_BKANK, true);
        track.setTrackWidth(20);
        TypeCurveInfo typeCurveInfo = new TypeCurveInfo();
        TypeCurve typeCurve = track.addCurve(typeCurveInfo);
        TypeCurveHead typeCurveHead = typeCurve.getCurveHead();
        typeCurveHead.setDataPathIndex(dataSourceIndex);
        typeCurveHead.setName(tableName);
        typeCurveHead.setLabel(tableName);
        typeCurve.setStartDepthName(TableHelper.OIL_FEILDSNAME_CLUSTER[0]);
        typeCurve.setEndDepthName(TableHelper.OIL_FEILDSNAME_CLUSTER[1]);
        typeCurve.setCategoryName(TableHelper.OIL_FEILDSNAME_CLUSTER[2]);
        typeCurveHead.setScaleType(ScaleType.Custom);
        
        ArrayList<CategoryItem> categoryList = new ArrayList<>();
        int gap = 255/clusterCount;
        for (int i = 0; i < clusterCount; i++) {
            CategoryItem item = new CategoryItem();
            item.setPropertyValue(i + "");
            Color temp = new Color(0,(i+1)*gap,(i+3)*gap%255);
            item.setBackgroundColor(temp);
            item.setWidth(100);
            categoryList.add(item);
        }
        
        typeCurve.setCategoryItems(categoryList);
        typeCurve.setDrawLabel(false);
        canvas.update(true, true);
        return 1;
    }
    /**
     * 为typeCurve创建categoryItems
     *
     * 待办，这个地方待办了。 这个地方属于门面工程，需要我去做一下。 重点待办
     */
    private ArrayList<CategoryItem> createCategoryItems(int clusterCount) {

        //颜色渐变的一个问题，如何设置颜色？
        //渐变色的东西，我还不确定这个应该怎么写，后面留着
        /*ArrayList<CategoryItem> categoryList = new ArrayList<>();
        int gap = 255 / clusterCount;
        
        for (int i = 0; i < clusterCount; i++) {
            CategoryItem item = new CategoryItem();
            item.setPropertyValue(i + "");
            
            if(mlModel!=null&&mlModel.clusterLayerMap!=null&&mlModel.clusterLayerMap.containsKey(i)){
                int j = mlModel.clusterLayerMap.get(i);
                Color temp = new Color(LoadConfigure.colorLayers.get(j).red,LoadConfigure.colorLayers.get(j).green,LoadConfigure.colorLayers.get(j).blue);
                item.setBackgroundColor(temp);
            }else{
                item.setBackgroundColor(new Color((i * gap) % 255, (i * gap * 2) % 255, (i * gap) % 255));
            }
            
            
            item.setWidth(100);
            categoryList.add(item);
        }
        CategoryItem item = new CategoryItem();
        item.setPropertyValue(100 + "");
        item.setWidth(100);
        item.setBackgroundColor(new Color(255,255,255));
        categoryList.add(item);
        return categoryList;*/
        return new ArrayList<CategoryItem>();
    }

    /**
     * 创建聚类结果表 这个里面的tableFileds可以在刚开始的时候就创建好了 然后后面填充数据的时候直接用就好了
     * 后续的话，就是需要每次给它填充数据
     *
     * 思路整理：我后续需要准备的东西包括--标准数据（标准数据后面有一个聚类结果列，然后我需要给它一个命名，手动，然后就是把它当做一条
     * 普通曲线对待就好了 1. 准备标准井数据 2. 标准井数据里面的那条曲线命名的问题StandardClusterResult 3.
     * 如果添加进去的最后一条曲线的名称为StandardCLusterResult，那么就用分类曲线的方式绘制，否则就普通的测井曲线 4.
     * 准确率的问题，通过获取最后两条曲线中分层所对应的颜色属性那些东西
     */
    /**
     * 创建聚类结果表 step1：创建tableFileds step2：填充数据 step3：调用createTable接口，创建结果表
     */
    private void createClusterResultTable(LogCategory logCategory, int dataColIndex, String tableName) {

//        if (mlModel.learningMode != MLGlobal.CLUSTERING_MODE || mlModel.clusterResult == null) {
//            return;
//        }
        //创建tableFileds
        tableFields = createTableFileds();

        //填充tableRecords  这个地方没问题
        TableRecords tableRecords = stuffTableRecords(dataColIndex, tableFields);

        if (logCategory.getLogCommonTable(tableName) != null) {
            logCategory.deleteLogging(tableName);
        }
        logCategory.createTable(tableName, "", Global.LOGGING_COMMON_TABLE, tableRecords);

    }

    /**
     * 创建表属性，聚类结果表和标准数据表都能用同个表属性， 所以将其设置为全局变量，然后只用配置一次就ok
     */
    private TableFields createTableFileds() {

        if (tableFields != null) {
            return tableFields;
        }

        //创建tableFileds
        tableFields = new TableFields();
        tableFields.init(MLGlobal.CLUSTER_TABLE_COLS);
        for (int i = 0; i < MLGlobal.CLUSTER_TABLE_COLS; i++) {
            tableFields.setName(i, MLGlobal.CLUSTER_TABLE_FILED_NAME[i]);
            tableFields.setDataType(i, MLGlobal.CLUSTER_TABLE_FILED_TYPE[i]);
            tableFields.setUnit(i, MLGlobal.CLUSTER_TABLE_FILED_UNITS[i]);
        }
        return tableFields;
    }

    /**
     * 填充tableRecords
     *
     * @param dataColIndex: 用tableModel中第dataColIndex列的数据填充表格
     */
    private TableRecords stuffTableRecords(int dataColIndex, TableFields tableFields) {
        //clusterTable的长度为数据行的长度--1
        TableRecords tableRecords = new TableRecords();
        tableRecords.init(dataHelper.getRealRowCount() - 1, tableFields);

        //填充数据
        for (int i = 0; i < tableRecords.getRecordsNum(); i++) {
            //起始、结束深度列
            tableRecords.setRecordDoubleData(i, 0, Double.valueOf((String) tableModel.getValueAt(i, 2)));
            tableRecords.setRecordDoubleData(i, 1, Double.valueOf((String) tableModel.getValueAt(i + 1, 2)));
            //聚类结果列
            int value = Double.valueOf(String.valueOf(tableModel.getValueAt(i, dataColIndex))).intValue();
            tableRecords.setRecordStringData(i, 2, String.valueOf(value));
        }

        return tableRecords;
    }

    /**
     * 映射mapping算法，一个手动，一个自动
     *
     * 手动：手动选择匹配颜色，然后直接计算。
     * 算法;n^2遍历那两个arrayList，找到颜色想同的value，并将其保存在一个map里面。key-standardValue,value-clusterValue
     * 然后，遍历数据，如果标准数据key里面的value与当前行clusterValue一样则记录匹配行。然后直接mappingCount/totalCount
     *
     *
     * 自动：颜色无关了，通过某种方式计算得到一个最大的准确率。 算法：待思考，需要动一下脑子。
     *
     * mapping匹配算法，label中第i种分类所对应结果中最多的分类，则确定其对应关系
     *
     */
    private double calAccuracy() {
//       standardClusterReusltTrack.get

        /*if (standardClusterResultColIndex < 0 || mlModel.clusterResult == null) {
            return -1;
        }

        int[][] counts = new int[stanadardCluterCount + 1][mlModel.clusterCount];

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int row = Double.valueOf(String.valueOf(tableModel.getValueAt(i, standardClusterResultColIndex))).intValue();
            int col = Double.valueOf(String.valueOf(tableModel.getValueAt(i, tableModel.getColumnCount() - 1))).intValue();
            if(row>=0&&col>=0){
                ++counts[row][col];
            }
            
        }*/

        //
        /*int mapCount = 0;
        mapping = new int[standardClusterResultColIndex];
        for (int i = 0; i < stanadardCluterCount; i++) {
            int maxVal = maxValueAt(counts[i]);
            mapping[i] = maxVal;
            mapCount += counts[i][maxVal];
        }

        return (double) mapCount / (double) tableModel.getColumnCount();*/

//        
//       ArrayList<CategoryItem> standardList = standardClusterResultTypeCurve.getCategories();
//        ArrayList<CategoryItem> list = clusterResultTypeCurve.getCategories();
//        Map<String, String> map = new HashMap();
//
//        for (int i = 0; i < standardList.size(); i++) {
//            for (int j = 0; j < list.size(); j++) {
//                if (standardList.get(i).getBackgroundColor().getRGB() == list.get(j).getBackgroundColor().getRGB()) {
//                    map.put(standardList.get(i).getPropertyValue(), list.get(j).getPropertyValue());
//                }
//            }
//        }
//        //记录满足条件的行数 satisifiedRowCount
//        int mappingCount = 0;
//        for (int i = 0; i < tableModel.getColumnCount(); i++) {
//            String sValue = (String) tableModel.getValueAt(standardClusterResultColIndex, i);
//            if (map.containsKey(sValue) && map.get(sValue).equals((String) tableModel.getValueAt(tableModel.getColumnCount() - 1, i))) {
//                ++mappingCount;
//            }
//        }
//
//        return (double) mappingCount / (double) tableModel.getColumnCount();
          return 0;
    }

    /**
     * 获取一维数组中最大值所对应的index
     */
    private int maxValueAt(int[] nums) {
        int max = 0;
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if (max > nums[i]) {
                max = nums[i];
                index = i;
            }
        }
        return index;
    }

    public void getAccuracy() {
        double result = calAccuracy();
        String msg = "";
        if (result < 0) {
            msg = "无法计算准确率！请检查是否执行聚类操作或检查数据源中是否存在标准聚类结果曲线！！！";
        } else {
//            msg="准确率： "+result;
            msg = String.format("准确率： %4f", result);
        }
        JOptionPane.showMessageDialog(this, msg);

//        测试是否能够成功获取到修改后的typeCurve的categoryItems，结果：成功   
//        clusterResultTypeCurve.getCategories().get(0).getBackgroundColor();
//        standardClusterResultTypeCurve.getCategories().get(0).getBackgroundColor();
//        JOptionPane.showMessageDialog(this, clusterResultTypeCurve.getCategories().get(0).getBackgroundColor());
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 539, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 453, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public String getTitle() {
        return "绘图";
    }

    @Override
    public String getIconName() {
        return "plot16.png";
    }

    @Override
    public String getID() {
        return "plot";
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
