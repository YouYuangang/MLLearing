/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning;

import cif.base.Global;
import cif.mllearning.components.CrossPlotPanel;
import cif.mllearning.components.DataPanel;
import cif.mllearning.components.HistogramPanel;
import cif.mllearning.components.MessagePanel;
import cif.mllearning.components.PagePanel;
import cif.mllearning.components.PlotPanel;
import cif.mllearning.functions.ClassifyingBPFunction;
import cif.mllearning.functions.ClassifyingSVMFunction;
import cif.mllearning.functions.DensityBasedClusterFunction;
import cif.mllearning.functions.EMClusterFunction;
import cif.mllearning.functions.FarthestFirstFunction;
import cif.mllearning.functions.FunctionProxy;
import cif.mllearning.functions.HierarchicalClustererFunction;
import cif.mllearning.functions.PredictingBPFunction;
import cif.mllearning.functions.PredictingSVMFunction;
import cif.mllearning.functions.RegressFunction;
import cif.mllearning.functions.SOMClusterFunction;
import cif.mllearning.functions.SimpleKMeansFunction;
import cif.mllearning.functions.WatershedClusterFunction;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.21
 */
public class MLGlobal {
    
    public final static int PREDICTING_MODE = 0;
    public final static int CLASSIFYING_MODE = 1;
    public final static int CLUSTERING_MODE = 2;

    public final static double INVALID_VALUE = -99999.0;

    public static String CLUSTER_RESULT_TABLE_NAME = "Cluster";
    public static int CLUSTER_TABLE_COLS = 4;
    public static String[] CLUSTER_TABLE_FILED_NAME = {"起始深度", "终止深度", "分层", "颜色"};
    public static byte[] CLUSTER_TABLE_FILED_TYPE = {Global.DATA_DEPTH, Global.DATA_DEPTH, Global.DATA_STRING, Global.DATA_CUSTOM_COLOR};
    public static String[] CLUSTER_TABLE_FILED_UNITS = {"meter", "meter", "", ""};

    public static String STANDARD_CLUSTER_RESULT_TABLE_NAME = "StandardResult--Label";
    public static String STANDARD_CLUSTER_RESULT_CURVE_NAME = "Label";
    

    public PagePanel dataPanel = new DataPanel();
    public PagePanel histogramPanel = new HistogramPanel();
    public PagePanel crossPlotPanel = new CrossPlotPanel();
    public PagePanel plotPanel = new PlotPanel();
    public PagePanel messagePanel = new MessagePanel();

    
    
    private final PagePanel[] predictingPanels = new PagePanel[]{
        dataPanel,
        histogramPanel,
        crossPlotPanel,
        plotPanel,
        messagePanel
    };
    private final PagePanel[] classifyingPanels = new PagePanel[]{
        dataPanel,
        plotPanel,
        messagePanel
    };
    private final PagePanel[] clusteringPanels = new PagePanel[]{
        dataPanel,
        histogramPanel,
        crossPlotPanel,
        plotPanel,
        messagePanel
    };

    public  PagePanel[] getPagePanels(int learningMode) {
        switch (learningMode) {
            case PREDICTING_MODE:
                return predictingPanels;
            case CLASSIFYING_MODE:
                return classifyingPanels;
            case CLUSTERING_MODE:
                return clusteringPanels;
        }
        return null;
    }

    private final FunctionProxy[] predictingFunctions = new FunctionProxy[]{
        new FunctionProxy("拟合", RegressFunction.class),
        new FunctionProxy("支持向量机（SVM）", PredictingSVMFunction.class),
        new FunctionProxy("BP神经网络", PredictingBPFunction.class)
    };
    private final FunctionProxy[] classifyingFunctions = new FunctionProxy[]{
        new FunctionProxy("支持向量机（SVM）", ClassifyingSVMFunction.class),
        new FunctionProxy("BP神经网络", ClassifyingBPFunction.class)
    };
    private final FunctionProxy[] clusteringFunctions = new FunctionProxy[]{
        new FunctionProxy("近邻指数分水岭聚类", WatershedClusterFunction.class),
        new FunctionProxy("分层聚类", HierarchicalClustererFunction.class),
        new FunctionProxy("SOM神经网络", SOMClusterFunction.class),
        new FunctionProxy("期望最大化(EM)", EMClusterFunction.class),
        new FunctionProxy("K-Means", SimpleKMeansFunction.class),
        new FunctionProxy("DensityBasedCluster", DensityBasedClusterFunction.class),
        new FunctionProxy("FarthestFirst", FarthestFirstFunction.class)
    };

    public FunctionProxy[] getFunctionProxys(int learningMode) {
        switch (learningMode) {
            case PREDICTING_MODE:
                return predictingFunctions;
            case CLASSIFYING_MODE:
                return classifyingFunctions;
            case CLUSTERING_MODE:
                return clusteringFunctions;
        }
        return null;
    }
}
