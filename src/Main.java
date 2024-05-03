import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    private static double[][] data;
    private static int numA;
    private static int numI;
    private static String[] labels;
    private static int k;
    private static int[] clusterA;
    private static double[][] cent;

    public static void main(String args[]) {
        readDataFromFile("iris_kmeans.txt");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the value of k (number of clusters): ");
        k = scanner.nextInt();
        initCent();
        boolean converged = false;
        while (!converged) {
            assignToClusters();
            double[][] newCentroids = calCentroids();
            if (Arrays.deepEquals(cent, newCentroids)) {
                converged = true;
            } else {
                cent = newCentroids;
            }
            printInfo();
        }
        scanner.close();
    }

    private static void readDataFromFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            List<double[]> dataList = new ArrayList<>();
            List<String> labelList = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                double[] instance = new double[parts.length - 1];
                for (int i = 0; i < parts.length - 1; i++) {
                    instance[i] = Double.parseDouble(parts[i]);
                }
                dataList.add(instance);
                labelList.add(parts[parts.length - 1]);
            }
            br.close();
            numI = dataList.size();
            numA = dataList.get(0).length;
            data = new double [numI][numA];
            labels = new String [numI];

            for (int i = 0; i < numI; i++) {
                data[i] = dataList.get(i);
                labels[i] = labelList.get(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initCent() {
        cent = new double[k][numA];
        for (int i = 0; i < k; i++) {
            cent[i] = data[(int)(Math.random()*numI)];
        }
    }

    private static void assignToClusters() {
        clusterA = new int [numI];
        for (int i = 0; i < numI; i++) {
            double minDist = Double.MAX_VALUE;
            int closestCluster = 0;
            for (int j = 0; j < k; j++) {
                double distance = calDist(data[i], cent[j]);
                if (distance < minDist) {
                    minDist = distance;
                    closestCluster = j;
                }
            }
            clusterA[i] = closestCluster;
        }
    }

    private static double calDist(double[] instance, double[] centroid) {
        double sum = 0;
        for (int i = 0; i < numA; i++) {
            sum += Math.pow(instance[i] - centroid[i], 2);
        }
        return Math.sqrt(sum);
    }

    private static double[][] calCentroids() {
        double[][] newCentroids = new double[k][numA];
        int[] clusterCounts = new int[k];
        for (int i = 0; i < numI; i++) {
            int cluster = clusterA[i];
            for (int j = 0; j < numA; j++) {
                newCentroids[cluster][j] += data[i][j];
            }
            clusterCounts[cluster]++;
        }
        for (int i = 0; i < k; i++) {
            if (clusterCounts[i] > 0) {
                for (int j = 0; j < numA; j++) {
                    newCentroids[i][j] /= clusterCounts[i];
                }
            }
        }
        return newCentroids;
    }

    private static void printInfo() {
        System.out.println();
        double totalDistance = 0;
        for (int i = 0; i < numI; i++) {
            totalDistance += calDist(data[i], cent[clusterA[i]]);
        }
        System.out.println("Sum of distances between instances and cent: " + totalDistance);
        int total;
        for (int i = 0; i < k; i++) {
            total=0;
            Map<String, Integer> labelCounts = new HashMap<>();
            for (int j = 0; j < numI; j++) {
                if (clusterA[j] == i) {
                    total++;
                    labelCounts.put(labels[j], labelCounts.getOrDefault(labels[j], 0) + 1);
                }
            }
            System.out.print("Cluster " + (i + 1) + ": ");
            for (Map.Entry<String, Integer> entry : labelCounts.entrySet()) {
                System.out.print(entry.getKey()+": "+((double)entry.getValue()/(double)total)*100+"%  ");
            }
            System.out.println();
        }
    }
}