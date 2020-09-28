/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.loglab.math.MathBase;
import cif.mllearning.functions.kdtree.EuclideanDistance;
import cif.mllearning.functions.kdtree.KDTree;
import cif.mllearning.functions.kdtree.KeyDuplicateException;
import cif.mllearning.functions.kdtree.KeySizeException;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author wangcaizhi
 * @create 2019.4.12
 */
public class WatershedClusterFunction extends Function {

    private final static double ALFA = 12;
    private final static int NI_NEIGHBOR_COUNT = 100;

    @Override
    public boolean setParameters(Frame parentWindow) {
        try {
            test();
        } catch (KeySizeException | KeyDuplicateException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    private void test() throws KeySizeException, KeyDuplicateException {

//        double[][] data = new double[6][];
//        data[0] = new double[]{2, 3};
//        data[1] = new double[]{5, 4};
//        data[2] = new double[]{9, 6};
//        data[3] = new double[]{4, 7};
//        data[4] = new double[]{8, 1};
//        data[5] = new double[]{7, 2};
//
//        KDTree<Integer> kdTree = new KDTree<>(2);
//        for (int i = 0; i < data.length; i++) {
//            kdTree.insert(data[i], i);
//        }
//        double[] nis = new double[data.length];
//        for (int i = 0; i < data.length; i++) {
//            List<Integer> neighbors = kdTree.nearest(data[i], data.length);
//            for (int m = 0; m < data.length; m++) {
//                int id = neighbors.get(m);
//                nis[id] += data.length - m - 1;
//            }
//        }
//        for (int i = 0; i < nis.length; i++) {
//            System.out.println(nis[i]);
//        }
    }

    @Override
    protected Integer doInBackground() throws KeySizeException {
        long startTime = System.currentTimeMillis();
        double[][] dataSet = formDataSet();
        normalize(dataSet);
        KDTree<Integer> kdTree = new KDTree<>(dataSet[0].length);
        for (int i = 0; i < dataSet.length; i++) {
            try {
                kdTree.insert(dataSet[i], i);
            } catch (KeyDuplicateException ex) {
                double random = (Math.random() - 0.5) * 0.001;
                dataSet[i][0] += random;
                i--;
            }
        }
        double[] nis = new double[dataSet.length];
        for (int row = 0; row < dataSet.length; row++) {
            List<Integer> neighbors = kdTree.nearest(dataSet[row], NI_NEIGHBOR_COUNT);
            for (int m = 0; m < NI_NEIGHBOR_COUNT; m++) {
                int id = neighbors.get(m);
                nis[id] += Math.exp((-(NI_NEIGHBOR_COUNT - m - 1) / ALFA));
            }
            progressPrint(String.format("%d, %d", row, dataSet.length));
        }
        double[] kris = new double[dataSet.length];
        for (int i = 0; i < dataSet.length; i++) {
            kris[i] = -1;
        }

        int count = 0;
        int[] searchRanges = getKRIgetKRISearchRanges(dataSet.length);
        for (int i = 0; i < searchRanges.length; i++) {
            for (int row = 0; row < dataSet.length; row++) {
                if (kris[row] < 0) {
                    List<Integer> neighbors = kdTree.nearest(dataSet[row], searchRanges[i]);
                    for (int m = searchRanges[i] - 1; m >= 0; m--) {
                        int id = neighbors.get(m);
                        if (nis[id] > nis[row] && id != row) {
                            double dis = EuclideanDistance.sqrdist(dataSet[row], dataSet[id]);
                            int n = searchRanges[i] - m - 1;
                            kris[row] = nis[row] * dis * n;
                            count++;
                            progressPrint(String.format("%d, %d", count, dataSet.length));
                            break;
                        }
                    }
                }
            }
        }
        int vv = 0;
        for (int i = 1; i < nis.length - 1; i++) {
            if (nis[i] > nis[i - 1] && nis[i] > nis[1 + 1]) {
                vv++;
            }
        }
        System.out.println("ssss" + vv);
        watershed(dataSet, kdTree, nis);

//        for (int i = 0; i < dataSet.length; i++) {
//            System.out.println(kris[i]);
//        }
//        for (int i = 0; i < dataSet.length; i++) {
//            System.out.println(nis[i]);
//        }
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
        return 1;
    }

    private int[] getKRIgetKRISearchRanges(int dataSetCount) {
        if (dataSetCount < 10) {
            return new int[]{dataSetCount};
        } else if (dataSetCount < 200) {
            return new int[]{10, dataSetCount};
        } else {
            return new int[]{10, 50, dataSetCount / 2, dataSetCount};
        }
    }

    private double[][] formDataSet() {
        int varCount = dataHelper.getOilXVariableCount();
        int rowCount = dataHelper.getRealRowCount();
        double[][] dataSet = new double[rowCount][varCount];
        for (int row = 0; row < rowCount; row++) {
            dataHelper.readRealRowOilXData(row, dataSet[row]);
        }
        return dataSet;
    }

    private void normalize(double[][] dataSet) {
        for (int col = 0; col < dataSet[0].length; col++) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (double[] rowData : dataSet) {
                if (rowData[col] < min) {
                    min = rowData[col];
                }
                if (rowData[col] > max) {
                    max = rowData[col];
                }
            }
            for (double[] rowData : dataSet) {
                rowData[col] = (rowData[col] - min) / (max - min);
            }
        }
    }

    private void watershed(double[][] dataSet, KDTree<Integer> kdTree, double[] nis) {
        WatershedFIFO queue = new WatershedFIFO();
        WatershedData watershedData = new WatershedData(dataSet, kdTree, nis);
        int curlab = 0;
        int heightIndex1 = 0;
        int heightIndex2 = 0;

        for (int val = WatershedData.WATERSHED_VALUE_MIN; val < WatershedData.WATERSHED_VALUE_MAX; val++) /*Geodesic SKIZ of level h-1 inside level h */ {

            for (int pointIndex = heightIndex1; pointIndex < watershedData.size(); pointIndex++) /*mask all pixels at level h*/ {
                WatershedDataPoint p = watershedData.get(pointIndex);
                if (p.getValue() != val) {
                    /**
                     * This pixel is at level h+1 *
                     */
                    heightIndex1 = pointIndex;
                    break;
                }
                p.setLabelToMASK();
                ArrayList<WatershedDataPoint> neighbours = p.getNeighbours();
                for (int i = 0; i < neighbours.size(); i++) {
                    WatershedDataPoint q = neighbours.get(i);
                    if (q.getLabel() >= 0) {/*Initialise queue with neighbours at level h of current basins or watersheds*/
                        p.setDistance(1);
                        queue.fifo_add(p);
                        break;
                    } // end if
                } // end for
            } // end for

            int curdist = 1;
            queue.fifo_add_FICTITIOUS();

            while (true) /**
             * extend basins *
             */
            {
                WatershedDataPoint p = queue.fifo_remove();

                if (p.isFICTITIOUS()) {
                    if (queue.fifo_empty()) {
                        break;
                    } else {
                        queue.fifo_add_FICTITIOUS();
                        curdist++;
                        p = queue.fifo_remove();
                    }
                }

                ArrayList<WatershedDataPoint> neighbours = p.getNeighbours();
                for (int i = 0; i < neighbours.size(); i++) /* Labelling p by inspecting neighbours */ {
                    WatershedDataPoint q = neighbours.get(i);

                    if ((q.getDistance() <= curdist)
                            && (q.getLabel() >= 0)) {
                        /* q belongs to an existing basin or to a watershed */
                        if (q.getLabel() > 0) {
                            if (p.isLabelMASK()) // Removed from original algorithm || p.isLabelWSHED() )
                            {
                                p.setLabel(q.getLabel());
                            } else if (p.getLabel() != q.getLabel()) {
                                p.setLabelToWSHED();
                            }
                        } // end if lab>0
                        else if (p.isLabelMASK()) {
                            p.setLabelToWSHED();
                        }
                    } else if (q.isLabelMASK() && (q.getDistance() == 0)) {
                        q.setDistance(curdist + 1);
                        queue.fifo_add(q);
                    }
                } // end for, end processing neighbours
            } // end while (loop)

            /* Detect and process new minima at level h */
            for (int pixelIndex = heightIndex2; pixelIndex < watershedData.size(); pixelIndex++) {
                WatershedDataPoint p = watershedData.get(pixelIndex);

                if (p.getValue() != val) {
                    /**
                     * This pixel is at level h+1 *
                     */
                    heightIndex2 = pixelIndex;
                    break;
                }
                p.setDistance(0);
                /* Reset distance to zero */
                if (p.isLabelMASK()) {
                    /* the pixel is inside a new minimum */
                    curlab++;
                    p.setLabel(curlab);
                    queue.fifo_add(p);

                    while (!queue.fifo_empty()) {
                        WatershedDataPoint q = queue.fifo_remove();

                        ArrayList<WatershedDataPoint> neighbours = q.getNeighbours();
                        for (int i = 0; i < neighbours.size(); i++) /* inspect neighbours of p2*/ {
                            WatershedDataPoint r = neighbours.get(i);
                            if (r.isLabelMASK()) {
                                r.setLabel(curlab);
                                queue.fifo_add(r);
                            }
                        }
                    } // end while
                } // end if
            } // end for
        }
        /**
         * End of flooding *
         */
        for (int pointIndex = 0; pointIndex < watershedData.size(); pointIndex++) {
            WatershedDataPoint p = watershedData.get(pointIndex);
            for (int m = 0; m < p.getCoordinates().length; m++) {
                System.out.print(p.getCoordinates()[m] + "\t");
            }
            System.out.println(p.getLabel());
        }
    }
}

class WatershedDataPoint implements Comparable {

