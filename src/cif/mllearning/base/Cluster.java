/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cif.mllearning.base;

import java.util.ArrayList;
import java.util.List;
import weka.core.Instance;

/**
 *
 * @author 10797
 */
public class Cluster {
    private List<Instance> puntos = new ArrayList<Instance>();
    private Instance centroide;
    private boolean termino = false;

    public Instance getCentroide() {
        return centroide;
    }

    public void setCentroide(Instance centroide) {
        this.centroide = centroide;
    }

    public List<Instance> getInstances() {
        return puntos;
    }

    public List<Instance> getPuntos() {
        return puntos;
    }

    public boolean isTermino() {
        return termino;
    }

    public void setTermino(boolean termino) {
        this.termino = termino;
    }

    public void limpiarInstances() {
        puntos.clear();
    }

    @Override
    public String toString() {
        return centroide.toString();
    }
}
