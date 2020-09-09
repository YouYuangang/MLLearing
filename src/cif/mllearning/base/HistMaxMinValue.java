/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cif.mllearning.base;

/**
 *
 * @author 10797
 */
public class HistMaxMinValue {
    
    private double max;
    private double min;
    
    public void setMaxValue(double max){
        this.max=max;
    }
    public void setMinValue(double min){
        this.min=min;
    }
    
    public double getMaxValue(){
        return this.max;
    }
    public double getMinValue(){
        return this.min;
    }
}
