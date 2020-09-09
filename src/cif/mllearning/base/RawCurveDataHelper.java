/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.base;

import cif.base.Global;
import cif.dataengine.io.LogCategory;
import cif.dataengine.io.LogCurve1D;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.3
 */
public class RawCurveDataHelper {
    public static final String DEPTH_METER = "meter";
    public static final String DEPTH_FEET = "feet";
    private MLDataModel mlModel;
    private LogCurve1D[] curve1Ds;
    private double depthLevel;
    private String depthUnit;
    private float[] buffer = new float[1];

    public RawCurveDataHelper(MLDataModel mlModel) {
        this.mlModel = mlModel;
        LogCategory category = mlModel.inputDataPath.getCategory();
        Variable[] variables = mlModel.getVariables();
        curve1Ds = new LogCurve1D[variables.length];
        for (int i = 0; i < variables.length; i++) {
            curve1Ds[i] = category.getLogCurve1D(variables[i].name);
        }
        depthLevel = getMinDepthLevel(curve1Ds);
        depthUnit = getCHNDepthUnit(category.getCategoryDepthUnit());
    }

    private String getCHNDepthUnit(String depUnit) {
        if (depUnit.equals(Global.DEPTH_METER)) {
            return "米";
        } else if (depUnit.equals(Global.DEPTH_FEET)) {
            return "英尺";
        } else {
            return depUnit;
        }
    }

    private double getMinDepthLevel(LogCurve1D[] curves) {
        double minLevel = Double.MAX_VALUE;
        for (LogCurve1D curve : curves) {
            double level = curve.getDepthLevel();
            minLevel = level < minLevel ? level : minLevel;
        }
        return minLevel;
    }

    public double getDepthLevel() {
        return depthLevel;
    }

    public int getCurveCount() {
        return curve1Ds.length;
    }

    public String getCurveName(int curveIndex) {
        return curve1Ds[curveIndex].getName();
    }

    public String getCurveUnit(int curveIndex) {
        return curve1Ds[curveIndex].getCurveUnit();
    }

    public String getDepthUnit() {
        return depthUnit;
    }

    public double getDepth(int dataIndex) {
        return mlModel.curveStdep + dataIndex * depthLevel;
    }

    public int getCurveSampleCount() {
        return (int) ((mlModel.curveEndep - mlModel.curveStdep) / depthLevel + 1.5);
    }

    public float getCurveData(int curveIndex, int dataIndex) {
        double depth = getDepth(dataIndex);
        int count = curve1Ds[curveIndex].readData(depth, 1, buffer, null);
        if (count <= 0) {
            return Global.NULL_FLOAT_VALUE;
        }
        return buffer[0];
    }
}
