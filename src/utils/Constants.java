package utils;

import java.util.Objects;

public class Constants {
//    public static boolean withTime = true;

    // Configuration
    public static int currentSlideID = -1;
    public static int nn = 2;
    public static int dn = 2;
    public static int nW = 500;
    public static String methodToGenerateFingerprint = "NETS"; //"NETS" "MCOD"
    public static String dataset = "RC";


    //calculated automatically

    public static double R = -1;// distance threshold, default=6.5(HPC), 115(EM), 1.9(TAO), 0.45(STK), 0.028(GAU), 525(FC), 2.75(GAS), 5(RC)
    public static int dim = -1; // dimension, default = 7(HPC), 16(EM), 55(FC), 3(TAO), 10(GAS),1(STK)
    public static int subDim = -1;
    public static int S = -1; // sliding size, default = 500(FC, TAO), 5000(Otherwise)
    public static int W = -1; // Window size, default = 10000(FC, TAO), 100000(Otherwise)
    public static int nS = -1;

    //fixed
    public static int K = 50; // neighborhood threshold, default = 50
    public static String prefix = "C:\\Users\\14198\\Desktop\\outlier_detection\\NETS\\Datasets\\";

    //Paths
    //    public static String prefix = "/home/xinyingzheng/Desktop/outlier_detection";
    public static String deviceIdPrefix = Constants.prefix + "\\DeviceId_data\\Device_" + nn * dn + "_" + dataset + "\\";
    public static String timePrefix = Constants.prefix + "\\Timestamp_data\\Device_" + nn * dn + "_" + dataset + "\\";

    public static String forestCoverFileName = prefix + "fc.txt";
    public static String taoFileName = prefix + "tao.txt";
    public static String emFileName = prefix + "ethylene.txt";
    public static String stockFileName = prefix + "stock.txt";
    public static String gaussFileName = prefix + "gaussian.txt";
    public static String hpcFileName = prefix + "household2.txt";
    public static String gasFileName = prefix + "gas.txt";
    public static String randomClusterFileName = prefix + "RandomCluster.txt";

    static {
        if (dataset.contains("TAO") || dataset.contains("FC") || dataset.contains("RC")) {
            S = 500;
            W = 10000;
        } else {
            S = 5000;
            W = 100000;
        }
        nS = S / W;

        if (dataset.contains("FC")) {
            R = 525;
            dim = 55;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        } else if (dataset.contains("TAO")) {
            R = 1.9;
            dim = 3;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        } else if (dataset.contains("EM")) {
            R = 115;
            dim = 16;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        } else if (dataset.contains("STK")) {
            R = 0.45;
            dim = 1;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        } else if (dataset.contains("GAU")) {
            R = 0.028;
            dim = 1;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        } else if (dataset.contains("HPC")) {
            R = 6.5;
            dim = 7;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        } else if (dataset.contains("GAS")) {
            R = 2.75;
            dim = 10;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        } else if (dataset.contains("RC")){
            R = 4;
            dim = 5;
            if (Objects.equals(methodToGenerateFingerprint, "NETS")){
                subDim = 1;
            }
        }
    }
}


