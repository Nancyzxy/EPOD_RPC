package utils;

/*
public class GenerateRandomClusteringData {
    static int dim = 5;
    static int clusterNum = 4;

    public static void main(String[] args) throws IOException {
        generateClusteringData(clusterNum);
    }

    public static void generateClusteringData(int number) throws IOException {
        String prefix = "C:\\Users\\14198\\Desktop\\outlier_detection\\NETS\\Datasets\\DeviceId_data\\Device_"
                + clusterNum + "_RC\\";
        File f = new File(prefix);
        if (f.exists()) {
            return;
        } else f.mkdirs();

        BufferedWriter bufferedWriter0 = new BufferedWriter(new FileWriter(
                "C:\\Users\\14198\\Desktop\\outlier_detection\\NETS\\Datasets\\RandomCluster.txt"));
        BufferedWriter[] bufferedWriters = new BufferedWriter[clusterNum];
        for (int i = 0; i < clusterNum; i++) {
            bufferedWriters[i] = new BufferedWriter(new FileWriter(prefix + i + ".txt"));
        }

        ArrayList<Vector> data = TestUtils.generate(dim, 1, 100);
        TestUtils.addNeighbours(data, number - 1, 3000);
        EuclideanDistance euclideanDistance = new EuclideanDistance();
        ArrayList<ArrayList<Vector>> clusters = new ArrayList<>();
        for (Vector v : data) {
            ArrayList<Vector> cluster = new ArrayList<>();
            cluster.add(v);
            TestUtils.addNeighbours(cluster, 1200000 - 1, 3);
            double avg = 0;
            for (Vector u : cluster) {
                avg += euclideanDistance.distance(cluster.get(0), u);
            }
            System.out.println("The avg distance of neighbor is " + avg / cluster.size());
            clusters.add(cluster);
        }


        for (int j = 0; j < clusters.size(); j++) {
            for (int k = 0; k < clusters.get(j).size(); k++) {
                Vector v = clusters.get(j).get(k);
                for (int x = 0; x < v.values.length - 1; x++) {
                    bufferedWriters[j].write(v.values[x] + ",");
                    bufferedWriter0.write(v.values[x] + ",");
                }
                bufferedWriters[j].write(v.values[v.values.length - 1] + "\n");
                bufferedWriter0.write(v.values[v.values.length - 1] + "\n");
            }
            bufferedWriters[j].flush();
            bufferedWriter0.flush();
        }

        for (int i = 0; i < clusterNum; i++) {
            bufferedWriters[i].close();
        }
        bufferedWriter0.close();

    }
}
*/