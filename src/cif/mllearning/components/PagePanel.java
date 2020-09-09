/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.components;

/**
 *
 * @author wangcaizhi
 * @create 2019.2.25
 */
public abstract class PagePanel extends javax.swing.JPanel {
    
    public boolean isUpdateRequired = true;

    public abstract String getTitle();

    public abstract String getIconName();

    public abstract String getID();
   

}
