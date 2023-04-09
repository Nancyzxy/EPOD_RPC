package utils;

import utils.Constants;

import java.io.*;
import java.util.Arrays;

public class decideMaxMin {
    public static int dim = 5; //dimension of the random cluster
    public static void main(String[] args) throws IOException {
        String filePath = Constants.randomClusterFileName;
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String x;
        double max[] = new double[dim];
        double min[] = new double[dim];
        for (int i=0;i<dim;i++){
            min[i] = Double.MAX_VALUE;
            max[i] = Double.MIN_VALUE;
        }
        while ((x=br.readLine())!=null){
            String[] tmp = x.split(",");
            for (int i=0;i<tmp.length;i++){
                max[i] = Math.max(max[i],Double.parseDouble(tmp[i]));
                min[i] = Math.min(min[i],Double.parseDouble(tmp[i]));
            }
        }
        Arrays.stream(max).forEach(System.out::println);
        Arrays.stream(min).forEach(System.out::println);
        br.close();
    }
}