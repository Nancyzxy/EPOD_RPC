package Detector;

import Framework.DeviceImpl;
import RPC.Vector;

import java.util.*;

public abstract class Detector {
    public Set<? extends Vector> outlierVector; // This field is only used to return to the global network
    public Map<Integer, Map<List<Double>, List<Vector>>> externalData;
    public Map<List<Double>, Integer> status;
    DeviceImpl deviceImpl;
    public Detector(DeviceImpl deviceImpl){
        this.deviceImpl = deviceImpl;
        this.externalData = Collections.synchronizedMap(new HashMap<>());
    }
    public abstract void detectOutlier(List<Vector> data);

    //pruning + 后续处理
    public abstract void processOutliers();

    public abstract Map<List<Double>,List<Vector>> sendData(Set<List<Double>> bucketIds, int lastSent);
}
