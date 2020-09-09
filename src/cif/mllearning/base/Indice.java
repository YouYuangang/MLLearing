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
public class Indice {

    public String name;
    public double result;
    public long time;

    public Indice(String name, double result, long time) {
        this.name = name;
        this.result = result;
        this.time = time;
    }

    public Indice(double result, long time) {
        this.result = result;
        this.time = time;
    }
}
