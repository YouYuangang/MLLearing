// Hamming distance metric class

package cif.mllearning.functions.kdtree;

class HammingDistance extends DistanceMetric {
    
    @Override
    protected double distance(double [] a, double [] b)  {

	double dist = 0;

	for (int i=0; i<a.length; ++i) {
	    double diff = (a[i] - b[i]);
	    dist += Math.abs(diff);
	}

	return dist;
    }     
}
