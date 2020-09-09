/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cif.mllearning.functions;

import cif.mllearning.base.Cluster;
import cif.mllearning.base.Indice;
import weka.core.DistanceFunction;
import weka.core.Instance;

/**
 * 工具包，实现了聚类测评的一些相关指标
 *
 *
 * @author 10797
 */
public class ClusterValidity {

    public static Indice calcularDunn(Cluster[] clusters, DistanceFunction distanceFunction) {
        double dunn = 0.0;
        double max = 0;
        double min = 0;
        long startTime = System.currentTimeMillis();
        try {
            for (Cluster cluster : clusters) {
                for (Instance punto : cluster.getInstances()) {
                    for (Cluster cluster2 : clusters) {
                        if (!cluster.equals(cluster2)) {
                            for (Instance punto2 : cluster.getInstances()) {
                                if (!punto.equals(punto2)) {
                                    double dist = distanceFunction.distance(punto, punto2);
                                    if (min != 0) {
                                        if (dist < min) {
                                            min = dist;
                                        }
                                    } else {
                                        min = dist;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (Cluster cluster : clusters) {
                for (Instance punto : cluster.getInstances()) {
                    for (Instance punto2 : cluster.getInstances()) {
                        if (!punto.equals(punto2)) {
                            double dist = distanceFunction.distance(punto, punto2);
                            if (dist > max) {
                                max = dist;
                            }
                        }

                    }
                }
            }

            dunn = min / max;
//            System.out.println("MINIMO: " + min);
//            System.out.println("MAXIMO: " + max);            
//            System.out.println("Dunn: " + dunn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        return new Indice("Dunn", dunn, elapsedTime);

    }

    public static Indice calcularSilhouette(Cluster[] clusters, DistanceFunction distanceFunction) {
        double silhouette;
        double a;
        double distA = 0;
        double b;
        double distB = 0;
        double cont;

        long startTime = System.currentTimeMillis();

        for (Cluster cluster : clusters) {
            for (Instance punto : cluster.getInstances()) {
                for (Cluster cluster2 : clusters) {
                    if (!cluster.equals(cluster2)) {
                        for (Instance punto2 : cluster.getInstances()) {
                            if (!punto.equals(punto2)) {
                                distB += distanceFunction.distance(punto, punto2);

                            }
                        }
                    }
                }
            }
        }
        b = distB / clusters.length;

        cont = 0;
        for (Cluster cluster : clusters) {
            for (Instance punto : cluster.getInstances()) {
                for (Instance punto2 : cluster.getInstances()) {
                    if (!punto.equals(punto2)) {
                        distA += distanceFunction.distance(punto, punto2);
                        cont++;
                    }
                }
            }
        }
        a = distA / clusters.length;
        //System.out.println("A: " + a);
        //System.out.println("B: " + b);

        silhouette = b - a / Math.max(a, b);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        return new Indice("silhouette", silhouette, elapsedTime);
    }

    public static Indice calcularBDDunn(Cluster[] clusters, DistanceFunction distanceFunction) {
        double bdDunn = 0.0;
        double max = 0;
        double min = 0;

        long startTime = System.currentTimeMillis();
        try {

            for (Cluster cluster : clusters) {
                for (Cluster cluster2 : clusters) {
                    if (!cluster.equals(cluster2) && cluster.getCentroide() != null && cluster2.getCentroide() != null) {
                        double dist = distanceFunction.distance(cluster.getCentroide(), cluster2.getCentroide());
                        if (min != 0) {
                            if (dist < min) {
                                min = dist;
                            }
                        } else {
                            min = dist;
                        }
                    }
                }
            }

            //get the maximum distance of the points to the centroid of the cluster they belong to
            for (Cluster cluster : clusters) {
                if (cluster.getCentroide() != null) {
                    for (Instance punto : cluster.getInstances()) {
                        double dist = distanceFunction.distance(punto, cluster.getCentroide());
                        if (dist > max) {
                            max = dist;
                        }
                    }
                }
            }
            //System.out.println("MINIMO: " + min);
            //System.out.println("MAXIMO: " + max);

            bdDunn = min / max;
        } catch (Exception e) {
            e.printStackTrace();
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        return new Indice("BDDunn", bdDunn, elapsedTime);

    }

    public static Indice calcularBDSilhouette(Cluster[] clusters, DistanceFunction distanceFunction) {
        double silhouette;
        double a;
        double distA = 0;
        double b;
        double distB = 0;
        double cont = 0;

        long startTime = System.currentTimeMillis();

        for (Cluster cluster : clusters) {
            if (cluster.getCentroide() != null) {
                for (Cluster cluster2 : clusters) {
                    if (cluster2.getCentroide() != null) {
                        if (!cluster.equals(cluster2)) {
                            distB += distanceFunction.distance(cluster.getCentroide(), cluster2.getCentroide());
                            cont++;
                        }
                    }
                }
            }
        }

        b = distB / cont;

        cont = 0;
        for (Cluster cluster : clusters) {
            if (cluster.getCentroide() != null) {
                for (Instance punto : cluster.getInstances()) {
                    distA += distanceFunction.distance(punto, cluster.getCentroide());
                    cont++;
                }
            }
        }
        a = distA / cont;
        //System.out.println("A: " + a);
        //System.out.println("B: " + b);

        silhouette = b - a / Math.max(a, b);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        return new Indice("BDSilhouette", silhouette, elapsedTime);
    }

    public static Indice calcularDavidBouldin(Cluster[] clusters, DistanceFunction distanceFunction) {
        int numberOfClusters = clusters.length;
        double david = 0.0;

        long startTime = System.currentTimeMillis();

        if (numberOfClusters == 1) {
            throw new RuntimeException(
                    "Impossible to evaluate Davies-Bouldin index over a single cluster");
        } else {
            // counting distances within
            double[] withinClusterDistance = new double[numberOfClusters];

            int i = 0;
            for (Cluster cluster : clusters) {
                for (Instance punto : cluster.getInstances()) {
                    withinClusterDistance[i] += distanceFunction.distance(punto, cluster.getCentroide());
                }
                withinClusterDistance[i] /= cluster.getInstances().size();
                i++;
            }

            double result = 0.0;
            double max = Double.NEGATIVE_INFINITY;

            try {
                for (i = 0; i < numberOfClusters; i++) {
                    //if the cluster is null
                    if (clusters[i].getCentroide() != null) {

                        for (int j = 0; j < numberOfClusters; j++) //if the cluster is null
                        {
                            if (i != j && clusters[j].getCentroide() != null) {
                                double val = (withinClusterDistance[i] + withinClusterDistance[j])
                                        / distanceFunction.distance(clusters[i].getCentroide(), clusters[j].getCentroide());
                                if (val > max) {
                                    max = val;
                                }
                            }
                        }
                    }
                    result = result + max;
                }
            } catch (Exception e) {
                System.out.println("Excepcion al calcular DAVID BOULDIN");
                e.printStackTrace();
            }
            david = result / numberOfClusters;
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        return new Indice("DB", david, elapsedTime);
    }

    public static Indice calcularCalinskiHarabasz(Cluster[] clusters, DistanceFunction distanceFunction) {
        double calinski = 0.0;
        double squaredInterCluter = 0;
        double aux;
        double cont = 0;

        long startTime = System.currentTimeMillis();

        try {
            for (Cluster cluster : clusters) {
                if (cluster.getCentroide() != null) {
                    for (Cluster cluster2 : clusters) {
                        if (cluster2.getCentroide() != null) {
                            if (!cluster.equals(cluster2)) {
                                aux = distanceFunction.distance(cluster.getCentroide(), cluster2.getCentroide());
                                squaredInterCluter += aux * aux;
                                cont++;
                            }
                        }
                    }
                }
            }

            calinski = (calcularSquaredDistance(clusters, distanceFunction).result) / (squaredInterCluter / cont);
        } catch (Exception e) {
            System.out.println("Excepcion al calcular CALINSKI");
            e.printStackTrace();
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        return new Indice("calinski", calinski, elapsedTime);
    }

    private static Indice calcularSquaredDistance(Cluster[] clusters, DistanceFunction distanceFunction) {
        double squaredDistance = 0;
        double aux;
        double cont = 0;

        long startTime = System.currentTimeMillis();

        for (Cluster cluster : clusters) {
            for (Instance punto : cluster.getInstances()) {
                for (Instance punto2 : cluster.getInstances()) {
                    if (!punto.equals(punto2)) {
                        aux = distanceFunction.distance(punto, punto2);
                        squaredDistance += aux * aux;
                        cont++;
                    }
                }
            }
        }

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        return new Indice(squaredDistance / cont, elapsedTime);
    }
}
