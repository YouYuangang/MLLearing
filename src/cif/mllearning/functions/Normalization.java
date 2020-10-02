/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.29
 */
public class Normalization {

    public double[] xVarLowers;
    public double[] xVarUppers;
    public double yVarLower;
    public double yVarUpper;
    public double[] varLowers;
    public double[] varUppers;
    public String[] names;
    
    
    
    public Normalization(int xVarCount, int varCount) {
        if (xVarCount > 0) {
            xVarLowers = new double[xVarCount];
            xVarUppers = new double[xVarCount];
            names = new String[xVarCount];
        }
        if (varCount > 0) {
            varLowers = new double[varCount];
            varUppers = new double[varCount];
        }
    }

    public void normalizeXVar(String variableName,int index, double[] data, double lower, double upper) {
        names[index] = variableName;
        if(variableName.equals("RT")){
            if(lower<2){
                lower = 2;
            }
            
            lower = Math.log(lower);
            upper = Math.log(upper);
            xVarLowers[index] = lower;
            xVarUppers[index] = upper;
            for(int i = 0 ;i<data.length;i++){
                if(data[i]<2){
                    data[i]=2;
                }
                data[i] = Math.log(data[i]);
            }
        }else{
            xVarLowers[index] = lower;
            xVarUppers[index] = upper;
        }
        
        
        normalize(data, lower, upper);
    }

    public void normalizeVar(int index, double[] data, double lower, double upper) {
        varLowers[index] = lower;
        varUppers[index] = upper;
        normalize(data, lower, upper);
    }

    public void normalizeYVar(double[] data, double lower, double upper) {
        yVarLower = lower;
        yVarUpper = upper;
        normalize(data, lower, upper);
    }

    public double getXVarLower(int index) {
        return xVarLowers[index];
    }

    public double getXVarUpper(int index) {
        return xVarUppers[index];
    }

    public double getYVarLower() {
        return yVarLower;
    }

    public double getYVarUpper() {
        return yVarUpper;
    }

    public double getVarLower(int index) {
        return varLowers[index];
    }

    public double getVarUpper(int index) {
        return varUppers[index];
    }

    public void unnormalizeYVar(double[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] * (yVarUpper - yVarLower) + yVarLower;
        }
    }

    private void normalize(double[] data, double lower, double upper) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (data[i] - lower) / (upper - lower);
        }
    }
}
