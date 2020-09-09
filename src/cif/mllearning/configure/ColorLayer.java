/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.configure;

import java.awt.Color;

/**
 *
 * @author Administrator
 */
public class ColorLayer {
    public String nameOfLayer;
    public int red = 0;
    public int green = 0;
    public int blue = 0;
    public ColorLayer(){
        
    }
    public ColorLayer(String nameOfLayer,int red,int green,int blue){
        this.nameOfLayer = nameOfLayer;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    public String toString(){
        return "{\"name\":"+nameOfLayer+",\"red\""+this.red+",\"green\""+this.green+",\"blue\""+this.blue+"}";
    }
}