    // Value used to initialise the image
    final static int INIT = -1;
    //Value used to indicate the new pixels that are going to be processed
    // (intial value at each level)
    final static int MASK = -2;
    // Value indicating that the pixel belongs to a watershed.
    final static int WSHED = 0;
    //  * Fictitious pixel *
    final static int FICTITIOUS = -3;
    private double[] coordinates;
    private int value;
    // Label used in the Watershed immersion algorithm *
    private int label;
    //Distance used for working on pixels
    private int dist;
    private ArrayList<WatershedDataPoint> neighbours;

    public WatershedDataPoint(double[] coordinates, int value) {
        this.coordinates = coordinates;
        this.value = value;
        label = INIT;
        dist = 0;
        neighbours = new ArrayList<>(WatershedData.WATERSHED_NEIGHBOR_COUNT);
    }

    public WatershedDataPoint() {
        label = FICTITIOUS;
    }

    public void addNeighbour(WatershedDataPoint neighbour) {
        neighbours.add(neighbour);
    }

    public ArrayList<WatershedDataPoint> getNeighbours() {
        return neighbours;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("coordinates: ").append(Arrays.toString(coordinates));
        sb.append("\tvalue:").append(value);
        sb.append("\tlabel:").append(label);
        sb.append("\tdistance:").append(dist);
        return sb.toString();
    }

