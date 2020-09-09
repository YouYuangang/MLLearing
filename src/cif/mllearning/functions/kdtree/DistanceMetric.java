// Abstract distance metric class

package cif.mllearning.functions.kdtree;

abstract class DistanceMetric {
    
    protected abstract double distance(double [] a, double [] b);
}
