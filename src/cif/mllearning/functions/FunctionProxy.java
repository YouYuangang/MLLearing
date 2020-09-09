/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

/**
 *
 * @author wangcaizhi
 * @create 2019.3.22
 */
public class FunctionProxy {

    /**
     *  代理函数的名称
     */
    public String displayName;

    /**
     *  代理函数的函数名
     */
    public Class classType;

    /**
     *
     * @param displayName   代理函数的名称
     * @param classType     代理函数的函数名
     */
    public FunctionProxy(String displayName, Class classType) {
        this.displayName = displayName;
        this.classType = classType;
    }
    
}
