/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.mllearning.base.DataHelper;
import cif.mllearning.base.MLDataModel;
import cif.mllearning.base.MLDataModelHelper;
import java.awt.Frame;
import javax.swing.SwingWorker;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.18
 */
public abstract class Function extends SwingWorker<Integer, Integer> {
    public static final int GENERATE_MODEL = 0;
    public static final int RUN_MODEL = 1;
    public int flag = 0;
    
    protected MLDataModel mlModel;
    protected MLDataModelHelper mlModelHelper;
    protected DataHelper dataHelper;

    public void setRunModel(int RunMode){
        flag = RunMode;
    }
    
    public void setMLModel(MLDataModel mlModel) {
        this.mlModel = mlModel;
        mlModelHelper = new MLDataModelHelper(mlModel);
        dataHelper = new DataHelper(mlModel);
    }

    @Override
    protected void done() {
        super.done();
        firePropertyChange("done", null, null);
    }

    public void print(String message) {
        firePropertyChange("print", null, message);
    }

    public void println(String message) {
        firePropertyChange("println", null, message);
    }

    public void printError(String message) {
        firePropertyChange("printError", null, message);
    }

    public void printHighlight(String message) {
        firePropertyChange("printHighlight", null, message);
    }

    public void printLine() {
        firePropertyChange("printLine", null, null);
    }

    public void progressPrint(String message) {
        firePropertyChange("progressPrint", null, message);
    }

    public abstract boolean setParameters(Frame parentWindow);

    protected String toStr(double d) {
        return String.format("%10.3f", d);
    }

    protected String toStr(int i) {
        return String.format("%10d", i);
    }

    protected String toStr(String s) {
        return String.format("%10s", s);
    }
}