    public final int getValue() {
        return value;
    }

    public final double[] getCoordinates() {
        return coordinates;
    }

    /**
     * Method to be able to use the Collections.sort static method. *
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof WatershedDataPoint)) {
            throw new ClassCastException();
        }
        WatershedDataPoint obj = (WatershedDataPoint) o;
        if (obj.getValue() < getValue()) {
            return 1;
        }
        if (obj.getValue() > getValue()) {
            return -1;
        }
        return 0;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public void setLabelToINIT() {
        label = INIT;
    }

    public void setLabelToMASK() {
        label = MASK;
    }

    public void setLabelToWSHED() {
        label = WSHED;
    }

    public boolean isLabelINIT() {
        return label == INIT;
    }

    public boolean isLabelMASK() {
        return label == MASK;
    }

    public boolean isLabelWSHED() {
        return label == WSHED;
    }

    public int getLabel() {
        return label;
    }

    public void setDistance(int distance) {
        dist = distance;
    }

    public int getDistance() {
        return dist;
    }

    public boolean isFICTITIOUS() {
        return label == FICTITIOUS;
    }

    public boolean allNeighboursAreWSHED() {
        for (int i = 0; i < neighbours.size(); i++) {
            WatershedDataPoint r = (WatershedDataPoint) neighbours.get(i);
            if (!r.isLabelWSHED()) {
                return false;
            }
        }
        return true;
    }
}

class WatershedData {

    public final static int WATERSHED_VALUE_MIN = 0;
    public final static int WATERSHED_VALUE_MAX = 256;
    public final static int WATERSHED_NEIGHBOR_COUNT = 8;
    ArrayList<WatershedDataPoint> points;

    public WatershedData(double[][] dataSet, KDTree<Integer> kdTree, double[] nis) {
        double maxNI = MathBase.maximum(nis);
        double minNI = MathBase.minimum(nis);

        points = new ArrayList<>(dataSet.length);
        for (int row = 0; row < dataSet.length; row++) {
            WatershedDataPoint point = new WatershedDataPoint(dataSet[row], toPointValue(nis[row], minNI, maxNI));
            points.add(point);
        }
        for (int row = 0; row < dataSet.length; row++) {
            WatershedDataPoint curPoint = points.get(row);
            List<Integer> neighbors = null;
            try {
                neighbors = kdTree.nearest(dataSet[row], WATERSHED_NEIGHBOR_COUNT + 1);// don't include itshelf
            } catch (KeySizeException | IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
            for (int m = WATERSHED_NEIGHBOR_COUNT - 1; m >= 0; m--) {
                int id = neighbors.get(m);
                curPoint.addNeighbour(points.get(id));
            }
        }
        Collections.sort(points);
    }

    private int toPointValue(double d, double dmin, double dmax) {
        int val = (int) ((d - dmin) * (WATERSHED_VALUE_MAX - WATERSHED_VALUE_MIN) / (dmax - dmin) + WATERSHED_VALUE_MIN);
        return WATERSHED_VALUE_MAX - val;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            sb.append(points.get(i).toString()).append("\n");
            sb.append("Neighbours :\n");

            ArrayList<WatershedDataPoint> neighbours = points.get(i).getNeighbours();
            for (int j = 0; j < neighbours.size(); j++) {
                sb.append(neighbours.get(j).toString()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int size() {
        return points.size();
    }

    public WatershedDataPoint get(int i) {
        return points.get(i);
    }
}

/**
 * This class implements a FIFO queue that uses the same formalism as the
 * Vincent and Soille algorithm (1991)
 *
 */
class WatershedFIFO {

    private LinkedList watershedFIFO;

    public WatershedFIFO() {
        watershedFIFO = new LinkedList();
    }

    public void fifo_add(WatershedDataPoint p) {
        watershedFIFO.addFirst(p);
    }

    public WatershedDataPoint fifo_remove() {
        return (WatershedDataPoint) watershedFIFO.removeLast();
    }

    public boolean fifo_empty() {
        return watershedFIFO.isEmpty();
    }

    public void fifo_add_FICTITIOUS() {
        watershedFIFO.addFirst(new WatershedDataPoint());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < watershedFIFO.size(); i++) {
            sb.append(((WatershedDataPoint) watershedFIFO.get(i)).toString()).append("\n");
        }
        return sb.toString();
    }
}
