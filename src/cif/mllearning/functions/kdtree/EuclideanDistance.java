// Hamming distance metric class

package cif.mllearning.functions.kdtree;

public class EuclideanDistance extends DistanceMetric {
    
    @Override
    protected double distance(double [] a, double [] b)  {
	
	return Math.sqrt(sqrdist(a, b));
	
    }
    
    public static double sqrdist(double [] a, double [] b) {

	double dist = 0;

	for (int i=0; i<a.length; ++i) {
	    double diff = (a[i] - b[i]);
	    dist += diff*diff;
	}

	return dist;
    }     
}
