/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.utils;

import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.renderer.xy.XYBarRenderer;

/**
 *
 * @author 10797
 */
public class HistCustomRenderer extends XYBarRenderer {

    private static final long serialVersionUID = 784630226449158436L;

    //直方图原来bin的颜色
    private Color mainColor;
    
    private int left;
    private int right;
    private boolean reverse;

    public HistCustomRenderer(Color c, int left, int right) {
        this.mainColor = c;
        this.left = left;
        this.right = right;
    }

    public HistCustomRenderer(Color c, int left, int right, boolean reverse) {
        this.mainColor = c;
        this.left = left;
        this.right = right;
        this.reverse = reverse;
    }

    /**
     * 在没有颜色反转的情况下
     * 0-left && right-binCount 的bin颜色设置为灰色 
     * 在有颜色反转的情况下 
     * left - right 的bin用的颜色设置为灰色
     */
    @Override
    public Paint getItemPaint(int i, int j) {
//        return colors[j % colors.length]; 
        if (!reverse) {
            return (j <= left || j >= right) ? Color.gray : mainColor;
        } else {
            return (j > left && j < right) ? Color.gray : mainColor;
        }
    }
}
