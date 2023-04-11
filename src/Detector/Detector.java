package Detector;

import RPC.Vector;
import Framework.Device;
import java.util.*;

public abstract class Detector {
    public Set<? extends Vector> outlierVector; // This field is only used to return to the global network
    public Map<Integer, Map<List<Double>, List<Vector>>> externalData;
    public Map<List<Double>, Integer> status;
    Device device;
    public Detector(Device device){
        this.device = device;
        this.externalData = Collections.synchronizedMap(new HashMap<>());
    }
    public abstract void detectOutlier(List<Vector> data);

    //pruning + 后续处理
    public abstract void processOutliers();

    public abstract Map<List<Double>,List<Vector>> sendData(Set<List<Double>> bucketIds, int lastSent);
}
